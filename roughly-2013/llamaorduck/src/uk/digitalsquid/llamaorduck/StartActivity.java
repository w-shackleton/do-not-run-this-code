package uk.digitalsquid.llamaorduck;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class StartActivity extends Activity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		findViewById(R.id.start).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		startActivity(new Intent(this, GameActivity.class));
	}
}
