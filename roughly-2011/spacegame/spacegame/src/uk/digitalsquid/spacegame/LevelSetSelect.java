package uk.digitalsquid.spacegame;

import uk.digitalsquid.spacegame.levels.LevelManager;
import uk.digitalsquid.spacegame.views.MainMenu;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Shows the screen to select the levelset
 * @author william
 *
 */
public class LevelSetSelect extends Activity implements OnClickListener {
	MainMenu menuView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.levelsetselect);
		
		menuView = (MainMenu) findViewById(R.id.menuview);
		menuView.setLevel(getResources().openRawResource(R.raw.menu_levelsets));
		menuView.setFocusable(false);
		menuView.setFocusableInTouchMode(false);
		menuView.create();
		
		findViewById(R.id.menubutton_levelset1).setOnClickListener(this);
		findViewById(R.id.menubutton_levelset2).setOnClickListener(this);
		findViewById(R.id.menubutton_levelset3).setOnClickListener(this);
		findViewById(R.id.menubutton_levelset4).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		String levelsetfilename = LevelManager.BUILTIN_PREFIX;
		switch(v.getId())
		{
		case R.id.menubutton_levelset1:
			levelsetfilename += "tutorial";
			break;
		case R.id.menubutton_levelset2:
			levelsetfilename += "easy";
			break;
		case R.id.menubutton_levelset3:
			levelsetfilename += "medium";
			break;
		case R.id.menubutton_levelset4:
			levelsetfilename += "hard";
			break;
		default:
			return;
		}
		
		menuView.stop();
		Intent intent = new Intent(this, LevelSelect.class);
		intent.putExtra(LevelSelect.LEVELSET_EXTRA, levelsetfilename);
		startActivity(intent);
	}
}
