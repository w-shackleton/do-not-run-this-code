package uk.digitalsquid.internetrestore;

import java.io.IOException;

import uk.digitalsquid.internetrestore.settings.WpaSettings.Config;
import uk.digitalsquid.internetrestore.settings.wpa.WpaCollection;
import uk.digitalsquid.internetrestore.settings.wpa.WpaParsedSettings;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.PasswordTransformationMethod;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EditWifiNetworks extends Activity implements OnItemClickListener {
	
	protected static final int DIALOG_ASK_USE_SYSTEM_WIFI = 1;
	protected static final int DIALOG_EDIT_FIRST_WIFI = 2;
	
	private int nextDialog = DIALOG_EDIT_FIRST_WIFI;
	
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
		
		list.setOnCreateContextMenuListener(this);
		list.setOnItemClickListener(this);
		
		if(savedInstanceState == null) {
			// Get network list from saved supplicant file.
			WpaCollection wpaRawConf = app.getWpaSettings().readLocalConfig();
			config = wpaRawConf.parse();
			if(config.size() == 0) {
				showDialog(DIALOG_ASK_USE_SYSTEM_WIFI);
			}
		} else {
			config = (WpaParsedSettings) savedInstanceState.getSerializable("wifiList");
		}
		listAdapter.setNetworks(config);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("wifiList", listAdapter.getNetworks());
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
		case R.id.importSystem:
			addNetworksFromSystemConfig();
			return true;
		default:
			return false;
		}
	}
		
	private int currentSelectedId;
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		switch(v.getId()) {
		case R.id.list:
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
			if(info.position < 0) menu.close();
			WifiConfiguration network = listAdapter.getItem(info.position);
			menu.setHeaderTitle(network.SSID);
			getMenuInflater().inflate(R.menu.edit_wifi_context_menu, menu);
			
			currentSelectedId = info.position;
			break;
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		super.onContextItemSelected(item);
		switch(item.getItemId()) {
		case R.id.edit:
			Bundle bundle = new Bundle();
			bundle.putInt("id", currentSelectedId);
			WifiConfiguration network = listAdapter.getItem(currentSelectedId);
			bundle.putParcelable("oldSettings", network);
			showDialog(DIALOG_EDIT_FIRST_WIFI + nextDialog++, bundle);
			return true;
		case R.id.delete:
			config.remove(currentSelectedId);
			updateNetworks();
			return true;
		default:
			return false;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
			Bundle bundle = new Bundle();
			bundle.putInt("id", pos);
			WifiConfiguration network = listAdapter.getItem(pos);
			bundle.putParcelable("oldSettings", network);
			showDialog(DIALOG_EDIT_FIRST_WIFI + nextDialog++, bundle);
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
	        
			if(conf.allowedAuthAlgorithms.get(AuthAlgorithm.SHARED)) { // WEP
		        description.setText("Secured with WEP");
			} else if(conf.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) { // WPA
		        description.setText("Secured with WPA");
			} else {
		        description.setText("Open network");
			}
	        
	        return convertView;
		}
		
		private WpaParsedSettings networks;
		
		public void setNetworks(WpaParsedSettings networks) {
			this.networks = networks;
			notifyDataSetChanged();
		}
		
		public WpaParsedSettings getNetworks() {
			return networks;
		}
	}
	
	public void addNetworksFromSystemConfig() {
		Logg.i("Adding networks from system conf");
		app.getWpaSettings().readConfigAsync(Config.SYSTEM_CONFIG, networksReadHandler);
	}
	
	@SuppressLint("HandlerLeak")
	private Handler networksReadHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.obj instanceof WpaCollection) {
				WpaCollection wpaRawConf = (WpaCollection) msg.obj;
				WpaParsedSettings wpaConf = new WpaParsedSettings(wpaRawConf);
				config.mergeFrom(wpaConf);
				updateNetworks();
			}
		}
	};
	
	public Dialog onCreateDialog(int id, final Bundle args) {
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
			builder = new Builder(this);
			builder.setTitle(R.string.editWifiTitle);
			
			final WifiConfiguration conf = args.getParcelable("oldSettings");
			
			final View view = getLayoutInflater().inflate(R.layout.edit_wifi_dialog, null);
			final Spinner security = (Spinner) view.findViewById(R.id.security);
			
			final LinearLayout wifiConfWpa = (LinearLayout) view.findViewById(R.id.wifiConfWpa);
			final LinearLayout wifiConfWep = (LinearLayout) view.findViewById(R.id.wifiConfWep);
			wifiConfWpa.setVisibility(View.GONE);
			wifiConfWep.setVisibility(View.GONE);
			
			final EditText priority = (EditText)view.findViewById(R.id.priority);
			
			final EditText ssid = (EditText) view.findViewById(R.id.ssid);
			final EditText wpaPsk = (EditText) view.findViewById(R.id.wpaPsk);
			final EditText wepKeys[] = new EditText[4];
			wepKeys[0] = (EditText) view.findViewById(R.id.wepKey0);
			wepKeys[1] = (EditText) view.findViewById(R.id.wepKey1);
			wepKeys[2] = (EditText) view.findViewById(R.id.wepKey2);
			wepKeys[3] = (EditText) view.findViewById(R.id.wepKey3);
			final RadioButton wepIxs[] = new RadioButton[4];
			wepIxs[0] = (RadioButton) view.findViewById(R.id.wepKeyIx0);
			wepIxs[1] = (RadioButton) view.findViewById(R.id.wepKeyIx1);
			wepIxs[2] = (RadioButton) view.findViewById(R.id.wepKeyIx2);
			wepIxs[3] = (RadioButton) view.findViewById(R.id.wepKeyIx3);
			
			CheckBox showWpaKeys = (CheckBox) view.findViewById(R.id.showPsk);
			CheckBox showWepKeys = (CheckBox) view.findViewById(R.id.showWepKeys);
			
			showWpaKeys.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					wpaPsk.setTransformationMethod(isChecked ? null : new PasswordTransformationMethod());
				}
			});
			showWepKeys.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					for(EditText wepKey : wepKeys)
						wepKey.setTransformationMethod(isChecked ? null : new PasswordTransformationMethod());
				}
			});
			
			security.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					wifiConfWpa.setVisibility(View.GONE);
					wifiConfWep.setVisibility(View.GONE);
					switch(position) {
					case 1: // WEP
						wifiConfWep.setVisibility(View.VISIBLE);
						break;
					case 2: // WEP
						wifiConfWpa.setVisibility(View.VISIBLE);
						break;
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					wifiConfWpa.setVisibility(View.GONE);
					wifiConfWep.setVisibility(View.GONE);
				}
			});
			builder.setView(view);
			
			ssid.setText(conf.SSID);
			priority.setText(String.valueOf(conf.priority));
			if(conf.allowedAuthAlgorithms.get(AuthAlgorithm.SHARED)) { // WEP
				security.setSelection(1);
				if(conf.wepTxKeyIndex < 0) conf.wepTxKeyIndex = 0;
				if(conf.wepTxKeyIndex > 3) conf.wepTxKeyIndex = 3;
				wepIxs[conf.wepTxKeyIndex].setChecked(true);
				if(conf.wepKeys != null) {
					for(int i = 0; i < conf.wepKeys.length && i < wepKeys.length; i++) {
						wepKeys[i].setText(conf.wepKeys[i]);
					}
				}
			} else if(conf.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
				security.setSelection(2);
				wpaPsk.setText(conf.preSharedKey);
			} else {
				security.setSelection(0);
			}
			
			builder.setNegativeButton(android.R.string.no, null);
			builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					conf.SSID = ssid.getText().toString();
					conf.priority = Integer.parseInt(priority.getText().toString());
					switch(security.getSelectedItemPosition()) {
					case 0: // Open
						conf.allowedKeyManagement.clear();
						conf.allowedAuthAlgorithms.clear();
						break;
					case 1: // WEP
						conf.allowedKeyManagement.clear();
						conf.allowedAuthAlgorithms.clear();
						conf.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
						conf.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
						
						if(conf.wepKeys == null)
							conf.wepKeys = new String[4];
						for(int i = 0; i < conf.wepKeys.length && i < wepKeys.length; i++) {
							conf.wepKeys[i] = wepKeys[i].getText().toString();
						}
						conf.wepTxKeyIndex = 0;
						for(int i = 0; i < wepIxs.length; i++)
							if(wepIxs[i].isChecked()) conf.wepTxKeyIndex = i;
						break;
					case 2: // WPA
						conf.allowedKeyManagement.clear();
						conf.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
						conf.allowedAuthAlgorithms.clear();
						
						conf.preSharedKey = wpaPsk.getText().toString();
						break;
					}
					int id = args.getInt("id");
					config.set(id, conf);
					updateNetworks();
				}
			});
			return builder.create();
		}
	}
}
