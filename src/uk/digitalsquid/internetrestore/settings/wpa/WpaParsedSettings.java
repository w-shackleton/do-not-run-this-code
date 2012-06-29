package uk.digitalsquid.internetrestore.settings.wpa;

import java.util.ArrayList;
import java.util.BitSet;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;

/**
 * Stores settings in a form where they can be edited easily
 * @author william
 *
 */
public class WpaParsedSettings {
	private WpaCollection remainingParameters;
	
	private ArrayList<WifiConfiguration> networks;
	
	@SuppressWarnings("unchecked")
	public WpaParsedSettings(WpaCollection config) {
		remainingParameters = (WpaCollection) config.clone();
		for(int i = 0; i < remainingParameters.size(); i++) {
			WpaVal val = remainingParameters.get(i);
			if("network".equals(val.getKey())) {
				if(val.getType() == WpaVal.TYPE_VALUE) continue;
				networks.add(convertConfToNetwork(val));
			}
		}
	}
	
	/**
	 * Converts the parsed settings back into the raw form
	 * @return
	 */
	public WpaCollection convertBackToConfig() {
		WpaCollection ret = (WpaCollection) remainingParameters.clone();
		
		for(WifiConfiguration network : networks)
			ret.add(convertNetwork(network));
		
		return ret;
	}
	
	private static WpaVal convertNetwork(WifiConfiguration network) {
		// TODO: Implement
		return null;
	}
	
	private static WifiConfiguration convertConfToNetwork(WpaVal config) {
		WifiConfiguration conf = new WifiConfiguration();
		for(WpaVal val : config.getChildren()) {
			String key = val.getKey().toLowerCase();
			String value = val.getValue();
			if(key.equals("bssid"))
				conf.BSSID = value;
			else if(key.equals("ssid"))
				conf.SSID = value;
			else if(key.equals("auth_alg")) {
				BitSet auth_alg = new BitSet();
				if(value.contains("LEAP"))
					auth_alg.set(AuthAlgorithm.LEAP);
				if(value.contains("OPEN"))
					auth_alg.set(AuthAlgorithm.OPEN);
				if(value.contains("SHARED"))
					auth_alg.set(AuthAlgorithm.SHARED);
			} else if(key.equals("psk"))
				conf.preSharedKey = value;
		}
		return conf;
	}
}
