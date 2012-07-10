package uk.digitalsquid.internetrestore;

import java.io.IOException;

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