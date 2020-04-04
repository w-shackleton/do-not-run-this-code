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
		if(key == null) throw new IllegalArgumentException("key is null");
		if(value == null) value = ""; // Really this shouldn't be added at all but it is too late here
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
	 * Constructor for a blank TYPE_CONTAINER
	 * @param key
	 */
	public WpaVal(String key) {
		this.key = key.trim();
		value = "{}";
		values = new WpaCollection();
		type = TYPE_CONTAINER;
	}
	
	/**
	 * Writes the contents of this {@link WpaVal} to the given {@link StringBuilder}
	 * @param out
	 */
	public void write(StringBuilder out) {
		out.append(key);
		out.append('=');
		switch(getType()) {
		case TYPE_VALUE:
			out.append(value);
			break;
		case TYPE_CONTAINER:
			values.writeAsChild(out);
			break;
		}
	}
	
	@Override
	public String toString() {
		switch(getType()) {
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
	
	/**
	 * Gets the value contained in this {@link WpaVal}. If it has multiple children,
	 * a text form of those is returned
	 * @return
	 */
	public String getValue() {
		switch(type) {
		case TYPE_VALUE:
			return value;
		case TYPE_CONTAINER:
			return values.toString();
		default:
			return "";
		}
	}

	public int getType() {
		return type;
	}
	
	/**
	 * Returns the children of this {@link WpaVal}. If it contains a single value,
	 * an exception is thrown
	 * @return
	 */
	public WpaCollection getChildren() {
		switch(type) {
		case TYPE_CONTAINER:
			return values;
		default:
			throw new IllegalStateException("This block only has a single child property");
		}
	}
}
