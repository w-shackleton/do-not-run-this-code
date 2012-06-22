package uk.digitalsquid.internetrestore;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Manages process daemons when the program is running.
 * @author william
 *
 */
public class DaemonManager extends Service {
	
	public static final String INTENT_STATUSUPDATE = "uk.digitalsquid.internetrestore.DaemonManager.StatusUpdate";
	public static final String INTENT_EXTRA_STATUS = "uk.digitalsquid.internetrestore.DaemonManager.status";
	
	@Override
	public void onCreate() {
          super.onCreate();
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
    	Toast.makeText(getApplicationContext(), "Loaded setup", Toast.LENGTH_LONG).show();
    	
    	AsyncTaskHelper.execute(thread);
    	
    	queueTask(new Task.StartTask());
    	
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    	
    	started = true;
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
	
	private void queueTask(Task task) {
		tasks.add(task);
	}
	
	private final DaemonManagerThread thread = new DaemonManagerThread();
	
	private class DaemonManagerThread extends AsyncTask<Void, GlobalStatus, Void> {
		
		boolean running = true;

		@Override
		protected Void doInBackground(Void... arg0) {
			while(running) {
				try {
					Task task = tasks.take();
					
					switch(task.getAction()) {
					case Task.ACTION_STOP:
						// TODO: Stop!
						running = false;
						break;
					}
				} catch (InterruptedException e) {
					Logg.w("DaemonManager interrupted.", e);
				}
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(GlobalStatus... status) {
			super.onProgressUpdate(status);
			if(status.length != 1) throw new IllegalArgumentException("Only 1 argument to publishProgress needed");
			setStatus(status[0]);
		}
	}
}
