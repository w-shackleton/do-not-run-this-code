package uk.digitalsquid.contactrecall;

import java.util.ArrayList;

import uk.digitalsquid.contactrecall.mgr.details.Contact;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
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
		
		private LeaderboardAdapter adapter;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			super.onCreateView(inflater, container, savedInstanceState);
			
			View rootView = inflater.inflate(R.layout.leaderboard_fragment, container);
			
			leaderboard = (ListView) rootView.findViewById(R.id.leaderboard);
			
			return rootView;
		}
		
		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			adapter = new LeaderboardAdapter((App) activity.getApplication(),
					getActivity());
			leaderboard.setAdapter(adapter);
		}
	}
	
	static final class LeaderboardAdapter extends BaseAdapter {
		
		private ArrayList<Contact> contacts = new ArrayList<Contact>();
		private LayoutInflater inflater;
		private App app;
		
		public LeaderboardAdapter(App app, Context context) {
			this.app = app;
			inflater = LayoutInflater.from(context);
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
	            convertView = inflater.inflate(R.layout.contactgriditem, null);
	        }
	        
	        Contact contact = getItem(position);
	        ImageView photo = (ImageView) convertView.findViewById(R.id.photo);
	        TextView name = (TextView) convertView.findViewById(R.id.name);
	        photo.setImageBitmap(contact.getPhoto(app.getPhotos()));

	        name.setText(contact.getDisplayName());
	        
			return convertView;
		}

		public ArrayList<Contact> getContacts() {
			return contacts;
		}

		public void setContacts(ArrayList<Contact> contacts) {
			this.contacts = contacts;
		}
		
	}
}
