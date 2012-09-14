package uk.digitalsquid.internetrestore;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import uk.digitalsquid.internetrestore.Task.ChangeNetworkTask;
import uk.digitalsquid.internetrestore.Task.StartTask;
import uk.digitalsquid.internetrestore.jni.WpaControl;
import uk.digitalsquid.internetrestore.manager.AndroidWifi;
import uk.digitalsquid.internetrestore.manager.Dhcpcd;
import uk.digitalsquid.internetrestore.manager.Wpa;
import uk.digitalsquid.internetrestore.util.MissingFeatureException;
import uk.digitalsquid.internetrestore.util.Util;
import uk.digitalsquid.internetrestore.util.Util.StringP;
import uk.digitalsquid.internetrestore.util.file.FileInstaller;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.SupplicantState;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.SparseArray;

/**
 * Manages process daemons when the program is running.
 * @author william
 *
 */
public class DaemonManager extends Service {
	
	public static final String INTENT_STATUSUPDATE = "uk.digitalsquid.internetrestore.DaemonManager.StatusUpdate";
	public static final String INTENT_EXTRA_STATUS = "uk.digitalsquid.internetrestore.DaemonManager.status";
	
	private App app;
	
	@Override
	public void onCreate() {
          super.onCreate();
          app = (App) getApplication();
	}
	
