package uk.digitalsquid.contactrecall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class StartActivity extends Activity implements OnClickListener {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        findViewById(R.id.viewContacts).setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.viewContacts:
			startActivity(new Intent(this, ContactViewer.class));
			break;
		}
	}
}