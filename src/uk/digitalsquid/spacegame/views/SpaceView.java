package uk.digitalsquid.spacegame.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import uk.digitalsquid.spacegame.BounceVibrate;
import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.PaintLoader;
import uk.digitalsquid.spacegame.PaintLoader.PaintDesc;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.Spacegame;
import uk.digitalsquid.spacegame.levels.LevelItem;
import uk.digitalsquid.spacegame.spaceitem.CompuFuncs;
import uk.digitalsquid.spacegame.spaceitem.SpaceItem;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Forceful;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Forceful.BallData;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Moveable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.TopDrawable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Warpable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Warpable.WarpData;
import uk.digitalsquid.spacegame.spaceview.gamemenu.GameMenu;
import uk.digitalsquid.spacegame.spaceview.gamemenu.GameMenu.ClickListener;
import uk.digitalsquid.spacegame.spaceview.gamemenu.GameMenu.GameMenuItem;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class SpaceView extends SurfaceView implements SurfaceHolder.Callback, OnTouchListener
{
	protected SpaceThread thread;
	protected Handler parentHandler;
	
	public SpaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context, null, null);
	}
	
	public SpaceView(Context context, LevelItem level, Handler parentHandler)
	{
		super(context);
		init(context, level, parentHandler);
	}
	
	private void init(Context context, LevelItem level, Handler parentHandler)
	{
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        this.parentHandler = parentHandler;
        
        if (isInEditMode() == false)
        {
        	thread = new SpaceThread(context, new Handler()
        	{
        		public void handleMessage(Message m)
        		{
        			if(m.what == SpaceThread.MESSAGE_END_GAME)
        			{
        				Message newM = new Message();
        				newM.what = Spacegame.MESSAGE_END_LEVEL;
        				SpaceView.this.parentHandler.sendMessage(newM);
        			}
        		}
        	}, holder, level);
        }
        
        setOnTouchListener(this);
        //SensorManager sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        //sm.registerListener(this, sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0), SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try
            {
                thread.join();
                retry = false;
            }
            catch(InterruptedException e)
            {
            	
            }
        }
	}
	
	class SpaceThread extends Thread
	{
		static final int MESSAGE_END_GAME = 1;
		
		public static final int MAX_FPS = 30; // 40 is goodish.
		public final int MAX_MILLIS;
		public static final int ITERS = 5; // must be an int
		public static final float AIR_RESISTANCE = 1f; // 1 = no resistance, must NEVER be greater than 1
		public static final int SPEED_SCALE = 20;
		
		private float BALL_RADIUS = 14;
		private float ballRotation = 0;
		private float ballRotationSpeed = 0;
		private float ballMomentum = 0;
		private static final float BALL_ROTATION_AIR_RESISTANCE = 0.98f;
		protected Drawable ball;

		protected float WORLD_ZOOM;
		protected float WORLD_ZOOM_UNSCALED;
		protected float WORLD_ZOOM_UNSCALED_ZOOMED;
		protected float WORLD_ZOOM_PRESCALE, WORLD_ZOOM_POSTSCALE;
		protected static final int REQ_SIZE_X = 480; // & 320 - scale to middle-screen size.
		
		protected float userZoom = 90;
		private float userZoomMultiplier = 1;
		protected static final float USER_ZOOM_INCREASE_SPEED = 1.01f;
		protected static final int USER_ZOOM_MIN = 70;
		protected static final int USER_ZOOM_MAX = 125;
		
		private static final int SCROLL_SPEED = 15;

		private static final int BG_REPEAT_X = 1000;
		private static final int BG_REPEAT_Y = 1000;
		private static final float BG_ZOOM_1 = 0.5f;
		private static final float BG_ZOOM_2 = 1f;
		private static final int BG_POINTS_PER_AREA = 1000;
		private static final int BG_BLUR_AMOUNT = 1;
		private BgPoint[] Bg_points = new BgPoint[BG_POINTS_PER_AREA];
		//private final Random randGen = new Random(); // Not used
		
		private int timeSinceStop = 0;
		private static final float STOPPING_SPEED = 1.5f;
		private static final int STEPS_TO_STOP = MAX_FPS * ITERS * 1; // 1 second.
		private boolean stopped = false;
		private Coord prevSmallAvgPos;
		
		private Handler msgHandler;
		private Context context;
		private SurfaceHolder surface;
		private boolean running = true;
		private boolean paused = false;
		private boolean gravOn = true;
		
		private LevelItem level;
		private List<SpaceItem> planetList;
		
		private Coord[] screenPos = new Coord[SCROLL_SPEED];
		private Coord avgPos = new Coord();
		private Coord[] avgPrevPos = new Coord[BG_BLUR_AMOUNT];
		
		private int borderBounceColour = 255;
		
		private List<GameMenu> gameMenus;
		private static final int GAME_MENU_ZOOM = 0;
		private static final int GAME_MAIN_MENU = 1;
		private static final int GAME_MENU_SHOW = 2;
		
		/**
		 * Used to store the 'warp' data returned from calculateVelocity
		 */
		private WarpData warpData = new WarpData();
		
		public SpaceThread(Context context, Handler handler, SurfaceHolder sView, LevelItem level)
		{
			this.level = level;
			
			planetList = level.planetList;
			if(planetList == null)
				planetList = new ArrayList<SpaceItem>();
			
			msgHandler = handler;
			this.context = context;
			surface = sView;
			
			MAX_MILLIS = (int) (1000 / MAX_FPS);

			currTime = System.currentTimeMillis();
			itemC = new Coord(level.startPos);
			itemVC = new Coord();
			itemRF = new Coord();
			
			millistep = MAX_MILLIS; // Since no initial benchmark

			for(int i = 0; i < screenPos.length; i++)
				screenPos[i] = new Coord();
			for(int i = 0; i < avgPrevPos.length; i++)
				avgPrevPos[i] = new Coord();
			
			BALL_RADIUS = (float)BALL_RADIUS * SpaceItem.ITEM_SCALE;
			
			// BG Points
			for(int i = 0; i < BG_POINTS_PER_AREA; i++)
			{
				Bg_points[i] = new BgPoint(); // Random constructor
			}
			
			BounceVibrate.initialise(context);
			
			if(gameMenus == null)
			{
				gameMenus = new ArrayList<GameMenu>();
				constructMenuZoom();
				constuctMainMenu();
				constuctMenuShow();
				gameMenus.get(GAME_MENU_SHOW).show();
			}
		}
		
		public void run()
		{
			// One time threaded initiations
			synchronized(context)
			{
				ball = context.getResources().getDrawable(R.drawable.ball);
			}
			
			while(running)
			{
				/*while(paused)
				{
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				}*/
				Canvas c = null;
				try
				{
					c = surface.lockCanvas(null);
					synchronized(surface)
					{
						drawCanvas(c);
					}
				}
				finally
				{
					if(c != null)
						surface.unlockCanvasAndPost(c);
				}
			}
			BounceVibrate.Nullify();
			
			if(endGame)
			{
				Message m = Message.obtain();
				m.what = MESSAGE_END_GAME;
				msgHandler.sendMessage(m);
			}
		}
		
		private PaintDesc
				bgpaint = new PaintDesc(0, 0, 0),
				warpDataPaint = new PaintDesc(0, 0, 0, 0),
				txtpaint = new PaintDesc(255, 255, 255, 255, 2, 10),
				starpaint = new PaintDesc(220, 255, 255, 255, 2),
				starprevpaint = new PaintDesc(200, 100, 100, 100, 2);
		private int width, height;
		private final Coord screenSize = new Coord();
		private long currTime, prevTime, millistep;
		private int i, iter;//, j, k;
		private SpaceItem currObj;
		private Coord itemC, itemVC, itemRF;
		
		/**
		 * True if the game should be forcibly ended, rather than ended by the view closing (ie player dies)
		 */
		private boolean endGame = false;
		
		private void drawCanvas(Canvas c)
		{
			prevTime = System.currentTimeMillis();
			
			width  = c.getWidth(); // Can canvases even be resized by the OS?
			height = c.getHeight();
			screenSize.x = width;
			screenSize.y = height;
			
			if(stopped) // Aiming ball - itemVC used to store velocity
			{
				
			}
			for(iter = 0; iter < ITERS; iter++) // Main physics loop
			{
				if(!paused)
				{
					// Check for collisions with wall
					if(Math.abs(itemC.x) > Math.abs(level.bounds.x) - BALL_RADIUS)
					{
						itemVC.x = -itemVC.x;
						borderBounceColour = 0; // Set bounce colour to 0 - which is red.
						BounceVibrate.Vibrate((long) (itemVC.getLength() / SpaceItem.ITEM_SCALE * 2));
					}
					if(Math.abs(itemC.y) > Math.abs(level.bounds.y) - BALL_RADIUS)
					{
						itemVC.y = -itemVC.y;
						borderBounceColour = 0;
						BounceVibrate.Vibrate((long) (itemVC.getLength() / SpaceItem.ITEM_SCALE * 2));
					}
				}
				
				itemRF.x = 0;
				itemRF.y = 0;
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
								itemRF.addThis(item.calculateRF(itemC, itemVC));
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
							BallData data = item.calculateVelocity(itemC, itemVC, BALL_RADIUS);
							if(data != null)
							{
								if(data.itemC != null)
									itemC = data.itemC;
								if(data.itemVC != null)
									itemVC = data.itemVC;
							}
						}
					}
					
					// Stage for object animation / movement
					Moveable mItem;
					try {
						mItem = (Moveable) currObj;
					} catch(RuntimeException e) {
						mItem = null;
					}
					if(mItem != null)
					{
						mItem.move((int) millistep, SPEED_SCALE);
					}
				}
				
				if(!paused)
				{
					itemVC.x += itemRF.x * millistep / ITERS / 1000f;
					itemVC.y += itemRF.y * millistep / ITERS / 1000f;
					
					itemC.x  += itemVC.x * millistep / ITERS / 1000f * SPEED_SCALE;
					itemC.y  += itemVC.y * millistep / ITERS / 1000f * SPEED_SCALE;
					
					// Air resistance
					itemVC.scaleThis(AIR_RESISTANCE);
					
					// Calculate rotation of ball
					ballRotation = ballRotation % 360;
					float itemRFDirection = itemRF.getRotation();
					if(Math.abs(ballRotation - itemRFDirection) < 180)
						ballMomentum = itemRFDirection - ballRotation;
					else
					{
						ballRotation -= 360;
						if(Math.abs(ballRotation - itemRFDirection) < 180) // Try again
							ballMomentum = itemRFDirection - ballRotation;
					}
					
					ballRotationSpeed += ballMomentum * millistep / ITERS / 1000f;
					ballRotationSpeed *= BALL_ROTATION_AIR_RESISTANCE;
					ballRotation += ballRotationSpeed * millistep / ITERS / 1000f * SPEED_SCALE;
					
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
			
			// object warp detect
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
						warpData.fade += data.fade; // Add on the data from this Warpable
						warpData.rotation += data.rotation;
						warpData.zoom *= data.zoom;
						if(data.endGame)
						{
							setRunning(false);
							endGame = true;
						}
					}
				}
			}
			
			// Calculate & move-scale canvas time!
			userZoom *= userZoomMultiplier;
			if(userZoom < USER_ZOOM_MIN) // Limit zoom factor
				userZoom = USER_ZOOM_MIN;
			if(userZoom > USER_ZOOM_MAX)
				userZoom = USER_ZOOM_MAX;
			WORLD_ZOOM = (float)width / REQ_SIZE_X / SpaceItem.ITEM_SCALE *
					userZoom / 100 * warpData.zoom; // Assuming all screens are about the same ratio?
			//WORLD_ZOOM_UNSCALED = (float)width / REQ_SIZE_X;
			WORLD_ZOOM_UNSCALED = (float)width / REQ_SIZE_X;
			WORLD_ZOOM_UNSCALED_ZOOMED = (float)width / REQ_SIZE_X * userZoom / 100 * warpData.zoom;
			WORLD_ZOOM_PRESCALE = (float)width / REQ_SIZE_X / SpaceItem.ITEM_SCALE;
			WORLD_ZOOM_POSTSCALE = userZoom / 100 * warpData.zoom;
			
			// Compute move screen
			for(int i = 1; i < screenPos.length; i++)
			{
				screenPos[i - 1] = screenPos[i];
			}
			screenPos[screenPos.length - 1].x = itemC.x - (width / 2 / WORLD_ZOOM_UNSCALED);
			screenPos[screenPos.length - 1].y = itemC.y - (height / 2 / WORLD_ZOOM_UNSCALED);
			avgPos.reset(); // Find average into this var
			int totNums = 1;
			for(int i = 1; i < screenPos.length; i++)
			{
				avgPos.x += screenPos[i].x * i;
				avgPos.y += screenPos[i].y * i;
				totNums += i;
			}
			avgPos.x /= totNums;
			avgPos.y /= totNums;
			
			/*c.clipRect(new RectF(
					(float)-(avgPos.x - level.bounds.x) * WORLD_ZOOM + 1,
					(float)-(avgPos.y - level.bounds.y) * WORLD_ZOOM + 1,
					(float)-(avgPos.x + level.bounds.x) * WORLD_ZOOM - 1,
					(float)-(avgPos.y + level.bounds.y) * WORLD_ZOOM - 1));*/

			
			c.drawPaint(PaintLoader.load(bgpaint));
			// Draw stars
			//for(int i = 1; i < avgPrevPos.length; i++) // Set speed-line points
			//	avgPrevPos[i - 1] = avgPrevPos[i];
			avgPrevPos[avgPrevPos.length - 1] = avgPos; // Since only one previous line
			
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
			
			
			// Move screen & zoom
			c.rotate(
					warpData.rotation,
					width / 2,
					height / 2);
			c.scale(WORLD_ZOOM_POSTSCALE, WORLD_ZOOM_POSTSCALE, width / 2, height / 2);
			
			c.translate((float)-avgPos.x * WORLD_ZOOM_PRESCALE, (float)-avgPos.y * WORLD_ZOOM_PRESCALE);
			c.scale(WORLD_ZOOM_PRESCALE, WORLD_ZOOM_PRESCALE);

			// DRAW TIME
			
			// Object draw
			for(i = 0; i < planetList.size(); i++)
			{
				currObj = planetList.get(i);
				currObj.draw(c, 1);
			}
			
			//c.drawCircle((float)itemC.x, (float)itemC.y, BALL_RADIUS, txtpaint);
			c.rotate(ballRotation, (float)itemC.x, (float)itemC.y);
			ball.setBounds(
					(int)((itemC.x - BALL_RADIUS)),
					(int)((itemC.y - BALL_RADIUS)),
					(int)((itemC.x + BALL_RADIUS)),
					(int)((itemC.y + BALL_RADIUS))
					);
			ball.draw(c);
			c.rotate(-ballRotation, (float)itemC.x, (float)itemC.y);
			
			if(stopped)
				c.drawText("MS: " + millistep + ", Spare millis: " + (MAX_MILLIS - System.currentTimeMillis() + prevTime), 20, 20, PaintLoader.load(txtpaint));
			
			/*c.drawLine(
					(float)(itemC.x),
					(float)(itemC.y),
					(float)((itemC.x + itemVC.x)),
					(float)((itemC.y + itemVC.y)),
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
			
			c.restore();
			
			// Draw objects static to screen (buttons)
			
			// Draw menus
			for(GameMenu menu : gameMenus)
			{
				menu.move(millistep, SPEED_SCALE);
				menu.draw(c, WORLD_ZOOM_UNSCALED, screenSize);
			}
			
			// Apply warpData, part 2
			warpDataPaint.a = (int) CompuFuncs.TrimMax(warpData.fade, 255);
			warpDataPaint.r = 0;
			warpDataPaint.g = 0;
			warpDataPaint.b = 0;
			c.drawRect(0, 0, width, height, PaintLoader.load(warpDataPaint));
			
			//Log.d("SpaceGame", "MS: " + millistep + ",\tPos: " + itemC + ",\tVel: " + itemVC + ",\tAcc: " + itemRF);
			
			// SLEEPY TIME!!
			currTime = System.currentTimeMillis();

			if(MAX_MILLIS - currTime + prevTime > 0)
			{
				//Log.v("SpaceGame", "Spare time, sleeping for " + (MAX_MILLIS - currTime + prevTime));
				try {
					//Log.d("SpaceGame", "MSleep: " + (MAX_MILLIS - currTime + prevTime) + ", Millis: " + (currTime - prevTime));
				Thread.sleep(MAX_MILLIS - currTime + prevTime);
				} catch (InterruptedException e) {
					Log.v("SpaceGame", "Sleep Interrupted! (Probably closing...)");
				}
			}
			// Update millistep for next draw loop
			//millistep = System.currentTimeMillis() - prevTime;
			millistep = MAX_MILLIS; // Set here to force a frame-rate
		}
		
		public synchronized void setRunning(boolean run)
		{
			running = run;
		}
		
		public synchronized boolean isRunning()
		{
			return running;
		}
		
		public synchronized void setPaused(boolean p)
		{
			paused = p;
		}
		
		public synchronized void setGravityOn(boolean on)
		{
			gravOn = !gravOn;
		}
		
		public synchronized void onTouch(View v, MotionEvent event)
		{
			Iterator<GameMenu> iter = gameMenus.iterator();
			while(iter.hasNext())
			{
				if(iter.next().computeClick(event)) return;
			}
			if(event.getAction() == MotionEvent.ACTION_UP)
				fireBall(event.getX(), event.getY());
		}
		
		private void fireBall(double sPosX, double sPosY)
		{
			if(stopped)
			{
				itemVC.x = ((sPosX / WORLD_ZOOM) - itemC.x + avgPos.x) / 6 * SpaceItem.ITEM_SCALE; // Scale down to compensate for power
				itemVC.y = ((sPosY / WORLD_ZOOM) - itemC.y + avgPos.y) / 6 * SpaceItem.ITEM_SCALE;
				
				itemC.x  += itemVC.x / 40;
				itemC.y  += itemVC.y / 40;
				
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
						itemC = new Coord(level.startPos);
						itemVC = new Coord();
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
						setPaused(true);
					}
						
					@Override
					public void onMoveOff(){}
					
					@Override
					public void onMoveOn(){}					
					}, R.drawable.pause),
			}, GameMenu.Corner.BOTTOM_LEFT));
		}
		
		public synchronized Bundle saveState(Bundle bundle)
		{
			bundle.putSerializable("itemC", itemC);
			bundle.putSerializable("itemVC", itemVC);
			bundle.putSerializable("avgPos", avgPos);
			bundle.putSerializable("avgPrevPos", avgPrevPos);
			bundle.putSerializable("screenPos", screenPos);
			bundle.putFloat("userZoom", userZoom);
			
			Iterator<GameMenu> menuIter = gameMenus.iterator();
			int i = 0;
			while(menuIter.hasNext())
			{
				GameMenu menu = menuIter.next();
				bundle.putBoolean("menu." + i++, menu.isHidden());
			}
			
			Log.v("SpaceGame", "State Saved");
			return bundle;
		}
		
		public synchronized void restoreState(Bundle bundle)
		{
			itemC = (Coord) bundle.getSerializable("itemC");
			itemVC = (Coord) bundle.getSerializable("itemVC");
			avgPos = (Coord) bundle.getSerializable("avgPos");
			avgPrevPos = (Coord[]) bundle.getSerializable("avgPrevPos");
			screenPos = (Coord[]) bundle.getSerializable("screenPos");
			userZoom = bundle.getFloat("userZoom");
			
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
			
			Log.v("SpaceGame", "State Restored");
		}
	}
	
	static class BgPoint
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
					rg.nextDouble() * SpaceThread.BG_REPEAT_X * 2 - SpaceThread.BG_REPEAT_X,
					rg.nextDouble() * SpaceThread.BG_REPEAT_Y * 2 - SpaceThread.BG_REPEAT_Y);
			zoom = rg.nextFloat() * (SpaceThread.BG_ZOOM_2 - SpaceThread.BG_ZOOM_1) + SpaceThread.BG_ZOOM_1;
			size = rg.nextFloat() * 1.5f + 1;
		}
	}
	
	double tempTouchX, tempTouchY;
	
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		thread.onTouch(v, event);
		return true;
	}
	
	public Bundle saveState(Bundle bundle)
	{
		return thread.saveState(bundle);
	}
	
	public void restoreState(Bundle bundle)
	{
		if(bundle != null)
		{
			thread.restoreState(bundle);
			thread.setPaused(true);
		}
	}
}
