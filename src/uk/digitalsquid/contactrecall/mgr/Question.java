package uk.digitalsquid.contactrecall.mgr;

import java.util.Arrays;
import java.util.List;

import uk.digitalsquid.contactrecall.mgr.details.Contact;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Encapsulates a single question. The data contained here should be all
 * that is needed to display a question on-screen.
 * @author william
 *
 */
public final class Question implements Parcelable {

	/**
	 * The contact(s) to use as questions. For some question styles, this is
	 * only one contact.
	 */
	private Contact[] contacts;
	
	private Contact[] otherAnswers;
	
	private int correctPosition;
	
	private int[] correctPairings;
	
	// Types of question that can be asked.
	public static final int FIELD_PHOTO = 1;
	public static final int FIELD_FIRST_NAME = 2;
	public static final int FIELD_LAST_NAME = 3;
	public static final int FIELD_DISPLAY_NAME = 4;
	public static final int FIELD_COMPANY = 5;
	public static final int FIELD_DEPARTMENT = 6;
	public static final int FIELD_COMPANY_TITLE = 7;

	public static final int FIELD_PHONE_HOME = 8;
	public static final int FIELD_PHONE_WORK = 9;
	public static final int FIELD_PHONE_MOBILE = 10;
	public static final int FIELD_PHONE_OTHER = 11;

	public static final int FIELD_EMAIL_HOME = 12;
	public static final int FIELD_EMAIL_WORK = 13;
	public static final int FIELD_EMAIL_MOBILE = 14;
	public static final int FIELD_EMAIL_OTHER = 15;
	
	// Formats of different types of question, eg. should a picture be shown?
	public static final int FORMAT_IMAGE = 1;
	public static final int FORMAT_TEXT = 2;
	
	// Styles of question
	public static final int STYLE_MULTI_CHOICE = 1;
	public static final int STYLE_TRUE_FALSE = 2;
	public static final int STYLE_PAIRING = 3;
	
	private QuestionAnswerPair questionAnswerPair = new QuestionAnswerPair();
	
	public Question(Contact contact) {
		this(contact, null);
	}

	public Question(Contact[] contacts) {
		setContacts(contacts);
	}
	
	public Question(Contact contact, Contact[] otherAnswers) {
		setContacts(new Contact[] { contact });
		this.otherAnswers = otherAnswers;
	}
	
	public Question(Parcel src) {
		Parcelable[] contacts = src.readParcelableArray(Contact.class.getClassLoader());
		contacts = Arrays.copyOf(contacts, contacts.length);
		otherAnswers = (Contact[]) src.readParcelableArray(Contact.class.getClassLoader());
		correctPosition = src.readInt();
		questionAnswerPair = src.readParcelable(QuestionAnswerPair.class.getClassLoader());
	}

	/**
	 * Gets the question contact, if there is only one.
	 * @return
	 */
	public Contact getContact() {
		return getContacts()[0];
	}

	/**
	 * Sets the question contact, if there is only one.
	 * @param contact
	 */
	public void setContact(Contact contact) {
		getContacts()[0] = contact;
	}

	public Contact[] getContacts() {
		return contacts;
	}

	public void setContacts(Contact[] contacts) {
		this.contacts = contacts;
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
		dest.writeParcelableArray(getContacts(), 0);
		dest.writeParcelableArray(otherAnswers, 0);
		dest.writeInt(correctPosition);
		dest.writeParcelable(getQuestionAnswerPair(), 0);
	}
	
	/**
	 * Gets the correct position through the other answers for the correct answer.
	 * In the case of a true/false question, a value of 1 here indicates <code>false</code>,
	 * 0 indicates <code>true</code>. This matches with "True" being the first button.
	 */
	public int getCorrectPosition() {
		return correctPosition;
	}

