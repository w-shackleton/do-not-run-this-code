package uk.digitalsquid.internetrestore;

import java.io.IOException;

import uk.digitalsquid.internetrestore.DaemonManager.DaemonManagerBinder;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class InetRestoreActivity extends Activity implements OnClickListener, OnItemSelectedListener {
	
	static final int DIALOG_INSTALL_COPYING_FAILED = 1;
	static final int DIALOG_INSTALL_FAILED_OTHER = 2;
	
	private Button start, stop;
	private Spinner currentNetworks;
	private SparseArray<String> currentNetworkList;
	
	/**
	 * The start of the incrementing {@link Dialog} pool
	 */
	static final int DIALOG_POOL = 3;
	/**
	 * The next {@link Dialog} ID to use
	 */
	int dialogPoolId = DIALOG_POOL;
	
	App app;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main);
        app = (App) getApplication();
        
        networkAdapter = new NetworkAdapter(this);
        
        start = (Button) findViewById(R.id.start);
        start.setOnClickListener(this);
        start.setEnabled(false);
        stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(this);
        stop.setEnabled(false);
        
        currentNetworks = (Spinner) findViewById(R.id.currentNetwork);
        currentNetworks.setOnItemSelectedListener(this);
        currentNetworks.setAdapter(networkAdapter);
        
        serviceIntent = new Intent(this, DaemonManager.class);
        
        AsyncTaskHelper.execute(loadTask);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	
    	getMenuInflater().inflate(R.menu.menu, menu);
    	
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent i;
    	switch(item.getItemId()) {
    	case R.id.editWifiNetworks:
    		i = new Intent(this, EditWifiNetworks.class);
    		startActivity(i);
    		return true;
		default:
			return false;
    	}
    }
    
    Intent serviceIntent;

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.start:
			startService(serviceIntent);
			if(service == null)
				bindService(serviceIntent, connection, 0);
			break;
		case R.id.stop:
			stopService(serviceIntent);
			break;
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		IntentFilter filter = new IntentFilter();
		filter.addAction(DaemonManager.INTENT_STATUSUPDATE);
		registerReceiver(serviceStatus, filter);
		bindService(serviceIntent, connection, 0);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		unregisterReceiver(serviceStatus);
		unbindService(connection);
	}
	
	BroadcastReceiver serviceStatus = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(DaemonManager.INTENT_STATUSUPDATE)) {
				GlobalStatus status = intent.getParcelableExtra(DaemonManager.INTENT_EXTRA_STATUS);
				
				currentNetworkList = status.getNetworkIDs();
				networkAdapter.setNetworks(status.getNetworkIDs());
				int selectedIndex = currentNetworkList.indexOfKey(status.getId());
				currentNetworks.setSelection(selectedIndex < 0 ? 0 : selectedIndex);
				
				if(service != null) {
					// Check message queues
					int msgId = service.getNextDialogueMessage();
					if(msgId != 0) {
						Bundle bundle = new Bundle();
						bundle.putInt("id", msgId);
						showDialog(dialogPoolId++, bundle);
					}
				}
				
				String statusText;
				switch(status.getStatus()) {
				case GlobalStatus.STATUS_STARTING:
					statusText = "Starting";
					start.setVisibility(View.GONE);
					stop.setVisibility(View.VISIBLE);
					break;
				case GlobalStatus.STATUS_STARTED:
					statusText = "Started";
					start.setVisibility(View.GONE);
					stop.setVisibility(View.VISIBLE);
					break;
				case GlobalStatus.STATUS_STOPPING:
					statusText = "Stopping";
					start.setVisibility(View.VISIBLE);
					stop.setVisibility(View.GONE);
					break;
				default:
				case GlobalStatus.STATUS_STOPPED:
					statusText = "Stopped";
					start.setVisibility(View.VISIBLE);
					stop.setVisibility(View.GONE);
					resetStatusText();
					break;
				}
				((TextView)findViewById(R.id.status)).setText(statusText);
				((TextView)findViewById(R.id.connected)).setText(status.isConnected() ? "Connected" : "Not connected");
				((TextView)findViewById(R.id.ssid)).setText(status.getSsid() == null ? "<Unknown>" : status.getSsid());
				((TextView)findViewById(R.id.state)).setText(status.getState().name());
				((TextView)findViewById(R.id.ip)).setText(status.getAddrString());
			}
		}
	};
	
	/**
	 * Resets the various onscreen statuses
	 */
	private void resetStatusText() {
		networkAdapter.setNetworks(null);
	}
	
	DaemonManager service;
	
	ServiceConnection connection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			service = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			DaemonManagerBinder binder = (DaemonManagerBinder) service;
			InetRestoreActivity.this.service = binder.getService();
			if(InetRestoreActivity.this.service.isStarted()) {
				start.setVisibility(View.GONE);
				stop.setVisibility(View.VISIBLE);
			} else {
				start.setVisibility(View.VISIBLE);
				stop.setVisibility(View.GONE);
			}
		}
	};
	
	public Dialog onCreateDialog(int id, Bundle args) {
		super.onCreateDialog(id, args);
		Builder builder = new Builder(this);
		switch(id) {
		case DIALOG_INSTALL_COPYING_FAILED:
			builder.setTitle(R.string.setup_failed);
			builder.setMessage(R.string.setup_failed_nospace);
			return builder.create();
		case DIALOG_INSTALL_FAILED_OTHER:
			builder.setTitle(R.string.setup_failed);
			builder.setMessage(R.string.setup_failed_other);
			return builder.create();
		default:
			int msgId = args.getInt("id");
			builder.setMessage(msgId);
			builder.setPositiveButton(android.R.string.ok, null);
			return builder.create();
		}
	}
	
	static enum LoadError {
		NO_ERROR,
		COPYING_FAILED,
		OTHER_ERROR,
	}
	
	AsyncTask<Void, Void, LoadError> loadTask = new AsyncTask<Void, Void, LoadError>() {
		
		@Override
		protected void onPreExecute() {
			setProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected LoadError doInBackground(Void... params) {
	        // Run File installer - this needs to be moved to a BG thread on load
	        try {
				app.getFileInstaller().installFiles();
			} catch (NotFoundException e1) {
				Logg.e("Files not found in APK", e1);
				return LoadError.OTHER_ERROR;
			} catch (IOException e1) {
				Logg.e("Failed to copy installation files", e1);
				return LoadError.COPYING_FAILED;
			}
	        return LoadError.NO_ERROR;
		}
		
		@Override
		protected void onPostExecute(LoadError result) {
			setProgressBarIndeterminateVisibility(false);
			switch(result) {
			case COPYING_FAILED:
				showDialog(DIALOG_INSTALL_COPYING_FAILED);
				break;
			case OTHER_ERROR:
				showDialog(DIALOG_INSTALL_FAILED_OTHER);
				break;
			case NO_ERROR:
				break;
			}
	        start.setEnabled(true);
	        stop.setEnabled(true);
		}
	};

	@Override
	public void onItemSelected(AdapterView<?> adapter, View view, int position,
			long id) {
		if(service != null) {
			service.requestConnectionTo((int)id);
		} else {
			Toast.makeText(this, "Couldn't change network", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> adapter) { }
	
	private NetworkAdapter networkAdapter;
	
	private static class NetworkAdapter extends BaseAdapter {
		
		LayoutInflater inflater;
		
		public NetworkAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return networkIDs.size();
		}

		@Override
		public String getItem(int position) {
			return networkIDs.valueAt(position);
		}

		@Override
		public long getItemId(int position) {
			return networkIDs.keyAt(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
	        if (convertView == null) {
	            convertView = inflater.inflate(R.layout.spinner_item, null);
	        }
	        TextView text = (TextView) convertView.findViewById(R.id.text);
	        
	        text.setText(getItem(position));
	        
	        return convertView;
		}
		
		private SparseArray<String> networkIDs = new SparseArray<String>();
		
		public void setNetworks(SparseArray<String> networkIDs) {
			this.networkIDs = networkIDs;
			if(this.networkIDs == null) this.networkIDs = new SparseArray<String>();
			notifyDataSetChanged();
		}
	};
}