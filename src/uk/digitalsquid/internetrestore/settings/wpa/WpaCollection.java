package uk.digitalsquid.internetrestore.settings.wpa;

import java.util.ArrayList;
import java.util.StringTokenizer;

import uk.digitalsquid.internetrestore.Logg;

/**
 * Represents a wpa_supplicant.conf file, or a subsection of it.
 * @author william
 *
 */
public class WpaCollection extends ArrayList<WpaVal> {

	private static final long serialVersionUID = -984589309176975957L;
	
	public WpaCollection() {
		
	}
	
	/**
	 * Fills in a {@link WpaCollection} with the given contents.
	 * @param contents
	 */
	public WpaCollection(String contents) {
		StringTokenizer tk = new StringTokenizer(contents, "\n\r");
		while(tk.hasMoreTokens()) {
			StringBuilder lines = new StringBuilder();
			
			int bracketDepth = 0;
			do { //Keep skipping (and appending) lines until any closing brackets are met
				String element = tk.nextToken();
				for(int i = 0; i < element.length(); i++) {
					char c = element.charAt(i);
					if(c == '{') bracketDepth++;
					if(c == '}') bracketDepth--;
				}
				if(lines.length() > 0) lines.append('\n');
				lines.append(element);
			} while(bracketDepth != 0 && tk.hasMoreTokens());
			
			String section = lines.toString().trim();
			
			if(section.equals("")) continue;
			String[] parts = section.split("=", 2);
			if(parts.length < 2) {
				Logg.d("Malformed WPA line: " + section);
				continue;
			}
			String key = parts[0];
			WpaVal entry = new WpaVal(key, parts[1]);
			add(entry);
		}
	}
	
	public void write(StringBuilder out) {
		for(WpaVal entry : this) {
			entry.write(out);
			out.append('\n');
		}
	}
	
	public int getNetworkCount() {
		int count = 0;
		for(WpaVal val : this) {
			if(val.getKey().toLowerCase().startsWith("network"))
				count++;
		}
		return count;
	}
}
