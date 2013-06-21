package uk.digitalsquid.contactrecall.ingame.games;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.game.GameDescriptor;
import uk.digitalsquid.contactrecall.game.GameDescriptor.SelectionMode;
import uk.digitalsquid.contactrecall.game.GameDescriptor.ShufflingMode;
import uk.digitalsquid.contactrecall.mgr.Contact;
import uk.digitalsquid.contactrecall.misc.ListUtils;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public abstract class GameAdapter extends FragmentStatePagerAdapter {
	
	protected final App app;
	
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
	public GameAdapter(FragmentManager fm, App app, GameDescriptor descriptor) {
		super(fm);
		this.app = app;
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
	
	protected final Contact get(int pos) {
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
}
