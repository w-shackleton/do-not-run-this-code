package uk.digitalsquid.remme;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Describes the setup process that the user goes through. This is used to create
 * a GameDescriptor
 * @author william
 *
 */
public class SetupDescriptor implements Parcelable {
	
	public static final int DIFFICULTY_CASUAL = 0;
	public static final int DIFFICULTY_EASY = 1;
	public static final int DIFFICULTY_MEDIUM = 2;
	public static final int DIFFICULTY_HARD = 3;
	public static final int DIFFICULTY_MEGA = 4;
	public static final int DIFFICULTY_CUSTOM = 5;
	
	private int difficulty;
	
	private boolean askPersonal;
	private boolean askCorporate;
	private boolean askGroups;
	
	public SetupDescriptor() {
		
	}
	
	public SetupDescriptor(Parcel in) {
		difficulty = in.readInt();
		askPersonal = in.readInt() == 1;
		askCorporate = in.readInt() == 1;
		askGroups = in.readInt() == 1;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(difficulty);
		dest.writeInt(askPersonal ? 1 : 0);
		dest.writeInt(askCorporate ? 1 : 0);
		dest.writeInt(askGroups ? 1 : 0);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	public boolean isAskPersonal() {
		return askPersonal;
	}

	public void setAskPersonal(boolean askPersonal) {
		this.askPersonal = askPersonal;
	}

	public boolean isAskCorporate() {
		return askCorporate;
	}

	public void setAskCorporate(boolean askCorporate) {
		this.askCorporate = askCorporate;
	}

	public boolean isAskGroups() {
		return askGroups;
	}

	public void setAskGroups(boolean askGroups) {
		this.askGroups = askGroups;
	}

	public static final Parcelable.Creator<SetupDescriptor> CREATOR = new Parcelable.Creator<SetupDescriptor>() {
		public SetupDescriptor createFromParcel(Parcel in) {
			return new SetupDescriptor(in);
		}
		public SetupDescriptor[] newArray(int size) {
			return new SetupDescriptor[size];
		}
	};
	
	public GameDescriptor generateGameDescriptor() {
		final GameDescriptor desc = new GameDescriptor();

		desc.setMaxTime(90);

		switch(difficulty) {
		case DIFFICULTY_CASUAL:
			desc.setHardTimerPerContact(false);
			desc.setMaxTime(0);
			desc.setMaxTimePerContact(0);
			desc.setMaxQuestions(100);
			desc.setFiniteGame(true);
			desc.setOptionSources(GameDescriptor.OPTION_SOURCE_ALL_CONTACTS);
			desc.setPairingChoices(4, 4);
			desc.setOtherAnswers(2, 3);
			break;
		case DIFFICULTY_EASY:
			desc.setHardTimerPerContact(false);
			desc.setMaxTimePerContact(0);
			desc.setFiniteGame(false);
			desc.setOptionSources(GameDescriptor.OPTION_SOURCE_ALL_CONTACTS);
			desc.setPairingChoices(4, 4);
			desc.setOtherAnswers(2, 3);
			break;
		case DIFFICULTY_MEDIUM:
			desc.setHardTimerPerContact(false);
			desc.setMaxTimePerContact(0);
			desc.setFiniteGame(false);
			desc.setOptionSources(GameDescriptor.OPTION_SOURCE_ALL_CONTACTS);
			desc.setPairingChoices(4, 4);
			desc.setOtherAnswers(3, 5);
			break;
		case DIFFICULTY_HARD:
			desc.setHardTimerPerContact(false);
			desc.setMaxTimePerContact(4);
			desc.setFiniteGame(false);
			desc.setOptionSources(GameDescriptor.OPTION_SOURCE_ALL_CONTACTS);
			desc.setPairingChoices(4, 4);
			desc.setOtherAnswers(3, 7);
			break;
		case DIFFICULTY_MEGA:
			desc.setHardTimerPerContact(true);
			desc.setMaxTimePerContact(2);
			desc.setFiniteGame(false);
			desc.setOptionSources(GameDescriptor.OPTION_SOURCE_ALL_CONTACTS);
			desc.setPairingChoices(4, 4);
			desc.setOtherAnswers(5, 7);
			break;
		}
		return desc;
	}
}
