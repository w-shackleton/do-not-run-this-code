package uk.digitalsquid.internetrestore.settings.wpa;

public class WpaVal {
	private final int type;
	
	private final String key;
	
	/**
	 * non-<code>null</code> if type = TYPE_VALUE
	 */
	private final String value;
	
	/**
	 * non-<code>null</code> if type = TYPE_CONTAINER
	 */
	private final WpaCollection values;
	
	/**
	 * A single value
	 */
	public static final int TYPE_VALUE = 1;
	/**
	 * A collection, usually network=...
	 */
	public static final int TYPE_CONTAINER = 2;
	
	public WpaVal(String key, String value) {
		this.key = key.trim();
		this.value = value.trim();
		if(this.value.startsWith("{")) {
			type = TYPE_CONTAINER;
			int ix1 = this.value.indexOf('{');
			int ix2 = this.value.lastIndexOf('}');
			String inner = this.value.substring(ix1+1, ix2);
			this.values = new WpaCollection(inner);
		}
		else {
			type = TYPE_VALUE;
			this.values = null;
		}
	}
	
	/**
	 * Writes the contents of this {@link WpaVal} to the given {@link StringBuilder}
	 * @param out
	 */
	public void write(StringBuilder out) {
		out.append(key);
		out.append('=');
		switch(type) {
		case TYPE_VALUE:
			out.append(value);
		case TYPE_CONTAINER:
			values.write(out);
		}
	}
	
	@Override
	public String toString() {
		switch(type) {
		case TYPE_VALUE:
			return key + ": " + value;
		case TYPE_CONTAINER:
			return key + ": " + values.toString();
		default:
			return "";
		}
	}

	public String getKey() {
		return key;
	}
}
