package uk.digitalsquid.internetrestore;

import java.net.InetAddress;

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
	
	/**
	 * This task instructs the worker thread to change the subnet which is being
	 * NATted.
	 * @author william
	 *
	 */
	public static class ChangeNatTask extends Task {
		public ChangeNatTask(InetAddress addr, short subnet) {
			super(ACTION_SUBCLASSED);
			this.addr = addr;
			this.subnet = subnet;
		}
		
		private short subnet;
		private InetAddress addr;
		
		ChangeNatTask(Parcel in) {
			super(in);
			subnet = (short) in.readInt();
			addr = (InetAddress) in.readSerializable();
		}
		
		public short getSubnet() {
			return subnet;
		}
		
		public void setSubnet(short subnet) {
			this.subnet = subnet;
		}
		public void setSubnet(int subnet) {
			this.subnet = (short)subnet;
		}
		
		public InetAddress getAddress() {
			return addr;
		}
		
		public void setSubnet(InetAddress address) {
			addr = address;
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(subnet);
			dest.writeSerializable(addr);
		}

		public static final Creator<ChangeNatTask> CREATOR = new Creator<ChangeNatTask>() {
			@Override
			public ChangeNatTask createFromParcel(Parcel in) {
				return new ChangeNatTask(in);
			}
			@Override
			public ChangeNatTask[] newArray(int len) {
				return new ChangeNatTask[len];
			}
		};
	}
}