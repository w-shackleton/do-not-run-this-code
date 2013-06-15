package uk.digitalsquid.contactrecall.ingame.games;

import java.util.LinkedList;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.ingame.fragments.PhotoNameFragment;
import uk.digitalsquid.contactrecall.mgr.Contact;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class PhotoNameGame extends GameAdapter {

	public PhotoNameGame(FragmentManager fm, App app) {
		super(fm, app);
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
        fragment.setArguments(args);
		return fragment;
	}
}
