package uk.digitalsquid.internetrestore.settings.wpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;

import uk.digitalsquid.internetrestore.util.ListProxy;
import uk.digitalsquid.internetrestore.util.NumberParser;
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
public class WpaParsedSettings extends ListProxy<WifiConfiguration> implements Serializable {
	private static final long serialVersionUID = -3170244308212339621L;

	/**
	 * Parameters which aren't networks.
	 */
	private WpaCollection remainingParameters = new WpaCollection();
	
	/**
	 * Networks, parsed into {@link WifiConfiguration}
	 */
	private ArrayList<WifiConfiguration> networks = new ArrayList<WifiConfiguration>();
	
	public WpaParsedSettings(WpaCollection config) {
		setProxy(networks);
		for(int i = 0; i < config.size(); i++) {
			WpaVal val = config.get(i);
			if("network".equals(val.getKey())) {
				if(val.getType() == WpaVal.TYPE_VALUE) continue;
				networks.add(convertConfToNetwork(val));
			} else
				remainingParameters.add(val);
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
		WpaVal val = new WpaVal("network");
		WpaCollection inner = val.getChildren();
		
		inner.add("bssid", network.BSSID);
		if(network.SSID != null)
			inner.add("ssid", enquote(network.SSID));
		if(network.allowedAuthAlgorithms != null)
			inner.add("auth_alg", bitSetToString(network.allowedAuthAlgorithms, AuthAlgorithm.strings));
		if(network.preSharedKey != null)
			inner.add("psk", enquote(network.preSharedKey));
		inner.add("priority", String.valueOf(network.priority));
		inner.add("scan_ssid", network.hiddenSSID ? "1" : "0");
		if(network.allowedProtocols != null)
			inner.add("proto", bitSetToString(network.allowedProtocols, replaceUnderscoreToDash(Protocol.strings)));
		if(network.allowedKeyManagement != null)
			inner.add("key_mgmt", bitSetToString(network.allowedKeyManagement, replaceUnderscoreToDash(KeyMgmt.strings)));
		if(network.allowedPairwiseCiphers != null)
			inner.add("pairwise", bitSetToString(network.allowedPairwiseCiphers, replaceUnderscoreToDash(PairwiseCipher.strings)));
		if(network.allowedGroupCiphers != null)
			inner.add("group", bitSetToString(network.allowedGroupCiphers, replaceUnderscoreToDash(GroupCipher.strings)));
		
		if(network.wepKeys != null) {
			for(int i = 0; i < 4; i++) {
				String rawKey = network.wepKeys[i];
				if(rawKey == null) continue;
				String key = isHex(rawKey) ? rawKey : enquote(rawKey);
				if(network.wepKeys.length > i) inner.add(String.format("wep_key%d", i), key);
			}
		}
		inner.add("wep_tx_keyidx", String.valueOf(network.wepTxKeyIndex));
		
		return val;
	}
	
	private static String enquote(String str) {
		return String.format("\"%s\"", str);
	}
	
	/**
	 * Fixes the fact that android's KeyMgmt.strings reports that a value is
	 * WPA_PSK, when it is actually WPA-PSK.
	 * @param in
	 * @return
	 */
	private static String[] replaceUnderscoreToDash(String[] in) {
		// Where is Haskell when you need it?
		if(in == null) return null;
		String[] ret = new String[in.length];
		int i = 0;
		for(String str : in) {
			ret[i++] = str.replace('_', '-');
		}
		return ret;
	}
	
	/**
	 * Returns <code>true</code> if the given string contains hexadecimal only.
	 * @param str
	 * @return
	 */
	private static boolean isHex(String str) {
		// TODO: Ask the user if they want a hex or string WEP key?
		for(char c : str.toCharArray()) {
			if(
					(c < '0' || c > '9') &&
					(c < 'a' || c > 'f') &&
					(c < 'A' || c > 'F'))
				return false;
		}
		return true;
	}
	
	private static String bitSetToString(BitSet set, String[] values) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < set.size(); i++) {
			if(set.get(i)) {
				if(i >= values.length) break;
				if(sb.length() != 0) sb.append(' ');
				sb.append(values[i]);
			}
		}
		return sb.toString();
	}
	
	private static WifiConfiguration convertConfToNetwork(WpaVal config) {
		WifiConfiguration conf = new WifiConfiguration();
		conf.wepKeys = new String[4];
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
				conf.priority = NumberParser.parseIntSafe(value);
			else if(key.equals("scan_ssid"))
				conf.hiddenSSID = NumberParser.parseBoolSafe(value);
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
				if(valLower.contains("wpa_psk"))
					key_mgmt.set(KeyMgmt.WPA_PSK);
				if(valLower.contains("none"))
					key_mgmt.set(KeyMgmt.NONE);
				if(valLower.contains("wpa-eap"))
					key_mgmt.set(KeyMgmt.WPA_EAP);
				if(valLower.contains("wpa_eap"))
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
			} else if(key.equals("wep_key0"))
				conf.wepKeys[0] = value;
			else if(key.equals("wep_key1"))
				conf.wepKeys[1] = value;
			else if(key.equals("wep_key2"))
				conf.wepKeys[2] = value;
			else if(key.equals("wep_key3"))
				conf.wepKeys[3] = value;
			else if(key.equals("wep_tx_keyidx"))
				conf.wepTxKeyIndex = NumberParser.parseIntSafe(value);
		}
		return conf;
	}
	
	private boolean hasNetwork(String networkName) {
		for(WifiConfiguration conf : networks) {
			if(conf.SSID.equals(networkName)) return true;
		}
		return false;
	}
	
	/**
	 * Inserts all the new settings from the given settings
	 * @param from
	 */
	public void mergeFrom(WpaParsedSettings from) {
		for(WpaVal val : from.remainingParameters) {
			if(!remainingParameters.hasKey(val))
				remainingParameters.add(val);
		}
		for(WifiConfiguration conf : from.networks) {
			if(!hasNetwork(conf.SSID))
				networks.add(conf);
		}
	}
}
