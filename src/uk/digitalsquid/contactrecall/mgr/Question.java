package uk.digitalsquid.contactrecall.mgr;

import uk.digitalsquid.contactrecall.GameDescriptor.NamePart;
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
	
	/**
	 * The part of the name to show on-screen.
	 */
	private NamePart namePart;
	
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
		namePart = NamePart.valueOf(src.readString());
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
		dest.writeString(namePart.name());
	}
	
	public int getCorrectPosition() {
		return correctPosition;
	}

	public void setCorrectPosition(int correctPosition) {
		this.correctPosition = correctPosition;
	}

	public NamePart getNamePart() {
		return namePart;
	}

	public void setNamePart(NamePart namePart) {
		this.namePart = namePart;
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
