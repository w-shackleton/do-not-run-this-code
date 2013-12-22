package uk.digitalsquid.remme.ingame;

import uk.digitalsquid.remme.App;
import uk.digitalsquid.remme.R;
import uk.digitalsquid.remme.mgr.Question;
import uk.digitalsquid.remme.mgr.details.DataItem;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Confirms what the user would like to do about the data error they just selected
 * @author william
 *
 */
public class DataErrorConfirmationFragment extends Fragment implements OnClickListener {
	
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
		case R.id.hide_detail:
			Toast.makeText(context, R.string.contact_hidden, Toast.LENGTH_SHORT).show();
			app.getDb().hidden.addHiddenField(error);
			break;
		case R.id.edit_contact:
			Intent intent = new Intent(Intent.ACTION_EDIT);
			intent.setData(error.getContact().getLookupUri());
			intent.putExtra("finishActivityOnSaveCompleted", true);
			startActivity(intent);
			break;
		case R.id.cancel:
			break;
		}
		getFragmentManager().popBackStack();
	}
}