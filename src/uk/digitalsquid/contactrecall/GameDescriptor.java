package uk.digitalsquid.contactrecall;

import java.util.Arrays;
import java.util.HashSet;

import uk.digitalsquid.contactrecall.mgr.Question;
import uk.digitalsquid.contactrecall.misc.Config;
import uk.digitalsquid.contactrecall.misc.Const;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Describes a possible configuration for a game.
 * @author william
 *
 */
public class GameDescriptor implements Parcelable, Config {
	
	/**
	 * Types of question-answer pairs that can be used in a game.
	 */
	private Question.QuestionAnswerPair[] questionTypes;
	/**
	 * Sources of potential 'other' contacts. Can be:
	 * <ul>
	 * <li>User's contacts</li>
	 * <li>Contacts the user struggles with</li>
	 * <li>Lists of names</li>
	 */
	private int optionSources;
	/**
	 * The maximum number of questions to be shown - this is how many questions
	 * are computed at the start of the game.
	 */
	private int maxQuestions;
	/**
	 * If <code>false</code>, have an 'infinite' number of questions.
	 * This just means that the end of the timer will always be reached first,
	 * or the user cancels.
	 * This will be implemented by generating some (say 10) questions,
	 * and at the end of that batch, generating more. This will mean that every
	 * nth question there will be a small delay as more are generated.
	 */
	private boolean finiteGame;
	/**
	 * Maximum time to show each contact for, in seconds.
	 * A value of zero indicates no timer per contact.
	 */
	private float maxTimePerContact;
	/**
	 * Maximum time for which a game may run.
	 */
	private float maxTime;
	/**
	 * How questions within groups are shuffled once generated. This is currently
	 * only random, and this option is planned for removal.
	 */
	private ShufflingMode shufflingMode;
	
	/**
	 * For multi-choice questions, the minimum number of other choices
	 * available.
	 */
	private int otherAnswersMinimum;
	/**
	 * For multi-choice questions, the maximum number of other choices
	 * available.
	 */
	private int otherAnswersMaximum;
	
	/**
	 * For pairing questions, the minimum number of choices available.
	 */
	private int pairingChoicesMinimum;
	/**
	 * For pairing questions, the maximum number of choices available.
	 */
	private int pairingChoicesMaximum;
	
