package uk.digitalsquid.spacegame.views;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.digitalsquid.spacegame.BounceVibrate;
import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.PaintLoader;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.Spacegame;
import uk.digitalsquid.spacegame.levels.LevelItem;
import uk.digitalsquid.spacegame.spaceitem.SpaceItem;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Clickable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Messageable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Messageable.MessageInfo;
import uk.digitalsquid.spacegame.spaceitem.interfaces.StaticDrawable;
import uk.digitalsquid.spacegame.spaceview.gamemenu.GameMenu;
import uk.digitalsquid.spacegame.spaceview.gamemenu.GameMenu.ClickListener;
import uk.digitalsquid.spacegame.spaceview.gamemenu.GameMenu.GameMenuItem;
import uk.digitalsquid.spacegame.subviews.MovingView;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.Region.Op;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnTouchListener;

public class GameView extends MovingView<GameView.ViewThread> implements OnTouchListener, KeyInput
{
	final Handler parentHandler, gvHandler;
	
	public GameView(Context context, AttributeSet attrs, InputStream level, Handler handler, Handler gvHandler)
	{
		super(context, attrs);
		
		parentHandler = handler;
		this.gvHandler = gvHandler;
        
        setOnTouchListener(this);
        
        if (isInEditMode() == false)
        {
        	thread = new ViewThread(context, new Handler()
        	{
        		public void handleMessage(Message m)
        		{
        			if(m.what == ViewThread.MESSAGE_END_GAME)
        			{
        				Message newM = new Message();
        				newM.what = Spacegame.MESSAGE_END_LEVEL;
        				GameView.this.parentHandler.sendMessage(newM);
        			}
        		}
        	}, this.gvHandler, holder, level);
        }
	}
	
	class ViewThread extends MovingView.ViewThread
	{
		static final int MESSAGE_END_GAME = 1;
		
		protected Handler msgHandler, gvHandler;
		
		public ViewThread(Context context, Handler handler, Handler gvHandler, SurfaceHolder surface,
				InputStream level)
		{
			super(context, surface, level);
			msgHandler = handler;
			this.gvHandler = gvHandler;
		}
		
		private List<GameMenu> gameMenus;
		private static final int GAME_MENU_ZOOM = 0;
		private static final int GAME_MAIN_MENU = 1;
		private static final int GAME_MENU_SHOW = 2;

		@Override
		protected void initialiseOnThread()
		{
			Canvas c = null;
			try
			{
				c = surface.lockCanvas(null);
				synchronized(surface)
				{
					// Draw simple loading screen
					WORLD_ZOOM_UNSCALED = (float)width / REQ_SIZE_X;
					c.scale(WORLD_ZOOM_UNSCALED, WORLD_ZOOM_UNSCALED);
					c.drawPaint(PaintLoader.load(bgpaint));
					c.drawText("Loading...", c.getWidth() / 2 / WORLD_ZOOM_UNSCALED, 100, PaintLoader.load(txtpaint));
					c.restore();
				}
			}
			finally
			{
				if(c != null)
					surface.unlockCanvasAndPost(c);
			}
			
			BounceVibrate.initialise(context);
			
			super.initialiseOnThread();
			if(gameMenus == null)
			{
				gameMenus = new ArrayList<GameMenu>();
				constructMenuZoom();
				constuctMainMenu();
				constuctMenuShow();
				gameMenus.get(GAME_MENU_SHOW).show();
			}
		}
		
		private final Coord tmpOuterBounds = new Coord();
		@Override
		protected void predraw(Canvas c)
		{
			c.drawPaint(PaintLoader.load(bgpaint)); // Different bg outside box could be drawn here...
			
			level.bounds.addInto(LevelItem.COORD_BOUNDS_DRAWEXT, tmpOuterBounds);
			RectF boundRect = tmpOuterBounds.toRectF();
			matrix.mapRect(boundRect);
			c.clipRect(boundRect, Op.REPLACE); // Set clip
			super.predraw(c);
		}
		
