package uk.digitalsquid.spacegame.spaceview.gamemenu;

import java.util.ArrayList;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.PaintLoader;
import uk.digitalsquid.spacegame.PaintLoader.PaintDesc;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Moveable;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

public class GameMenu extends ArrayList<GameMenu.GameMenuItem> implements Moveable
{
	private static final int ICON_SIZE = 30;
	private static final int BORDER = 10;
	private static final int HEIGHT = BORDER + ICON_SIZE + BORDER;
	private static final int SHIFT_OFF_SCREEN = 10;
	private static final int CORNER_ROUNDING = 10;
	
	private static final PaintDesc borderPaint = new PaintDesc(255, 255, 255, 255, 2, Style.STROKE);
	private static final PaintDesc innerPaint = new PaintDesc(180, 0, 0, 0);
	private static final PaintDesc innerPaintSelected = new PaintDesc(50, 50, 50);
	
	private float worldZoom = 1;
	
	//private static NinePatchDrawable bgImg = null;
	
	private int movePos = 180;
	private int aimingFor = 180;
	
	public enum Corner
	{
		TOP_LEFT,
		TOP_RIGHT,
		BOTTOM_LEFT,
		BOTTOM_RIGHT
	}
	private static final long serialVersionUID = 7875605945532702160L;
	
	private Coord screenSize;
	
	private Corner corner;
	
	public GameMenu(Context context, GameMenuItem[] items, Corner corner)
	{
		super();
		this.corner = corner;
		
		for(int i = 0; i < items.length; i++)
		{
			items[i].loadImage(context);
			add(items[i]);
		}
	}
	
	/**
	 * Move the animation of the menu
	 */
	@Override
	public void move(float millistep, float speedScale)
	{
		if(movePos == aimingFor)
			return;
		if(movePos < aimingFor)
			movePos += millistep * speedScale / 40;
		if(movePos > aimingFor)
			movePos -= millistep * speedScale / 40;
	}
	
	/**
	 * Set this menu to start hiding
	 */
	public void hide()
	{
		aimingFor = 180;
	}
	
	/**
	 * Set this menu to start showing
	 */
	public void show()
	{
		aimingFor = 0;
	}
	
	/**
	 * Immediately hide the menu
	 */
	public void hideImmediately()
	{
		hide();
		movePos = 180;
	}
	
	/**
	 * Immediately show the menu
	 */
	public void showImmediately()
	{
		show();
		movePos = 0;
	}
	
	public boolean isHidden()
	{
		if(aimingFor == 0) return false;
		return true;
	}
	
	/**
	 * Draw this menu onto the canvas
	 * @param worldZoom The UNSCALED current world zoom.
	 */
	public void draw(Canvas c, float worldZoom, Coord screenSize)
	{
		this.worldZoom = worldZoom;
		this.screenSize = screenSize;
		drawAt(c, getRect(), worldZoom);
	}
	
	/**
	 * Draw the menu in the specified position
	 * @param pos The position at which to draw the menu
	 */
	private void drawAt(Canvas c, Rect pos, float worldZoom)
	{
		//bgImg.setBounds(pos);
		//bgImg.draw(c);
		c.drawRoundRect(new RectF(pos), CORNER_ROUNDING * worldZoom, CORNER_ROUNDING * worldZoom, PaintLoader.load(innerPaint));
		c.drawRoundRect(new RectF(pos), CORNER_ROUNDING * worldZoom, CORNER_ROUNDING * worldZoom, PaintLoader.load(borderPaint));
		
		for(int i = 0; i < size(); i++)
		{
			int border = (int) (BORDER * worldZoom);
			int width = (int) (ICON_SIZE * worldZoom);
			
			Rect itemPos = new Rect(
					pos.left + border + ((width + border) * i),
					pos.top + border,
					pos.left + ((width + border) * (i+1)),
					pos.bottom - border
					);
			
			if(i == currItemHover | i == initialItemClicked) c.drawRoundRect(new RectF(itemPos), CORNER_ROUNDING / 2 * worldZoom, CORNER_ROUNDING / 2 * worldZoom, PaintLoader.load(innerPaintSelected));
			
			get(i).draw(c, itemPos);
		}
	}
	
	public int getHeight()
	{
		return HEIGHT;
	}
	
	public int getWidth()
	{
		return (ICON_SIZE + BORDER) * size() + BORDER;
	}
	
