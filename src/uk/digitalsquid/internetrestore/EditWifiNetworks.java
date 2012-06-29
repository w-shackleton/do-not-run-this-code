package uk.digitalsquid.internetrestore;

import java.util.List;

import uk.digitalsquid.internetrestore.settings.wpa.WpaCollection;
import android.app.Activity;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class EditWifiNetworks extends Activity {
	
	WifiListAdapter listAdapter;
	App app;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (App) getApplication();
		setContentView(R.layout.edit_wifi_networks);
		listAdapter = new WifiListAdapter();
		ListView list = (ListView) findViewById(R.id.editWifiNetworks);
		list.setAdapter(listAdapter);
		
		// Get network list from saved supplicant file.
		WpaCollection wpaRawConf = app.getWpaSettings().readLocalConfig();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		getMenuInflater().inflate(R.menu.edit_wifi_menu, menu);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.addNew:
			return true;
		default:
			return false;
		}
	}
	
	class WifiListAdapter extends BaseAdapter {
		
		LayoutInflater inflater;
		
		public WifiListAdapter() {
			inflater = LayoutInflater.from(EditWifiNetworks.this);
		}
		
		@Override
		public int getCount() {
			return networks.size();
		}

		@Override
		public WifiConfiguration getItem(int position) {
			return networks.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
	        if (convertView == null) {
	            convertView = inflater.inflate(R.layout.wifi_network_item, null);
	        }
	        TextView name = (TextView) convertView.findViewById(R.id.name);
	        TextView description = (TextView) convertView.findViewById(R.id.description);
	        
	        WifiConfiguration conf = getItem(position);
	        
	        name.setText(conf.SSID);
	        description.setText("Description");
	        
	        return convertView;
		}
		
		private List<WifiConfiguration> networks;
		
		public void setNetworks(List<WifiConfiguration> networks) {
			this.networks = networks;
			notifyDataSetChanged();
		}
	}
}
