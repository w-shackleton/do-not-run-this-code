package uk.digitalsquid.internetrestore;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import uk.digitalsquid.internetrestore.Task.StartTask;
import uk.digitalsquid.internetrestore.jni.WpaControl;
import uk.digitalsquid.internetrestore.manager.AndroidWifi;
import uk.digitalsquid.internetrestore.manager.Wpa;
import uk.digitalsquid.internetrestore.util.MissingFeatureException;
import uk.digitalsquid.internetrestore.util.file.FileInstaller;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

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
    	
    	queueTask(new Task.StartTask());
    	
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    	
    	started = true;
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	queueTask(new Task(Task.ACTION_STOP));
    }
    
	private GlobalStatus status;
	private void setStatus(GlobalStatus status) {
		this.status = status;
		broadcastStatus();
	}
	public GlobalStatus getStatus() {
		return status;
	}
	
	public void broadcastStatus() {
		Intent intent = new Intent(INTENT_STATUSUPDATE);
		intent.putExtra(INTENT_EXTRA_STATUS, getStatus());
		sendBroadcast(intent);
	}
    
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
	
	private final DaemonManagerThread thread = new DaemonManagerThread();
	
	private class DaemonManagerThread extends AsyncTask<Void, GlobalStatus, Void> {
		
		boolean running = true;
		
		GlobalStatus status = new GlobalStatus();
		
		Wpa wpa;
		WpaControl wpaControl;
		AndroidWifi androidWifi;

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
							} catch (IOException e) {
								Logg.e("Failed to start wpa", e);
								showDialogue(R.string.wpa_no_start);
								stopSelf();
							}
							
							// Connect to control interface
							MissingFeatureException wpaControlExc = null;
							for(int i = 0; i < 30; i++) {
								try {
									File ctrl = findCtrlSocket(app.getFileInstaller().getSockPath(FileInstaller.SOCK_CTRL));
									File local = app.getFileInstaller().getSockPath(FileInstaller.SOCK_LOCAL);
									int perm = 0;
									if(ctrl.canRead()) perm += 4;
									if(ctrl.canWrite()) perm += 2;
									if(ctrl.canExecute()) perm += 1;
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
							}
							
							// Connect to socket
							if(wpaControl != null) wpaControl.start();
							status.setStatus(GlobalStatus.STATUS_STARTED);
						}
						break;
					case Task.ACTION_STOP:
						status.setStatus(GlobalStatus.STATUS_STOPPING);
						Logg.i("DaemonManager stopping");
						Logg.d("Disconnecting wpa_ctrl_iface");
						if(wpaControl != null) wpaControl.stop();
						if(wpaControl != null) wpaControl.close();
						Logg.d("Stopping wpa_supplicant");
						if(wpa != null) wpa.stop();
						Logg.d("Restarting Android wifi");
						if(androidWifi != null) androidWifi.startWifiIfNecessary();
						Logg.i("DaemonManager stopped");
						status.setStatus(GlobalStatus.STATUS_STOPPED);
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
