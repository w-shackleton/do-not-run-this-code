package uk.digitalsquid.internetrestore.jni;

public class FileUtil {
	private FileUtil() {}
	
	static {
		System.loadLibrary("misc");
	}
	
	public static native void setExecutable(String path);
	
	/**
	 * Sets permissions to 666 for a file, 777 for a directory
	 * @param path
	 */
	public static native void setPublicVisible(String path, boolean isDirectory);
}
