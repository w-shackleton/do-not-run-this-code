package uk.digitalsquid.remme;

import java.util.ArrayList;

import uk.digitalsquid.remme.mgr.GroupManager.AccountDetails;
import uk.digitalsquid.remme.mgr.GroupManager.Group;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public final class GroupSelectDialog extends DialogFragment {

    static GroupSelectDialog newInstance() {
        GroupSelectDialog f = new GroupSelectDialog();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	final App app = (App) getActivity().getApplication();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View rootView = getActivity().getLayoutInflater()
        		.inflate(R.layout.group_select_fragment, null);
        builder.setView(rootView);
        builder.setMessage(R.string.dialog_choose_groups)
               .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                   }
               })
               .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                   }
               });
        
        // Construct rootView innards
        
        ListView list = (ListView) rootView.findViewById(R.id.listView);
        GroupAdapter adapter = new GroupAdapter(getActivity());
        adapter.setGroups(app.getGroups().getAccountDetails());
        list.setAdapter(adapter);
        
        return builder.create();
    }
    
    private static final class GroupAdapter extends BaseAdapter {
    	
    	private ArrayList<AccountDetails> list;
    	private final LayoutInflater inflater;
    	private final Context context;
    	
    	public GroupAdapter(Context context) {
    		inflater = LayoutInflater.from(context);
    		this.context = context;
    	}

		@Override
		public int getCount() {
			if(list == null) return 0;
			int acc = 0;
			for(AccountDetails details : list) {
				acc++;
				acc += details.getGroups().size();
			}
			return acc;
		}

		@Override
		public Object getItem(int position) {
			if(list == null) return null;
			for(AccountDetails details : list) {
				if(position == 0) return details;
				position--;
				if(position < details.getGroups().size())
					return details.getGroups().get(position);
				position -= details.getGroups().size();
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Object item = getItem(position);
			if(item instanceof AccountDetails) {
				AccountDetails details = (AccountDetails) item;
				View rootView = inflater.inflate(R.layout.group_select_fragment_item_top, null);
				TextView title = (TextView) rootView.findViewById(R.id.title);
				TextView desc = (TextView) rootView.findViewById(R.id.description);
				ImageView icon = (ImageView) rootView.findViewById(R.id.icon);
				
				title.setText(details.getLabel(context));
				desc.setText(details.getAccountName());
				icon.setImageDrawable(details.getIcon(context));

				return rootView;
			} else if(item instanceof Group) {
				Group group = (Group) item;
				View rootView = inflater.inflate(R.layout.group_select_fragment_item_bottom, null);
				TextView title = (TextView) rootView.findViewById(R.id.title);
				
				title.setText(group.name);

				return rootView;
			}
			return new View(context);
		}
    	
		public void setGroups(ArrayList<AccountDetails> list) {
			this.list = list;
			notifyDataSetChanged();
		}
    };
}
