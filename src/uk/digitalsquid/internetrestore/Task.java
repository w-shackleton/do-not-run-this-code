package uk.digitalsquid.internetrestore;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * General task type. Should be subclassed for more detailed tasks.
 * @author william
 *
 */
public class Task implements Parcelable {
	/**
	 * If this action is set then this task is actually a subclass.
	 */
	public static final int ACTION_SUBCLASSED = 1;
	public static final int ACTION_STOP = 2;
	
	public Task(int action) {
		this.action = action;
	}
	
	Task(Parcel in) {
		action = in.readInt();
	}
	
	private int action;

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(action);
	}
	
	public static final Creator<Task> CREATOR = new Creator<Task>() {
		@Override
		public Task createFromParcel(Parcel in) {
			return new Task(in);
		}
		@Override
		public Task[] newArray(int len) {
			return new Task[len];
		}
	};
	
	// Subtypes
	
	public static class StartTask extends Task {
		public StartTask() {
			super(ACTION_SUBCLASSED);
		}
		
		StartTask(Parcel in) {
			super(in);
		}
		
		public static final Creator<StartTask> CREATOR = new Creator<StartTask>() {
			@Override
			public StartTask createFromParcel(Parcel in) {
				return new StartTask(in);
			}
			@Override
			public StartTask[] newArray(int len) {
				return new StartTask[len];
			}
		};
		// TODO: Startup info here
	}
	
	public static class ChangeNetworkTask extends Task {
		public ChangeNetworkTask(int networkID) {
			super(ACTION_SUBCLASSED);
			setNetworkID(networkID);
		}
		
		private int networkID;
		
		ChangeNetworkTask(Parcel in) {
			super(in);
			networkID = in.readInt();
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(networkID);
		}
		
		public int getNetworkID() {
			return networkID;
		}

		public void setNetworkID(int networkID) {
			this.networkID = networkID;
		}

		public static final Creator<ChangeNetworkTask> CREATOR = new Creator<ChangeNetworkTask>() {
			@Override
			public ChangeNetworkTask createFromParcel(Parcel in) {
				return new ChangeNetworkTask(in);
			}
			@Override
			public ChangeNetworkTask[] newArray(int len) {
				return new ChangeNetworkTask[len];
			}
		};
	}
}