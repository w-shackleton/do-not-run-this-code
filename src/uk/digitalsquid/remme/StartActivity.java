package uk.digitalsquid.remme;

import java.util.ArrayList;

import uk.digitalsquid.remme.mgr.ContactManager;
import uk.digitalsquid.remme.mgr.GroupManager.AccountDetails;
import uk.digitalsquid.remme.mgr.GroupManager.Group;
import uk.digitalsquid.remme.misc.Config;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;

public class StartActivity extends Activity implements OnClickListener, Config {
	
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
        findViewById(R.id.start2).setOnClickListener(this);
        findViewById(R.id.leaderboard).setOnClickListener(this);
        loadStatus = (ProgressBar) findViewById(R.id.loadStatus);
        loadStatus.setMax(1000);
        
        ArrayList<AccountDetails> accounts = app.getGroups().getAccountDetails();
        for(AccountDetails details : accounts) {
        	Log.d(TAG, details.toString(this));
        	for(Group group : details.getGroups()) {
        		Log.d(TAG, "    --  " + group.toString());
        	}
        }
        
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        GroupSelectDialog dialog = new GroupSelectDialog();
        dialog.show(ft, "groupSelectDialog");
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