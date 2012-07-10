package uk.digitalsquid.internetrestore.settings.wpa;

import java.util.ArrayList;
import java.util.BitSet;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.GroupCipher;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.PairwiseCipher;
import android.net.wifi.WifiConfiguration.Protocol;

/**
 * Stores settings in a form where they can be edited easily
 * @author william
 *
 */
public class WpaParsedSettings {
	private WpaCollection remainingParameters;
	
	private ArrayList<WifiConfiguration> networks = new ArrayList<WifiConfiguration>();
	
	public WpaParsedSettings(WpaCollection config) {
		remainingParameters = (WpaCollection) config.clone();
		for(int i = 0; i < remainingParameters.size(); i++) {
			WpaVal val = remainingParameters.get(i);
			if("network".equals(val.getKey())) {
				if(val.getType() == WpaVal.TYPE_VALUE) continue;
				networks.add(convertConfToNetwork(val));
			} else
				remainingParameters.remove(i);
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
	
	/**
	 * Parses an integer safely. Returns 0 on error.
	 * @param number
	 * @return
	 */
	private static int parseIntSafe(String number) {
		try {
			return Integer.parseInt(number);
		} catch(NumberFormatException e) {
			return 0;
		}
	}
	
	private static boolean parseBoolSafe(String bool) {
		String val = bool.toLowerCase();
		if("true".equals(val)) return true;
		if("false".equals(val)) return false;
		if("1".equals(val)) return true;
		if("0".equals(val)) return false;
		return false;
	}
	
	private static WifiConfiguration convertConfToNetwork(WpaVal config) {
		WifiConfiguration conf = new WifiConfiguration();
		for(WpaVal val : config.getChildren()) {
			String key = val.getKey().toLowerCase();
			String value = val.getValue();
			String valLower = value.toLowerCase();
			if(key.equals("bssid"))
				conf.BSSID = value;
			else if(key.equals("ssid"))
				conf.SSID = value;
			else if(key.equals("auth_alg")) {
				BitSet auth_alg = new BitSet();
				if(valLower.contains("leap"))
					auth_alg.set(AuthAlgorithm.LEAP);
				if(valLower.contains("open"))
					auth_alg.set(AuthAlgorithm.OPEN);
				if(valLower.contains("shared"))
					auth_alg.set(AuthAlgorithm.SHARED);
				conf.allowedAuthAlgorithms = auth_alg;
			} else if(key.equals("psk"))
				conf.preSharedKey = value;
			else if(key.equals("priority"))
				conf.priority = parseIntSafe(value);
			else if(key.equals("scan_ssid"))
				conf.hiddenSSID = parseBoolSafe(value);
			else if(key.equals("proto")) {
				BitSet proto = new BitSet();
				if(valLower.contains("wpa"))
					proto.set(Protocol.WPA);
				if(valLower.contains("rsn"))
					proto.set(Protocol.RSN);
			} else if(key.equals("key_mgmt")) {
				BitSet key_mgmt = new BitSet();
				if(valLower.contains("ieee8021x"))
					key_mgmt.set(KeyMgmt.IEEE8021X);
				if(valLower.contains("wpa-psk"))
					key_mgmt.set(KeyMgmt.WPA_PSK);
				if(valLower.contains("none"))
					key_mgmt.set(KeyMgmt.NONE);
				if(valLower.contains("wpa-eap"))
					key_mgmt.set(KeyMgmt.WPA_EAP);
				conf.allowedKeyManagement = key_mgmt;
			} else if(key.equals("pairwise")) {
				BitSet pairwise = new BitSet();
				if(valLower.contains("ccmp"))
					pairwise.set(PairwiseCipher.CCMP);
				if(valLower.contains("none"))
					pairwise.set(PairwiseCipher.NONE);
				if(valLower.contains("tkip"))
					pairwise.set(PairwiseCipher.TKIP);
				conf.allowedPairwiseCiphers = pairwise;
			} else if(key.equals("group")) {
				BitSet group = new BitSet();
				if(valLower.contains("ccmp"))
					group.set(GroupCipher.CCMP);
				if(valLower.contains("tkip"))
					group.set(GroupCipher.TKIP);
				if(valLower.contains("wep104"))
					group.set(GroupCipher.WEP104);
				if(valLower.contains("wep40"))
					group.set(GroupCipher.WEP40);
				conf.allowedGroupCiphers = group;
			}
		}
		return conf;
	}
}
