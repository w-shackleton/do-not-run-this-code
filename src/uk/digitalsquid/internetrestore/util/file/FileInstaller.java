package uk.digitalsquid.internetrestore.util.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import uk.digitalsquid.internetrestore.Logg;
import uk.digitalsquid.internetrestore.R;
import uk.digitalsquid.internetrestore.jni.FileUtil;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;

public final class FileInstaller {
	private static final String BIN_DIR = "/bin";
	private static final String CONF_DIR = "/conf";
	private final Context context;
	
	public static final String CONF_WPA_SUPPLICANT = "wpa_supplicant.conf";
	public static final String BIN_BUSYBOX = "busybox";
	
	private final File binDir;
	private final File confDir;
	
	public FileInstaller(Context context) {
		this.context = context;
		binDir = new File(context.getFilesDir().getParent() + BIN_DIR);
		confDir = new File(context.getFilesDir().getParent() + CONF_DIR);
		try {
			new File(context.getFilesDir().getParent() + BIN_DIR).mkdir();
		} catch (NullPointerException e) {
			// One of the above fileops failed, most likely due to broken phone.
			Logg.e("Failed to create binary directory!");
		}
	}
	
	private void installFile(String filename, boolean executable, int id) throws Resources.NotFoundException, IOException {
		installFile(filename, id);
		if(executable) FileUtil.setExecutable(filename);
	}
	
	public void installScript(String scriptName, int id) throws Resources.NotFoundException, IOException {
		String scriptPath = getScriptPath(scriptName);
		installFile(scriptPath, true, id);
	}
	
	private void installFile(String filename, int id) throws Resources.NotFoundException, IOException {
		InputStream is = context.getResources().openRawResource(id);
		File outFile = new File(filename);
		outFile.createNewFile();
        Logg.d("Copying file '"+filename+"' ...");
        byte buf[] = new byte[1024];
        int len;
        OutputStream out = new FileOutputStream(outFile);
        while((len = is.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        is.close();
	}
	
	public String getScriptPath(String scriptName) {
		return binDir.getAbsolutePath() + "/" + scriptName;
	}
	
	public File getConfFilePath(String confFileName) {
		return new File(confDir.getAbsolutePath(), confFileName);
	}
	
	/**
	 * Installs necessary files from assets folder to bin folder
	 * @throws IOException 
	 * @throws NotFoundException 
	 */
	public void installFiles() throws NotFoundException, IOException {
		installScript("xtables-multi", R.raw.xtables);
		installScript("busybox", R.raw.busybox);
	}
}
