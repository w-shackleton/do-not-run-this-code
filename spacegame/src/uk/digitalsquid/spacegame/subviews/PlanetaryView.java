package uk.digitalsquid.spacegame.subviews;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.xml.sax.SAXException;

import uk.digitalsquid.spacegame.levels.LevelItem;
import uk.digitalsquid.spacegame.levels.SaxLoader;
import uk.digitalsquid.spacegame.spaceitem.assistors.BgPoints;
import uk.digitalsquid.spacegame.spaceitem.assistors.LaunchingMechanism;
import uk.digitalsquid.spacegame.spaceitem.assistors.Simulation;
import uk.digitalsquid.spacegame.spaceitem.items.AnimatedPlayer;
import uk.digitalsquid.spacegame.spaceitem.items.Player;
import uk.digitalsquid.spacegame.spaceitem.items.PlayerBase;
import uk.digitalsquid.spacegame.spaceitem.items.Portal;
import uk.digitalsquid.spacegame.spaceitem.items.Star;
import uk.digitalsquid.spacegamelib.CompuFuncs;
import uk.digitalsquid.spacegamelib.Geometry;
import uk.digitalsquid.spacegamelib.SimulationContext;
import uk.digitalsquid.spacegamelib.StaticInfo;
import uk.digitalsquid.spacegamelib.VecHelper;
import uk.digitalsquid.spacegamelib.gl.Lines;
import uk.digitalsquid.spacegamelib.gl.RectMesh;
import uk.digitalsquid.spacegamelib.misc.LevelWall;
import uk.digitalsquid.spacegamelib.spaceitem.SpaceItem;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Moveable;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.TopDrawable;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Warpable;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Warpable.WarpData;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

public abstract class PlanetaryView<VT extends PlanetaryView.ViewWorker> extends DrawBaseView<VT>
{
	public PlanetaryView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public static abstract class ViewWorker extends DrawBaseView.ViewWorker implements ContactListener
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

		private BgPoints[] bgPoints;
		
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
		
		protected static final int GAME_STATE_MOVING = 0;
		protected static final int GAME_STATE_STOPPED = 1;
		protected static final int GAME_STATE_AIMING = 2;
		
		protected int state = GAME_STATE_MOVING;
		protected boolean gravOn = true;
		
		protected float borderBounceColour = 255;
		
		public void setPaused(boolean p) {
			paused = p;
		}
		
		public Player p;
		protected Portal portal;
		// protected Tether tether;
		protected LaunchingMechanism launch;
		
		protected boolean initialised = false;
		
		/**
		 * If not null this should be restored in the thread
		 */
		GameState toRestore;
		Object toRestoreLock = new Object();
		
		@Override
		protected void initialiseOnThread() {
			initialised = true;
			Log.i(TAG, "Loading level..." + hashCode());
			
			// Make sure box2d has enough vertices
			Settings.maxPolygonVertices = Geometry.SHAPE_RESOLUTION;
			World world = new World(new Vec2());
			sim = new SimulationContext(context, world);
			// world.setContactListener(this);
			
			boolean loadError = false;
			try {
				level = SaxLoader.parse(sim, CompuFuncs.decodeIStream(xml));
			} catch (SAXException e) {
				Log.e(TAG, "Error parsing level: error in data. (Message: " + e.getMessage() + ")");
				e.printStackTrace();
				loadError = true;
			} catch (IOException e) {
				Log.e(TAG, "Error loading level from data source. (Message: " + e.getMessage() + ")");
				e.printStackTrace();
				loadError = true;
			}
			if(loadError) {
				stop();
				return;
			}
			
			level.initialiseBox2D(sim);
			
			planetList = level.planetList;
			if(planetList == null)
				planetList = new ArrayList<SpaceItem>();
			
			// Restore state
			synchronized(toRestoreLock) {
				if(toRestore != null) {
					level.startPos.set(toRestore.userPos);
					level.startSpeed.set(toRestore.userVelocity);
					avgPos = toRestore.avgPos;
					screenPos = toRestore.screenPos;
					
					userZoom = toRestore.userZoom;
				} else {
					for(int i = 0; i < screenPos.length; i++)
						screenPos[i] = new Vec2();
				}
			}

			p = new AnimatedPlayer(sim, level.startPos, level.startSpeed);
			
			levelBorder = new Lines(0, 0, new float[] {
					(float) -level.bounds.x / 2, (float) -level.bounds.y / 2, 0,
					(float) +level.bounds.x / 2, (float) -level.bounds.y / 2, 0,
					(float) +level.bounds.x / 2, (float) +level.bounds.y / 2, 0,
					(float) -level.bounds.x / 2, (float) +level.bounds.y / 2, 0
			}, GL10.GL_LINE_LOOP, 1, 1, 1, 1);
			
			portal = new Portal(sim, level.portal);
			// tether = new Tether(sim, p);
			launch = new LaunchingMechanism(sim, p);
			s = new Simulation();
			
			// BG Points
			BG_POINTS_PER_AREA = (int) level.bounds.length() * 5;
			bgPoints = new BgPoints[6];
			bgPoints[0] = new BgPoints(BG_POINTS_PER_AREA, (int)level.bounds.x, (int)level.bounds.y, 0.1f);
			bgPoints[1] = new BgPoints(BG_POINTS_PER_AREA, (int)level.bounds.x, (int)level.bounds.y, 0.2f);
			bgPoints[2] = new BgPoints(BG_POINTS_PER_AREA, (int)level.bounds.x, (int)level.bounds.y, 0.4f);
			bgPoints[3] = new BgPoints(BG_POINTS_PER_AREA, (int)level.bounds.x, (int)level.bounds.y, 0.5f);
			bgPoints[4] = new BgPoints(BG_POINTS_PER_AREA, (int)level.bounds.x, (int)level.bounds.y, 0.6f);
			bgPoints[5] = new BgPoints(BG_POINTS_PER_AREA, (int)level.bounds.x, (int)level.bounds.y, 0.75f);
			
			startTime = System.currentTimeMillis();
		}
		
