package uk.digitalsquid.spacegame.subviews;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.xml.sax.SAXException;

import uk.digitalsquid.spacegame.BounceVibrate;
import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.StaticInfo;
import uk.digitalsquid.spacegame.levels.LevelItem;
import uk.digitalsquid.spacegame.levels.SaxLoader;
import uk.digitalsquid.spacegame.misc.Lines;
import uk.digitalsquid.spacegame.misc.RectMesh;
import uk.digitalsquid.spacegame.spaceitem.CompuFuncs;
import uk.digitalsquid.spacegame.spaceitem.SpaceItem;
import uk.digitalsquid.spacegame.spaceitem.assistors.BgPoints;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Forceful;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Forceful.BallData;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Moveable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.TopDrawable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Warpable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Warpable.WarpData;
import uk.digitalsquid.spacegame.spaceitem.items.AnimatedPlayer;
import uk.digitalsquid.spacegame.spaceitem.items.Player;
import uk.digitalsquid.spacegame.spaceitem.items.Portal;
import uk.digitalsquid.spacegame.spaceitem.items.Star;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;

public abstract class PlanetaryView<VT extends PlanetaryView.ViewWorker> extends DrawBaseView<VT>
{
	public PlanetaryView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public static abstract class ViewWorker extends DrawBaseView.ViewWorker
	{
		protected float WORLD_ZOOM;
		protected float WORLD_ZOOM_UNSCALED_ZOOMED;
		protected float WORLD_ZOOM_PRESCALE, WORLD_ZOOM_POSTSCALE;
		
		protected static final float WALL_BOUNCINESS = 0.8f;
		
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
		
		public ViewWorker(Context context, InputStream xml)
		{
			super(context);
			
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
			WORLD_ZOOM = 1f / SpaceItem.ITEM_SCALE *
					userZoom / 100 * warpData.zoom; // Assuming all screens are about the same ratio?
			WORLD_ZOOM_UNSCALED_ZOOMED = userZoom / 100 * warpData.zoom;
			WORLD_ZOOM_PRESCALE = 1f / SpaceItem.ITEM_SCALE;
			WORLD_ZOOM_POSTSCALE = userZoom / 100 * warpData.zoom;
		}
		
		private RectMesh warpDataPaint = new RectMesh(0, 0, scaledWidth, scaledHeight, 0, 0, 0, 0);


		private static final int BG_POINTS_PER_AREA = 1000;

		private BgPoints bgPoints;
		protected Coord avgPos = new Coord();
		
		protected static final int SCROLL_SPEED = 15;
		/**
		 * Used to scroll the scene smoothly
		 */
		protected Coord[] screenPos = new Coord[SCROLL_SPEED];
		
		private Coord prevSmallAvgPos;
		private int timeSinceStop;
		private static final float STOPPING_SPEED = 1.5f;
		private static final int STEPS_TO_STOP = 60 * ITERS * 1; // 1 second.
		
		
		protected LevelItem level;
		protected List<SpaceItem> planetList;
		
		private SpaceItem currObj;
		//protected Coord p.itemC, p.itemVC, p.itemRF;

		protected boolean paused = false;
		private Object pauseNotify = new Object();
		protected boolean stopped = false;
		protected boolean gravOn = true;
		
		private float borderBounceColour = 255;
		
		public void setPaused(boolean p)
		{
			synchronized(pauseNotify) {
				paused = p;
				pauseNotify.notifyAll();
			}
		}
		
		protected Player p;
		protected Portal portal;
		
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
				return;
			}
			
			planetList = level.planetList;
			if(planetList == null)
				planetList = new ArrayList<SpaceItem>();

			p = new AnimatedPlayer(context, level.startPos, level.startSpeed);
//			p.itemC = new Coord(level.startPos);
//			p.itemVC = new Coord();
//			p.itemRF = new Coord();
			
			levelBorder = new Lines(0, 0, new float[] {
					(float) -level.bounds.x / 2, (float) -level.bounds.y / 2, 0,
					(float) +level.bounds.x / 2, (float) -level.bounds.y / 2, 0,
					(float) +level.bounds.x / 2, (float) +level.bounds.y / 2, 0,
					(float) -level.bounds.x / 2, (float) +level.bounds.y / 2, 0
			}, GL10.GL_LINE_LOOP, 1, 1, 1, 1);
			
			portal = new Portal(context, level.portal);
			
