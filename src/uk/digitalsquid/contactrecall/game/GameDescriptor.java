package uk.digitalsquid.contactrecall.game;

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
	
	private int optionSources;
	
	private GameDescriptor(Parcel parcel) {
		type = parcel.readInt();
		optionSources = parcel.readInt();
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
		dest.writeInt(optionSources);
	}
	
	public int getOptionSources() {
		return optionSources;
	}
	public void setOptionSources(int optionSources) {
		this.optionSources = optionSources;
	}

	public static final Parcelable.Creator<GameDescriptor> CREATOR = new Parcelable.Creator<GameDescriptor>() {
		public GameDescriptor createFromParcel(Parcel in) {
			return new GameDescriptor(in);
		}
		public GameDescriptor[] newArray(int size) {
			return new GameDescriptor[size];
		}
	};
	
	/**
	 * How contacts will be selected from the possible contacts
	 * @author william
	 *
	 */
	public static enum SelectionMode {
		RANDOM
	}
	
	/**
	 * How selected contacts will be shuffled
	 * @author william
	 *
	 */
	public static enum ShufflingMode {
		RANDOM
	}
	
	/**
	 * The part of the name to use
	 * @author william
	 *
	 */
	public static enum NamePart {
		FIRST,
		LAST,
		DISPLAY,
		RANDOM
	}
	
	/**
	 * Contacts who the user is bad at remembering
	 */
	public static final int OPTION_SOURCE_BAD_CONTACTS = 1;
	/**
	 * All contacts
	 */
	public static final int OPTION_SOURCE_ALL_CONTACTS = 2;
	/**
	 * Lists of names
	 */
	public static final int OPTION_SOURCE_NAME_LISTS = 4;
}