	/**
	 * Sets the correct position through the other answers for the correct answer.
	 * In the case of a true/false question, a value of 1 here indicates <code>false</code>,
	 * 0 indicates <code>true</code>.
	 * @param correctPosition
	 */
	public void setCorrectPosition(int correctPosition) {
		this.correctPosition = correctPosition;
	}

	public int getQuestionType() {
		return getQuestionAnswerPair().getQuestionType();
	}
	
	public int getQuestionFormat() {
		switch(getQuestionType()) {
		case FIELD_PHOTO:
			return FORMAT_IMAGE;
		default:
			return FORMAT_TEXT;
		}
	}

	public void setQuestionType(int questionType) {
		getQuestionAnswerPair().setQuestionType(questionType);
	}

	public int getAnswerType() {
		return getQuestionAnswerPair().getAnswerType();
	}
	
	public int getAnswerFormat() {
		switch(getAnswerType()) {
		case FIELD_PHOTO:
			return FORMAT_IMAGE;
		default:
			return FORMAT_TEXT;
		}
	}

	public void setAnswerType(int answerType) {
		getQuestionAnswerPair().setAnswerType(answerType);
	}
	
	public int getQuestionStyle() {
		return questionAnswerPair.getQuestionStyle();
	}
	
	public void setQuestionStyle(int style) {
		questionAnswerPair.setQuestionStyle(style);
	}
	
	private QuestionAnswerPair getQuestionAnswerPair() {
		return questionAnswerPair;
	}

	public void setQuestionAnswerPair(QuestionAnswerPair questionAnswerPair) {
		if(questionAnswerPair == null) return;
		this.questionAnswerPair.answerType = questionAnswerPair.answerType;
		this.questionAnswerPair.questionType = questionAnswerPair.questionType;
		this.questionAnswerPair.questionStyle = questionAnswerPair.questionStyle;
	}

	public int[] getCorrectPairings() {
		return correctPairings;
	}

	public void setCorrectPairings(int[] correctPairings) {
		this.correctPairings = correctPairings;
	}
	public void setCorrectPairings(List<Integer> correctPairings) {
		this.correctPairings = new int[correctPairings.size()];
		int i = 0;
		for(Integer p : correctPairings)
			this.correctPairings[i++] = p;
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
	
	/**
	 * Defines a possible pair of question and answer types, and the style of question.
	 * @author william
	 *
	 */
	public static class QuestionAnswerPair implements Parcelable {
		/**
		 * The style of question, ie. multi-choice, pairing, true/false.
		 */
		private int questionStyle;
		/**
		 * The field to use for the question
		 */
		private int questionType;
		/**
		 * The field to use for the answer
		 */
		private int answerType;
		
		public QuestionAnswerPair() { }
		public QuestionAnswerPair(int questionType, int answerType) {
			this.questionStyle = Question.STYLE_MULTI_CHOICE;
			this.questionType = questionType;
			this.answerType = answerType;
		}
		public QuestionAnswerPair(int style, int questionType, int answerType) {
			this.questionStyle = style;
			this.questionType = questionType;
			this.answerType = answerType;
		}
		public QuestionAnswerPair(Parcel in) {
			setQuestionType(in.readInt());
			setAnswerType(in.readInt());
			setQuestionStyle(in.readInt());
		}
	
		public static final Parcelable.Creator<QuestionAnswerPair> CREATOR = new Parcelable.Creator<QuestionAnswerPair>() {
			public QuestionAnswerPair createFromParcel(Parcel in) {
				return new QuestionAnswerPair(in);
			}
			public QuestionAnswerPair[] newArray(int size) {
				return new QuestionAnswerPair[size];
			}
		};
	
		@Override
		public int describeContents() {
			return 0;
		}
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeInt(getQuestionType());
			dest.writeInt(getAnswerType());
			dest.writeInt(getQuestionStyle());
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
		public int getQuestionStyle() {
			return questionStyle;
		}
		public void setQuestionStyle(int questionStyle) {
			this.questionStyle = questionStyle;
		}
	}
}
