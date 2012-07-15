package uk.digitalsquid.internetrestore;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * General status type. Should be subclassed for more detailed statuses.
 * @author william
 *
 */
public class GlobalStatus implements Parcelable {
	public static final int STATUS_STARTING = 1;
	public static final int STATUS_STARTED = 2;
	public static final int STATUS_STOPPING = 3;
	public static final int STATUS_STOPPED = 4;
	
	private int status;
	
	public GlobalStatus() {
		this.setStatus(0);
	}
	
	public GlobalStatus(int status) {
		this.setStatus(status);
	}
	
	GlobalStatus(Parcel in) {
		setStatus(in.readInt());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel p, int flags) {
		p.writeInt(getStatus());
	}
	
	int getStatus() {
		return status;
	}

	void setStatus(int status) {
		this.status = status;
	}

	public static final Creator<GlobalStatus> CREATOR = new Creator<GlobalStatus>() {

		@Override
		public GlobalStatus createFromParcel(Parcel in) {
			return new GlobalStatus(in);
		}

		@Override
		public GlobalStatus[] newArray(int len) {
			return new GlobalStatus[len];
		}
		
	};
}