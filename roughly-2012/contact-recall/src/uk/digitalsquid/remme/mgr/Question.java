package uk.digitalsquid.remme.mgr;

import java.util.Arrays;
import java.util.List;

import uk.digitalsquid.remme.GameDescriptor;
import uk.digitalsquid.remme.R;
import uk.digitalsquid.remme.R.string;
import uk.digitalsquid.remme.mgr.details.Contact;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract.CommonDataKinds;

/**
 * Encapsulates a single question. The data contained here should be all
 * that is needed to display a question on-screen, combined with a
 * {@link GameDescriptor}.
 * @author william
 *
 */
public final class Question implements Parcelable {

	/**
	 * The contact(s) to use as questions. For some question styles, this is
	 * only one contact.
	 */
	private Contact[] contacts;
	
	/**
	 * The contacts to use as false answers.
	 */
	private Contact[] otherAnswers;
	
	/**
	 * For true-false and multi-choice, the position that the correct contact
	 * should be shown at.
	 */
	private int correctPosition;
	
	/**
	 * For pairing questions, the correct combinations.
	 */
	private int[] correctPairings;
	
	/**
	 * The question style, and question and answer field types.
	 */
	private QuestionAnswerPair questionAnswerPair = new QuestionAnswerPair();
	
	/**
	 * The maximum number of points the user could gain for answering this question
	 */
	private int maxPoints;
	
	// Types of field that can be represented on-screen
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

	public static final int FIELD_ADDRESS_HOME = 16;
	public static final int FIELD_OTHERS_START = FIELD_ADDRESS_HOME;
	public static final int FIELD_ADDRESS_WORK = 17;
	public static final int FIELD_ADDRESS_OTHER = 18;

	public static final int FIELD_WEBSITE = 19;

	public static final int FIELD_ASSISTANT = 20;
	public static final int FIELD_BROTHER = 21;
	public static final int FIELD_CHILD = 22;
	public static final int FIELD_DOMESTIC_PARTNER = 23;
	public static final int FIELD_FATHER = 24;
	public static final int FIELD_FRIEND = 25;
	public static final int FIELD_MANAGER = 26;
	public static final int FIELD_MOTHER = 27;
	public static final int FIELD_PARENT = 28;
	public static final int FIELD_PARTNER = 29;
	public static final int FIELD_REFERRED_BY = 30;
	public static final int FIELD_RELATIVE = 31;
	public static final int FIELD_SISTER = 32;
	public static final int FIELD_SPOUSE = 33;
	
	// Formats of different types of question, ie. should a picture be shown?
	public static final int FORMAT_IMAGE = 1;
	public static final int FORMAT_TEXT = 2;
	