		protected long startTime, finishTime;
		
		public abstract void wallBounced(float amount);
		
		protected Simulation s;
		
		private Vec2 prevSmallAvgPos;
		private int timeSinceStop;
		private static final float STOPPING_SPEED = 1.5f;
		private static final int STEPS_TO_STOP = 30 * 1; // 1 second.
		
		@Override
		protected void calculate() {
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
				stop();
				endGame = true;
			}
			if(warpData.stopTimer) {
				finishTime = System.currentTimeMillis();
			}
		}
		
		@Override
		public void stop() {
			super.stop();
			setPaused(true);
		}
		
		/**
		 * Sets the points in the game where the player should look towards.
		 */
		private void setNearestLookPoint() {
			// Firstly, set looking point. Currently only uses stars.
			{
				double lowestDist = -1;
				Vec2 lowestPoint = null;
				for(SpaceItem item : planetList) {
					if(item instanceof Star) {
						if(lowestDist == -1) lowestDist = VecHelper.dist(item.getPos(), p.itemC);
						double thisDist = VecHelper.dist(item.getPos(), p.itemC);
						if(thisDist < lowestDist) {
							lowestDist = thisDist;
							lowestPoint = item.getPos();
						}
					}
				}
				
				if(lowestPoint != null) {
					p.lookTo(lowestPoint); // Set point if found
				}
			}
		}

		boolean stopAnimation = false;
		float stopAnimationTime = 0;
		float stopAnimationFade = 0;
		float stopAnimationFadeSpeed = 1;
		
		protected void startStopping() {
			stopAnimation = true;
		}
		
		@Override
		protected void predraw(GL10 gl) { }
		
		int i, iter;
		
		protected Lines levelBorder;
		
		@Override
		protected void draw(GL10 gl) {
			super.draw(gl);
			// DRAW TIME
			
			if(StaticInfo.Starfield)
			{
				for(BgPoints p : bgPoints) {
					p.draw(gl);
				}
			}

			// DRAW TIME for objects behind ball
			for(SpaceItem obj : planetList)
			{
				if(obj instanceof TopDrawable)
				{
					((TopDrawable)obj).drawBelow(gl, 1);
				}
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
		protected void postdraw(GL10 gl) {
			// Apply warpData, part 2. Part 1 is not done here, but in a non-abstract class, in scale()
			warpDataPaint.setAlpha((float) CompuFuncs.trimMax(warpData.fade / 256, 1));
			if(warpDataPaint.getAlpha() != 0) {
				warpDataPaint.draw(gl);
			}
		}
		
		@Override
		protected void onSizeChanged(int w, int h) {
			warpDataPaint.setWH(scaledWidth, scaledHeight);
		}
		
		@Override
		public synchronized void saveState(GameState state) {
			if(!initialised) {
				Log.w(TAG, "State NOT saved for " + hashCode() + ", trying to use old state.");
				synchronized(toRestoreLock) {
					if(toRestore != null) {
						state.avgPos = toRestore.avgPos;
						state.screenPos = toRestore.screenPos;
						state.userPos = toRestore.userPos;
						state.userVelocity = toRestore.userVelocity;
						state.userZoom = toRestore.userZoom;
					} else {
						Log.w(TAG, "State NOT restored");
					}
				}
				return;
			}
			Log.d(TAG, "State saved for " + hashCode());
			state.userPos = p.itemC;
			state.userVelocity = p.getVelocity();
			state.avgPos = avgPos;
			
			state.screenPos = screenPos;
			state.userZoom = userZoom;
		}

		@Override
		public synchronized void restoreState(GameState state) {
			synchronized (toRestoreLock) {
				toRestore = state;
			}
			Log.d(TAG, "State Restored");
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
	
	public void stop() {
		thread.startStopping();
	}
}
