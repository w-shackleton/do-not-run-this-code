package uk.digitalsquid.contactrecall.ingame;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.GameDescriptor;
import uk.digitalsquid.contactrecall.R;
import uk.digitalsquid.contactrecall.ingame.TimerView.OnFinishedListener;
import uk.digitalsquid.contactrecall.mgr.Question;
import uk.digitalsquid.contactrecall.mgr.details.Contact;
import uk.digitalsquid.contactrecall.mgr.details.DataItem;
import uk.digitalsquid.contactrecall.mgr.details.DataItem.DataItemAdapter;
import uk.digitalsquid.contactrecall.misc.Config;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
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
		if(getFragmentManager().getBackStackEntryCount() > 0) {
			// Let handler pop pause screen from stack
			if(getFragmentManager().getBackStackEntryCount() == 1)
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
	
	public static class GameFragment extends Fragment implements GameCallbacks, OnFinishedListener {
		App app;
		
		GameDescriptor gameDescriptor;
		GameAdapter gameAdapter;
		FrameLayout frameLayout;
		Handler handler = new Handler();
		
		private TimerView timer;
		
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
				failedContacts = new ArrayList<QuestionFailData>();
			}
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			super.onCreateView(inflater, container, savedInstanceState);
			View rootView = inflater.inflate(R.layout.game_fragment, container, false);
			
			frameLayout = (FrameLayout) rootView.findViewById(R.id.pager);
			
			timer = (TimerView)rootView.findViewById(R.id.timer);
	        if(gameDescriptor.hasTimer()) {
		        timer.setOnFinishedListener(this);
		        timer.setTotalTime(gameDescriptor.getMaxTime());
		        timer.setTextAsCountdown();
	        } else {
	        	timer.setVisibility(View.GONE);
	        	timer = null; // Done with timer now
	        }
			
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
			timer.start();
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
			postAdvanceQuestion(correct);
			
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
				data.contact = question.getContact();
				failedContacts.add(data);
			} else if(correct) {
				
			} else {
				QuestionFailData data = new QuestionFailData();
				data.question = question;
				data.contact = question.getContact();
				data.incorrectChoice = choice;
				failedContacts.add(data);
			}
		}

		@Override
		public void pairingChoiceMade(ArrayList<Contact> correct,
				ArrayList<Pair<Contact, Contact>> incorrect, ArrayList<Contact> timeout,
				float timeTaken) {
			if(isGamePaused()) return;
			position++;
			// true if all is fully correct
			boolean allCorrect = incorrect.size() == 0 && timeout.size() == 0;
			postAdvanceQuestion(allCorrect);
			
			// TODO: Background this?
			Question question = gameAdapter.getItem(position-1);
			// Add each type of result to persistent DB
			for(Contact contact : correct) {
				app.getDb().progress.addSuccess(contact, timeTaken);
			}
			for(Contact contact : timeout) {
				app.getDb().progress.addTimeout(contact, timeTaken);

				QuestionFailData data = new QuestionFailData();
				data.question = question;
				data.contact = contact;
				failedContacts.add(data);
			}
			for(Pair<Contact, Contact> pair : incorrect) {
				app.getDb().progress.addFail(pair.first, pair.second, timeTaken);

				QuestionFailData data = new QuestionFailData();
				data.question = question;
				data.contact = pair.first;
				data.incorrectChoice = pair.second;
				failedContacts.add(data);
			}
		}
		
		/**
		 * Changes the view to the next question. Does not change any game data.
		 * @param correct
		 */
		private void postAdvanceQuestion(boolean correct) {
			if(position >= gameAdapter.getCount()) {
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
		}

		boolean isGamePaused() {
			return gamePaused;
		}

		@Override
		public void setGamePaused(boolean gamePaused) {
			this.gamePaused = gamePaused;
			if(timer != null) timer.setPaused(gamePaused);
		}
		
		private ArrayList<QuestionFailData> failedContacts;
		
		@Override
		public void dataErrorFound(ArrayList<DataItem> possibleErrors) {
			Log.e(TAG, "dataErrorFound called inside fragment");
		}

		@Override
		public void onTimerFinished(TimerView view) {
			position = gameAdapter.getCount();
			postAdvanceQuestion(true);
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
			// Remove duplicates through a linkedHashSet
			ArrayList<QuestionFailData> data = args.getParcelableArrayList("failedContacts");
			Set<QuestionFailData> dataSet = new LinkedHashSet<QuestionFailData>(data);
			failedContacts = new ArrayList<QuestionFailData>(dataSet);
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
		        Contact contact = getItem(position).contact;
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
	
	/**
	 * Allows the user to tell the app about an error in their contacts.
	 * @author william
	 *
	 */
	public static class DataErrorFragment extends Fragment implements OnItemSelectedListener, OnItemClickListener {
		
		App app;
		Context context;
		
		private ListView dataList;
		private ArrayList<DataItem> possibleErrors;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			app = (App) activity.getApplication();
			context = activity.getBaseContext();
			Bundle args = getArguments();
			possibleErrors = args.getParcelableArrayList("possibleErrors");
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.data_error, container, false);
			dataList = (ListView) rootView.findViewById(R.id.dataList);
			DataItemAdapter adapter = new DataItemAdapter(app, context, possibleErrors, R.layout.data_item_text, R.layout.data_item_image);
			dataList.setAdapter(adapter);
			dataList.setOnItemSelectedListener(this);
			dataList.setOnItemClickListener(this);
			return rootView;
		}

		@Override
		public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
			DataItem error = possibleErrors.get(position);
			if(error == null) return;
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			DataErrorConfirmationFragment fragment = new DataErrorConfirmationFragment();
			Bundle args = new Bundle();
			args.putParcelable("error", error);
			fragment.setArguments(args);
			// TODO: Different animations
			transaction.setCustomAnimations(
					R.animator.pause_flip_in,
					R.animator.pause_flip_out,
					R.animator.pause_pop_flip_in,
					R.animator.pause_pop_flip_out);
			transaction.replace(R.id.container, fragment);
			// User can press back to get back
			transaction.addToBackStack(null);
			transaction.commit();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) { }

		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
			onItemSelected(adapter, view, position, id);
		}
	}

	/**
	 * Confirms what the user would like to do about the data error they just selected
	 * @author william
	 *
	 */
	public static class DataErrorConfirmationFragment extends Fragment implements OnClickListener {
		
		App app;
		Context context;
		
		private DataItem error;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			app = (App) activity.getApplication();
			context = activity.getBaseContext();
			Bundle args = getArguments();
			error = args.getParcelable("error");
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.data_error_confirm, container, false);
			FrameLayout detailContainer = (FrameLayout) rootView.findViewById(R.id.detail_container);
			View detailView;
			switch(error.getFormat()) {
			case Question.FORMAT_IMAGE:
				detailView = inflater.inflate(R.layout.data_item_image, detailContainer);
		        ImageView photo = (ImageView) detailView.findViewById(R.id.photo);
		        photo.setImageBitmap(error.getContact().getPhoto(app.getPhotos()));
				break;
			case Question.FORMAT_TEXT:
			default:
				detailView = inflater.inflate(R.layout.data_item_text, detailContainer);
		        TextView text = (TextView) detailView.findViewById(R.id.text);
		        text.setText(error.getContact().getTextField(error.getField()));
				break;
			}
			rootView.findViewById(R.id.hide_contact).setOnClickListener(this);
			rootView.findViewById(R.id.delete_detail).setOnClickListener(this);
			rootView.findViewById(R.id.hide_detail).setOnClickListener(this);
			rootView.findViewById(R.id.edit_contact).setOnClickListener(this);
			rootView.findViewById(R.id.cancel).setOnClickListener(this);
			return rootView;
		}

		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.hide_contact:
				app.getDb().hidden.addHiddenContact(error);
				Toast.makeText(context, R.string.contact_hidden, Toast.LENGTH_SHORT).show();
				break;
			case R.id.delete_detail:
				Toast.makeText(context, "Not implemented!", Toast.LENGTH_LONG).show();
				break;
			case R.id.hide_detail:
				Toast.makeText(context, R.string.contact_hidden, Toast.LENGTH_SHORT).show();
				app.getDb().hidden.addHiddenField(error);
				break;
			case R.id.edit_contact:
				Intent intent = new Intent(Intent.ACTION_EDIT);
				int id = error.getContact().getId();
				intent.setData(ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id));
				startActivity(intent);
				break;
			case R.id.cancel:
				break;
			}
			getFragmentManager().popBackStack();
		}
	}

	@Override
	public void choiceMade(Contact choice, boolean correct, boolean timeout, float timeTaken) {
		callbacks.choiceMade(choice, correct, timeout, timeTaken);
	}
	@Override
	public void pairingChoiceMade(ArrayList<Contact> correct,
			ArrayList<Pair<Contact, Contact>> incorrect, ArrayList<Contact> timeout,
			float timeTaken) {
		callbacks.pairingChoiceMade(correct, incorrect, timeout, timeTaken);
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

	/**
	 * Show data error screen
	 */
	@Override
	public void dataErrorFound(ArrayList<DataItem> possibleErrors) {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		DataErrorFragment fragment = new DataErrorFragment();
		Bundle args = new Bundle();
		args.putParcelableArrayList("possibleErrors", possibleErrors);
		fragment.setArguments(args);
		// TODO: Different animations
		transaction.setCustomAnimations(
				R.animator.pause_flip_in,
				R.animator.pause_flip_out,
				R.animator.pause_pop_flip_in,
				R.animator.pause_pop_flip_out);
		transaction.replace(R.id.container, fragment);
		// User can press back to get back
		transaction.addToBackStack(null);
		transaction.commit();
		setGamePaused(true);
	}
}
