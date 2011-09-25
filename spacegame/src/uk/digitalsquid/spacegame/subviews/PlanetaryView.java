package uk.digitalsquid.spacegame.subviews;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.xml.sax.SAXException;

import uk.digitalsquid.spacegame.levels.LevelItem;
import uk.digitalsquid.spacegame.levels.SaxLoader;
import uk.digitalsquid.spacegame.spaceitem.assistors.BgPoints;
import uk.digitalsquid.spacegame.spaceitem.assistors.LaunchingMechanism;
import uk.digitalsquid.spacegame.spaceitem.assistors.Simulation;
import uk.digitalsquid.spacegame.spaceitem.assistors.Simulation.SimulationCallbackListener;
import uk.digitalsquid.spacegame.spaceitem.items.AnimatedPlayer;
import uk.digitalsquid.spacegame.spaceitem.items.Player;
import uk.digitalsquid.spacegame.spaceitem.items.PlayerBase;
import uk.digitalsquid.spacegame.spaceitem.items.Portal;
import uk.digitalsquid.spacegame.spaceitem.items.Star;
import uk.digitalsquid.spacegamelib.CompuFuncs;
import uk.digitalsquid.spacegamelib.SimulationContext;
import uk.digitalsquid.spacegamelib.StaticInfo;
import uk.digitalsquid.spacegamelib.VecHelper;
import uk.digitalsquid.spacegamelib.gl.Lines;
import uk.digitalsquid.spacegamelib.gl.RectMesh;
import uk.digitalsquid.spacegamelib.misc.LevelWall;
import uk.digitalsquid.spacegamelib.spaceitem.Rectangular;
import uk.digitalsquid.spacegamelib.spaceitem.SpaceItem;
import uk.digitalsquid.spacegamelib.spaceitem.Spherical;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Moveable;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.TopDrawable;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Warpable;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Warpable.WarpData;
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
	
	public static abstract class ViewWorker extends DrawBaseView.ViewWorker implements SimulationCallbackListener, ContactListener
	{
		protected SimulationContext sim;
		
		protected float WORLD_ZOOM;
		protected float WORLD_ZOOM_UNSCALED_ZOOMED;
		protected float WORLD_ZOOM_PRESCALE, WORLD_ZOOM_POSTSCALE;
		
		public static final int ITERS = Simulation.ITERS;
		
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
			WORLD_ZOOM = userZoom / 100 * warpData.zoom; // Assuming all screens are about the same ratio?
			WORLD_ZOOM_UNSCALED_ZOOMED = userZoom / 100 * warpData.zoom;
			WORLD_ZOOM_PRESCALE = warpData.zoom;
			WORLD_ZOOM_POSTSCALE = userZoom / 100;
		}
		
		private RectMesh warpDataPaint = new RectMesh(0, 0, scaledWidth, scaledHeight, 0, 0, 0, 0);


		private int BG_POINTS_PER_AREA = 1000;

		private BgPoints bgPoints;
		protected Vec2 avgPos = new Vec2();
		
		protected static final int SCROLL_SPEED = 20;
		/**
		 * Used to scroll the scene smoothly
		 */
		protected Vec2[] screenPos = new Vec2[SCROLL_SPEED];
		
		protected LevelItem level;
		protected List<SpaceItem> planetList;
		
		//protected Vec2 p.itemC, p.itemVC, p.itemRF;

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
		// protected Tether tether;
		protected LaunchingMechanism launch;
		
		@Override
		protected void initialiseOnThread()
		{
			Log.i("SpaceGame", "Loading level...");
			
			World world = new World(new Vec2(), true);
			sim = new SimulationContext(context, world);
			// world.setContactListener(this);
			
			boolean loadError = false;
			try {
				level = SaxLoader.parse(sim, CompuFuncs.decodeIStream(xml));
			} catch (SAXException e) {
				Log.e("SpaceGame", "Error parsing level: error in data. (Message: " + e.getMessage() + ")");
				e.printStackTrace();
				loadError = true;
			} catch (IOException e) {
				Log.e("SpaceGame", "Error loading level from data source. (Message: " + e.getMessage() + ")");
				e.printStackTrace();
				loadError = true;
			}
			if(loadError) {
				setRunning(false);
				return;
			}
			
			level.initialiseBox2D(sim);
			
			planetList = level.planetList;
			if(planetList == null)
				planetList = new ArrayList<SpaceItem>();

			p = new AnimatedPlayer(sim, level.startPos, level.startSpeed);
//			p.itemC = new Vec2(level.startPos);
//			p.itemVC = new Vec2();
//			p.itemRF = new Vec2();
			
			levelBorder = new Lines(0, 0, new float[] {
					(float) -level.bounds.x / 2, (float) -level.bounds.y / 2, 0,
					(float) +level.bounds.x / 2, (float) -level.bounds.y / 2, 0,
					(float) +level.bounds.x / 2, (float) +level.bounds.y / 2, 0,
					(float) -level.bounds.x / 2, (float) +level.bounds.y / 2, 0
			}, GL10.GL_LINE_LOOP, 1, 1, 1, 1);
			
			portal = new Portal(sim, level.portal);
			// tether = new Tether(sim, p);
			launch = new LaunchingMechanism(sim, p);
			s = new Simulation(this);
			
			for(int i = 0; i < screenPos.length; i++)
				screenPos[i] = new Vec2();
			
			// BG Points
			BG_POINTS_PER_AREA = (int) level.bounds.length() * 30;
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
		
		private Vec2 prevSmallAvgPos;
		private int timeSinceStop;
		private static final float STOPPING_SPEED = 1.5f;
		private static final int STEPS_TO_STOP = 30 * 1; // 1 second.
		
		@Override
		protected void calculate()
		{
			super.calculate();
			
			s.calculate(sim, level, p, portal, launch, paused, gravOn, (int) millistep);
			
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
					launch.move(millistep, Simulation.SPEED_SCALE);
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
				launch.drawMove(millistep, Simulation.SPEED_SCALE);
				
				// Work out if no longer moving, from last 4 positions
				
				Vec2 smallAvgPos = new Vec2();
				for(int i = screenPos.length - 4; i < screenPos.length; i++)
				{
					smallAvgPos.addLocal(screenPos[i]);
				}
				smallAvgPos.x /= 4;
				smallAvgPos.y /= 4;
				
				if(prevSmallAvgPos == null)
					prevSmallAvgPos = smallAvgPos;
				
				if(VecHelper.dist(prevSmallAvgPos, screenPos[screenPos.length - 1]) < STOPPING_SPEED)
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
				Vec2 lowestPoint = null;
				for(SpaceItem item : planetList) {
					if(item.getClass().equals(Star.class)) {
						if(lowestDist == -1) lowestDist = VecHelper.dist(item.getPos(), p.itemC);
						double thisDist = VecHelper.dist(item.getPos(), p.itemC);
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
				Vec2 lowestPoint = new Vec2();
				for(SpaceItem item : planetList) {
					if(!(item instanceof Star)) {
						if(item instanceof Spherical || item instanceof Rectangular) {
							if(lowestDist == -1) lowestDist = VecHelper.dist(item.getPos(), p.itemC);
							float thisDist = VecHelper.dist(item.getPos(), p.itemC);
							if(thisDist <= lowestDist) {
								lowestDist = thisDist;
								lowestPoint.set(item.getPos());
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
			launch.draw(gl);
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
					((TopDrawable)obj).drawTop(gl, 1);
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
			bundle.putSerializable("p.itemVC", p.getVelocity());
			bundle.putSerializable("avgPos", avgPos);
			
			for(int i = 0; i < screenPos.length; i++)
				bundle.putSerializable("screenPos" + i, screenPos[i]);
			bundle.putFloat("userZoom", userZoom);
		}

		@Override
		public synchronized void restoreState(Bundle bundle)
		{
			Log.v("SpaceGame", "State Restored");
			
			p.itemC.set((Vec2) bundle.getSerializable("p.itemC"));
			p.setVelocity((Vec2) bundle.getSerializable("p.itemVC"));
			avgPos = (Vec2) bundle.getSerializable("avgPos");
			for(int i = 0; i < screenPos.length; i++)
				screenPos[i] = (Vec2) bundle.getSerializable("screenPos" + i);
			
			userZoom = bundle.getFloat("userZoom");
		}

		@Override
		public void beginContact(Contact contact) {
			Object gameObject1 = contact.getFixtureA().getUserData();
			Object gameObject2 = contact.getFixtureB().getUserData();
			
			if(
					(gameObject1 instanceof LevelWall && gameObject2 instanceof PlayerBase) ||
					(gameObject2 instanceof LevelWall && gameObject1 instanceof PlayerBase)) { // Player hit wall
				wallBounced(1); // TODO: Change?
			}
		}

		@Override
		public void endContact(Contact contact) { }

		@Override
		public void postSolve(Contact contact, ContactImpulse impulse) { }

		@Override
		public void preSolve(Contact contact, Manifold oldManifold) { }
	}
	
	public void stop(int messageCode)
	{
		thread.startStopping(messageCode);
	}
}
