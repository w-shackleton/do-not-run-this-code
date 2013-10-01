package uk.digitalsquid.remme;

import uk.digitalsquid.remme.mgr.ContactManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;

public class StartActivity extends Activity implements OnClickListener {
	
	private LocalBroadcastManager localBroadcastManager;
	private App app;
	
	ProgressBar loadStatus;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        app = (App) getApplication();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        
        findViewById(R.id.start).setOnClickListener(this);
        findViewById(R.id.leaderboard).setOnClickListener(this);
        loadStatus = (ProgressBar) findViewById(R.id.loadStatus);
        loadStatus.setMax(1000);
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.start:
			startActivity(new Intent(this, ModeSelect.class));
			break;
		case R.id.leaderboard:
			startActivity(new Intent(this, Leaderboard.class));
			break;
		}
	}
	
	private final BroadcastReceiver loadReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(ContactManager.BROADCAST_LOADSTATUS)) {
				float status = intent.getFloatExtra(ContactManager.LOADSTATUS_STATUS, 1f);
				loadStatus.setVisibility(status == 1 ? View.INVISIBLE : View.VISIBLE);
				loadStatus.setProgress((int) (status * 1000));
			}
		}
	};
	
	@Override
	public void onStart() {
		super.onStart();
		IntentFilter filter = new IntentFilter(ContactManager.BROADCAST_LOADSTATUS);
		localBroadcastManager.registerReceiver(loadReceiver, filter);
		// This does nothing if called multiple times so should be OK
		app.getContacts().beginBackgroundLoad();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		localBroadcastManager.unregisterReceiver(loadReceiver);
	}
}