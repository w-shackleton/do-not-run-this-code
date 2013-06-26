package uk.digitalsquid.contactrecall.ingame.games;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.GameDescriptor;
import uk.digitalsquid.contactrecall.GameDescriptor.SelectionMode;
import uk.digitalsquid.contactrecall.GameDescriptor.ShufflingMode;
import uk.digitalsquid.contactrecall.ingame.GameCallbacks;
import uk.digitalsquid.contactrecall.mgr.Contact;
import uk.digitalsquid.contactrecall.misc.ListUtils;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Adapter;

public abstract class GameAdapter implements Adapter, Parcelable {
	
	protected transient App app;
	protected transient Context context;
	protected transient GameCallbacks callbacks;
	
	// TODO: These will be settings at some point
	private boolean gameIsFinite = true;
	// Only if game is finite
	private int maxNum = 10;
	// Value of 0 will indicate no timer
	private float maxTimePerContact = 5;
	
	private Contact[] otherAnswers;
	
	/**
	 * How many choices are presented per question
	 */
	protected int numberOfChoices = 4;
	
	SelectionMode selectionMode = SelectionMode.RANDOM;
	ShufflingMode shufflingMode = ShufflingMode.RANDOM;
	
	/**
	 * Holds all the possible contacts we could use.
	 * Check for null-ness - this will be deleted in a
	 * finite game once a subset has been collected.
	 */
	LinkedList<Contact> possibleContacts;
	
	/**
	 * The contacts to use - usually pre-shuffled.
	 */
	ArrayList<Contact> selectedContacts;

	@SuppressWarnings("unchecked")
	public GameAdapter(Context context, App app, GameDescriptor descriptor, GameCallbacks callbacks) {
		this.app = app;
		this.context = context;
		this.callbacks = callbacks;
		possibleContacts = getPossibleContacts();
		
		ArrayList<Contact> badContacts = null; // TODO: Implement
		ArrayList<Contact> allContacts = new ArrayList<Contact>(app.getContacts().getContacts());
		ArrayList<Contact> nameLists = null; // TODO: Implement
		otherAnswers = ListUtils.concat(new ArrayList<Contact>(), badContacts, allContacts, nameLists).toArray(new Contact[0]);
		
		ArrayList<Contact> selection =
				selectContacts(possibleContacts, maxNum, selectionMode);
		selectedContacts = shuffleContacts(selection, shufflingMode);
	}

	@Override
	public int getCount() {
		if(gameIsFinite) {
			if(selectedContacts == null) return 0;
			return selectedContacts.size();
		}
		return 1000; // TODO: Infinite game?!?
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public Contact getItem(int pos) {
		if(selectedContacts == null) return null;
		if(gameIsFinite)
			return selectedContacts.get(pos);
		throw new RuntimeException("Infinite mode not yet implemented");
	}

	protected abstract LinkedList<Contact> getPossibleContacts();
	
	/**
	 * Compares two contacts. Default instance just compares by contact ID
	 * @return
	 */
	protected Comparator<Contact> getContactComparator() {
		return new Comparator<Contact>() {
			@Override
			public int compare(Contact lhs, Contact rhs) {
				if(lhs == null) return -1;
				if(rhs == null) return 1;
				return lhs.getId() - rhs.getId();
			}
		};
	}
	
	private ArrayList<Contact> selectContacts(LinkedList<Contact> possibles, int num, SelectionMode mode) {
		switch(mode) {
		case RANDOM:
			return ListUtils.selectRandomExclusiveDistinctSet(possibles, getContactComparator(), null, num);
		default:
			return new ArrayList<Contact>(possibles);
		}
	}

	private ArrayList<Contact> shuffleContacts(ArrayList<Contact> selection, ShufflingMode mode) {
		switch(mode) {
		case RANDOM:
			Collections.shuffle(selection);
			return selection;
		default:
			return selection;
		}
	}
	
	protected Contact[] getOtherAnswers() {
		return otherAnswers;
	}

	// Not needed for this
	@Override
	public int getItemViewType(int position) {
		return 0;
	}
	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return selectedContacts == null;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) { }

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) { }

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(gameIsFinite ? 1 : 0);
		dest.writeInt(maxNum);
		dest.writeFloat(maxTimePerContact);
		dest.writeParcelableArray(otherAnswers, 0);
		dest.writeInt(numberOfChoices);
		// Eurgh
		dest.writeString(selectionMode.name());
		dest.writeString(shufflingMode.name());
		
		dest.writeList(possibleContacts);
		dest.writeList(selectedContacts);
	}
	
	GameAdapter(Parcel in) {
		gameIsFinite = in.readInt() == 1;
		maxNum = in.readInt();
		maxTimePerContact = in.readFloat();
		otherAnswers = (Contact[]) in.readParcelableArray(null);
		numberOfChoices = in.readInt();
		
		selectionMode = SelectionMode.valueOf(in.readString());
		shufflingMode = ShufflingMode.valueOf(in.readString());
		
		possibleContacts = new LinkedList<Contact>();
		in.readList(possibleContacts, null);
		selectedContacts = new ArrayList<Contact>();
		in.readList(selectedContacts, null);
	}
	
	/**
	 * Initialises the adapter's transient fields. Must be called after
	 * de-parcelisation
	 * @param app
	 * @param context
	 */
	public void init(App app, Context context, GameCallbacks callbacks) {
		if(app == null || context == null || callbacks == null)
			throw new IllegalArgumentException("Arguments must be non-null");
		this.app = app;
		this.context = context;
		this.callbacks = callbacks;
	}
}
