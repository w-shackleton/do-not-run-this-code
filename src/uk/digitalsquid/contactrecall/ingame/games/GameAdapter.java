package uk.digitalsquid.contactrecall.ingame.games;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.GameDescriptor;
import uk.digitalsquid.contactrecall.GameDescriptor.NamePart;
import uk.digitalsquid.contactrecall.GameDescriptor.SelectionMode;
import uk.digitalsquid.contactrecall.GameDescriptor.ShufflingMode;
import uk.digitalsquid.contactrecall.ingame.GameCallbacks;
import uk.digitalsquid.contactrecall.mgr.Contact;
import uk.digitalsquid.contactrecall.mgr.Question;
import uk.digitalsquid.contactrecall.misc.Const;
import uk.digitalsquid.contactrecall.misc.ListUtils;
import android.app.Fragment;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public abstract class GameAdapter implements Parcelable {
	
	protected transient App app;
	protected transient Context context;
	protected transient GameCallbacks callbacks;
	
	protected GameDescriptor descriptor;
	
	private Contact[] otherAnswers;
	
	/**
	 * Holds all the possible contacts we could use.
	 * Check for null-ness - this will be deleted in a
	 * finite game once a subset has been collected.
	 */
	LinkedList<Contact> possibleContacts;
	
	/**
	 * The questions to use - usually pre-shuffled.
	 */
	ArrayList<Question> questions;

	@SuppressWarnings("unchecked")
	public GameAdapter(Context context, App app, GameDescriptor descriptor, GameCallbacks callbacks) {
		this.app = app;
		this.context = context;
		this.callbacks = callbacks;
		this.descriptor = descriptor;
		possibleContacts = getPossibleContacts();

		// Construct the lists that are used as false answers to questions.
		ArrayList<Contact> badContacts = null; // TODO: Implement
		ArrayList<Contact> allContacts = new ArrayList<Contact>(app.getContacts().getContacts());
		ArrayList<Contact> nameLists = null; // TODO: Implement
		otherAnswers = filterOtherAnswers(
				ListUtils.concat(new ArrayList<Contact>(), badContacts, allContacts, nameLists)
				.toArray(new Contact[0]));
		
		ArrayList<Contact> selection =
				selectContacts(possibleContacts, descriptor.getMaxQuestions(), descriptor.getSelectionMode());
		ArrayList<Contact> selectedContacts = shuffleContacts(selection, descriptor.getShufflingMode());
		
		questions = new ArrayList<Question>(selectedContacts.size());
		for(Contact contact : selectedContacts) {
			questions.add(createQuestion(contact));
		}
	}

	public int getCount() {
		if(descriptor.isFiniteGame()) {
			if(questions == null) return 0;
			return questions.size();
		}
		return 1000; // TODO: Infinite game?!?
	}

	public Question getItem(int pos) {
		if(questions == null) return null;
		if(descriptor.isFiniteGame())
			return questions.get(pos);
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
	
	/**
	 * Can be overridden to filter out some contacts, allowing only others
	 * to be used as possible false answers.
	 * For example, all contacts without photos could be filtered.
	 * @param otherAnswers
	 * @return
	 */
	protected Contact[] filterOtherAnswers(Contact[] otherAnswers) {
		return otherAnswers;
	}
	
	protected Contact[] getOtherAnswers() {
		return otherAnswers;
	}

	public boolean isEmpty() {
		return questions == null;
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	protected Question createQuestion(Contact contact) {
		Question question = new Question(contact);
		// TODO: Customise
		question.setNamePart(NamePart.DISPLAY);
		int numOtherChoices = 3;
		question.setCorrectPosition(Const.RAND.nextInt(numOtherChoices + 1));
		
		Contact[] otherChoices = new Contact[numOtherChoices];
        String correctText = contact.getNamePart(question.getNamePart());
		for(int i = 0; i < otherChoices.length; i++) {
    		Contact other = Contact.getNullContact(); // TODO: Lots of nulls created
    		for(int j = 0; j < 20; j++) { // Attempt to find a different name
    			other = otherAnswers[Const.RAND.nextInt(otherAnswers.length)];
    			if(!other.getNamePart(question.getNamePart())
    					.equalsIgnoreCase(correctText)) break;
    		}
    		otherChoices[i] = other;
		}
		
		question.setOtherAnswers(otherChoices);
		
		return question;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(descriptor, 0);
		dest.writeParcelableArray(otherAnswers, 0);
		
		dest.writeList(possibleContacts);
		dest.writeList(questions);
	}
	
	GameAdapter(Parcel in) {
		descriptor = in.readParcelable(null);
		otherAnswers = (Contact[]) in.readParcelableArray(null);
		
		possibleContacts = new LinkedList<Contact>();
		in.readList(possibleContacts, null);
		questions = new ArrayList<Question>();
		in.readList(questions, null);
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
	
	protected abstract Fragment createFragment(int position);
	
	/**
	 * Gets the {@link Fragment} at position.
	 * @param position
	 * @return
	 */
	public Fragment getFragment(int position) {
		return createFragment(position);
	}
}
