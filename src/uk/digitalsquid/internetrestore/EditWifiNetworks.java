package uk.digitalsquid.internetrestore;

import java.io.IOException;
import java.util.List;

import uk.digitalsquid.internetrestore.settings.wpa.WpaCollection;
import uk.digitalsquid.internetrestore.settings.wpa.WpaParsedSettings;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

public class EditWifiNetworks extends Activity {
	
	protected static final int DIALOG_ASK_USE_SYSTEM_WIFI = 1;
	
	WifiListAdapter listAdapter;
	App app;
	
	WpaParsedSettings config;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (App) getApplication();
		setContentView(R.layout.edit_wifi_networks);
		listAdapter = new WifiListAdapter();
		ListView list = (ListView) findViewById(R.id.list);
		list.setAdapter(listAdapter);
		
		// Get network list from saved supplicant file.
		WpaCollection wpaRawConf = app.getWpaSettings().readLocalConfig();
		config = wpaRawConf.parse();
		if(config.size() == 0) {
			showDialog(DIALOG_ASK_USE_SYSTEM_WIFI);
		}
		listAdapter.setNetworks(config);
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
	
	/**
	 * Displays and saves the current network list back to the conf file.
	 * Should be called after each change to ensure Android consistency etc.
	 */
	void updateNetworks() {
		listAdapter.setNetworks(config);
		WpaCollection rawConfig = config.convertBackToConfig();
		try {
			app.getWpaSettings().writeLocalConfig(rawConfig);
		} catch (IOException e) {
			Logg.e("Failed to write wpa config back to wpa_supplicant.conf", e);
			Toast.makeText(this, "Failed to write new configuration", Toast.LENGTH_SHORT).show();
		}
	}
	
	class WifiListAdapter extends BaseAdapter {
		
		LayoutInflater inflater;
		
		public WifiListAdapter() {
			inflater = LayoutInflater.from(EditWifiNetworks.this);
		}
		
		@Override
		public int getCount() {
			return networks == null ? 0 : networks.size();
		}

		@Override
		public WifiConfiguration getItem(int position) {
			return networks == null ? null : networks.get(position);
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
	
	public void addNetworksFromSystemConfig() {
		Logg.i("Adding networks from system conf");
		WpaCollection wpaRawConf = null;
		try {
			wpaRawConf = app.getWpaSettings().readSystemConfig();
		} catch (IOException e) {
			Logg.e("Failed to get system wpa_supplicant config", e);
			Toast.makeText(this, "Failed to import system Wifi networks. Either the file couldn't be found or superuser wasn't accepted.", Toast.LENGTH_LONG).show();
		}
		WpaParsedSettings wpaConf = new WpaParsedSettings(wpaRawConf);
		config.mergeFrom(wpaConf);
		updateNetworks();
	}
	
	public Dialog onCreateDialog(int id, Bundle args) {
		super.onCreateDialog(id, args);
		Builder builder;
		switch(id) {
		case DIALOG_ASK_USE_SYSTEM_WIFI:
			builder = new Builder(this);
			builder.setTitle(R.string.importWifiTitle);
			builder.setMessage(R.string.importWifiDesc);
			builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					addNetworksFromSystemConfig();
				}
			});
			builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			return builder.create();
		default:
			return null;
		}
	}
}
