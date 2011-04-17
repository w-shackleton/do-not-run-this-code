package uk.digitalsquid.spacegame;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

class LevelCompletedDialog extends Dialog {

	public LevelCompletedDialog(Context context) {
		super(context, R.style.dialogbox);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.levelcompleted);
	}
}