	private boolean started = false;
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	super.onStartCommand(intent, flags, startId);
    	if(!started) start();
    	return START_NOT_STICKY;
    }
    
    NotificationManager notificationManager;
    
    private void start() {
    	AsyncTaskHelper.execute(thread);
    	
    	IntentFilter filter = new IntentFilter(WpaControl.INTENT_WPASTATUS);
    	registerReceiver(wpaReceiver, filter);
    	
    	filter = new IntentFilter(Dhcpcd.INTENT_IPSTATUS);
    	registerReceiver(ipReceiver, filter);
    	
    	queueTask(new Task.StartTask());
    	
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    	
    	started = true;
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	started = false;
    	unregisterReceiver(wpaReceiver);
    	unregisterReceiver(ipReceiver);
    	queueTask(new Task(Task.ACTION_STOP));
    }
    
	private GlobalStatus status;
	private boolean wpaConnected;
	private String wpaSsid = "<Unknown>";
	private int wpaId = -1;
	private InetAddress ipAddr;
	private SupplicantState wpaSupplicantState = SupplicantState.UNINITIALIZED;
	private SparseArray<String> wpaNetworkIDs = new SparseArray<String>();
	private void setStatus(GlobalStatus status) {
		if(status != null) this.status = status;
		if(this.status != null) {
			this.status.setConnected(wpaConnected);
			this.status.setSsid(wpaSsid);
			this.status.setId(wpaId);
			this.status.setState(wpaSupplicantState);
			this.status.setAddr(ipAddr);
			this.status.setNetworkIDs(wpaNetworkIDs);
			broadcastStatus();
		}
	}
	public GlobalStatus getStatus() {
		return status;
	}
	
	private void setWpaStatus(boolean connected, String ssid, int id, SupplicantState state, SparseArray<String> networkIDs) {
		wpaConnected = connected;
		wpaSsid = ssid;
		wpaId = id;
		wpaSupplicantState = state;
		wpaNetworkIDs = networkIDs;
		// Update & broadcast
		setStatus(null);
	}
	private void setIpStatus(InetAddress addr) {
		this.ipAddr = addr;
		setStatus(null);
	}
	
	public void broadcastStatus() {
		Intent intent = new Intent(INTENT_STATUSUPDATE);
		intent.putExtra(INTENT_EXTRA_STATUS, getStatus());
		sendBroadcast(intent);
	}
	
	private BroadcastReceiver wpaReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			boolean connected = intent.getBooleanExtra(WpaControl.INTENT_EXTRA_CONNECTED, false);
			String ssid = intent.getStringExtra(WpaControl.INTENT_EXTRA_SSID);
			int id = intent.getIntExtra(WpaControl.INTENT_EXTRA_SSID_ID, -1);
			if(ssid == null) ssid = "";
			
			Bundle extras = intent.getBundleExtra(WpaControl.INTENT_EXTRAS);
			SparseArray<StringP> networkIDsP = extras.getSparseParcelableArray("networks");
			SparseArray<String> networkIDs = Util.stringPSparseArrayToSparseArray(networkIDsP);
			
			SupplicantState state = intent.getParcelableExtra(WpaControl.INTENT_EXTRA_SUPPLICANT_STATE);
			setWpaStatus(connected, ssid, id, state, networkIDs);
		}
	};
	
	private BroadcastReceiver ipReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			InetAddress addr = (InetAddress) intent.getSerializableExtra(Dhcpcd.INTENT_EXTRA_INET_ADDR);
			setIpStatus(addr);
		}
	};
    
	/**
	 * Gets the ID of the next dialogue message. If there are none, returns 0.
	 */
	public int getNextDialogueMessage() {
		Integer ret = dialogMessages.poll();
		if(ret == null) return 0;
		return ret.intValue();
	}

	public class DaemonManagerBinder extends Binder {
        public DaemonManager getService() {
            return DaemonManager.this;
        }
	}
	private final DaemonManagerBinder binder = new DaemonManagerBinder();
	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}
	
	protected final BlockingQueue<Task> tasks = new LinkedBlockingQueue<Task>();
	
	/**
	 * A set of messages to show the user (in dialogues)
	 */
	protected final Queue<Integer> dialogMessages = new ConcurrentLinkedQueue<Integer>();
	
	private void queueTask(Task task) {
		tasks.add(task);
	}
	
	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}
	
	/**
	 * Sends a request to the wpa_supplicant daemon to connect to the network with
	 * the given ID
	 * @param id
	 */
	public void requestConnectionTo(int id) {
		queueTask(new Task.ChangeNetworkTask(id));
	}

	private final DaemonManagerThread thread = new DaemonManagerThread();
	
	private class DaemonManagerThread extends AsyncTask<Void, GlobalStatus, Void> {
		
		boolean running = true;
		
		GlobalStatus status = new GlobalStatus();
		
		Wpa wpa;
		WpaControl wpaControl;
		AndroidWifi androidWifi;
		Dhcpcd dhcpcd;

		@Override
		protected Void doInBackground(Void... arg0) {
			Logg.i("DaemonManager started");
			while(running) {
				try {
					Task task = tasks.take();
					
					switch(task.getAction()) {
					case Task.ACTION_SUBCLASSED:
						if(task instanceof StartTask) {
							status.setStatus(GlobalStatus.STATUS_STARTING);
							publishProgress(status);
							StartTask startTask = (StartTask) task;
							// Initiate managers
							Logg.d("Init AndroidWifi");
							androidWifi = new AndroidWifi(app);
							Logg.d("Init Wpa");
							try {
								wpa = new Wpa(app);
							} catch (MissingFeatureException e) {
								Logg.e("Failed to initiate wpa", e);
								showDialogue(e.getLocalisedMessageId());
								stopSelf();
								break;
							}
							// Stop wifi
							Logg.d("Stop AndroidWifi");
							androidWifi.stopWifiSync();
							// Start wpa_supplicant
							Logg.d("Start Wpa");
							try {
								wpa.start();
							} catch (MissingFeatureException e) {
								Logg.e("Failed to start wpa", e);
								showDialogue(e.getLocalisedMessageId());
								stopSelf();
								break;
							} catch (IOException e) {
								Logg.e("Failed to start wpa", e);
								showDialogue(R.string.wpa_no_start);
								stopSelf();
								break;
							}
							
							// Connect to control interface
							MissingFeatureException wpaControlExc = null;
							for(int i = 0; i < 30; i++) {
								try {
									File ctrl = findCtrlSocket(app.getFileInstaller().getSockPath(FileInstaller.SOCK_CTRL));
									File local = app.getFileInstaller().getSockPath(FileInstaller.SOCK_LOCAL);
									int perm = 0;
									if(ctrl.canRead()) perm |= 04;
									if(ctrl.canWrite()) perm |= 02;
									if(ctrl.canExecute()) perm |= 01;
									Logg.v("Permissions of ctrl read as " + perm);
									Logg.v(String.format("Using control socket %s", ctrl.getAbsolutePath()));
									
									wpaControl = new WpaControl(app, ctrl.getAbsolutePath(), local.getAbsolutePath());
									wpaControlExc = null;
									break; // Successful
								} catch (MissingFeatureException e) {
									wpaControlExc = e;
									Logg.d(String.format("Failed to find or connect to wpa socket (%d)", i));
									Thread.sleep(300);
								}
							}
							// Throw exception if it still remains
							if(wpaControlExc != null) {
								Logg.e("Failed to connect to wpa socket (final)", wpaControlExc);
								showDialogue(wpaControlExc.getLocalisedMessageId());
								stopSelf();
								break;
							}
							
							// Connect to socket
							if(wpaControl != null) wpaControl.start();
							
							// Start dhcpcd
							try {
								Logg.d("Init dhcpcd");
								dhcpcd = new Dhcpcd(app);
							} catch (MissingFeatureException e) {
								Logg.e("Failed to initialise dhcpcd", e);
								showDialogue(e.getLocalisedMessageId());
								stopSelf();
								break;
							}
							try {
								dhcpcd.start();
							} catch (IOException e) {
								Logg.e("Failed to start dhcpcd", e);
							}
							status.setStatus(GlobalStatus.STATUS_STARTED);
							publishProgress(status);
						} else if(task instanceof Task.ChangeNetworkTask) {
							ChangeNetworkTask changeTask = (ChangeNetworkTask) task;
							if(wpaControl != null) {
								if(wpaControl.isConnectionOpen()) {
									wpaControl.selectNetwork(changeTask.getNetworkID());
								}
							}
						}
						break;
					case Task.ACTION_STOP:
						status.setStatus(GlobalStatus.STATUS_STOPPING);
						publishProgress(status);
						Logg.i("DaemonManager stopping");
						Logg.d("Stopping dhcpcd");
						if(dhcpcd != null) dhcpcd.stop();
						Logg.d("Disconnecting wpa_ctrl_iface");
						if(wpaControl != null) wpaControl.stop();
						if(wpaControl != null) wpaControl.close();
						Logg.d("Stopping wpa_supplicant");
						if(wpa != null) wpa.stop();
						Logg.d("Restarting Android wifi");
						if(androidWifi != null) androidWifi.startWifiIfNecessary();
						Logg.i("DaemonManager stopped");
						status.setStatus(GlobalStatus.STATUS_STOPPED);
						publishProgress(status);
						running = false;
						break;
					}
				} catch (InterruptedException e) {
					Logg.w("DaemonManager interrupted.", e);
				}
			}
			DaemonManager.this.stopSelf();
			return null;
		}
		
		/**
		 * Stops this service
		 */
		private void stopSelf() {
			queueTask(new Task(Task.ACTION_STOP));
		}
		
		protected void showDialogue(int stringId) {
			dialogMessages.add(stringId);
			publishProgress(status); // Notify UI of new dialog
		}
		
		/**
		 * Given a folder, scans through the control sockets inside it and chooses the best one.
		 * @param parentFolder
		 * @throws MissingFeatureException 
		 */
		protected File findCtrlSocket(File parentFolder) throws MissingFeatureException {
			File[] children = parentFolder.listFiles();
			if(children == null) throw new MissingFeatureException(
					"Couldn't find wpa_supplicant socket folder (1)", R.string.wpa_no_ctrl);
			switch(children.length) {
			case 0:
				throw new MissingFeatureException(
					"Couldn't find wpa_supplicant socket folder (2)", R.string.wpa_no_ctrl);
			case 1:
				return children[0];
			default: // Try to find the one with this iface name
				try {
					String wifi = app.getInfoCollector().getWifiIface();
					for(File child : children) {
						if(child.getName().equals(wifi))
							return child;
					}
					return children[0];
				} catch (UnknownHostException e) {
					Logg.i("Couldn't get Wifi interface name for ctrl choice, will choose first iface", e);
					return children[0];
				}
			}
		}
		
		@Override
		protected void onProgressUpdate(GlobalStatus... status) {
			super.onProgressUpdate(status);
			if(status.length != 1) throw new IllegalArgumentException("Only 1 argument to publishProgress needed");
			setStatus(status[0]);
		}
	}
}
