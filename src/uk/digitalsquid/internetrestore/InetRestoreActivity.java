package uk.digitalsquid.internetrestore;

import java.net.UnknownHostException;

import uk.digitalsquid.internetrestore.settings.InfoCollector;
import android.app.Activity;
import android.os.Bundle;

public class InetRestoreActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Testing
        InfoCollector info = new InfoCollector(this);
        try {
			Logg.d("Wifi: " + info.getWifiIface());
		} catch (UnknownHostException e) {
			Logg.e("Failed to get wifi iface", e);
		}
    }
}