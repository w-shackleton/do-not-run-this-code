package uk.digitalsquid.spacegame.views;

import java.util.ArrayList;

import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.Spacegame;
import uk.digitalsquid.spacegame.levels.LevelManager;
import uk.digitalsquid.spacegame.levels.LevelManager.LevelExtendedInfo;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

public class LevelSelectLayout extends FrameLayout implements KeyInput, OnItemClickListener, OnItemSelectedListener
{
	protected Context context;
	protected Handler parentHandler;
	
	protected LevelManager lmanager;
	
	protected LevelSelectAdapter adapter;
	
	private TextView title, authour;
	
	public LevelSelectLayout(Context context, AttributeSet attrs, Handler handler, LevelManager lmanager, String levelset)
	{
		super(context, attrs);
		this.context = context;
		parentHandler = handler;
		this.lmanager = lmanager;
		
		LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.levelselect, this);
		
		Animation fadein = AnimationUtils.loadAnimation(context, R.anim.fadein);
		startAnimation(fadein);
		
		ListView lv = (ListView) findViewById(R.id.levelselect_list);
		title = (TextView) findViewById(R.id.levelselect_itemtitle);
		authour = (TextView) findViewById(R.id.levelselect_itemauthor);
		
		adapter = new LevelSelectAdapter(context, lmanager.GetLevelsFromSet(levelset));
		lv.requestFocus();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		lv.setOnItemSelectedListener(this);
		
		title.setText("");
		authour.setText("");
	}

	@Override
	public void onBackPress()
	{
		Animation fadeout = AnimationUtils.loadAnimation(context, R.anim.fadeout);
		startAnimation(fadeout);
		
		Message msg = Message.obtain();
		msg.what = Spacegame.MESSAGE_RETURN_TO_LEVELSET_SELECT;
		parentHandler.sendMessageAtTime(msg, SystemClock.uptimeMillis() + fadeout.getDuration());
	}
	
	private static class LevelSelectAdapter extends BaseAdapter
	{
		protected LayoutInflater inflater;
		protected ArrayList<LevelExtendedInfo> items;
		
		public LevelSelectAdapter(Context context, ArrayList<LevelExtendedInfo> items)
		{
			inflater = LayoutInflater.from(context);
			this.items = items;
		}
		
		@Override
		public int getCount()
		{
			return items.size();
		}

		@Override
		public LevelExtendedInfo getItem(int arg0)
		{
			return items.get(arg0);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if(convertView == null)
			{
				convertView = inflater.inflate(R.layout.levelselectitem, null); // Construct from XML if view is non-existent
			}
			LevelExtendedInfo currItem = items.get(position);
			if(currItem != null)
			{
				TextView title = (TextView) convertView.findViewById(R.id.levelselectitem_text);
				title.setText(currItem.filename + " "  + currItem.fileNumber + (currItem.name.equals("") ? "" : (" - " + currItem.name)));
			}
			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		Animation fadeout = AnimationUtils.loadAnimation(context, R.anim.fadeout);
		startAnimation(fadeout);
		
		Message m = Message.obtain();
		m.what = Spacegame.MESSAGE_START_LEVEL;
		m.obj = adapter.getItem(arg2);
		parentHandler.sendMessageAtTime(m, SystemClock.uptimeMillis() + fadeout.getDuration());
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		LevelExtendedInfo info = adapter.items.get(arg2);
		title.setText(info.name);
		authour.setText(info.author);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		title.setText("");
		authour.setText("");
	}
}
