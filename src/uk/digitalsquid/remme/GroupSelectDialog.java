package uk.digitalsquid.remme;

import java.util.ArrayList;

import uk.digitalsquid.remme.mgr.GroupManager.AccountDetails;
import uk.digitalsquid.remme.mgr.GroupManager.Group;
import uk.digitalsquid.remme.misc.Config;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public final class GroupSelectDialog extends DialogFragment implements Config {

    static GroupSelectDialog newInstance() {
        GroupSelectDialog f = new GroupSelectDialog();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    GroupAdapter adapter;
    
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
                	   if(adapter == null) return;
                	   ArrayList<AccountDetails> accounts = adapter.getGroups();
                	   app.getDb().groups.setVisibleAccountsAndGroups(accounts);
                	   app.getContacts().beginBackgroundReload();
                   }
               })
               .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                   }
               });
        
        // Construct rootView innards
        
        ListView list = (ListView) rootView.findViewById(R.id.listView);
        adapter = new GroupAdapter(getActivity());
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
				final AccountDetails details = (AccountDetails) item;
				View rootView = inflater.inflate(R.layout.group_select_fragment_item_top, null);
				TextView title = (TextView) rootView.findViewById(R.id.title);
				TextView desc = (TextView) rootView.findViewById(R.id.description);
				ImageView icon = (ImageView) rootView.findViewById(R.id.icon);
				final CheckBox check = (CheckBox) rootView.findViewById(R.id.checkBox);
				
				if(details.isUserVisible())
					check.setChecked(true);
				check.setFocusable(false);
				check.setClickable(false);
				
				title.setText(details.getLabel(context));
				desc.setText(details.getAccountName());
				icon.setImageDrawable(details.getIcon(context));
				
				rootView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						check.toggle();
						Log.d(TAG, "Setting " +
						details.getAccountName() + " " +
								details.getPackageName() + " to " + check.isChecked());
						details.setUserVisible(check.isChecked());
					}
				});
				return rootView;
			} else if(item instanceof Group) {
				final Group group = (Group) item;
				View rootView = inflater.inflate(R.layout.group_select_fragment_item_bottom, null);
				TextView title = (TextView) rootView.findViewById(R.id.title);
				final CheckBox check = (CheckBox) rootView.findViewById(R.id.checkBox);
				
				title.setText(group.name);
				if(group.isUserVisible())
					check.setChecked(true);
				check.setFocusable(false);
				check.setClickable(false);
				
				rootView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						check.toggle();
						group.setUserVisible(check.isChecked());
					}
				});

				return rootView;
			}
			return new View(context);
		}
    	
		public void setGroups(ArrayList<AccountDetails> list) {
			this.list = list;
			notifyDataSetChanged();
		}
		
		public ArrayList<AccountDetails> getGroups() {
			return list;
		}
    };
}
