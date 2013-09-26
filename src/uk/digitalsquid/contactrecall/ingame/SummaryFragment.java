package uk.digitalsquid.contactrecall.ingame;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.R;
import uk.digitalsquid.contactrecall.mgr.Question;
import uk.digitalsquid.contactrecall.mgr.details.Contact;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Shows summary information about the game just played
 * @author william
 *
 */
public class SummaryFragment extends Fragment implements OnClickListener {
	
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
	        
	        // Note: we currently print display name in attr1, and something else in attr2

        	attr1.setText(contact.getDisplayName());

	        if(question.getQuestionFormat() == Question.FORMAT_TEXT
	        		&& question.getQuestionType() != Question.FIELD_DISPLAY_NAME)
	        	attr2.setText(contact.getTextField(question.getQuestionType()));
	        else if(question.getAnswerFormat() == Question.FORMAT_TEXT
	        		&& question.getAnswerType() != Question.FIELD_DISPLAY_NAME)
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