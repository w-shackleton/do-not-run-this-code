package uk.digitalsquid.physicsland;

import uk.digitalsquid.physicsland.pfile.PFileParser;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class PhysicsLand extends Activity
{
	PhysicsLandView sview;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  
        setContentView(R.layout.main);
        
        @SuppressWarnings("unused")
		PFileParser fileparser = new PFileParser(
        		"NAME Test 1\n" +
        		"DESC First Land\n" +
        		"BEGOBJ\n" +
        		"SQ 5,5,10,10\n" +
        		"SQ 30,5,10,10\n" +
        		"ENDOBJ"
        		);

    }
}