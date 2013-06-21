package uk.digitalsquid.contactrecall.ingame.games;

import java.util.LinkedList;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.game.GameDescriptor;
import uk.digitalsquid.contactrecall.ingame.fragments.PhotoNameView;
import uk.digitalsquid.contactrecall.mgr.Contact;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.view.View;
import android.view.ViewGroup;

/**
 * Using {@link Contact} as the answer type so we can encapsulate
 * first and last names
 * @author william
 *
 */
public class PhotoNameGame extends GameAdapter {
	
	public PhotoNameGame(Context context, App app, GameDescriptor descriptor) {
		super(context, app, descriptor);
	}
	
	protected LinkedList<Contact> getPossibleContacts() {
		return app.getGame().getAllPhotoContacts();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Contact contact = getItem(position);
        Bundle args = new Bundle();
        
        args.putParcelable(PhotoNameView.ARG_CONTACT, contact);
        args.putInt(PhotoNameView.ARG_NUMBER_CHOICES, numberOfChoices);
        args.putParcelableArray(PhotoNameView.ARG_OTHER_NAMES, getOtherAnswers());
        
        PhotoNameView viewCreator = new PhotoNameView(app, context, parent, args);
        
		return viewCreator.getRootView();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
}
