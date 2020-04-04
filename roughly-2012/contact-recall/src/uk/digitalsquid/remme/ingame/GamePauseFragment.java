package uk.digitalsquid.remme.ingame;

import uk.digitalsquid.remme.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class GamePauseFragment extends Fragment implements OnClickListener {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.game_pause_fragment, container, false);
		
		// Buttons
		rootView.findViewById(R.id.resume).setOnClickListener(this);
		rootView.findViewById(R.id.leave).setOnClickListener(this);
		
		return rootView;
	}

	@Override
	public void onClick(View v) {
		Game activity = (Game)getActivity();
		switch(v.getId()) {
		case R.id.resume:
			if(activity != null) activity.resumeGame();
			break;
		case R.id.leave:
			if(activity != null) activity.finish();
			break;
		}
	}
}