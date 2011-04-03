package uk.digitalsquid.spacegame.views;

import java.io.InputStream;

import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.Spacegame;
import android.content.Context;
import android.os.Bundle;
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
import android.widget.TextView;

public class GameViewLayout extends FrameLayout implements KeyInput, OnClickListener
{
	protected Context context;
	protected Handler parentHandler;
	
	GameView gameView;
	
	Animation panout, panin;

	protected static final int GVL_MSG_INFOBOX = 1;
	protected static final int GVL_MSG_PAUSE = 2;
	
	protected Handler gvHandler = new Handler()
	{
		@Override
		public void handleMessage(Message m)
		{
			switch(m.what)
			{
			case GVL_MSG_INFOBOX:
				gameView.setPaused(true);
				infoBoxTextPointer = 0;
				infoBoxText = (String[]) m.obj;
				nextInfoMessage();
				findViewById(R.id.gameviewinfobox).setVisibility(View.VISIBLE);
				findViewById(R.id.gameviewinfobox).startAnimation(panin);
				break;
			case GVL_MSG_PAUSE:
				onBackPress();
				break;
			}
		}
	};
	
	protected String[] infoBoxText;
	protected int infoBoxTextPointer;
	
	protected void nextInfoMessage()
	{
		if(infoBoxTextPointer < infoBoxText.length)
		{
			((TextView)findViewById(R.id.gameviewinfoboxtext)).setText(infoBoxText[infoBoxTextPointer++]);
		}
		else
		{
			findViewById(R.id.gameviewinfobox).setVisibility(View.INVISIBLE);
			findViewById(R.id.gameviewinfobox).startAnimation(panout);
			gameView.setPaused(false);
		}
	}
	
	public GameViewLayout(Context context, AttributeSet attrs, InputStream level, Handler handler)
	{
		super(context, attrs);
		this.context = context;
		parentHandler = handler;
		
		LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.gameview, this);
		
		panout = AnimationUtils.loadAnimation(context, R.anim.panout);
		panin = AnimationUtils.loadAnimation(context, R.anim.panin);

		gameView = new GameView(context, attrs, level, handler, gvHandler);
		((LinearLayout) findViewById(R.id.gameviewlayout)).addView(gameView);
		gameView.setFocusable(false);
		gameView.setFocusableInTouchMode(false);

		findViewById(R.id.gameviewbuttonresume).setOnClickListener(this);
		findViewById(R.id.gameviewbuttonquit).setOnClickListener(this);

		findViewById(R.id.gameviewinfoboxpic).setOnClickListener(this);
		findViewById(R.id.gameviewinfoboxtext).setOnClickListener(this);
		
		findViewById(R.id.gameviewbuttons).setVisibility(View.GONE);
	}

	@Override
	public void onBackPress()
	{
		if(findViewById(R.id.gameviewinfobox).getVisibility() == View.INVISIBLE)
		{
			if(findViewById(R.id.gameviewbuttons).getVisibility() == View.GONE)
			{
				findViewById(R.id.gameviewbuttons).setVisibility(View.VISIBLE);
				findViewById(R.id.gameviewbuttons).startAnimation(panin);
				gameView.setPaused(true);	
			}
			else
			{
				findViewById(R.id.gameviewbuttons).startAnimation(panout);
				findViewById(R.id.gameviewbuttons).setVisibility(View.GONE);
				gameView.setPaused(false);	
			}
		}
		else
			nextInfoMessage();
	}
	
	public void restoreState(Bundle bundle)
	{
		gameView.restoreState(bundle);
		if(bundle != null)
		{
			findViewById(R.id.gameviewbuttons).setVisibility(View.VISIBLE);
		}
	}
	
	public void saveState(Bundle bundle)
	{
		gameView.saveState(bundle);
	}
	
	@Override
	public void onClick(View arg0)
	{
		switch(arg0.getId())
		{
		case R.id.gameviewbuttonresume:
			gameView.setPaused(false);
			findViewById(R.id.gameviewbuttons).startAnimation(panout);
			findViewById(R.id.gameviewbuttons).setVisibility(View.INVISIBLE);
			break;
		case R.id.gameviewbuttonquit:
			findViewById(R.id.gameviewbuttons).startAnimation(panout);
			findViewById(R.id.gameviewbuttons).setVisibility(View.INVISIBLE);
			Message m = Message.obtain();
			m.what = Spacegame.MESSAGE_END_LEVEL;
			parentHandler.sendMessageAtTime(m, SystemClock.uptimeMillis() + panout.getDuration());
			break;
		case R.id.gameviewinfoboxpic:
			nextInfoMessage();
			break;
		case R.id.gameviewinfoboxtext:
			nextInfoMessage();
			break;
		}
	}
}
