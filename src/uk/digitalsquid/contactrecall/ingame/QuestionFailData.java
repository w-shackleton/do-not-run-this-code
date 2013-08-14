package uk.digitalsquid.contactrecall.ingame;

import uk.digitalsquid.contactrecall.mgr.Question;
import uk.digitalsquid.contactrecall.mgr.details.Contact;
import android.os.Parcel;
import android.os.Parcelable;

class QuestionFailData implements Parcelable {
	
	public QuestionFailData() {}

	public Question question;
	
	/**
	 * The {@link Contact} from the question that was wrong.
	 * This is only variable for pairing questions.
	 */
	public Contact contact;
	/**
	 * The incorrect choice the user made. <code>null</code> indicates
	 * a timeout
	 */
	public Contact incorrectChoice;
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(question, 0);
		dest.writeParcelable(contact, 0);
		dest.writeParcelable(incorrectChoice, 0);
	}
	
	public QuestionFailData(Parcel source) {
		question = source.readParcelable(Question.class.getClassLoader());
		contact = source.readParcelable(Contact.class.getClassLoader());
		incorrectChoice = source.readParcelable(Contact.class.getClassLoader());
	}
	
	public static final Creator<QuestionFailData> CREATOR = new Creator<QuestionFailData>() {

		@Override
		public QuestionFailData createFromParcel(Parcel source) {
			return new QuestionFailData(source);
		}

		@Override
		public QuestionFailData[] newArray(int size) {
			return new QuestionFailData[size];
		}
	};
	
	@Override
	public boolean equals(Object right) {
		if(right == null) return false;
		if(!(right instanceof QuestionFailData)) return false;
		if(contact == null) return false;
		return contact.equals(((QuestionFailData)right).contact);
	}
	
	@Override
	public int hashCode() {
		if(contact == null) return super.hashCode();
		return contact.hashCode();
	}
}