package uk.digitalsquid.contactrecall.ingame.games;

import java.util.LinkedList;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.game.GameDescriptor;
import uk.digitalsquid.contactrecall.ingame.fragments.PhotoNameFragment;
import uk.digitalsquid.contactrecall.mgr.Contact;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Using {@link Contact} as the answer type so we can encapsulate
 * first and last names
 * @author william
 *
 */
public class PhotoNameGame extends GameAdapter {
	
	public PhotoNameGame(FragmentManager fm, App app, GameDescriptor descriptor) {
		super(fm, app, descriptor);
	}
	
	protected LinkedList<Contact> getPossibleContacts() {
		return app.getGame().getAllPhotoContacts();
	}

	@Override
	public Fragment getItem(int pos) {
		Contact contact = get(pos);
        Fragment fragment = new PhotoNameFragment();
        Bundle args = new Bundle();
        
        args.putParcelable(PhotoNameFragment.ARG_CONTACT, contact);
        args.putInt(PhotoNameFragment.ARG_NUMBER_CHOICES, numberOfChoices);
        args.putParcelableArray(PhotoNameFragment.ARG_OTHER_NAMES, getOtherAnswers());
        
        fragment.setArguments(args);
		return fragment;
	}
}
