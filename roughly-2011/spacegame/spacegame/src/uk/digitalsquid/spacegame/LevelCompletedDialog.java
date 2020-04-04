package uk.digitalsquid.spacegame;

import uk.digitalsquid.spacegame.levels.LevelItem.LevelSummary;
import uk.digitalsquid.spacegamelib.StaticInfo;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

class LevelCompletedDialog extends Dialog implements android.view.View.OnClickListener, OnKeyListener {
	
	private final Handler handler;

	public LevelCompletedDialog(Context context, LevelSummary summary, final Handler handler) {
		super(context, R.style.dialogbox);
		this.handler = handler;
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.levelcompleted);
		setOnKeyListener(this);
		
		TextView starsCollected = (TextView) findViewById(R.id.starsCollected);
		starsCollected.setTypeface(StaticInfo.Fonts.bangers);
		starsCollected.setText("" + summary.starsCollected + " / " + summary.starsToCollect + " "); // Extra space because of font weirdness
		
		int time = summary.timeTaken;
		int deci = (time / 100) % 10;
		int secs = (time / 1000) % 60;
		int mins = (time / 1000) / 60;
		TextView timeTaken = (TextView) findViewById(R.id.timeTaken);
		timeTaken.setTypeface(StaticInfo.Fonts.bangers);
		timeTaken.setText(String.format("%d:%d.%d ", mins, secs, deci)); // Extra space because of font weirdness
		
		((Button)findViewById(R.id.buttonContinue)).setOnClickListener(this);
		((Button)findViewById(R.id.buttonRetry)).setOnClickListener(this);
		((Button)findViewById(R.id.buttonToMenu)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.buttonContinue:
			handler.sendEmptyMessage(Game.LEVELCOMPLETED_CONTINUE);
			dismiss();
			break;
		case R.id.buttonRetry:
			handler.sendEmptyMessage(Game.LEVELCOMPLETED_RETRY);
			dismiss();
			break;
		case R.id.buttonToMenu:
			handler.sendEmptyMessage(Game.LEVELCOMPLETED_TOMENU);
			dismiss();
			break;
		}
	}

	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			handler.sendEmptyMessage(Game.LEVELCOMPLETED_TOMENU);
			dismiss();
			return true;
		}
		return false;
	}
}
