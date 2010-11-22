package uk.digitalsquid.spacegame.views;

import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.Spacegame;
import uk.digitalsquid.spacegame.levels.LevelManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class LevelSetSelectLayout extends FrameLayout implements KeyInput, OnClickListener
{
	protected Context context;
	protected Handler parentHandler;
	
	MainMenu menuView;
	
	Animation panout, panin;
	
	public LevelSetSelectLayout(Context context, AttributeSet attrs, Handler handler)
	{
		super(context, attrs);
		this.context = context;
		parentHandler = handler;
		
		LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.levelsetselect, this);
		
		panout = AnimationUtils.loadAnimation(context, R.anim.panout);
		panin = AnimationUtils.loadAnimation(context, R.anim.panin);

		menuView = new MainMenu(context, attrs, handler, context.getResources()
				.openRawResource(R.raw.menu_levelsets));
		((LinearLayout) findViewById(R.id.levelsetselectlayout)).addView(menuView);
		menuView.setFocusable(false);
		menuView.setFocusableInTouchMode(false);
		
		Animation fadein = AnimationUtils.loadAnimation(context, R.anim.fadein);
		findViewById(R.id.levelsetselectbuttons).startAnimation(fadein);

		findViewById(R.id.menubutton_levelset1).setOnClickListener(this);
		findViewById(R.id.menubutton_levelset2).setOnClickListener(this);
		findViewById(R.id.menubutton_levelset3).setOnClickListener(this);
		findViewById(R.id.menubutton_levelset4).setOnClickListener(this);
	}

	@Override
	public void onBackPress()
	{
		findViewById(R.id.levelsetselectbuttons).startAnimation(panout);
		findViewById(R.id.levelsetselectbuttons).setVisibility(View.INVISIBLE);
//		menuView.stop(Spacegame.MESSAGE_RETURN_TO_MAIN_SCREEN);
		Message m = Message.obtain();
		m.what = Spacegame.MESSAGE_RETURN_TO_MAIN_SCREEN;
		parentHandler.sendMessageAtTime(m, SystemClock.uptimeMillis() + panout.getDuration());
	}

	@Override
	public void onClick(View arg0)
	{
		String levelsetfilename = LevelManager.BUILTIN_PREFIX;
		switch(arg0.getId())
		{
		case R.id.menubutton_levelset1:
			levelsetfilename += "tutorial";
			break;
		case R.id.menubutton_levelset2:
			levelsetfilename += "easy";
			break;
		case R.id.menubutton_levelset3:
			levelsetfilename += "medium";
			break;
		case R.id.menubutton_levelset4:
			levelsetfilename += "hard";
			break;
		}
		
		Animation fadeout = AnimationUtils.loadAnimation(context, R.anim.fadeout);
		findViewById(R.id.levelsetselectbuttons).startAnimation(fadeout);
		Message m = Message.obtain();
		m.what = Spacegame.MESSAGE_OPEN_LEVELSET;
		m.obj = levelsetfilename;
		parentHandler.sendMessageAtTime(m, SystemClock.uptimeMillis() + fadeout.getDuration());
//		menuView.stop(-1);
	}
}
