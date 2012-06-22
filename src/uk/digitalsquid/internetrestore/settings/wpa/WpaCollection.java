package uk.digitalsquid.internetrestore.settings.wpa;

import java.util.HashMap;
import java.util.StringTokenizer;

import uk.digitalsquid.internetrestore.Logg;

/**
 * Represents a wpa_supplicant.conf file, or a subsection of it.
 * @author william
 *
 */
public class WpaCollection extends HashMap<String, WpaVal> {

	private static final long serialVersionUID = -984589309176975957L;
	
	/**
	 * Fills in a {@link WpaCollection} with the given contents.
	 * @param contents
	 */
	public WpaCollection(String contents) {
		StringTokenizer tk = new StringTokenizer(contents, "\n\r");
		while(tk.hasMoreTokens()) {
			String element = tk.nextToken();
			if(element.equals("")) continue;
			String[] parts = element.split("=", 2);
			if(parts.length < 2) {
				Logg.d("Malformed WPA line: " + element);
				continue;
			}
			String key = parts[0];
			WpaVal val = new WpaVal(parts[1]);
			put(key, val);
		}
	}
	
	public void write(StringBuilder out) {
		
	}
}
