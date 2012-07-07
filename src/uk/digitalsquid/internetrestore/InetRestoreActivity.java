package uk.digitalsquid.internetrestore;

import java.io.IOException;
import java.net.UnknownHostException;

import uk.digitalsquid.internetrestore.settings.InfoCollector;
import uk.digitalsquid.internetrestore.settings.WpaSettings;
import uk.digitalsquid.internetrestore.settings.wpa.WpaCollection;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class InetRestoreActivity extends Activity {
	
	App app;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        app = (App) getApplication();
        
        // Run File installer - this needs to be moved to a BG thread on load
        try {
			app.getFileInstaller().installFiles();
		} catch (NotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        
        // Testing
        InfoCollector info = new InfoCollector(this);
        try {
			Logg.d("Wifi: " + info.getWifiIface());
		} catch (UnknownHostException e) {
			Logg.e("Failed to get wifi iface", e);
		}
        
        WpaSettings wpa = new WpaSettings((App)getApplication());
        try {
			WpaCollection wpaCfg = wpa.readSystemConfig();
			wpaCfg.isEmpty();
		} catch (IOException e) {
			e.printStackTrace();
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
}