	// Styles of question
	public static final int STYLE_MULTI_CHOICE = 1;
	public static final int STYLE_TRUE_FALSE = 2;
	public static final int STYLE_PAIRING = 3;
	
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
		this.contacts = (Contact[]) Arrays.copyOf(contacts, contacts.length);
		otherAnswers = (Contact[]) src.readParcelableArray(Contact.class.getClassLoader());
		correctPosition = src.readInt();
		correctPairings = src.createIntArray();
		questionAnswerPair = src.readParcelable(QuestionAnswerPair.class.getClassLoader());
		maxPoints = src.readInt();
	}

	/**
	 * Gets the question contact, if there is only one.
	 * @return
	 */
	public Contact getContact() {
		return getContacts()[0];
	}
	
	/**
	 * Returns the idxth contact
	 * @param idx
	 * @return
	 */
	public Contact getContact(int idx) {
		return getContacts()[idx];
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
	
	public int getContactCount() {
		return contacts.length;
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
		// In the case of a Pairing question, number of questions.
		if(getQuestionStyle() == STYLE_PAIRING)
			return contacts.length;
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
		dest.writeIntArray(correctPairings);
		dest.writeParcelable(getQuestionAnswerPair(), 0);
		dest.writeInt(maxPoints);
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
	
	public static int getFieldFormat(int field) {
		switch(field) {
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

	public int getMaxPoints() {
		return maxPoints;
	}

	public void setMaxPoints(int maxPoints) {
		this.maxPoints = maxPoints;
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
		 * The style of question. Can be:
		 * <ul>
		 * <li>Multi-choice - <code>Question.STYLE_MULTI_CHOICE</code></li>
		 * <li>True-false - <code>Question.STYLE_TRUE_FALSE</code></li>
		 * <li>Pairing - <code>Question.STYLE_PAIRING</code></li>
		 * </ul>
		 */
		private int questionStyle;
		/**
		 * The field to use for the question - see <code>Question.FIELD_...</code>
		 */
		private int questionType;
		/**
		 * The field to use for the answer - see <code>Question.FIELD_...</code>
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
	
	/**
	 * Given a field, returns the ID of a {@link string} that describes that field
	 * @param field
	 * @return
	 */
	public static int getFieldDescriptionId(int field) {
		switch(field) {
		case FIELD_PHOTO:
			return R.string.description_photo;
		case FIELD_FIRST_NAME:
			return R.string.description_first_name;
		case FIELD_LAST_NAME:
			return R.string.description_last_name;
		case FIELD_DISPLAY_NAME:
			return R.string.description_display_name;
		case FIELD_COMPANY:
			return R.string.description_company;
		case FIELD_DEPARTMENT:
			return R.string.description_department;
		case FIELD_COMPANY_TITLE:
			return R.string.description_company_title;
		case FIELD_PHONE_HOME:
			return R.string.description_phone_home;
		case FIELD_PHONE_WORK:
			return R.string.description_phone_work;
		case FIELD_PHONE_MOBILE:
			return R.string.description_phone_mobile;
		case FIELD_PHONE_OTHER:
			return R.string.description_phone_other;
		case FIELD_EMAIL_HOME:
			return R.string.description_email_home;
		case FIELD_EMAIL_WORK:
			return R.string.description_email_work;
		case FIELD_EMAIL_MOBILE:
			return R.string.description_email_mobile;
		case FIELD_EMAIL_OTHER:
			return R.string.description_email_other;
		case FIELD_ADDRESS_HOME:
			return R.string.description_address_home;
		case FIELD_ADDRESS_WORK:
			return R.string.description_address_work;
		case FIELD_ADDRESS_OTHER:
			return R.string.description_address_other;
		case FIELD_WEBSITE:
			return R.string.description_website;
		case FIELD_ASSISTANT:
			return CommonDataKinds.Relation.getTypeLabelResource(
					CommonDataKinds.Relation.TYPE_ASSISTANT);
		case FIELD_BROTHER:
			return CommonDataKinds.Relation.getTypeLabelResource(
					CommonDataKinds.Relation.TYPE_BROTHER);
		case FIELD_CHILD:
			return CommonDataKinds.Relation.getTypeLabelResource(
					CommonDataKinds.Relation.TYPE_CHILD);
		case FIELD_DOMESTIC_PARTNER:
			return CommonDataKinds.Relation.getTypeLabelResource(
					CommonDataKinds.Relation.TYPE_DOMESTIC_PARTNER);
		case FIELD_FATHER:
			return CommonDataKinds.Relation.getTypeLabelResource(
					CommonDataKinds.Relation.TYPE_FATHER);
		case FIELD_FRIEND:
			return CommonDataKinds.Relation.getTypeLabelResource(
					CommonDataKinds.Relation.TYPE_FRIEND);
		case FIELD_MANAGER:
			return CommonDataKinds.Relation.getTypeLabelResource(
					CommonDataKinds.Relation.TYPE_MANAGER);
		case FIELD_MOTHER:
			return CommonDataKinds.Relation.getTypeLabelResource(
					CommonDataKinds.Relation.TYPE_MOTHER);
		case FIELD_PARENT:
			return CommonDataKinds.Relation.getTypeLabelResource(
					CommonDataKinds.Relation.TYPE_PARENT);
		case FIELD_PARTNER:
			return CommonDataKinds.Relation.getTypeLabelResource(
					CommonDataKinds.Relation.TYPE_PARTNER);
		case FIELD_REFERRED_BY:
			return CommonDataKinds.Relation.getTypeLabelResource(
					CommonDataKinds.Relation.TYPE_REFERRED_BY);
		case FIELD_RELATIVE:
			return CommonDataKinds.Relation.getTypeLabelResource(
					CommonDataKinds.Relation.TYPE_RELATIVE);
		case FIELD_SISTER:
			return CommonDataKinds.Relation.getTypeLabelResource(
					CommonDataKinds.Relation.TYPE_SISTER);
		case FIELD_SPOUSE:
			return CommonDataKinds.Relation.getTypeLabelResource(
					CommonDataKinds.Relation.TYPE_SPOUSE);
		default:
			return R.string.placeholder;
		}
	}
}
