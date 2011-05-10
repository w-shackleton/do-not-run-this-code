package uk.digitalsquid.spacegame.views;

import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.spacegame.BounceVibrate;
import uk.digitalsquid.spacegame.Spacegame;
import uk.digitalsquid.spacegame.levels.LevelItem.LevelSummary;
import uk.digitalsquid.spacegame.spaceitem.SpaceItem;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Clickable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.LevelAffectable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.LevelAffectable.AffectData;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Messageable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Messageable.MessageInfo;
import uk.digitalsquid.spacegame.spaceitem.interfaces.StaticDrawable;
import uk.digitalsquid.spacegame.spaceview.gamemenu.StarDisplay;
import uk.digitalsquid.spacegame.subviews.MovingView;
import android.content.Context;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class GameView extends MovingView<GameView.ViewWorker> implements OnTouchListener, KeyInput
{
	final Handler parentHandler, gvHandler;
	private final InputStream level;
	
	public GameView(Context context, AttributeSet attrs, InputStream level, Handler handler, Handler gvHandler)
	{
		super(context, attrs);
		
		parentHandler = handler;
		this.gvHandler = gvHandler;
		this.level = level;
        
        setOnTouchListener(this);
        
        initP2();
	}

	@Override
	protected ViewWorker createThread() {
    	return new ViewWorker(context, new Handler()
    	{
    		public void handleMessage(Message m)
    		{
    			if(m.what == ViewWorker.MESSAGE_END_GAME)
    			{
    				Message newM = new Message();
    				newM.what = Spacegame.MESSAGE_END_LEVEL;
    				newM.arg1 = m.arg1;
    				newM.obj = m.obj;
    				GameView.this.parentHandler.sendMessage(newM);
    			}
    		}
    	}, this.gvHandler, level);
	}
	
	class ViewWorker extends MovingView.ViewWorker
	{
		static final int MESSAGE_END_GAME = 1;
		
		protected Handler msgHandler, gvHandler;
		
		public ViewWorker(Context context, Handler handler, Handler gvHandler,
				InputStream level)
		{
			super(context, level);
			msgHandler = handler;
			this.gvHandler = gvHandler;
		}
		
		private StarDisplay starCount;

		@Override
		protected void initialiseOnThread()
		{
			BounceVibrate.initialise(context);
			
			super.initialiseOnThread();
			starCount = new StarDisplay(context, level.starsToCollect, portal);
		}
		
		@Override
		protected void predraw(GL10 gl)
		{
			super.predraw(gl);
		}
		
		private final Matrix matrix2d = new Matrix();
		private final Matrix matrix2dInverse = new Matrix();
		
		/**
		 * Scales the game to the correct size. We also use a 2D canvas matrix to simplify screen-to-world conversions.
		 */
		@Override
		protected void scale(GL10 gl)
		{
			matrix2d.reset();
			
			gl.glScalef(WORLD_ZOOM_PRESCALE, WORLD_ZOOM_PRESCALE, WORLD_ZOOM_PRESCALE);
			matrix2d.postScale(WORLD_ZOOM_PRESCALE, WORLD_ZOOM_PRESCALE);
			
			gl.glRotatef(warpData.rotation, 0, 0, 1);
			matrix2d.postRotate(warpData.rotation);
			
			gl.glTranslatef((float)-avgPos.x, (float)-avgPos.y, 0);
			matrix2d.postTranslate((float)-avgPos.x, (float)-avgPos.y);
			
			gl.glScalef(WORLD_ZOOM_POSTSCALE, WORLD_ZOOM_POSTSCALE, WORLD_ZOOM_POSTSCALE);
			matrix2d.postScale(WORLD_ZOOM_POSTSCALE, WORLD_ZOOM_POSTSCALE);
			
			matrix2d.invert(matrix2dInverse);
		}
		
		@Override
		protected void calculate()
		{
			super.calculate();
			
			// Message displays
			MessageInfo message;
			for(SpaceItem item : planetList)
			{
				if(item instanceof Messageable)
				{
					message = ((Messageable)item).sendMessage();
					if(message != null)
					{
						if(message.display)
						{
							Log.v("SpaceGame", "Opening info box...");
							message.display = false;
							Message m = Message.obtain();
							m.what = GameViewLayout.GVL_MSG_INFOBOX;
							m.obj = message.messages;
							gvHandler.sendMessage(m);
						}
					}
				}
				
				// Stage for game detail returning
				if(item instanceof LevelAffectable)
				{
					AffectData d = ((LevelAffectable)item).affectLevel();
					if(d != null) {
						if(d.incScore) starCount.incStarCount();
						if(d.incDisplayedScore) starCount.incDisplayedStarCount();
					}
				}
			}
			
			starCount.move(millistep, SPEED_SCALE); // TODO: Move these to the ITER-part part? Probably no need.
			starCount.drawMove(millistep, SPEED_SCALE);
		}
		
		@Override
		protected void postdraw(GL10 gl)
		{
			// Draw objects static to screen (buttons)
			for(SpaceItem obj : level.planetList) {
				// Stage for static drawing
				if(obj instanceof StaticDrawable)
				{
					((StaticDrawable)obj).drawStatic(gl, (int)scaledWidth, (int)scaledHeight, matrix2d);
				}
			}
			
			starCount.drawStatic(gl, scaledWidth, scaledHeight, matrix2d);
			
			super.postdraw(gl);
		}
		
		@Override
		public synchronized void saveState(Bundle bundle)
		{
			super.saveState(bundle);
		}
		
		@Override
		public synchronized void restoreState(Bundle bundle)
		{
			super.restoreState(bundle);
		}
		
		public synchronized void onTouch(View v, MotionEvent event)
		{
			final float[] tmpData = {
					+event.getX() * scaledWidth / width - scaledWidth / 2,
					-event.getY() * scaledHeight / height + scaledHeight / 2 };
			matrix2dInverse.mapPoints(tmpData);
			
			Log.v("SpaceGame", "CLICK X: " + tmpData[0] + ", Y: " + tmpData[1]);
			
			for(SpaceItem obj : planetList)
			{
				if(obj instanceof Clickable)
				{
					Clickable item = (Clickable) obj;
					
					if(item.isClicked(tmpData[0], tmpData[1]))
					{
						item.onClick();
						Log.v("SpaceGame", "Item clicked");
						return;
					}
				}
			}
			if(event.getAction() == MotionEvent.ACTION_UP)
				fireBall(tmpData[0], tmpData[1]);
		}
		
		private void fireBall(double x, double y)
		{
			if(stopped)
			{
				p.itemVC.x = (x - p.itemC.x) / 3 * SpaceItem.ITEM_SCALE; // Scale down to compensate for power
				p.itemVC.y = (y - p.itemC.y) / 3 * SpaceItem.ITEM_SCALE;
				
				p.itemC.x  += p.itemVC.x / 40;
				p.itemC.y  += p.itemVC.y / 40;
				
				stopped = false;
			}
		}

		@Override
		protected void onThreadEnd()
		{
			Message m = Message.obtain();
			m.what = MESSAGE_END_GAME;
			m.arg1 = warpData.endReason;
			
			LevelSummary sum = new LevelSummary(level.starsToCollect, starCount.getStarCount(), (int) (finishTime - startTime));
			m.obj = sum;
			msgHandler.sendMessage(m);
		}
	}
	
	@Override
	public void restoreState(Bundle bundle)
	{
		super.restoreState(bundle);
		if(bundle != null)
		{
			thread.setPaused(true);
		}
	}
	
	protected void setPaused(boolean p)
	{
		thread.setPaused(p);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		if(thread != null)
		{
			((ViewWorker) thread).onTouch(v, event);
		}
		return true;
	}
	
	@Override
	public void onBackPress()
	{
		stop(-1);
	}
}