	/**
	 * Get a {@link Rect} describing the position of this menu
	 * @return The position of this menu
	 */
	public final Rect getRect()
	{
		Rect rect;
		int width = (int) (getWidth() * worldZoom);
		int height = (int) (HEIGHT * worldZoom);
		int shiftOffScreen = (int) (SHIFT_OFF_SCREEN * worldZoom);
		float pos = (float) (HEIGHT * (Math.cos(movePos * Math.PI / 180) - 1) / -2) * worldZoom;
		
		if(corner == Corner.TOP_LEFT)
		{
			rect = new Rect(
					0 - shiftOffScreen,
					(int)-pos - shiftOffScreen,
					width - shiftOffScreen,
					(int)(-pos + height) - shiftOffScreen);
		}
		else if(corner == Corner.TOP_RIGHT)
		{
			rect = new Rect(
					(int)(screenSize.x - width) + shiftOffScreen,
					(int)-pos - shiftOffScreen,
					(int) screenSize.x + shiftOffScreen,
					(int)-pos + height - shiftOffScreen);
		}
		else if(corner == Corner.BOTTOM_LEFT)
		{
			rect = new Rect(
					0 - shiftOffScreen,
					(int)(screenSize.y - height + pos) + shiftOffScreen,
					width - shiftOffScreen,
					(int)(screenSize.y + pos) + shiftOffScreen);
		}
		else if(corner == Corner.BOTTOM_RIGHT)
		{
			rect = new Rect(
					(int)(screenSize.x - width) + shiftOffScreen,
					(int)(screenSize.y - height + pos) + shiftOffScreen,
					(int) screenSize.x + shiftOffScreen,
					(int)(screenSize.y + pos) + shiftOffScreen);
		}
		else
			rect = null;
		return rect;
	}
	
	private boolean menuIsSelected = false;
	
	/**
	 * The index of the first menu item that was clicked
	 */
	private int initialItemClicked = -1;
	
	/**
	 * The index of the item that the pointer was over in the previous loop
	 */
	private int prevItemHover = -1;
	
	/**
	 * The index of the current item that is being hovered over with the pointer
	 */
	private int currItemHover = -1;
	
	/**
	 * Process {@code event}, and check for any buttons in this menu that have been clicked.
	 * @param event The {@link MotionEvent} passed by the system
	 * @return {@literal true} if a click was found, {@literal false} otherwise.
	 */
	public boolean computeClick(MotionEvent event)
	{
		Coord pos = new Coord(event.getX(), event.getY());
		Rect rect = getRect();

		int border = (int) (BORDER * worldZoom);
		int width = (int) (ICON_SIZE * worldZoom);
		boolean menuIsDeselecting = false;
		
		if(event.getAction() == MotionEvent.ACTION_DOWN &
				pos.x > rect.left & pos.x < rect.right & pos.y > rect.top & pos.y < rect.bottom) // If pressed inside
		{
			menuIsSelected = true;
		}
		else if(event.getAction() == MotionEvent.ACTION_CANCEL)
		{
			menuIsDeselecting = true;
		}
		else if(event.getAction() == MotionEvent.ACTION_UP)
		{
			menuIsDeselecting = true;
		}
		
		if(menuIsSelected)
		{
			boolean pointerOnItem = false;
			if(pos.y > rect.top + border & pos.y < rect.bottom - border)
			{
				for(int i = 0; i < size(); i++)
				{
					if(pos.x > rect.left + border + ((width + border) * i) & pos.x < rect.left + ((width + border) * (i+1))) // If inside single button
					{
						currItemHover = i;
						pointerOnItem = true;
						if(event.getAction() == MotionEvent.ACTION_DOWN)
						{
							initialItemClicked = i;
							get(i).clickListener.onClickDown();
						}
						else if(event.getAction() == MotionEvent.ACTION_MOVE)
						{
							if(i != prevItemHover) // If just moved on
							{
								get(i).clickListener.onMoveOn();
							}
						}
						else if(event.getAction() == MotionEvent.ACTION_UP)
						{
							if(initialItemClicked == i)
								get(i).clickListener.onRelease();
							else
								get(i).clickListener.onMoveOff();
							initialItemClicked = -1;
							currItemHover = -1;
						}
						else if(event.getAction() == MotionEvent.ACTION_CANCEL)
						{
							get(i).clickListener.onMoveOff();
							initialItemClicked = -1;
							currItemHover = -1;
						}
						prevItemHover = i;
					}
				}
			}
			
			if(!pointerOnItem)
			{
				if(prevItemHover != -1) // If just moved off
					get(prevItemHover).clickListener.onMoveOff();
				prevItemHover = -1;
			}

			if(menuIsDeselecting)
			{
				menuIsSelected = false; // Disable menu after release
				initialItemClicked = -1; // Reset vals
				currItemHover = -1;
			}
			
			return true;
		}
		return false;
	}
	
	public static class GameMenuItem
	{
		protected ClickListener clickListener;
		protected Drawable icon;
		protected int resId;
		
		public GameMenuItem(ClickListener clickListener, int resId)
		{
			this.clickListener = clickListener;
			if(this.clickListener == null)
				throw new IllegalArgumentException();
			this.resId = resId;
		}
		
		protected void loadImage(Context context)
		{
			synchronized(context)
			{
				icon = context.getResources().getDrawable(resId);
			}
		}
		
		protected void draw(Canvas c, Rect rect)
		{
			icon.setBounds(rect);
			icon.draw(c);
		}
	}
	
	public static interface ClickListener
	{
		public void onClickDown();
		public void onRelease();
		public void onMoveOn();
		public void onMoveOff();
	}
}
