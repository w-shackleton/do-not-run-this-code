package uk.digitalsquid.contactrecall.ingame;

import java.util.ArrayList;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.R;
import uk.digitalsquid.contactrecall.mgr.details.DataItem;
import uk.digitalsquid.contactrecall.mgr.details.DataItem.DataItemAdapter;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Allows the user to tell the app about an error in their contacts.
 * @author william
 *
 */
public class DataErrorFragment extends Fragment implements OnItemSelectedListener, OnItemClickListener {
	
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