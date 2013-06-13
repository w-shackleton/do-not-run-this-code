package uk.digitalsquid.contactrecall.ingame.games;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;

import uk.digitalsquid.contactrecall.App;
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

	public GameAdapter(FragmentManager fm, App app) {
		super(fm);
		this.app = app;
		possibleContacts = getPossibleContacts();
		
		ArrayList<Contact> selection =
				selectContacts(possibleContacts, maxNum, selectionMode);
	}

	@Override
	public int getCount() {
		if(gameIsFinite) {
			if(selectedContacts == null) return 0;
			return selectedContacts.size();
		}
		return 1000; // Infinite game?!?
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
			return null;
		}
	}
}
