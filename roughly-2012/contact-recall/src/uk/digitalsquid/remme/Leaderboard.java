package uk.digitalsquid.remme;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;

import uk.digitalsquid.remme.ingame.views.AsyncImageView;
import uk.digitalsquid.remme.ingame.views.ImageLoader;
import uk.digitalsquid.remme.ingame.views.StatsBarView;
import uk.digitalsquid.remme.mgr.details.Contact;
import uk.digitalsquid.remme.stats.Stats;
import uk.digitalsquid.remme.stats.Stats.ContactStats;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.Intents.Insert;
import android.provider.ContactsContract.QuickContact;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

/**
 * Shows a list of contacts, rated from worst to best, according to how
 * good the user is at recognising that contact.
 * @author william
 *
 */
public class Leaderboard extends Activity {

	private static final int ACTIVITY_PICTURE_RESULT = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.leaderboard);
		
		if(savedInstanceState != null)
			currentContact = savedInstanceState.getParcelable("currentContact");
		
		LeaderboardFragment leaderboard = new LeaderboardFragment();
		getFragmentManager().
				beginTransaction().
				add(R.id.container, leaderboard).
				commit();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable("currentContact", currentContact);
	}
	
	public static final class LeaderboardFragment extends Fragment implements OnItemClickListener {
		private ListView leaderboard;
		
		private App app;
		private Stats stats;
		
		private LeaderboardAdapter adapter;
		private boolean sortAscending;
		private int sortType;
		
		static final int SORT_SCORE = 1;
		static final int SORT_NAME = 2;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setHasOptionsMenu(true);
			if(savedInstanceState != null) {
				sortType = savedInstanceState.getInt("sortType");
				sortAscending = savedInstanceState.getBoolean("sortAscending");
			} else {
				sortType = SORT_SCORE;
				sortAscending = true;
			}
		}
		
		@Override
		public void onSaveInstanceState(Bundle out) {
			super.onSaveInstanceState(out);
			out.putInt("sortType", sortType);
			out.putBoolean("sortAscending", sortAscending);
		}
		
		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			app = (App) activity.getApplication();
			Leaderboard act = (Leaderboard) activity;
			adapter = act.new LeaderboardAdapter(app, getActivity());
			stats = app.getStats();
		}
		
		MenuItem sortByScore, sortByName;
		
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) { 
			super.onCreateOptionsMenu(menu, inflater);
			inflater.inflate(R.menu.leaderboard, menu);
			sortByScore = menu.findItem(R.id.sortByScore);
			sortByName = menu.findItem(R.id.sortByName);
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			super.onOptionsItemSelected(item);
			switch(item.getItemId()) {
			case R.id.sortByScore:
				sortType = SORT_SCORE;
				sortByScore.setVisible(false);
				sortByName.setVisible(true);
				if(adapter != null) adapter.setOrder(sortType, sortAscending);
				return true;
			case R.id.sortByName:
				sortType = SORT_NAME;
				sortByScore.setVisible(true);
				sortByName.setVisible(false);
				if(adapter != null) adapter.setOrder(sortType, sortAscending);
				return true;
			case R.id.changeSortDirection:
				sortAscending = !sortAscending;
				if(adapter != null) adapter.setOrder(sortType, sortAscending);
				return true;
			}
			return false;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			super.onCreateView(inflater, container, savedInstanceState);
			
			View rootView = inflater.inflate(R.layout.leaderboard_fragment, container, false);
			
			leaderboard = (ListView) rootView.findViewById(R.id.leaderboard);
			leaderboard.setAdapter(adapter);
			leaderboard.setOnItemClickListener(this);
			
			ArrayList<Contact> contacts = new ArrayList<Contact>(app.getContacts().getContacts());
			for(Contact contact : contacts) {
				contact.setCustomSortField((int) (stats.computeScoreWeight(contact) * 10000));
			}
			Collections.sort(contacts, Contact.CUSTOM_COMPARATOR);
			adapter.setContacts(contacts);
			
			return rootView;
		}

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position,
				long id) {
			Contact contact = adapter.getItem(position);
			View target = view.findViewById(R.id.photo);
			QuickContact.showQuickContact(
					getActivity(),
					target,
					contact.getLookupUri(),
					QuickContact.MODE_LARGE,
					null);
		}
	}
	
	final class LeaderboardAdapter extends BaseAdapter implements OnClickListener, OnMenuItemClickListener {
		
		private ArrayList<Contact> contacts = new ArrayList<Contact>();
		private final LayoutInflater inflater;
		private final App app;
		private final Context context;
		private final Stats stats;
		private final Resources res;
		private final Bitmap noPhoto;
		
		public LeaderboardAdapter(App app, Context context) {
			this.app = app;
			this.context = context;
			inflater = LayoutInflater.from(context);
			stats = app.getStats();
			res = app.getResources();
			noPhoto = BitmapFactory.decodeResource(res, R.drawable.no_photo);
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
	        if(convertView instanceof ViewGroup)
		        ((ViewGroup)convertView).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
	        
	        final Contact contact = getItem(position);
	        
	        ImageButton more = (ImageButton) convertView.findViewById(R.id.more);
	        more.setTag(contact);
	        more.setOnClickListener(this);

	        AsyncImageView photo = (AsyncImageView) convertView.findViewById(R.id.photo);
	        TextView name = (TextView) convertView.findViewById(R.id.name);
	        TextView correctText = (TextView) convertView.findViewById(R.id.guess_correct_count);
	        TextView incorrectText = (TextView) convertView.findViewById(R.id.guess_incorrect_count);
	        TextView discardText = (TextView) convertView.findViewById(R.id.guess_discard_count);

	        photo.setImageBitmap(noPhoto);
	        photo.setImageBitmapAsync(new ImageLoader<AsyncImageView>() {
				@Override
				public Bitmap loadImage(Context context) {
			        Bitmap contactPhoto = contact.getPhoto(app.getPhotos());
			        if(contactPhoto == null) return noPhoto;
			        return contactPhoto;
				}
				@Override public void onImageLoaded(AsyncImageView asyncImageView) { }
			});

	        name.setText(contact.getDisplayName());
	        
	        StatsBarView statsBars = (StatsBarView) convertView.findViewById(R.id.statsBarView);
	        
	        ContactStats contactStats = stats.getContactStats(contact);
	        if(contactStats != null) {
		        // Reduce the total a bit to make the bars a bit bigger
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
		
		public void setOrder(int sortType, boolean ascending) {
			switch(sortType) {
			case LeaderboardFragment.SORT_NAME:
				Collections.sort(contacts, Contact.CONTACT_NAME_COMPARATOR);
				break;
			case LeaderboardFragment.SORT_SCORE:
				Collections.sort(contacts, Contact.CUSTOM_COMPARATOR);
				break;
			}
			if(!ascending) Collections.reverse(contacts);
			notifyDataSetChanged();
		}
		
		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.more:
				Object tag = v.getTag();
				if(tag instanceof Contact) {
					Contact contact = (Contact) tag;
					currentContact = contact;
					PopupMenu popup = new PopupMenu(context, v);
					popup.setOnMenuItemClickListener(this);
					popup.inflate(R.menu.contact_more);
					popup.show();
				}
				break;
			}
		}

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			switch(item.getItemId()) {
			case R.id.setPhoto:
				// Start camera activity
				Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(camera, Leaderboard.ACTIVITY_PICTURE_RESULT);
				
				break;
			}
			return false;
		}
	}
	
	/**
	 * The {@link Contact} whose menu is currently open
	 */
	private Contact currentContact;

	// Picture capture finished
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	
		switch(requestCode) {
		case ACTIVITY_PICTURE_RESULT:
			switch(resultCode) {
			case RESULT_OK:
				if(data == null) break;
				if(data.getExtras() == null) break;
				Bitmap image = (Bitmap) data.getExtras().get("data");
				
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				image.compress(Bitmap.CompressFormat.PNG, 100, stream);
				byte[] byteArray = stream.toByteArray();

				Contact contact = currentContact;
			    Uri selectedContactUri = contact.getLookupUri();
			    // Creates a new Intent to edit a contact
			    Intent editIntent = new Intent(Intent.ACTION_EDIT);
				editIntent.putExtra("finishActivityOnSaveCompleted", true);
				
				// Insert new data
				ArrayList<ContentValues> contentValues = new ArrayList<ContentValues>();
				// Photo
				ContentValues row1 = new ContentValues();
				row1.put(Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
				row1.put(ContactsContract.CommonDataKinds.Photo.PHOTO, byteArray);
				contentValues.add(row1);

			    editIntent.setDataAndType(selectedContactUri, Contacts.CONTENT_ITEM_TYPE);
				editIntent.putParcelableArrayListExtra(Insert.DATA, contentValues);
			    Leaderboard.this.startActivity(editIntent);
				break;
			default:
				setResult(RESULT_CANCELED);
				finish();
				break;
			}
			break;
		}
	}
}
