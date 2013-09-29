package uk.digitalsquid.remme;

import java.util.ArrayList;
import java.util.Collections;

import uk.digitalsquid.remme.ingame.views.StatsBarView;
import uk.digitalsquid.remme.mgr.details.Contact;
import uk.digitalsquid.remme.stats.Stats;
import uk.digitalsquid.remme.stats.Stats.ContactStats;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Shows a list of contacts, rated from worst to best, according to how
 * good the user is at recognising that contact.
 * @author william
 *
 */
public class Leaderboard extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.leaderboard);
		
		LeaderboardFragment leaderboard = new LeaderboardFragment();
		getFragmentManager().
				beginTransaction().
				add(R.id.container, leaderboard).
				commit();
	}
	
	public static final class LeaderboardFragment extends Fragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		}
		
		private ListView leaderboard;
		
		private App app;
		private Stats stats;
		
		private LeaderboardAdapter adapter;
		
		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			app = (App) activity.getApplication();
			adapter = new LeaderboardAdapter(app, getActivity());
			stats = app.getStats();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			super.onCreateView(inflater, container, savedInstanceState);
			
			View rootView = inflater.inflate(R.layout.leaderboard_fragment, container, false);
			
			leaderboard = (ListView) rootView.findViewById(R.id.leaderboard);
			leaderboard.setAdapter(adapter);
			
			
			ArrayList<Contact> contacts = new ArrayList<Contact>(app.getContacts().getContacts());
			for(Contact contact : contacts) {
				contact.setCustomSortField((int) (stats.computeScoreWeight(contact) * 10000));
			}
			Collections.sort(contacts, Contact.CUSTOM_COMPARATOR);
			adapter.setContacts(contacts);
			
			return rootView;
		}
	}
	
	static final class LeaderboardAdapter extends BaseAdapter {
		
		private ArrayList<Contact> contacts = new ArrayList<Contact>();
		private LayoutInflater inflater;
		private App app;
		private Stats stats;
		private Resources res;
		
		public LeaderboardAdapter(App app, Context context) {
			this.app = app;
			inflater = LayoutInflater.from(context);
			stats = app.getStats();
			res = app.getResources();
		}

		@Override
		public int getCount() {
			return getContacts().size();
		}

		@Override
		public Contact getItem(int position) {
			return getContacts().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
	        if (convertView == null) {
	            convertView = inflater.inflate(R.layout.leaderboarditem, null);
	        }
	        
	        Contact contact = getItem(position);

	        ImageView photo = (ImageView) convertView.findViewById(R.id.photo);
	        TextView name = (TextView) convertView.findViewById(R.id.name);
	        TextView correctText = (TextView) convertView.findViewById(R.id.guess_correct_count);
	        TextView incorrectText = (TextView) convertView.findViewById(R.id.guess_incorrect_count);
	        TextView discardText = (TextView) convertView.findViewById(R.id.guess_discard_count);

	        photo.setImageBitmap(contact.getPhoto(app.getPhotos()));
	        name.setText(contact.getDisplayName());
	        
	        StatsBarView statsBars = (StatsBarView) convertView.findViewById(R.id.statsBarView);
	        
	        ContactStats contactStats = stats.getContactStats(contact);
	        // Reduce the total a bit to make the bars a bit bigger
	        if(contactStats != null) {
		        float total = (float)contactStats.getTotalTries() * 0.8f;
		        statsBars.setCorrect((float)contactStats.getSuccesses() / total);
		        statsBars.setIncorrect((float)contactStats.getFails() / total);
		        statsBars.setDiscard((float)contactStats.getDiscards() / total);
		        
		        correctText.setText(res.getString(R.string.guess_correct_count, contactStats.getSuccesses()));
		        incorrectText.setText(res.getString(R.string.guess_incorrect_count, contactStats.getFails()));
		        discardText.setText(res.getString(R.string.guess_discard_count, contactStats.getDiscards()));
	        } else {
		        statsBars.setCorrect(0);
		        statsBars.setIncorrect(0);
		        statsBars.setDiscard(0);
		        
		        correctText.setText(res.getString(R.string.guess_correct_count, 0));
		        incorrectText.setText(res.getString(R.string.guess_incorrect_count, 0));
		        discardText.setText(res.getString(R.string.guess_discard_count, 0));
	        }
	        
			return convertView;
		}

		public ArrayList<Contact> getContacts() {
			return contacts;
		}

		public void setContacts(ArrayList<Contact> contacts) {
			this.contacts = contacts;
			notifyDataSetChanged();
		}
		
	}
}
