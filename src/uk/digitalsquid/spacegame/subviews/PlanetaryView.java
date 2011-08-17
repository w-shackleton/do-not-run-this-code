package uk.digitalsquid.spacegame.subviews;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.xml.sax.SAXException;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.StaticInfo;
import uk.digitalsquid.spacegame.levels.LevelItem;
import uk.digitalsquid.spacegame.levels.SaxLoader;
import uk.digitalsquid.spacegame.misc.Lines;
import uk.digitalsquid.spacegame.misc.RectMesh;
import uk.digitalsquid.spacegame.spaceitem.Bounceable;
import uk.digitalsquid.spacegame.spaceitem.BounceableRect;
import uk.digitalsquid.spacegame.spaceitem.CompuFuncs;
import uk.digitalsquid.spacegame.spaceitem.SpaceItem;
import uk.digitalsquid.spacegame.spaceitem.assistors.BgPoints;
import uk.digitalsquid.spacegame.spaceitem.assistors.Simulation;
import uk.digitalsquid.spacegame.spaceitem.assistors.Simulation.SimulationCallbackListener;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Moveable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.TopDrawable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Warpable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Warpable.WarpData;
import uk.digitalsquid.spacegame.spaceitem.items.AnimatedPlayer;
import uk.digitalsquid.spacegame.spaceitem.items.Player;
import uk.digitalsquid.spacegame.spaceitem.items.Portal;
import uk.digitalsquid.spacegame.spaceitem.items.Star;
import uk.digitalsquid.spacegame.spaceitem.items.Tether;
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
	
	public static abstract class ViewWorker extends DrawBaseView.ViewWorker implements SimulationCallbackListener
	{
		protected float WORLD_ZOOM;
		protected float WORLD_ZOOM_UNSCALED_ZOOMED;
		protected float WORLD_ZOOM_PRESCALE, WORLD_ZOOM_POSTSCALE;
		
		public static final int ITERS = 5;
		
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
			WORLD_ZOOM_PRESCALE = 1f / SpaceItem.ITEM_SCALE * warpData.zoom;
			WORLD_ZOOM_POSTSCALE = userZoom / 100;
		}
		
		private RectMesh warpDataPaint = new RectMesh(0, 0, scaledWidth, scaledHeight, 0, 0, 0, 0);


		private int BG_POINTS_PER_AREA = 1000;

		private BgPoints bgPoints;
		protected Coord avgPos = new Coord();
		
		protected static final int SCROLL_SPEED = 20;
		/**
		 * Used to scroll the scene smoothly
		 */
		protected Coord[] screenPos = new Coord[SCROLL_SPEED];
		
		protected LevelItem level;
		protected List<SpaceItem> planetList;
		
		//protected Coord p.itemC, p.itemVC, p.itemRF;

		protected boolean paused = false;
		private Object pauseNotify = new Object();
		
		protected static final int GAME_STATE_MOVING = 0;
		protected static final int GAME_STATE_STOPPED = 1;
		protected static final int GAME_STATE_AIMING = 2;
		
		protected int state = GAME_STATE_MOVING;
		protected boolean gravOn = true;
		
		protected float borderBounceColour = 255;
		
		public void setPaused(boolean p)
		{
			synchronized(pauseNotify) {
				paused = p;
				pauseNotify.notifyAll();
			}
		}
		
		protected Player p;
		protected Portal portal;
		protected Tether tether;
		
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
			tether = new Tether(context);
			s = new Simulation(this);
			
			for(int i = 0; i < screenPos.length; i++)
				screenPos[i] = new Coord();
			
			// BG Points
			BG_POINTS_PER_AREA = (int) level.bounds.getLength();
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
		
		public abstract void wallBounced(float amount);
		
		protected Simulation s;
		
		private Coord prevSmallAvgPos;
		private int timeSinceStop;
		private static final float STOPPING_SPEED = 1.5f;
		private static final int STEPS_TO_STOP = 30 * 1; // 1 second.
		
		@Override
		protected void calculate()
		{
			super.calculate();
			
			s.calculate(level, p, portal, tether, paused, gravOn, (int) millistep, false);
			
			for(int i = 0; i < ITERS; i++) {
				if(!paused) { // Animated move
					for(SpaceItem obj : planetList) {
						// Stage for object animation / movement
						if(obj instanceof Moveable)
						{
							((Moveable) obj).move((int) millistep, Simulation.SPEED_SCALE);
						}
					}
					p.move(millistep, Simulation.SPEED_SCALE);
					portal.move(millistep, Simulation.SPEED_SCALE);
				}
			}
			
			if(!paused)
			{
				for(SpaceItem obj : planetList)
				{
					// Stage for object animation / movement
					if(obj instanceof Moveable)
					{
						((Moveable) obj).drawMove((int) millistep, Simulation.SPEED_SCALE);
					}
				}
				
				// Move ball - animation
				p.drawMove(millistep, Simulation.SPEED_SCALE);
				portal.calculateAnimation(p);
				portal.drawMove(millistep, Simulation.SPEED_SCALE);
				tether.drawMove(millistep, Simulation.SPEED_SCALE);
				
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
				if(timeSinceStop > STEPS_TO_STOP && state == GAME_STATE_MOVING)
					state = GAME_STATE_STOPPED;
				prevSmallAvgPos = smallAvgPos;
			}
			
			setNearestLookPoint();
			
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
			{
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
			
			{
				double lowestDist = -1;
				Coord lowestPoint = new Coord();
				for(SpaceItem item : planetList) {
					if(!item.getClass().equals(Star.class)) {
						if(item instanceof Bounceable || item instanceof BounceableRect) {
							if(lowestDist == -1) lowestDist = Coord.getLength(item.getPos(), p.itemC);
							double thisDist = Coord.getLength(item.getPos(), p.itemC);
							if(thisDist < lowestDist) {
								lowestDist = thisDist;
								lowestPoint.copyFrom(item.getPos());
							}
						}
					}
				}
				
				if(lowestPoint != null) {
					p.setNearestLandingPoint(lowestPoint);
				}
			}
		}
		
		@Override
		public void onStop() {
			if(state == GAME_STATE_MOVING) {
				state = GAME_STATE_STOPPED;
			}
			p.openLanding();
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
		}
		
		int i, iter;
		
		protected Lines levelBorder;
		
		@Override
		protected void draw(GL10 gl)
		{
			super.draw(gl);
			// DRAW TIME
			
			if(StaticInfo.Starfield)
			{
				bgPoints.draw(gl);
			}
			
			// Object draw
			for(SpaceItem item : planetList)
			{
				item.draw(gl, 1);
			}
			
			portal.draw(gl, 1);
			tether.draw(gl, 1);
			p.draw(gl, 1);
			
			levelBorder.setColour(1, 1, borderBounceColour, borderBounceColour);
			levelBorder.draw(gl);
			borderBounceColour += 0.05;
			if(borderBounceColour > 1) borderBounceColour = 1;

			// DRAW TIME for objects on top of ball
			for(SpaceItem obj : planetList)
			{
				if(obj instanceof TopDrawable)
				{
					((TopDrawable)obj).drawTop(gl, 1); // TODO: Re-enable!
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
		protected void onSizeChanged(int w, int h) {
			warpDataPaint.setWH(scaledWidth, scaledHeight);
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