			for(int i = 0; i < screenPos.length; i++)
				screenPos[i] = new Coord();
			
			// BG Points
			bgPoints = new BgPoints(BG_POINTS_PER_AREA, (int)level.bounds.x, (int)level.bounds.y);
			
			startTime = System.currentTimeMillis();
		}
		
		protected long startTime, finishTime;
		
		@Override
		public synchronized void setRunning(boolean run) {
			super.setRunning(run);
			if(!run)
				setPaused(false);
		}
		
		/**
		 * Used to pause the game properly
		 */
		@Override
		protected void afterdraw() {
			super.afterdraw();
			synchronized(pauseNotify) {
				if(paused && running) {
					try {
						pauseNotify.wait();
					} catch (InterruptedException e) {
					}
				}
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
					if(p.itemC.x > level.bounds.x / 2 - AnimatedPlayer.BALL_RADIUS)
					{
						p.itemVC.x = -p.itemVC.x * WALL_BOUNCINESS;
						if(p.itemVC.x > 0) // Solves physics error
							p.itemVC.x = 0;
						borderBounceColour = 0; // Set bounce colour to 0 - which is red.
						BounceVibrate.Vibrate((long) (p.itemVC.getLength() / SpaceItem.ITEM_SCALE * 2));
					}
					if(p.itemC.x < -(level.bounds.x / 2 - AnimatedPlayer.BALL_RADIUS))
					{
						p.itemVC.x = -p.itemVC.x * WALL_BOUNCINESS;
						if(p.itemVC.x < 0)
							p.itemVC.x = 0;
						borderBounceColour = 0; // Set bounce colour to 0 - which is red.
						BounceVibrate.Vibrate((long) (p.itemVC.getLength() / SpaceItem.ITEM_SCALE * 2));
					}
					if(p.itemC.y > level.bounds.y / 2 - AnimatedPlayer.BALL_RADIUS)
					{
						p.itemVC.y = -p.itemVC.y * WALL_BOUNCINESS;
						if(p.itemVC.y > 0) // Solves physics error
							p.itemVC.y = 0;
						borderBounceColour = 0; // Set bounce colour to 0 - which is red.
						BounceVibrate.Vibrate((long) (p.itemVC.getLength() / SpaceItem.ITEM_SCALE * 2));
					}
					if(p.itemC.y < -(level.bounds.y / 2 - AnimatedPlayer.BALL_RADIUS))
					{
						p.itemVC.y = -p.itemVC.y * WALL_BOUNCINESS;
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
				
				p.itemRF.reset();
				for(i = 0; i < planetList.size(); i++)
				{
					currObj = planetList.get(i);
					
					if(!paused)
					{
						if(currObj instanceof Forceful)
						{
							Forceful item = (Forceful) currObj;
							if(gravOn) // Stage for gravity forces
							{
								p.itemRF.addThis(item.calculateRF(p.itemC, p.itemVC));
							}
							
							// Stage for velocity changes
							BallData data = item.calculateVelocity(p.itemC, p.itemVC, AnimatedPlayer.BALL_RADIUS);
							if(data != null)
							{
								if(data.itemC != null)
									p.itemC.copyFrom(data.itemC);
								if(data.itemVC != null)
									p.itemVC.copyFrom(data.itemVC);
							}
						}
					}

					if(!paused)
					{
						// Stage for object animation / movement
						if(currObj instanceof Moveable)
						{
							((Moveable) currObj).move((int) millistep, SPEED_SCALE);
						}
					}
				}
				
				if(gravOn)
					p.itemRF.addThis(portal.calculateRF(p.itemC, p.itemVC));
				
				if(!paused) {
					portal.move((int) millistep, SPEED_SCALE);
					
					BallData data = portal.calculateVelocity(p, Player.BALL_RADIUS);
					if(data != null)
					{
						if(data.itemC != null)
							p.itemC.copyFrom(data.itemC);
						if(data.itemVC != null)
							p.itemVC.copyFrom(data.itemVC);
					}
				}
				
				setNearestLookPoint();
				
				if(!paused)
				{
					p.itemVC.x += p.itemRF.x * millistep / ITERS / 1000f;
					p.itemVC.y += p.itemRF.y * millistep / ITERS / 1000f;
					
					p.itemC.x  += p.itemVC.x * millistep / ITERS / 1000f * SPEED_SCALE;
					p.itemC.y  += p.itemVC.y * millistep / ITERS / 1000f * SPEED_SCALE;
					
					// Air resistance
					p.itemVC.scaleThis(AIR_RESISTANCE);
					
					// Move ball - animation
					p.move(millistep, SPEED_SCALE);
					
					// Work out if no longer moving, from last 4 positions
					
					Coord smallAvgPos = new Coord();
					for(int i = screenPos.length - 4; i < screenPos.length; i++)
					{
						smallAvgPos.addThis(screenPos[i]);
					}
					smallAvgPos.x /= 4;
					smallAvgPos.y /= 4;
					
					if(prevSmallAvgPos == null)
						prevSmallAvgPos = smallAvgPos;
					
					if(Coord.getLength(prevSmallAvgPos, screenPos[screenPos.length - 1]) < STOPPING_SPEED)
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
			warpData.reset();
			for(SpaceItem currObj : planetList)
			{
				if(currObj instanceof Warpable)
				{
					WarpData data = ((Warpable)currObj).sendWarpData();
					if(data != null)
					{
						warpData.apply(data);
					}
				}
			}
			{
				WarpData data = portal.sendWarpData();
				if(data != null)
					warpData.apply(data);
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
			if(warpData.stopTimer) {
				finishTime = System.currentTimeMillis();
			}
		}
		
		private void setNearestLookPoint() {
			double lowestDist = -1;
			Coord lowestPoint = null;
			for(SpaceItem item : planetList) {
				if(item.getClass().equals(Star.class)) {
					if(lowestDist == -1) lowestDist = Coord.getLength(item.getPos(), p.itemC);
					double thisDist = Coord.getLength(item.getPos(), p.itemC);
					if(thisDist < lowestDist) {
						lowestDist = thisDist;
						lowestPoint = item.getPos();
					}
				}
			}
			
			if(lowestPoint != null) {
				p.lookTo(lowestPoint);
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
		
		@Override
		protected void predraw(GL10 gl)
		{
			if(StaticInfo.Starfield)
			{
				bgPoints.draw(gl);
			}
		}
		
		int i, iter;
		
		private Lines levelBorder;
		
		@Override
		protected void draw(GL10 gl)
		{
			super.draw(gl);
			// DRAW TIME
			
			// Object draw
			for(SpaceItem item : planetList)
			{
				item.draw(gl, 1);
			}
			
			// portal.draw(c, 1);
			// p.draw(c, 1);
			
			levelBorder.setColour(1, 1, borderBounceColour, borderBounceColour);
			levelBorder.draw(gl);
			borderBounceColour += 0.05;
			if(borderBounceColour > 1) borderBounceColour = 1;

			// DRAW TIME for objects on top of ball
			for(SpaceItem obj : planetList)
			{
				if(obj instanceof TopDrawable)
				{
					// ((TopDrawable)obj).drawTop(c, 1); // TODO: Re-enable!
				}
			}
		}
		
		@Override
		protected void postdraw(GL10 gl)
		{
			// Apply warpData, part 2. Part 1 is not done here, but in a non-abstract class, in scale()
			warpDataPaint.setAlpha((float) CompuFuncs.TrimMax(warpData.fade / 256, 1));
			if(warpDataPaint.getAlpha() != 0) {
				warpDataPaint.draw(gl);
			}
		}
		
		@Override
		public synchronized void saveState(Bundle bundle)
		{
			Log.v("SpaceGame", "State Saved");
			bundle.putSerializable("p.itemC", p.itemC);
			bundle.putSerializable("p.itemVC", p.itemVC);
			bundle.putSerializable("avgPos", avgPos);
			
			for(int i = 0; i < screenPos.length; i++)
				bundle.putSerializable("screenPos" + i, screenPos[i]);
			bundle.putFloat("userZoom", userZoom);
		}

		@Override
		public synchronized void restoreState(Bundle bundle)
		{
			Log.v("SpaceGame", "State Restored");
			
			p.itemC.copyFrom((Coord) bundle.getSerializable("p.itemC"));
			p.itemVC.copyFrom((Coord) bundle.getSerializable("p.itemVC"));
			avgPos = (Coord) bundle.getSerializable("avgPos");
			for(int i = 0; i < screenPos.length; i++)
				screenPos[i] = (Coord) bundle.getSerializable("screenPos" + i);
			
			userZoom = bundle.getFloat("userZoom");
		}
	}
	
	public void stop(int messageCode)
	{
		thread.startStopping(messageCode);
	}
}