		@Override
		protected void scaleCalculate()
		{
			matrix.reset();
			matrix.preRotate(
					warpData.rotation,
					width / 2,
					height / 2);
			matrix.preScale(WORLD_ZOOM_POSTSCALE, WORLD_ZOOM_POSTSCALE, width / 2, height / 2);
			
			matrix.preTranslate((float)-avgPos.x * WORLD_ZOOM_PRESCALE, (float)-avgPos.y * WORLD_ZOOM_PRESCALE);
			matrix.preScale(WORLD_ZOOM_PRESCALE, WORLD_ZOOM_PRESCALE);
		}
		
		@Override
		protected void scale(Canvas c)
		{
//			// Move screen & zoom
//			c.rotate(
//					warpData.rotation,
//					width / 2,
//					height / 2);
//			c.scale(WORLD_ZOOM_POSTSCALE, WORLD_ZOOM_POSTSCALE, width / 2, height / 2);
//			
//			c.translate((float)-avgPos.x * WORLD_ZOOM_PRESCALE, (float)-avgPos.y * WORLD_ZOOM_PRESCALE);
//			c.scale(WORLD_ZOOM_PRESCALE, WORLD_ZOOM_PRESCALE);
			
			c.setMatrix(matrix);
		}
		
		@Override
		protected void calculate()
		{
			super.calculate();
			
			// Message displays
			int i;
			SpaceItem currObj;
			MessageInfo message;
			for(i = 0; i < planetList.size(); i++)
			{
				currObj = planetList.get(i);
				
				Messageable mItem;
				try {
					mItem = (Messageable) currObj;
				} catch(RuntimeException e) {
					mItem = null;
				}
				if(mItem != null)
				{
					message = mItem.sendMessage();
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
			}
		}
		
		private final Coord screenSize = new Coord();
		
		@Override
		protected void postdraw(Canvas c)
		{
			// Draw objects static to screen (buttons)
			
			// Draw menus
			screenSize.x = width;
			screenSize.y = height;
			
			for(GameMenu menu : gameMenus)
			{
				menu.move(millistep, SPEED_SCALE);
				menu.draw(c, WORLD_ZOOM_UNSCALED, screenSize);
			}
			
			super.postdraw(c);
			
			for(SpaceItem obj : level.planetList) {
				// Stage for static drawing
				StaticDrawable item;
				try {
					item = (StaticDrawable) obj;
				} catch(RuntimeException e) {
					item = null;
				}
				if(item != null)
				{
					item.drawStatic(c, 1, width, height, matrix);
				}
			}
		}
		
		@Override
		public synchronized void saveState(Bundle bundle)
		{
			super.saveState(bundle);
			
			Iterator<GameMenu> menuIter = gameMenus.iterator();
			int i = 0;
			while(menuIter.hasNext())
			{
				GameMenu menu = menuIter.next();
				bundle.putBoolean("menu." + i++, menu.isHidden());
			}
		}
		
		@Override
		public synchronized void restoreState(Bundle bundle)
		{
			super.restoreState(bundle);
			
			Iterator<GameMenu> menuIter = gameMenus.iterator();
			int i = 0;
			while(menuIter.hasNext())
			{
				GameMenu menu = menuIter.next();
				if(bundle.getBoolean("menu." + i++, true))
					menu.hideImmediately();
				else
					menu.showImmediately();
			}
		}
		
		public synchronized void onTouch(View v, MotionEvent event)
		{
			Iterator<GameMenu> iter = gameMenus.iterator();
			while(iter.hasNext())
			{
				if(iter.next().computeClick(event)) return;
			}
			Iterator<SpaceItem> itemIter = planetList.iterator();
			while(itemIter.hasNext())
			{
				SpaceItem currObj = itemIter.next();

				Clickable item;
				try {
					item = (Clickable) currObj;
				} catch(RuntimeException e) {
					item = null;
				}
				if(item != null)
				{
					if(item.isClicked(new Coord(event.getX(), event.getY(), matrix)))
					{
						item.onClick();
						Log.v("SpaceGame", "Item clicked");
						return;
					}
				}
			}
			if(event.getAction() == MotionEvent.ACTION_UP)
				fireBall(event.getX(), event.getY());
		}
		
		private void fireBall(double sPosX, double sPosY)
		{
			if(stopped)
			{
				p.itemVC.x = ((sPosX / WORLD_ZOOM) - p.itemC.x + avgPos.x) / 3 * SpaceItem.ITEM_SCALE; // Scale down to compensate for power
				p.itemVC.y = ((sPosY / WORLD_ZOOM) - p.itemC.y + avgPos.y) / 3 * SpaceItem.ITEM_SCALE;
				
				p.itemC.x  += p.itemVC.x / 40;
				p.itemC.y  += p.itemVC.y / 40;
				
				stopped = false;
			}
		}
		
		private void constructMenuZoom()
		{
			gameMenus.add(GAME_MENU_ZOOM, new GameMenu(context, new GameMenuItem[]{
				new GameMenuItem(new ClickListener()
				{
					@Override
					public void onClickDown()
					{
						userZoomMultiplier = USER_ZOOM_INCREASE_SPEED;
					}

					@Override
					public void onMoveOff()
					{
						onRelease();
					}

					@Override
					public void onRelease()
					{
						userZoomMultiplier = 1;
					}

					@Override
					public void onMoveOn()
					{
						onClickDown();
					}
				}, R.drawable.magplus),
				new GameMenuItem(new ClickListener()
				{
					@Override
					public void onClickDown()
					{
						userZoomMultiplier = 1 / USER_ZOOM_INCREASE_SPEED;
					}
					@Override
					public void onMoveOff()
					{
						onRelease();
					}
					@Override
					public void onRelease()
					{
						userZoomMultiplier = 1;
					}
					@Override
					public void onMoveOn()
					{
						onClickDown();
					}
				}, R.drawable.magminus),
			}, GameMenu.Corner.TOP_RIGHT));
		}
		
		private void constuctMenuShow()
		{
			gameMenus.add(GAME_MENU_SHOW, new GameMenu(context, new GameMenuItem[]{
				new GameMenuItem(new ClickListener()
				{
					@Override
					public void onClickDown()
					{}

					@Override
					public void onMoveOff()
					{}

					@Override
					public void onMoveOn()
					{}

					@Override
					public void onRelease()
					{
						gameMenus.get(GAME_MAIN_MENU).show();
						gameMenus.get(GAME_MENU_ZOOM).show();
						gameMenus.get(GAME_MENU_SHOW).hide();
					}}, R.drawable.uparrow),
			},GameMenu.Corner.BOTTOM_LEFT));
		}
		
		private void constuctMainMenu()
		{
			gameMenus.add(GAME_MAIN_MENU, new GameMenu(context, new GameMenuItem[]{
				new GameMenuItem(new ClickListener()
				{
					@Override
					public void onClickDown()
					{}

					@Override
					public void onMoveOff()
					{}

					@Override
					public void onMoveOn()
					{}

					@Override
					public void onRelease()
					{
						gameMenus.get(GAME_MAIN_MENU).hide();
						gameMenus.get(GAME_MENU_ZOOM).hide();
						gameMenus.get(GAME_MENU_SHOW).show();
					}}, R.drawable.downarrow),
				new GameMenuItem(new ClickListener()
				{
					@Override
					public void onClickDown(){}
					
					@Override
					public void onRelease()
					{
						p.itemC.copyFrom(level.startPos);
						p.itemVC.reset();
					}
						
					@Override
					public void onMoveOff(){}
					
					@Override
					public void onMoveOn(){}					
					}, R.drawable.restart),
				new GameMenuItem(new ClickListener()
				{
					@Override
					public void onClickDown(){}
					
					@Override
					public void onRelease()
					{
						Message m = Message.obtain();
						m.what = GameViewLayout.GVL_MSG_PAUSE;
						gvHandler.sendMessage(m);
					}
						
					@Override
					public void onMoveOff(){}
					
					@Override
					public void onMoveOn(){}					
					}, R.drawable.pause),
			}, GameMenu.Corner.BOTTOM_LEFT));
		}

		@Override
		protected void onThreadEnd()
		{
			Message m = Message.obtain();
			m.what = MESSAGE_END_GAME;
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
			((ViewThread) thread).onTouch(v, event);
		}
		return true;
	}
	
	@Override
	public void onBackPress()
	{
		stop(-1);
	}
}
