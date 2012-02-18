package uk.digitalsquid.spacegame;

import java.util.ArrayList;

import uk.digitalsquid.spacegame.levels.LevelManager.LevelExtendedInfo;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class LevelSelect extends Activity implements OnItemClickListener, OnItemSelectedListener {
	protected Handler parentHandler;
	
	protected App app;
	
	protected LevelSelectAdapter adapter;
	
	private TextView title, authour, time;
	
	public static final String LEVELSET_EXTRA = "uk.digitalsquid.spacegame.LevelSelect.LevelSet";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (App)getApplication();
		
		setContentView(R.layout.levelselect);
		
		ListView lv = (ListView) findViewById(R.id.levelselect_list);
		title = (TextView) findViewById(R.id.levelselect_itemtitle);
		authour = (TextView) findViewById(R.id.levelselect_itemauthor);
		time = (TextView) findViewById(R.id.levelselect_itemtime);
		
		String levelset = getIntent().getExtras().getString(LEVELSET_EXTRA);
		
		adapter = new LevelSelectAdapter(this, app.getLevelManager().getLevelsFromSet(levelset));
		lv.requestFocus();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		lv.setOnItemSelectedListener(this);
		
		title.setText("");
		authour.setText("");
		time.setText("");
	}
	
	private static class LevelSelectAdapter extends BaseAdapter {
		protected LayoutInflater inflater;
		protected ArrayList<LevelExtendedInfo> items;
		
		public LevelSelectAdapter(Context context, ArrayList<LevelExtendedInfo> items) {
			inflater = LayoutInflater.from(context);
			this.items = items;
		}
		
		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public LevelExtendedInfo getItem(int arg0) {
			return items.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				convertView = inflater.inflate(R.layout.levelselectitem, null); // Construct from XML if view is non-existent
			}
			LevelExtendedInfo currItem = items.get(position);
			if(currItem != null) {
				TextView title = (TextView) convertView.findViewById(R.id.levelselectitem_text);
				title.setText(currItem.filename + " "  + currItem.fileNumber + (currItem.name.equals("") ? "" : (" - " + currItem.name)));
				ImageView img = (ImageView) convertView.findViewById(R.id.levelselectitem_statusimg);
				if(currItem.completed) {
					img.setImageResource(android.R.drawable.presence_online);
				} else {
					if(currItem.playable) {
						img.setImageResource(android.R.drawable.presence_away);
					} else {
						img.setImageResource(android.R.drawable.presence_offline);
					}
				}
			}
			convertView.setEnabled(currItem.playable);
			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(adapter.getItem(arg2).playable) {
			Intent intent = new Intent(this, Game.class);
			intent.putExtra(Game.LEVELINFO_EXTRA, adapter.getItem(arg2));
			
			startActivity(intent);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		LevelExtendedInfo info = adapter.items.get(arg2);
		title.setText(info.name);
		authour.setText(info.author);
		
		if(info.completed) {
			int deci = (info.time / 100) % 10;
			int secs = (info.time / 1000) % 60;
			int mins = (info.time / 1000) / 60;
			time.setText(String.format("%d:%d.%d", mins, secs, deci));
		} else {
			time.setText("Not yet completed");
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		title.setText("");
		authour.setText("");
		time.setText("");
	}
}
