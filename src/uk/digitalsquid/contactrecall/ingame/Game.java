package uk.digitalsquid.contactrecall.ingame;

import java.util.ArrayList;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.GameDescriptor;
import uk.digitalsquid.contactrecall.R;
import uk.digitalsquid.contactrecall.ingame.Game.GameFragment.QuestionFailData;
import uk.digitalsquid.contactrecall.mgr.Question;
import uk.digitalsquid.contactrecall.mgr.details.Contact;
import uk.digitalsquid.contactrecall.misc.Config;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The actual game itself.
 * @author william
 * TODO: Currently, the view hierarchy is too complicated.
 * Container contains the pager, pause and summary screens.
 * The pager contains the cards. It will probably work also
 * if the container contains all these things. Might be worth doing.
 */
public class Game extends Activity implements GameCallbacks, Config {
	
	public static final String GAME_DESRIPTOR = "uk.digitalsquid.contactrecall.gameInstance";
	
	GameDescriptor gameDescriptor;
	
	private boolean gamePaused = false;
	private boolean gameRunning = true;
	
	/**
	 * This class implements {@link GameCallbacks} -
	 * this implementation proxies to the given callbacks.
	 */
	GameCallbacks callbacks;
	
	View pauseLayout;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.game);
		
		if(savedInstanceState == null) {
			GameFragment gameFragment = new GameFragment();
			callbacks = gameFragment;
			
			Bundle fragmentArgs = new Bundle();
			
			// Get game descriptor
			try {
				gameDescriptor = getIntent().getParcelableExtra(GAME_DESRIPTOR);
			} catch(Exception e) { // Who knows
				Log.e(TAG, "Failed to get game descriptor", e);
			}
			if(gameDescriptor == null) {
				Toast.makeText(this,  "Failed to load game", Toast.LENGTH_LONG).show();
				finish();
				return;
			}
			
			fragmentArgs.putParcelable("gameDescriptor", gameDescriptor);
			
			gameFragment.setArguments(fragmentArgs);
			
			getFragmentManager().
					beginTransaction().
					add(R.id.container, gameFragment).
					commit();
		} else {
			GameFragment gameFragment = (GameFragment) getFragmentManager().findFragmentById(R.id.container);
			callbacks = gameFragment;
			
			gamePaused = savedInstanceState.getBoolean("gamePaused");
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("gamePaused", gamePaused);
	}
	
	private void resumeGame() {
		if(isGamePaused()) {
			// Let handler pop pause screen from stack
			setGamePaused(false); // TODO: Move this to a point where the game is
			// fully visible
			super.onBackPressed();
		}
	}
	
	@Override
	public void onBackPressed() {
		if(isGamePaused()) {
			// Let handler pop pause screen from stack
			setGamePaused(false); // TODO: Move this to a point where the game is
			// fully visible
			super.onBackPressed();
		} else if(isGameRunning()) {
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.setCustomAnimations(
					R.animator.pause_flip_in,
					R.animator.pause_flip_out,
					R.animator.pause_pop_flip_in,
					R.animator.pause_pop_flip_out);
			transaction.replace(R.id.container, new GamePauseFragment());
			transaction.addToBackStack(null);
			transaction.commit();
			setGamePaused(true);
		} else finish();
	}
	
	public static class GameFragment extends Fragment implements GameCallbacks {
		App app;
		
		GameDescriptor gameDescriptor;
		GameAdapter gameAdapter;
		FrameLayout frameLayout;
		Handler handler = new Handler();
		
		private boolean gamePaused;
		
		int position = 0;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			Bundle args = getArguments();
			if(args == null) return;
			gameDescriptor = args.getParcelable("gameDescriptor");
			
			
			if(savedInstanceState != null) {
				position = savedInstanceState.getInt("position");
				setGamePaused(savedInstanceState.getBoolean("gamePaused"));
				failedContacts = savedInstanceState.getParcelableArrayList("failedContacts");
			} else {
				failedContacts = new ArrayList<Game.GameFragment.QuestionFailData>();
			}
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			super.onCreateView(inflater, container, savedInstanceState);
			View rootView = inflater.inflate(R.layout.game_fragment, container, false);
			
			frameLayout = (FrameLayout) rootView.findViewById(R.id.pager);
			
			return rootView;
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			app = (App)getActivity().getApplication();
			
			if(gameAdapter == null)
				gameAdapter = getGameAdapter(gameDescriptor, savedInstanceState);
			else // Otherwise, replace context and app, to reduce dead objects
				gameAdapter.init(app, getActivity(), this);
			
			if(gameAdapter.getCount() == 0) {
				Toast.makeText(getActivity(), "No questions to ask!", Toast.LENGTH_LONG).show();
				getActivity().finish();
				return;
			}
			
			getFragmentManager().beginTransaction().
				replace(R.id.pager, gameAdapter.getFragment(position)).commit();
		}
		
		@Override
		public void onDestroyView() {
			super.onDestroyView();
		}
		
		/**
		 * Generates a {@link GameAdapter}
		 * @param descriptor
		 * @return
		 */
		GameAdapter getGameAdapter(GameDescriptor descriptor, Bundle savedInstanceState) {
			if(savedInstanceState == null) {
				return new GameAdapter(getActivity(), app, descriptor, this);
			} else {
				GameAdapter gameAdapter = savedInstanceState.getParcelable("gameAdapter");
				if(gameAdapter == null) return getGameAdapter(descriptor, null);
				gameAdapter.init(app, getActivity(), this);
				return gameAdapter;
			}
		}
		
		@Override
		public void onSaveInstanceState(Bundle outState) {
			super.onSaveInstanceState(outState);
			if(gameAdapter != null) {
				outState.putParcelable("gameAdapter", gameAdapter);
				outState.putInt("position", position);
				outState.putBoolean("gamePaused", gamePaused);
				outState.putParcelableArrayList("failedContacts", failedContacts);
			}
		}

		@Override
		public void choiceMade(Contact choice, boolean correct, boolean timeout, float timeTaken) {
			if(isGamePaused()) return;
			position++;
			if(position == gameAdapter.getCount()) {
				// Run transition to summary card.
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						SummaryFragment fragment = new SummaryFragment();
						Bundle args = new Bundle();
						args.putParcelableArrayList("failedContacts", failedContacts);
						fragment.setArguments(args);
						if(getFragmentManager() != null)
							getFragmentManager().beginTransaction().
								setCustomAnimations(R.animator.summary_card_in, R.animator.next_card_out).
								replace(R.id.container, fragment).
								commit();
						Game activity = (Game) getActivity();
						if(activity != null) {
							activity.setGameFinished();
						}
					}
				}, getResources().getInteger(
						correct ?
								R.integer.page_correct_wait_time :
								R.integer.page_incorrect_wait_time));
			} else {
				// Run transition to next card after the given delay.
				// The position has already been updated so we shouldn't have
				// to worry about view rotation etc., in which case
				// the next card would be shown immediately
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if(getFragmentManager() != null)
							getFragmentManager().beginTransaction().
								setCustomAnimations(R.animator.next_card_in, R.animator.next_card_out).
								replace(R.id.pager, gameAdapter.getFragment(position)).
								commit();
					}
				}, getResources().getInteger(
						correct ?
								R.integer.page_correct_wait_time :
								R.integer.page_incorrect_wait_time));
			}
			
			// TODO: Background this?
			Question question = gameAdapter.getItem(position-1);
			Contact contact = gameAdapter.getItem(position-1).getContact();
			// Add to persistent DB
			if(timeout) app.getDb().progress.addTimeout(
					contact, timeTaken);
			else if(correct) app.getDb().progress.addSuccess(
					contact, timeTaken);
			else app.getDb().progress.addFail(
					contact, choice, timeTaken);
			// Add to local data
			if(timeout) {
				QuestionFailData data = new QuestionFailData();
				data.question = question;
				failedContacts.add(data);
			} else if(correct) {
				
			} else {
				QuestionFailData data = new QuestionFailData();
				data.question = question;
				data.incorrectChoice = choice;
				failedContacts.add(data);
			}
		}

		boolean isGamePaused() {
			return gamePaused;
		}

		@Override
		public void setGamePaused(boolean gamePaused) {
			this.gamePaused = gamePaused;
		}
		
		private ArrayList<QuestionFailData> failedContacts;
		
		protected static class QuestionFailData implements Parcelable {
			
			public QuestionFailData() {}

			public Question question;
			/**
			 * The incorrect choice the user made. <code>null</code> indicates
			 * a timeout
			 */
			public Contact incorrectChoice;
			@Override
			public int describeContents() {
				return 0;
			}
			@Override
			public void writeToParcel(Parcel dest, int flags) {
				dest.writeParcelable(question, 0);
				dest.writeParcelable(incorrectChoice, 0);
			}
			
			public QuestionFailData(Parcel source) {
				question = source.readParcelable(Contact.class.getClassLoader());
				incorrectChoice = source.readParcelable(Contact.class.getClassLoader());
			}
			
			public static final Creator<QuestionFailData> CREATOR = new Creator<QuestionFailData>() {

				@Override
				public QuestionFailData createFromParcel(Parcel source) {
					return new QuestionFailData(source);
				}

				@Override
				public QuestionFailData[] newArray(int size) {
					return new QuestionFailData[size];
				}
			};
		}
	}
	
	public static class GamePauseFragment extends Fragment implements OnClickListener {
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
				// TODO: perform game status save and cleanup here!
				if(activity != null) activity.finish();
				break;
			}
		}
	}
	
	/**
	 * Shows summary information about the game just played
	 * @author william
	 *
	 */
	public static class SummaryFragment extends Fragment implements OnClickListener {
		
		GridView grid;
		ArrayList<QuestionFailData> failedContacts;
		App app; Context context;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			Bundle args = getArguments();
			failedContacts = args.getParcelableArrayList("failedContacts");
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			app = (App) activity.getApplication();
			context = activity.getBaseContext();
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.summary_fragment, container, false);
			
			grid = (GridView) rootView.findViewById(R.id.contactGrid);
			
			grid.setAdapter(new ContactAdapter(app, context, failedContacts));
			
			return rootView;
		}
		
		private static final class ContactAdapter extends BaseAdapter {
			
			private ArrayList<QuestionFailData> data;
			private LayoutInflater inflater;
			private App app;
			
			public ContactAdapter(App app, Context context, ArrayList<QuestionFailData> data) {
				this.data = data;
				this.inflater = LayoutInflater.from(context);
				this.app = app;
			}

			@Override
			public int getCount() {
				return data.size();
			}

			@Override
			public QuestionFailData getItem(int position) {
				return data.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
		        if (convertView == null) {
		            convertView = inflater.inflate(R.layout.contactgriditem, null);
		        }
		        
		        Question question = getItem(position).question;
		        Contact contact = question.getContact();
		        ImageView photo = (ImageView) convertView.findViewById(R.id.photo);
		        TextView attr1 = (TextView) convertView.findViewById(R.id.contact_attr1);
		        TextView attr2 = (TextView) convertView.findViewById(R.id.contact_attr2);
		        photo.setImageBitmap(contact.getPhoto(app.getPhotos()));

		        if(question.getQuestionFormat() == Question.FORMAT_TEXT)
		        	attr1.setText(contact.getTextField(question.getQuestionType()));
		        else attr1.setVisibility(View.GONE);

		        if(question.getAnswerFormat() == Question.FORMAT_TEXT)
		        	attr2.setText(contact.getTextField(question.getAnswerType()));
		        else attr2.setVisibility(View.GONE);
		        
				return convertView;
			}
			
		}
		
		@Override
		public void onResume() {
			super.onResume();
			getActivity().getActionBar().setTitle(R.string.game_summary);
		}

		@Override
		public void onClick(View v) {
		}
	}

	@Override
	public void choiceMade(Contact choice, boolean correct, boolean timeout, float timeTaken) {
		callbacks.choiceMade(choice, correct, timeout, timeTaken);
	}

	boolean isGamePaused() {
		return gamePaused;
	}

	@Override
	public void setGamePaused(boolean gamePaused) {
		this.gamePaused = gamePaused;
		callbacks.setGamePaused(gamePaused);
	}

	boolean isGameRunning() {
		return gameRunning;
	}

	void setGameFinished() {
		this.gameRunning = false;
	}
}