	public GameDescriptor() {
		questionTypes = new Question.QuestionAnswerPair[0];
		optionSources = OPTION_SOURCE_ALL_CONTACTS;
		maxQuestions = 10;
		finiteGame = true;
		maxTimePerContact = 0;
		maxTime = 0;
		shufflingMode = ShufflingMode.RANDOM;
		otherAnswersMinimum = 3;
		otherAnswersMaximum = 3;
		pairingChoicesMinimum = 4;
		pairingChoicesMaximum = 4;
	}
	private GameDescriptor(Parcel parcel) {
		// Apparently you can't cast arrays easily
		Parcelable[] array = parcel.readParcelableArray(Question.QuestionAnswerPair.class.getClassLoader());
		setQuestionTypes(Arrays.copyOf(array, array.length, Question.QuestionAnswerPair[].class));
		optionSources = parcel.readInt();
		maxQuestions = parcel.readInt();
		finiteGame = parcel.readInt() == 1;
		maxTimePerContact = parcel.readFloat();
		maxTime = parcel.readFloat();
		shufflingMode = ShufflingMode.valueOf(parcel.readString());
		otherAnswersMinimum = parcel.readInt();
		otherAnswersMaximum = parcel.readInt();
		pairingChoicesMinimum = parcel.readInt();
		pairingChoicesMaximum = parcel.readInt();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelableArray(getQuestionTypes(), 0);
		dest.writeInt(optionSources);
		dest.writeInt(maxQuestions);
		dest.writeInt(finiteGame ? 1 : 0);
		dest.writeFloat(maxTimePerContact);
		dest.writeFloat(maxTime);
		dest.writeString(shufflingMode.name());
		dest.writeInt(otherAnswersMinimum);
		dest.writeInt(otherAnswersMaximum);
		dest.writeInt(pairingChoicesMinimum);
		dest.writeInt(pairingChoicesMaximum);
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

	/**
	 * Gets the maximum time to show each contact for, in seconds.
	 * A value of zero indicates no timer per contact.
	 */
	public float getMaxTimePerContact() {
		return maxTimePerContact;
	}
	/**
	 * If <code>true</code>, a timer should be shown per question.
	 * @return
	 */
	public boolean hasTimerPerContact() {
		return maxTimePerContact != 0;
	}
	/**
	 * Sets the maximum time per contact.
	 * @param maxTimePerContact
	 */
	public void setMaxTimePerContact(float maxTimePerContact) {
		this.maxTimePerContact = maxTimePerContact;
		if(maxTime != 0 && maxTimePerContact != 0)
			Log.w(TAG, "maxTime and maxTimePerContact are both nonzero");
	}

	public ShufflingMode getShufflingMode() {
		return shufflingMode;
	}
	public void setShufflingMode(ShufflingMode shufflingMode) {
		this.shufflingMode = shufflingMode;
	}

	public Question.QuestionAnswerPair[] getQuestionTypes() {
		return questionTypes;
	}

	public void setQuestionTypes(Question.QuestionAnswerPair[] questionTypes) {
		this.questionTypes = questionTypes;
	}
	
	public Question.QuestionAnswerPair getRandomQuestionType() {
		return questionTypes[Const.RAND.nextInt(questionTypes.length)];
	}
	
	/**
	 * Searches through the types of question declared to be used and constructs
	 * a unique list of types of field that will be used during this game.
	 * @return
	 */
	public HashSet<Integer> getUsedFieldTypes() {
		HashSet<Integer> result = new HashSet<Integer>();
		for(Question.QuestionAnswerPair pair : questionTypes) {
			result.add(pair.getQuestionType());
			result.add(pair.getAnswerType());
		}
		return result;
	}
	
	public int getOtherAnswersMinimum() {
		return otherAnswersMinimum;
	}
	public void setOtherAnswersMinimum(int otherAnswersMinimum) {
		this.otherAnswersMinimum = trim(otherAnswersMinimum, 1, 8);
	}

	public int getOtherAnswersMaximum() {
		return otherAnswersMaximum;
	}
	public void setOtherAnswersMaximum(int otherAnswersMaximum) {
		this.otherAnswersMaximum = trim(otherAnswersMaximum, 1, 8);
	}

	public int getPairingChoicesMinimum() {
		return pairingChoicesMinimum;
	}
	public void setPairingChoicesMinimum(int pairingChoicesMinimum) {
		this.pairingChoicesMinimum = trim(pairingChoicesMinimum, 2, 4);
	}

	public int getPairingChoicesMaximum() {
		return pairingChoicesMaximum;
	}
	public void setPairingChoicesMaximum(int pairingChoicesMaximum) {
		this.pairingChoicesMaximum = trim(pairingChoicesMaximum, 2, 4);
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
	 * How selected contacts will be shuffled
	 * @author william
	 *
	 */
	public static enum ShufflingMode {
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
	
	private static final int trim(int num, int min, int max) {
		if(num < min) return min;
		if(num > max) return max;
		return num;
	}
	public float getMaxTime() {
		return maxTime;
	}
	public void setMaxTime(float maxTime) {
		this.maxTime = maxTime;
		if(maxTime != 0 && maxTimePerContact != 0)
			Log.w(TAG, "maxTime and maxTimePerContact are both nonzero");
	}
	public void setMaxTime(int maxTime) {
		setMaxTime((float)maxTime);
	}
	/**
	 * If <code>true</code>, a timer should be shown for the game.
	 * @return
	 */
	public boolean hasTimer() {
		return maxTime != 0;
	}
}
