package uk.digitalsquid.internetrestore;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import uk.digitalsquid.internetrestore.Task.StartTask;
import uk.digitalsquid.internetrestore.manager.AndroidWifi;
import uk.digitalsquid.internetrestore.manager.Wpa;
import uk.digitalsquid.internetrestore.util.MissingFeatureException;
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
							status.setStatus(GlobalStatus.STATUS_STARTED);
						}
						break;
					case Task.ACTION_STOP:
						status.setStatus(GlobalStatus.STATUS_STOPPING);
						Logg.i("DaemonManager stopping");
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
		
		@Override
		protected void onProgressUpdate(GlobalStatus... status) {
			super.onProgressUpdate(status);
			if(status.length != 1) throw new IllegalArgumentException("Only 1 argument to publishProgress needed");
			setStatus(status[0]);
		}
	}
}
