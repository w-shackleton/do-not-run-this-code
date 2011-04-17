package uk.digitalsquid.spacegame;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

class LevelCompletedDialog extends Dialog {

	public LevelCompletedDialog(Context context, int stars, int total) {
		super(context, R.style.dialogbox);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.levelcompleted);
		
		TextView starsCollected = (TextView) findViewById(R.id.starsCollected);
		starsCollected.setTypeface(StaticInfo.Fonts.bangers);
		starsCollected.setText("" + stars + " / " + total);
	}
}
