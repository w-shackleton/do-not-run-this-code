package uk.digitalsquid.spacegame;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

class LevelCompletedDialog extends Dialog implements android.view.View.OnClickListener {
	
	private final Handler handler;

	public LevelCompletedDialog(Context context, int stars, int total, final Handler handler) {
		super(context, R.style.dialogbox);
		this.handler = handler;
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.levelcompleted);
		
		TextView starsCollected = (TextView) findViewById(R.id.starsCollected);
		starsCollected.setTypeface(StaticInfo.Fonts.bangers);
		starsCollected.setText("" + stars + " / " + total);
		
		((Button)findViewById(R.id.buttonContinue)).setOnClickListener(this);
		((Button)findViewById(R.id.buttonRetry)).setOnClickListener(this);
		((Button)findViewById(R.id.buttonToMenu)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.buttonContinue:
			handler.sendEmptyMessage(Spacegame.LEVELCOMPLETED_CONTINUE);
			dismiss();
			break;
		case R.id.buttonRetry:
			handler.sendEmptyMessage(Spacegame.LEVELCOMPLETED_RETRY);
			dismiss();
			break;
		case R.id.buttonToMenu:
			handler.sendEmptyMessage(Spacegame.LEVELCOMPLETED_TOMENU);
			dismiss();
			break;
		}
	}
}
