package uk.digitalsquid.contactrecall;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import uk.digitalsquid.contactrecall.mgr.GroupManager.Group;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Shows the contact list, allows contact viewing and editing.
 * @author william
 *
 */
public class ContactViewer extends Activity {
	private App app;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		app = (App) getApplication();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.contactviewermenu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.editgroups:
	    	showDialog(DIALOG_GROUPS);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private static final int DIALOG_GROUPS = 1;
	
	public Dialog onCreateDialog(int id) {
		switch(id) {
		case DIALOG_GROUPS:
			Builder builder = new Builder(this);
			
			builder.setTitle(R.string.changegroups);
			
			final Map<Integer, Group> groups = app.getGroups().getContactGroups();
			String[] names = new String[groups.size()];
			final boolean[] selecteds = new boolean[groups.size()];
			int i = 0;
			for(Group g : groups.values()) {
				names[i] = g.name;
				selecteds[i] = g.visible;
				i++;
			}
			
			builder.setMultiChoiceItems(names, selecteds, new OnMultiChoiceClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					// selection made.
					selecteds[which] = isChecked;
				}
			});
			builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// OK Selected
					// Create map of indices to new selected values
					List<Integer> newSelectedValues = new LinkedList<Integer>();
					int i = 0;
					for(Group g : groups.values()) {
						if(selecteds[i]) {
							newSelectedValues.add(g.id);
						}
						i++;
					}
					app.getDb().groups.setSelectedGroupIds(newSelectedValues);
				}
			});
			
			return builder.create();
		default:
			return null;
		}
	}
}
