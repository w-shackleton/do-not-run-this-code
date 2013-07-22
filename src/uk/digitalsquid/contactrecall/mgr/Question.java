package uk.digitalsquid.contactrecall.mgr;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Encapsulates a single question. The data contained here should be all
 * that is needed to display a question on-screen.
 * @author william
 *
 */
public final class Question implements Parcelable {
	
	private Contact contact;
	
	private Contact[] otherAnswers;
	
	private int correctPosition;
	
	public static final int TYPE_PHOTO = 1;
	public static final int TYPE_FIRST_NAME = 2;
	public static final int TYPE_LAST_NAME = 3;
	public static final int TYPE_DISPLAY_NAME = 4;
	
	private int questionType;
	private int answerType;
	
	public Question(Contact contact) {
		this(contact, null);
	}
	
	public Question(Contact contact, Contact[] otherAnswers) {
		this.contact = contact;
		this.otherAnswers = otherAnswers;
	}
	
	public Question(Parcel src) {
		contact = src.readParcelable(null);
		otherAnswers = (Contact[]) src.readParcelableArray(null);
		correctPosition = src.readInt();
		questionType = src.readInt();
		answerType = src.readInt();
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public Contact[] getOtherAnswers() {
		return otherAnswers;
	}

	public void setOtherAnswers(Contact[] otherAnswers) {
		this.otherAnswers = otherAnswers;
	}

	public int getNumberOfChoices() {
		// All the other choices, plus the correct one.
		return otherAnswers.length + 1;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(contact, 0);
		dest.writeParcelableArray(otherAnswers, 0);
		dest.writeInt(correctPosition);
		dest.writeInt(questionType);
		dest.writeInt(answerType);
	}
	
	public int getCorrectPosition() {
		return correctPosition;
	}

	public void setCorrectPosition(int correctPosition) {
		this.correctPosition = correctPosition;
	}

	public int getQuestionType() {
		return questionType;
	}

	public void setQuestionType(int questionType) {
		this.questionType = questionType;
	}

	public int getAnswerType() {
		return answerType;
	}

	public void setAnswerType(int answerType) {
		this.answerType = answerType;
	}

	public static final Creator<Question> CREATOR = new Creator<Question>() {

		@Override
		public Question createFromParcel(Parcel source) {
			return new Question(source);
		}

		@Override
		public Question[] newArray(int size) {
			return new Question[size];
		}
	};
}
