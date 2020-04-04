package uk.digitalsquid.remme;

import uk.digitalsquid.remme.ingame.views.CurtainImageView;
import uk.digitalsquid.remme.mgr.ContactManager;
import uk.digitalsquid.remme.misc.Config;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;

public class StartActivity extends Activity implements OnClickListener, Config {
	
	private LocalBroadcastManager localBroadcastManager;
	private App app;
	
	CurtainImageView curtains;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        app = (App) getApplication();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        
        findViewById(R.id.start).setOnClickListener(this);
        findViewById(R.id.start2).setOnClickListener(this);
        findViewById(R.id.leaderboard).setOnClickListener(this);
        curtains = (CurtainImageView) findViewById(R.id.loadingImage);
        curtains.setClickable(false);
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.start:
			startActivity(new Intent(this, ModeSelect.class));
			break;
		case R.id.start2:
			startActivity(new Intent(this, DifficultyListActivity.class));
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
				curtains.setProgress(status);
			}
		}
	};
	
	@Override
	public void onStart() {
		super.onStart();
		IntentFilter filter = new IntentFilter(ContactManager.BROADCAST_LOADSTATUS);
		localBroadcastManager.registerReceiver(loadReceiver, filter);
		// This does nothing if called multiple times so should be OK
		if(!app.getContacts().beginBackgroundLoad())
			curtains.setVisibility(View.GONE);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		localBroadcastManager.unregisterReceiver(loadReceiver);
	}
}