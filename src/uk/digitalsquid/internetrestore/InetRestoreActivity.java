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
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class InetRestoreActivity extends Activity implements OnClickListener {
	
	/**
	 * The start of the incrementing {@link Dialog} pool
	 */
	static final int DIALOG_POOL = 1;
	/**
	 * The next {@link Dialog} ID to use
	 */
	int dialogPoolId = DIALOG_POOL;
	
	App app;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        app = (App) getApplication();
        
        findViewById(R.id.start).setOnClickListener(this);
        findViewById(R.id.stop).setOnClickListener(this);
        
        serviceIntent = new Intent(this, DaemonManager.class);
        
        // Run File installer - this needs to be moved to a BG thread on load
        try {
			app.getFileInstaller().installFiles();
		} catch (NotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
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
				
				if(service != null) {
					// Check message queues
					int msgId = service.getNextDialogueMessage();
					Bundle bundle = new Bundle();
					bundle.putInt("id", msgId);
					showDialog(dialogPoolId++, bundle);
				}
			}
		}
	};
	
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
		}
	};
	
	public Dialog onCreateDialog(int id, Bundle args) {
		super.onCreateDialog(id, args);
		Builder builder = new Builder(this);
		switch(id) {
		default:
			int msgId = args.getInt("id");
			builder.setMessage(msgId);
			builder.setPositiveButton(android.R.string.ok, null);
			return builder.create();
		}
	}
}