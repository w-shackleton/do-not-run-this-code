package uk.digitalsquid.contactrecall.ingame.games;

import java.util.LinkedList;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.GameDescriptor;
import uk.digitalsquid.contactrecall.ingame.GameCallbacks;
import uk.digitalsquid.contactrecall.ingame.fragments.PhotoNameView;
import uk.digitalsquid.contactrecall.mgr.Contact;
import uk.digitalsquid.contactrecall.mgr.Question;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;

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
	
	// TODO: Remove?
	Bundle state;

	@Override
	protected Fragment createFragment(int position) {
		Question question = getItem(position);
        Bundle args = new Bundle();
        
        args.putParcelable(PhotoNameView.ARG_QUESTION, question);
        
        PhotoNameView fragment = new PhotoNameView();
        fragment.setArguments(args);
        
		return fragment;
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
