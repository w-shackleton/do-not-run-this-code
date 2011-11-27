package uk.digitalsquid.contactrecall.game;

import uk.digitalsquid.contactrecall.App;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Describes a game config
 * @author william
 *
 */
public class GameDescriptor implements Parcelable {
	
	public static final int GAME_PHOTO_TO_NAME = 1;
	
	private int type;
	
	private GameDescriptor(Parcel parcel) {
		type = parcel.readInt();
	}
	public GameDescriptor(int type) {
		this.type = type;
		
	}
	
	public int getType() {
		return type;
	}
	

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(type);
	}
	
	public static final Parcelable.Creator<GameDescriptor> CREATOR = new Parcelable.Creator<GameDescriptor>() {
		public GameDescriptor createFromParcel(Parcel in) {
			return new GameDescriptor(in);
		}
		public GameDescriptor[] newArray(int size) {
			return new GameDescriptor[size];
		}
	};

	public GameInstance createGameInstance(App app) {
		switch(type) {
		case GAME_PHOTO_TO_NAME:
			return new PhotoToNameGame(app);
		default:
			return null;
		}
	}
}
