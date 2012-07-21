package uk.digitalsquid.internetrestore;

import android.net.wifi.SupplicantState;
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
	
	private boolean connected;
	private String ssid;
	private SupplicantState state;
	
	public GlobalStatus() {
		setStatus(0);
		setConnected(false);
		setSsid("<Unknown>");
		setState(SupplicantState.UNINITIALIZED);
	}
	
	public GlobalStatus(int status) {
		this.setStatus(status);
		setConnected(false);
		setSsid("<Unknown>");
		setState(SupplicantState.UNINITIALIZED);
	}
	
	GlobalStatus(Parcel in) {
		setStatus(in.readInt());
		setConnected(in.readByte() == 1);
		setSsid(in.readString());
		setState((SupplicantState) in.readParcelable(null));
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel p, int flags) {
		p.writeInt(getStatus());
		p.writeByte((byte) (isConnected() ? 1 : 0));
		p.writeString(getSsid());
		p.writeParcelable(getState(), 0);
	}
	
	int getStatus() {
		return status;
	}

	void setStatus(int status) {
		this.status = status;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public SupplicantState getState() {
		return state;
	}

	public void setState(SupplicantState state) {
		if(state == null) state = SupplicantState.UNINITIALIZED;
		this.state = state;
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