package uk.digitalsquid.internetrestore.jni;

public class FileUtil {
	private FileUtil() {}
	
	static {
		System.loadLibrary("misc");
	}
	
	public static native void setExecutable(String path);
}
