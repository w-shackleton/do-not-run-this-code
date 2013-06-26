package uk.digitalsquid.contactrecall.ingame.games;

import java.util.LinkedList;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.GameDescriptor;
import uk.digitalsquid.contactrecall.ingame.GameCallbacks;
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
	
	public PhotoNameGame(Context context, App app, GameDescriptor descriptor, GameCallbacks callbacks) {
		super(context, app, descriptor, callbacks);
		state = new Bundle();
	}
	
	public PhotoNameGame(Parcel in) {
		super(in);
		state = in.readBundle();
	}
	
	protected LinkedList<Contact> getPossibleContacts() {
		return app.getGame().getAllPhotoContacts();
	}
	
	Bundle state;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Contact contact = getItem(position);
        Bundle args = new Bundle();
        
        args.putParcelable(PhotoNameView.ARG_CONTACT, contact);
        args.putInt(PhotoNameView.ARG_NUMBER_CHOICES, numberOfChoices);
        args.putParcelableArray(PhotoNameView.ARG_OTHER_NAMES, getOtherAnswers());
        
        Bundle viewState = state.getBundle(String.format("view%d", position));
        PhotoNameView viewCreator = new PhotoNameView(app, context, parent, args, viewState, callbacks);
        
        Bundle viewStateModified = new Bundle();
        viewCreator.onSaveInstanceState(viewStateModified);
        state.putBundle(String.format("view%d", position), viewStateModified);
        
		return viewCreator.getRootView();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeBundle(state);
	}
	
	public static final Creator<PhotoNameGame> CREATOR = new Creator<PhotoNameGame>() {

		@Override
		public PhotoNameGame createFromParcel(Parcel source) {
			return new PhotoNameGame(source);
		}

		@Override
		public PhotoNameGame[] newArray(int size) {
			return new PhotoNameGame[size];
		}
	};
}
