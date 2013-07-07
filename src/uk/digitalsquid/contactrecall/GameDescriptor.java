package uk.digitalsquid.contactrecall;

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
	private int maxQuestions;
	private boolean finiteGame;
	private float maxTimePerContact;
	private SelectionMode selectionMode;
	private ShufflingMode shufflingMode;
	private NamePart namePart;
	
	private GameDescriptor(Parcel parcel) {
		type = parcel.readInt();
		optionSources = parcel.readInt();
		maxQuestions = parcel.readInt();
		finiteGame = parcel.readInt() == 1;
		maxTimePerContact = parcel.readFloat();
		selectionMode = SelectionMode.valueOf(parcel.readString());
		shufflingMode = ShufflingMode.valueOf(parcel.readString());
		namePart = NamePart.valueOf(parcel.readString());
	}
	public GameDescriptor(int type) {
		this.type = type;
		optionSources = OPTION_SOURCE_ALL_CONTACTS;
		maxQuestions = 10;
		finiteGame = true;
		maxTimePerContact = 5;
		selectionMode = SelectionMode.RANDOM;
		shufflingMode = ShufflingMode.RANDOM;
		namePart = NamePart.DISPLAY;
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
		dest.writeInt(maxQuestions);
		dest.writeInt(finiteGame ? 1 : 0);
		dest.writeFloat(maxTimePerContact);
		dest.writeString(selectionMode.name());
		dest.writeString(shufflingMode.name());
		dest.writeString(namePart.name());
	}
	
	public int getOptionSources() {
		return optionSources;
	}
	public void setOptionSources(int optionSources) {
		this.optionSources = optionSources;
	}

	public int getMaxQuestions() {
		return maxQuestions;
	}
	public void setMaxQuestions(int maxQuestions) {
		this.maxQuestions = maxQuestions;
	}

	public boolean isFiniteGame() {
		return finiteGame;
	}
	public void setFiniteGame(boolean finiteGame) {
		this.finiteGame = finiteGame;
	}

	public float getMaxTimePerContact() {
		return maxTimePerContact;
	}
	public void setMaxTimePerContact(float maxTimePerContact) {
		this.maxTimePerContact = maxTimePerContact;
	}

	public SelectionMode getSelectionMode() {
		return selectionMode;
	}
	public void setSelectionMode(SelectionMode selectionMode) {
		this.selectionMode = selectionMode;
	}

	public ShufflingMode getShufflingMode() {
		return shufflingMode;
	}
	public void setShufflingMode(ShufflingMode shufflingMode) {
		this.shufflingMode = shufflingMode;
	}

	public NamePart getNamePart() {
		return namePart;
	}
	public void setNamePart(NamePart namePart) {
		this.namePart = namePart;
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
