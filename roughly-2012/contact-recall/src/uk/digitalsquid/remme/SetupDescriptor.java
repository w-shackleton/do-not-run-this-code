package uk.digitalsquid.remme;

import java.util.ArrayList;

import uk.digitalsquid.remme.mgr.Question;
import uk.digitalsquid.remme.mgr.Question.QuestionAnswerPair;
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
		
		final ArrayList<QuestionAnswerPair> pairs = new ArrayList<Question.QuestionAnswerPair>();
		
		if(isAskPersonal()) {
			switch(difficulty) {
			case DIFFICULTY_MEGA:
				// TODO: Add some!
			case DIFFICULTY_HARD:
				addQAllTBothDirs(pairs, Question.FIELD_FIRST_NAME, Question.FIELD_LAST_NAME);
				addQ(pairs, Question.STYLE_TRUE_FALSE, Question.FIELD_DISPLAY_NAME, Question.FIELD_EMAIL_MOBILE);
				addQ(pairs, Question.STYLE_TRUE_FALSE, Question.FIELD_DISPLAY_NAME, Question.FIELD_EMAIL_OTHER);
				addQBothDirs(pairs, Question.FIELD_DISPLAY_NAME, Question.FIELD_EMAIL_HOME);
				addQBothDirs(pairs, Question.FIELD_DISPLAY_NAME, Question.FIELD_EMAIL_WORK);
			case DIFFICULTY_MEDIUM:
				addQSimpleT(pairs, Question.FIELD_FIRST_NAME, Question.FIELD_LAST_NAME);
				addQSimpleT(pairs, Question.FIELD_LAST_NAME, Question.FIELD_FIRST_NAME);
				addQ(pairs, Question.STYLE_TRUE_FALSE, Question.FIELD_DISPLAY_NAME, Question.FIELD_EMAIL_HOME);
				addQ(pairs, Question.STYLE_TRUE_FALSE, Question.FIELD_DISPLAY_NAME, Question.FIELD_EMAIL_WORK);
			case DIFFICULTY_EASY:
				addQAllT(pairs, Question.FIELD_PHOTO, Question.FIELD_FIRST_NAME);
				addQAllT(pairs, Question.FIELD_PHOTO, Question.FIELD_LAST_NAME);
				addQAllT(pairs, Question.FIELD_DISPLAY_NAME, Question.FIELD_PHOTO);
			case DIFFICULTY_CASUAL:
				addQAllT(pairs, Question.FIELD_PHOTO, Question.FIELD_DISPLAY_NAME);
				addQ(pairs, Question.FIELD_PHOTO, Question.FIELD_FIRST_NAME);
				addQ(pairs, Question.FIELD_PHOTO, Question.FIELD_LAST_NAME);
				break;
			}
		}
		
		desc.setQuestionTypes(pairs.toArray(new QuestionAnswerPair[pairs.size()]));

		return desc;
	}
	
	private static final void addQ(ArrayList<QuestionAnswerPair> pairs, int questionField, int answerField) {
		pairs.add(new QuestionAnswerPair(questionField, answerField));
	}
	private static final void addQBothDirs(ArrayList<QuestionAnswerPair> pairs, int questionField, int answerField) {
		pairs.add(new QuestionAnswerPair(questionField, answerField));
		pairs.add(new QuestionAnswerPair(answerField, questionField));
	}
	private static final void addQAllT(ArrayList<QuestionAnswerPair> pairs, int questionField, int answerField) {
		pairs.add(new QuestionAnswerPair(Question.STYLE_MULTI_CHOICE, questionField, answerField));
		pairs.add(new QuestionAnswerPair(Question.STYLE_PAIRING, questionField, answerField));
		pairs.add(new QuestionAnswerPair(Question.STYLE_TRUE_FALSE, questionField, answerField));
	}
	private static final void addQAllTBothDirs(ArrayList<QuestionAnswerPair> pairs, int questionField, int answerField) {
		pairs.add(new QuestionAnswerPair(Question.STYLE_MULTI_CHOICE, questionField, answerField));
		pairs.add(new QuestionAnswerPair(Question.STYLE_PAIRING, questionField, answerField));
		pairs.add(new QuestionAnswerPair(Question.STYLE_TRUE_FALSE, questionField, answerField));
		pairs.add(new QuestionAnswerPair(Question.STYLE_MULTI_CHOICE, answerField, questionField));
		pairs.add(new QuestionAnswerPair(Question.STYLE_PAIRING, answerField, questionField));
		pairs.add(new QuestionAnswerPair(Question.STYLE_TRUE_FALSE, answerField, questionField));
	}
	private static final void addQSimpleT(ArrayList<QuestionAnswerPair> pairs, int questionField, int answerField) {
		pairs.add(new QuestionAnswerPair(Question.STYLE_MULTI_CHOICE, questionField, answerField));
		pairs.add(new QuestionAnswerPair(Question.STYLE_TRUE_FALSE, questionField, answerField));
	}
	private static final void addQ(ArrayList<QuestionAnswerPair> pairs, int style, int questionField, int answerField) {
		pairs.add(new QuestionAnswerPair(style, questionField, answerField));
	}
}
