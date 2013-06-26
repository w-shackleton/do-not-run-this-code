package uk.digitalsquid.contactrecall;

import uk.digitalsquid.contactrecall.ingame.Game;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class ModeSelect extends Activity implements OnClickListener {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modeselect);
		
		findViewById(R.id.guessName).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.guessName:
			// Game entry buttons
			GameDescriptor descriptor;
			switch(view.getId()) {
			case R.id.guessName:
				descriptor = new GameDescriptor(GameDescriptor.GAME_PHOTO_TO_NAME);
				break;
			default:
				descriptor = null;
				break;
			}
			
			Intent intent = new Intent(this, Game.class);
			intent.putExtra(Game.GAME_DESRIPTOR, descriptor);
			startActivity(intent);
			break;
		}
	}
}
