package uk.digitalsquid.remme;

import uk.digitalsquid.remme.ingame.Game;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * A fragment representing a single Difficulty detail screen. This fragment is
 * either contained in a {@link DifficultyListActivity} in two-pane mode (on
 * tablets) or a {@link DifficultyDetailActivity} on handsets.
 */
public class DifficultyDetailFragment extends Fragment implements OnClickListener {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_DIFFICULTY = "difficulty";

	/**
	 * The difficulty level being presented.
	 */
	private int difficulty;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public DifficultyDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_DIFFICULTY)) {
			difficulty = getArguments().getInt(ARG_DIFFICULTY);
		}
	}
	
	private CheckBox askPersonal, askCorporate, askGroups;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_difficulty_detail,
				container, false);
		
		final String[] titles = getResources().
				getStringArray(R.array.difficulty_titles);
		final String[] descriptions = getResources().
				getStringArray(R.array.difficulty_descriptions);

		TextView title = (TextView) rootView.findViewById(R.id.title);
		TextView description = (TextView) rootView.findViewById(R.id.description);
		title.setText(getElement(titles, difficulty));
		description.setText(getElement(descriptions, difficulty));
		
		rootView.findViewById(R.id.start).setOnClickListener(this);

		askPersonal = (CheckBox) rootView.findViewById(R.id.askPersonal);
		askCorporate = (CheckBox) rootView.findViewById(R.id.askCorporate);
		askGroups = (CheckBox) rootView.findViewById(R.id.askGroups);

		return rootView;
	}
	
	private static final String getElement(final String[] array, final int pos) {
		if(pos < 0) return "";
		if(pos >= array.length) return ""; 
		return array[pos];
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.start:
			// Construct SetupDescriptor
			SetupDescriptor setup = new SetupDescriptor();
			setup.setDifficulty(difficulty);
			setup.setAskPersonal(askPersonal.isChecked());
			setup.setAskCorporate(askCorporate.isChecked());
			setup.setAskGroups(askGroups.isChecked());
			
			GameDescriptor descriptor = setup.generateGameDescriptor();
			
			Intent intent = new Intent(getActivity(), Game.class);
			intent.putExtra(Game.GAME_DESRIPTOR, descriptor);
			startActivity(intent);
			
			break;
		}
	}
}
