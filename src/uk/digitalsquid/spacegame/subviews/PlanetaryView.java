package uk.digitalsquid.spacegame.subviews;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.xml.sax.SAXException;

import uk.digitalsquid.spacegame.BounceVibrate;
import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.PaintLoader;
import uk.digitalsquid.spacegame.PaintLoader.PaintDesc;
import uk.digitalsquid.spacegame.StaticInfo;
import uk.digitalsquid.spacegame.levels.LevelItem;
import uk.digitalsquid.spacegame.levels.SaxLoader;
import uk.digitalsquid.spacegame.spaceitem.CompuFuncs;
import uk.digitalsquid.spacegame.spaceitem.SpaceItem;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Forceful;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Forceful.BallData;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Moveable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.TopDrawable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Warpable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Warpable.WarpData;
import uk.digitalsquid.spacegame.spaceitem.items.AnimatedPlayer;
import uk.digitalsquid.spacegame.spaceitem.items.Player;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

public abstract class PlanetaryView<VT extends PlanetaryView.ViewThread> extends ThreadedView<VT>
{
	public PlanetaryView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public static abstract class ViewThread extends ThreadedView.ViewThread
	{
		protected float WORLD_ZOOM;
		protected float WORLD_ZOOM_UNSCALED_ZOOMED;
		protected float WORLD_ZOOM_PRESCALE, WORLD_ZOOM_POSTSCALE;
		
		public static final int ITERS = 5;
		public static final float AIR_RESISTANCE = 1f; // 1 = no resistance, must NEVER be greater than 1
		public static final int SPEED_SCALE = 20;
		
		/**
		 * Used to store the 'warp' data returned from calculateVelocity
		 */
		protected WarpData warpData = new WarpData();
		
		/**
		 * True if the game should be forcibly ended, rather than ended by the view closing (ie player dies)
		 */
		protected boolean endGame = false;
		
		private float userZoom = 100;
		protected float userZoomMultiplier = 1;
		protected static final float USER_ZOOM_INCREASE_SPEED = 1.01f;
		protected static final int USER_ZOOM_MAX = 125;
		protected static final int USER_ZOOM_MIN = 60;
		
		protected final InputStream xml;
		
		public ViewThread(Context context, SurfaceHolder surface, InputStream xml)
		{
			super(context, surface);
			
			this.xml = xml;
		}
		
		@Override
		protected void postcalculate()
		{
			super.postcalculate();
			
			// Calculate & move-scale canvas time!
			userZoom *= userZoomMultiplier;
			if(userZoom < USER_ZOOM_MIN) // Limit zoom factor
				userZoom = USER_ZOOM_MIN;
			if(userZoom > USER_ZOOM_MAX)
				userZoom = USER_ZOOM_MAX;
			WORLD_ZOOM = (float)width / REQ_SIZE_X / SpaceItem.ITEM_SCALE *
					userZoom / 100 * warpData.zoom; // Assuming all screens are about the same ratio?
			WORLD_ZOOM_UNSCALED = (float)width / REQ_SIZE_X;
			WORLD_ZOOM_UNSCALED_ZOOMED = (float)width / REQ_SIZE_X * userZoom / 100 * warpData.zoom;
			WORLD_ZOOM_PRESCALE = (float)width / REQ_SIZE_X / SpaceItem.ITEM_SCALE;
			WORLD_ZOOM_POSTSCALE = userZoom / 100 * warpData.zoom;
		}
		
		protected PaintDesc
				bgpaint = new PaintDesc(0, 0, 0);
		private PaintDesc warpDataPaint = new PaintDesc(0, 0, 0, 0);
		protected PaintDesc txtpaint = new PaintDesc(255, 255, 255, 255, 2, 12);
		private PaintDesc starpaint = new PaintDesc(220, 255, 255, 255, 2), starprevpaint = new PaintDesc(200, 160, 160, 160, 2);


		private static final int BG_REPEAT_X = 1000;
		private static final int BG_REPEAT_Y = 1000;
		private static final float BG_ZOOM_1 = 0.5f;
		private static final float BG_ZOOM_2 = 1f;
		private static final int BG_POINTS_PER_AREA = 1000;
		private static final int BG_BLUR_AMOUNT = 1;

		protected BgPoint[] Bg_points = new BgPoint[BG_POINTS_PER_AREA];
		protected Coord avgPos = new Coord();
		protected Coord[] avgPrevPos = new Coord[BG_BLUR_AMOUNT];
		
		protected static final int SCROLL_SPEED = 15;
		protected Coord[] screenPos = new Coord[SCROLL_SPEED];
		
		private Coord prevSmallAvgPos;
		private int timeSinceStop;
		private static final float STOPPING_SPEED = 1.5f;
		private static final int STEPS_TO_STOP = MAX_FPS * ITERS * 1; // 1 second.
		
		
		protected LevelItem level;
		protected List<SpaceItem> planetList;
		
		private SpaceItem currObj;
		//protected Coord p.itemC, p.itemVC, p.itemRF;

		protected boolean paused = false;
		protected boolean stopped = false;
		protected boolean gravOn = true;
		
		private int borderBounceColour = 255;
		
		public synchronized void setPaused(boolean p)
		{
			paused = p;
		}
		
		protected Player p;
		
		@Override
		protected void initialiseOnThread()
		{
			Log.i("SpaceGame", "Loading level...");
			boolean loadError = false;
			try
			{
				level = SaxLoader.parse(context, CompuFuncs.decodeIStream(xml));
			} catch (SAXException e)
			{
				Log.e("SpaceGame", "Error parsing level: error in data. (Message: " + e.getMessage() + ")");
				e.printStackTrace();
				loadError = true;
			} catch (IOException e)
			{
				Log.e("SpaceGame", "Error loading level from data source. (Message: " + e.getMessage() + ")");
				e.printStackTrace();
				loadError = true;
			}
			if(loadError)
			{
				setRunning(false);
			}
			
			planetList = level.planetList;
			if(planetList == null)
				planetList = new ArrayList<SpaceItem>();

			currTime = System.currentTimeMillis();
			
			p = new AnimatedPlayer(context, level.startPos, level.startSpeed);
//			p.itemC = new Coord(level.startPos);
//			p.itemVC = new Coord();
//			p.itemRF = new Coord();
			
			millistep = MAX_MILLIS; // Since no initial benchmark

			for(int i = 0; i < screenPos.length; i++)
				screenPos[i] = new Coord();
			for(int i = 0; i < avgPrevPos.length; i++)
				avgPrevPos[i] = new Coord();
			
			// BG Points
			for(int i = 0; i < BG_POINTS_PER_AREA; i++)
			{
				Bg_points[i] = new BgPoint(); // Random constructor
			}
		}
		
		@Override
		protected void calculate()
		{
			super.calculate();
			if(stopped) // Aiming ball - p.p.itemVC used to store velocity
			{
				
			}
			for(iter = 0; iter < ITERS; iter++) // Main physics loop
			{
				if(!paused)
				{
					// Check for collisions with wall
					if(p.itemC.x > level.bounds.x - AnimatedPlayer.BALL_RADIUS)
					{
						p.itemVC.x = -p.itemVC.x;
						if(p.itemVC.x > 0) // Solves physics error
							p.itemVC.x = 0;
						borderBounceColour = 0; // Set bounce colour to 0 - which is red.
						BounceVibrate.Vibrate((long) (p.itemVC.getLength() / SpaceItem.ITEM_SCALE * 2));
					}
					if(p.itemC.x < -(level.bounds.x - AnimatedPlayer.BALL_RADIUS))
					{
						p.itemVC.x = -p.itemVC.x;
						if(p.itemVC.x < 0)
							p.itemVC.x = 0;
						borderBounceColour = 0; // Set bounce colour to 0 - which is red.
						BounceVibrate.Vibrate((long) (p.itemVC.getLength() / SpaceItem.ITEM_SCALE * 2));
					}
					if(p.itemC.y > level.bounds.y - AnimatedPlayer.BALL_RADIUS)
					{
						p.itemVC.y = -p.itemVC.y;
						if(p.itemVC.y > 0) // Solves physics error
							p.itemVC.y = 0;
						borderBounceColour = 0; // Set bounce colour to 0 - which is red.
						BounceVibrate.Vibrate((long) (p.itemVC.getLength() / SpaceItem.ITEM_SCALE * 2));
					}
					if(p.itemC.y < -(level.bounds.y - AnimatedPlayer.BALL_RADIUS))
					{
						p.itemVC.y = -p.itemVC.y;
						if(p.itemVC.y < 0)
							p.itemVC.y = 0;
						borderBounceColour = 0; // Set bounce colour to 0 - which is red.
						BounceVibrate.Vibrate((long) (p.itemVC.getLength() / SpaceItem.ITEM_SCALE * 2));
					}
					/*if(Math.abs(p.p.itemC.y) > Math.abs(level.bounds.y) - BALL_RADIUS)
					{
						p.itemVC.y = -p.itemVC.y;
						borderBounceColour = 0;
						BounceVibrate.Vibrate((long) (p.itemVC.getLength() / SpaceItem.ITEM_SCALE * 2));
					}*/
				}
				
				p.itemRF = new Coord();
				for(i = 0; i < planetList.size(); i++)
				{
					currObj = planetList.get(i);
					
					if(!paused)
					{
						if(gravOn) // Stage for gravity forces
						{
							Forceful item;
							try {
								item = (Forceful) currObj;
							} catch(RuntimeException e) {
								item = null;
							}
							if(item != null)
							{
								p.itemRF = p.itemRF.add(item.calculateRF(p.itemC, p.itemVC));
							}
						}
						
						// Stage for velocity changes
						Forceful item;
						try {
							item = (Forceful) currObj;
						} catch(RuntimeException e) {
							item = null;
						}
						if(item != null)
						{
							BallData data = item.calculateVelocity(p.itemC, p.itemVC, AnimatedPlayer.BALL_RADIUS);
							if(data != null)
							{
								if(data.itemC != null)
									p.itemC = data.itemC;
								if(data.itemVC != null)
									p.itemVC = data.itemVC;
							}
						}
					}

					if(!paused)
					{
						// Stage for object animation / movement
						Moveable mItem;
						try
						{
							mItem = (Moveable) currObj;
						} catch (RuntimeException e)
						{
							mItem = null;
						}
						if(mItem != null)
						{
							mItem.move((int) millistep, SPEED_SCALE);
						}
					}
				}
				
				if(!paused)
				{
					p.itemVC.x += p.itemRF.x * millistep / ITERS / 1000f;
					p.itemVC.y += p.itemRF.y * millistep / ITERS / 1000f;
					
					p.itemC.x  += p.itemVC.x * millistep / ITERS / 1000f * SPEED_SCALE;
					p.itemC.y  += p.itemVC.y * millistep / ITERS / 1000f * SPEED_SCALE;
					
					// Air resistance
					p.itemVC = p.itemVC.scale(AIR_RESISTANCE);
					
					// Move ball - animation
					p.move(millistep, SPEED_SCALE);
					
					// Work out if no longer moving, from last 4 positions
					
					Coord smallAvgPos = new Coord();
					for(int i = screenPos.length - 4; i < screenPos.length; i++)
					{
						smallAvgPos = smallAvgPos.add(screenPos[i]);
					}
					smallAvgPos.x /= 4;
					smallAvgPos.y /= 4;
					
					if(prevSmallAvgPos == null)
						prevSmallAvgPos = smallAvgPos;
					
					if(prevSmallAvgPos.minus(screenPos[screenPos.length - 1]).getLength() < STOPPING_SPEED)
						timeSinceStop++;
					else
						timeSinceStop = 0;
					if(timeSinceStop > STEPS_TO_STOP)
						stopped = true;
					else
						stopped = false;
					prevSmallAvgPos = smallAvgPos;
				}
			}
			
			// object warp data collect
			warpData = new WarpData();
			for(i = 0; i < planetList.size(); i++)
			{
				currObj = planetList.get(i);
				
				Warpable wItem;
				try {
					wItem = (Warpable) currObj;
				} catch(RuntimeException e) {
					wItem = null;
				}
				if(wItem != null)
				{
					WarpData data = wItem.sendWarpData();
					if(data != null)
					{
						warpData.apply(data);
					}
				}
			}

			if(stopAnimation)
			{
				BounceVibrate.Nullify(); // Nullify here since weird things happen otherwise...
				WarpData data;
				if(stopAnimationTime > 30)
				{
					data = new WarpData(0, 0, 0, true);
				}
				else
					data = new WarpData(0, 0, 0, false);
//				stopAnimationFade = 0;
//				stopAnimationFadeSpeed += 0.05f;
				stopAnimationTime++;
				warpData.apply(data);
			}
			
			if(warpData.endGame)
			{
				setRunning(false);
				endGame = true;
			}
		}

		boolean stopAnimation = false;
		float stopAnimationTime = 0;
		float stopAnimationFade = 0;
		float stopAnimationFadeSpeed = 1;
		
		protected int returnCode;

		protected void startStopping(int messageCode)
		{
			stopAnimation = true;
			returnCode = messageCode;
		}
		
		protected Matrix matrix = new Matrix();
		
		@Override
		protected void predraw(Canvas c)
		{
			c.drawPaint(PaintLoader.load(bgpaint));
			
			if(StaticInfo.Starfield)
			{
				// Draw stars
				//for(int i = 1; i < avgPrevPos.length; i++) // Set speed-line points
				//	avgPrevPos[i - 1] = avgPrevPos[i];
				avgPrevPos[avgPrevPos.length - 1] = avgPos; // Since only one previous line
				
//				c.clipRect(new Rect(
//						(int)((-level.bounds.x - avgPrevPos[0].x) * WORLD_ZOOM_UNSCALED_ZOOMED),
//						(int)((-level.bounds.x - avgPrevPos[0].y) * WORLD_ZOOM_UNSCALED_ZOOMED),
//						(int)((-level.bounds.x + avgPrevPos[0].x) * WORLD_ZOOM_UNSCALED_ZOOMED),
//						(int)((-level.bounds.x + avgPrevPos[0].y) * WORLD_ZOOM_UNSCALED_ZOOMED)
//						), Op.REPLACE); // Set clip
				
				for(int i = 0; i < Bg_points.length; i++) // Draw speed-line
				{
					for(int j = 1; j < avgPrevPos.length; j++)
						
						if(
								Bg_points[i].point.x > avgPrevPos[j - 1].x * Bg_points[i].zoom &
								Bg_points[i].point.x < avgPrevPos[j - 1].x * Bg_points[i].zoom + (width / WORLD_ZOOM_UNSCALED_ZOOMED) &
								Bg_points[i].point.y > avgPrevPos[j - 1].y * Bg_points[i].zoom &
								Bg_points[i].point.y < avgPrevPos[j - 1].y * Bg_points[i].zoom + (height / WORLD_ZOOM_UNSCALED_ZOOMED)
								)
							c.drawLine(
									(float)(Bg_points[i].point.x + (-avgPrevPos[j - 1].x * Bg_points[i].zoom)) * WORLD_ZOOM_UNSCALED_ZOOMED,
									(float)(Bg_points[i].point.y + (-avgPrevPos[j - 1].y * Bg_points[i].zoom)) * WORLD_ZOOM_UNSCALED_ZOOMED,
									(float)(Bg_points[i].point.x + (-avgPrevPos[j].x * Bg_points[i].zoom)) * WORLD_ZOOM_UNSCALED_ZOOMED,
									(float)(Bg_points[i].point.y + (-avgPrevPos[j].y * Bg_points[i].zoom)) * WORLD_ZOOM_UNSCALED_ZOOMED,
									PaintLoader.load(starprevpaint));
				}
				for(int i = 0; i < Bg_points.length; i++) // Draw points - redundant?
					if(
							Bg_points[i].point.x > avgPrevPos[0].x * Bg_points[i].zoom &
							Bg_points[i].point.x < avgPrevPos[0].x * Bg_points[i].zoom + (width / WORLD_ZOOM_UNSCALED_ZOOMED) &
							Bg_points[i].point.y > avgPrevPos[0].y * Bg_points[i].zoom &
							Bg_points[i].point.y < avgPrevPos[0].y * Bg_points[i].zoom + (height / WORLD_ZOOM_UNSCALED_ZOOMED)
							)
					{
						starpaint.stroke = Bg_points[i].size;
						c.drawPoint(
								(float)(Bg_points[i].point.x + (-avgPrevPos[0].x * Bg_points[i].zoom)) * WORLD_ZOOM_UNSCALED_ZOOMED,
								(float)(Bg_points[i].point.y + (-avgPrevPos[0].y * Bg_points[i].zoom)) * WORLD_ZOOM_UNSCALED_ZOOMED,
								PaintLoader.load(starpaint));
					}
			}
		}
		
		int i, iter;
		
		@Override
		protected void draw(Canvas c)
		{
			super.draw(c);
			// DRAW TIME
			
			// Object draw
			for(i = 0; i < planetList.size(); i++)
			{
				currObj = planetList.get(i);
				currObj.draw(c, 1);
			}
			
			p.draw(c, 1);
			
//			if(stopped)
//				c.drawText("MS: " + millistep + ", Spare millis: " + (MAX_MILLIS - System.currentTimeMillis() + prevTime), 20, 20, PaintLoader.load(txtpaint));
			
			/*c.drawLine(
					(float)(p.itemC.x),
					(float)(p.itemC.y),
					(float)((p.itemC.x + p.itemVC.x)),
					(float)((p.itemC.y + p.itemVC.y)),
					txtpaint);*/
			
			txtpaint.a = 255;
			txtpaint.r = 255;
			txtpaint.g = borderBounceColour;
			txtpaint.b = borderBounceColour;
			borderBounceColour += 20;
			if(borderBounceColour > 255) borderBounceColour = 255;
			c.drawLine( // Draw outer bounds
					(float)-level.bounds.x,
					(float)level.bounds.y,
					(float)level.bounds.x,
					(float)level.bounds.y, PaintLoader.load(txtpaint));
			c.drawLine(
					(float)level.bounds.x,
					(float)-level.bounds.y,
					(float)level.bounds.x,
					(float)level.bounds.y, PaintLoader.load(txtpaint));
			c.drawLine(
					(float)-level.bounds.x,
					(float)level.bounds.y,
					(float)-level.bounds.x,
					(float)-level.bounds.y, PaintLoader.load(txtpaint));
			c.drawLine(
					(float)level.bounds.x,
					(float)-level.bounds.y,
					(float)-level.bounds.x,
					(float)-level.bounds.y, PaintLoader.load(txtpaint));
			txtpaint.a = 255;
			txtpaint.r = 255;
			txtpaint.g = 255;
			txtpaint.b = 255;

			// DRAW TIME for objects on top of ball
			for(i = 0; i < planetList.size(); i++)
			{
				currObj = planetList.get(i);
				TopDrawable item;
				try {
					item = (TopDrawable) currObj;
				} catch(RuntimeException e) {
					item = null;
				}
				if(item != null)
				{
					item.drawTop(c, 1);
				}
			}
		}
		
		@Override
		protected void postdraw(Canvas c)
		{
			// Apply warpData, part 2. Part 1 is not done here, but in a non-abstract class, in scale()
			warpDataPaint.a = (int) CompuFuncs.TrimMax(warpData.fade, 255);
			warpDataPaint.r = 0;
			warpDataPaint.g = 0;
			warpDataPaint.b = 0;
			c.drawRect(new Rect(0, 0, width, height), PaintLoader.load(warpDataPaint));
		}
		
		@Override
		public synchronized void saveState(Bundle bundle)
		{
			Log.v("SpaceGame", "State Saved");
			bundle.putSerializable("p.itemC", p.itemC);
			bundle.putSerializable("p.itemVC", p.itemVC);
			bundle.putSerializable("avgPos", avgPos);
			
			for(int i = 0; i < avgPrevPos.length; i++)
				bundle.putSerializable("avgPrevPos" + i, avgPrevPos[i]);
			for(int i = 0; i < screenPos.length; i++)
				bundle.putSerializable("screenPos" + i, screenPos[i]);
			bundle.putFloat("userZoom", userZoom);
		}

		@Override
		public synchronized void restoreState(Bundle bundle)
		{
			Log.v("SpaceGame", "State Restored");
			
			p.itemC = (Coord) bundle.getSerializable("p.itemC");
			p.itemVC = (Coord) bundle.getSerializable("p.itemVC");
			avgPos = (Coord) bundle.getSerializable("avgPos");
			for(int i = 0; i < avgPrevPos.length; i++)
				avgPrevPos[i] = (Coord) bundle.getSerializable("avgPrevPos" + i);
			for(int i = 0; i < screenPos.length; i++)
				screenPos[i] = (Coord) bundle.getSerializable("screenPos" + i);
			
			userZoom = bundle.getFloat("userZoom");
		}
	}
	
	protected static class BgPoint
	{
		Coord point;
		float zoom;
		float size;

		private static final Random rg = new Random();
		
		public BgPoint(Coord point, float zoom, float size)
		{
			this.point = point;
			this.zoom = zoom;
			this.size = size;
		}
		
		/**
		 * Constucts a new BgPoint with random settings
		 */
		public BgPoint()
		{
			point = new Coord(
					rg.nextDouble() * ViewThread.BG_REPEAT_X * 2 - ViewThread.BG_REPEAT_X,
					rg.nextDouble() * ViewThread.BG_REPEAT_Y * 2 - ViewThread.BG_REPEAT_Y);
			zoom = rg.nextFloat() * (ViewThread.BG_ZOOM_2 - ViewThread.BG_ZOOM_1) + ViewThread.BG_ZOOM_1;
			size = rg.nextFloat() * 1.5f + 1;
		}
	}

	public void stop(int messageCode)
	{
		thread.startStopping(messageCode);
	}
}
