package uk.digitalsquid.physicsland;

import java.util.ArrayList;
import java.util.List;

import uk.digitalsquid.physicsland.PhysicsObject.ObjectType;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class PhysicsLandView extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener, OnTouchListener
{
	private PhysicsLandThread thread;
	private float accelX = 0, accelY = 0, accelZ = 0;
	private SensorManager sm;
	public PhysicsLandView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        
        if (isInEditMode() == false)
        {
			thread = new PhysicsLandThread(holder, context, new Handler()
			{
				public void handleMessage(Message m)
				{
					
				}
			}, this);
        }
        sm = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        sm.registerListener(this, sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0), SensorManager.SENSOR_DELAY_FASTEST);
        this.setOnTouchListener(this);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3)
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
        sm.unregisterListener(this);
	}
	
	class PhysicsLandThread extends Thread
	{
		public static final float RADIAN_CONVERT = (float) (Math.PI / 180);
		//public static final float RADIAN_CONVERT = 1f;
		public static final float PI = (float) Math.PI;
		public static final int SCALE = 10;
		public static final int FRAME_SPEED = 18;
		SurfaceHolder surfaceHolder;
		Context context;
		Handler handler;
		boolean running;
		long oldMilliTime;
		Box2DInterface box2d;
		//int[] objects = new int[8];
		//PhysicsObject[] pobjects = new PhysicsObject[8];
		List<PhysicsObject> pobjects = new ArrayList<PhysicsObject>();
		PhysicsLandView parent;
		public PhysicsLandThread(SurfaceHolder surfaceHolder, Context context, Handler handler, PhysicsLandView parent)
		{
			this.surfaceHolder = surfaceHolder;
			this.context = context;
			this.handler = handler;
			this.parent = parent;
			running = true;
			oldMilliTime = System.currentTimeMillis();
			box2d = new Box2DInterface();

			box2d.createWorld(0,0,32,48,0,10);

			addRectangle(true, 0 , 46, 32, 1 , 0, 0, 0, 0, Color.rgb(255, 100, 0  ));
			addRectangle(true, 1 , 0 , 1 , 48, 0, 0, 0, 0, Color.rgb(255, 0  , 100));
			addRectangle(true, 0 , 1 , 32, 1 , 0, 0, 0, 0, Color.rgb(100, 100, 100));
			addRectangle(true, 30, 0 , 1 , 48, 0, 0, 0, 0, Color.rgb(0  , 0  , 100));

			addRectangle(true, 0 , 15 , 16, 1 , (float)(Math.PI / 4), 0, 0, 0, Color.rgb(100, 100, 100));
			

			addRectangle(false, 15.5f, 14, 2, 3, 0, 10, 0.6f, 1, Color.rgb(0  , 255, 0));
			addRectangle(false, 20   , 30, 5, 5, 0, 5 , 0   , 6, Color.rgb(100, 255, 0));
			addRectangle(false, 25   , 3 , 5, 2, 0, 14, 0.1f, 2, Color.rgb(255, 255, 0));
			
			addCircle(false, 10, 10, 2, 10, 1, Color.rgb(100, 0, 140));
			addCircle(true, 16, 20, 1, 0, 0, Color.rgb(255, 0, 0));
			

			paint = new Paint();
			paint.setAntiAlias(true);
			//paint.setStrokeWidth(1);
			paint.setStyle(Style.FILL);
			xpaint = new Paint();
			xpaint.setAntiAlias(true);
			//xpaint.setStrokeWidth(1);
			xpaint.setStyle(Style.FILL);
			xpaint.setARGB(255, 0, 0, 0);
			
			bgpaint = new Paint();
			//bgpaint.setStyle(Style.FILL_AND_STROKE);
			bgpaint.setARGB(255, 255, 255, 255);
			Log.v("BrightDay", "PLV: Initiated");
		}
		
		public void run()
		{
			while(running)
			{
				Canvas c = null;
				try
				{
					c = surfaceHolder.lockCanvas(null);
					synchronized(surfaceHolder)
					{
						drawCanvas(c);
					}
				}
				finally
				{
					if(c != null)
						surfaceHolder.unlockCanvasAndPost(c);
				}
			}
		}
		
		public void setRunning(boolean r)
		{
			running = r;
		}
		
		private BodyInfo info = new BodyInfo();
		private long milliTime;
		private Paint paint, bgpaint, xpaint;
		private PhysicsObject obj;
		private PhysicsObjectSquare objsq;
		private PhysicsObjectCircle obje;
		private Rect rect;
		private float[] accelValues;
		
		private void drawCanvas(Canvas c)
		{
			milliTime = System.currentTimeMillis();
			c.drawRect(0, 0, c.getWidth(), c.getHeight(), bgpaint);
			//c.drawText("Width: " + String.valueOf(c.getWidth()) + ", Height: " + String.valueOf(c.getHeight()), 10, 20, paint);
			//c.drawText("Time in millis: " + String.valueOf(milliTime), 10, 40, paint);
			//c.drawText("Time diff in millis: " + String.valueOf(milliTime - oldMilliTime), 10, 60, paint);
			//c.drawCircle(10, 80 + milliTime - oldMilliTime, 5, paint);
			
			for(int i = 0; i < pobjects.size(); i++)
			{
				obj = pobjects.get(i);
				box2d.getBodyInfo(info, obj.objId);
				
				paint.setColor(obj.color);
				if(obj.type == ObjectType.RECTANGLE)
				{
					objsq = (PhysicsObjectSquare) obj;
					//Path path = new Path();

					//path.lineTo((float)Math.cos(info.getAngle() * RADIAN_CONVERT) * objsq.width * SCALE, (float)Math.sin(info.getAngle() * RADIAN_CONVERT) * objsq.width * SCALE);
					//path.rLineTo((float)Math.cos((info.getAngle() + 90) * RADIAN_CONVERT) * objsq.height * SCALE, (float)Math.sin((info.getAngle() + 90) * RADIAN_CONVERT) * objsq.height * SCALE);
					//path.rLineTo((float)Math.cos((info.getAngle() + 180) * RADIAN_CONVERT) * objsq.width * SCALE, (float)Math.sin((info.getAngle() + 180) * RADIAN_CONVERT) * objsq.width * SCALE);
					//path.rLineTo((float)Math.cos((info.getAngle() + 270) * RADIAN_CONVERT) * objsq.height * SCALE, (float)Math.sin((info.getAngle() + 270) * RADIAN_CONVERT) * objsq.height * SCALE);
					
					// path.lineTo((float)Math.cos(info.getAngle())              * objsq.width  * SCALE, (float)Math.sin(info.getAngle())              * objsq.width  * SCALE);
					//path.rLineTo((float)Math.cos(info.getAngle() + (0.5 * PI)) * objsq.height * SCALE, (float)Math.sin(info.getAngle() + (0.5 * PI)) * objsq.height * SCALE);
					//path.rLineTo((float)Math.cos(info.getAngle() + PI)         * objsq.width  * SCALE, (float)Math.sin(info.getAngle() + PI)         * objsq.width  * SCALE);
					//path.rLineTo((float)Math.cos(info.getAngle() + (1.5 * PI)) * objsq.height * SCALE, (float)Math.sin(info.getAngle() + (1.5 * PI)) * objsq.height * SCALE);
					
					//path.offset((info.getX() - (objsq.width / 2)) * SCALE, (info.getY() - (objsq.height / 2)) * SCALE);
					
					//c.drawPath(path, paint);

					//Log.v("PhysicsLand", "PLV: 1");
					rect = new Rect();                    // TRY DELETING
					//Log.v("PhysicsLand", "PLV: 2");
					rect.set(
							(int)((info.x - (objsq.width  / 2)) * SCALE),
							(int)((info.y - (objsq.height / 2)) * SCALE),
							(int)((info.x + (objsq.width  / 2)) * SCALE),
							(int)((info.y + (objsq.height / 2)) * SCALE)
							);
					//Log.v("PhysicsLand", "PLV: 3");
					c.rotate(info.getAngle() / RADIAN_CONVERT, info.x * SCALE, info.y * SCALE);
					//Log.v("PhysicsLand", "PLV: 4");
					c.drawRect(rect, paint);
					//Log.v("PhysicsLand", "PLV: 5");
					c.rotate(-info.getAngle() / RADIAN_CONVERT, info.x * SCALE, info.y * SCALE);
					//Log.v("PhysicsLand", "PLV: " + String.valueOf(objsq.width) + ", " + String.valueOf(objsq.height) + ", " + String.valueOf(info.getX()) + ", " + String.valueOf(info.getY()));
				}
				if(obj.type == ObjectType.CIRCLE)
				{
					obje = (PhysicsObjectCircle) obj;
					c.drawCircle(info.getX() * SCALE, info.getY() * SCALE, obje.rad * SCALE, paint);
				}
				
				//if(obj.fixed)
				//{
				//	c.drawLine(info.x * SCALE - 2, info.y * SCALE - 2, info.x * SCALE + 2, info.y * SCALE + 2, xpaint);
				//	c.drawLine(info.x * SCALE - 2, info.y * SCALE + 2, info.x * SCALE + 2, info.y * SCALE - 2, xpaint);
				//}
			}
			accelValues = parent.getAccelValues();
			box2d.setGravity(-accelValues[0], accelValues[1]);
			//Log.v("PhysicsLand", "PLV: Values: " + String.valueOf(-accelValues[0]) + ", " + String.valueOf(accelValues[1]));
	    	box2d.step((float)(milliTime - oldMilliTime) / 1000f, 8, 2);
	    	try
	    	{
				Thread.sleep(
						FRAME_SPEED - (milliTime - System.currentTimeMillis()) > 0 ?
						FRAME_SPEED - (milliTime - System.currentTimeMillis()) :
						1);
			}
	    	catch (InterruptedException e)
			{
	    		
			}
	    	//Log.v("PhysicsLand", "PLV: " + String.valueOf(milliTime - oldMilliTime));
			oldMilliTime = milliTime;
		}
		
		private synchronized void addRectangle(
				boolean solid,
				float x,
				float y,
				float width,
				float height,
				float rotation,
				float density,
				float restitution,
				float friction,
				int col
				)
		{
			int id = solid ?
					box2d.createBox(x, y, width, height) :
					box2d.createBox2(x, y, width, height, density, restitution, friction);
			box2d.setBodyXForm(id, x + (width / 2), y + (height / 2), rotation);
			pobjects.add(
					new PhysicsObjectSquare(solid, width, height, col, id)
					);
		}
		
		private void addCircle(
				boolean solid,
				float x,
				float y,
				float radius,
				float density,
				float restitution,
				int col
				)
		{
			pobjects.add(
					new PhysicsObjectCircle(
								solid,
								radius,
								col,
								solid ?
										box2d.createCircle(x, y, radius) :
										box2d.createCircle2(x, y, radius, density, restitution)
								)
					);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
		
	}

	@Override
	public void onSensorChanged(SensorEvent event)
	{
		accelX = event.values[0];
		accelY = event.values[1];
		accelZ = event.values[2];
	}
	private synchronized float[] getAccelValues()
	{
		return new float[] { accelX, accelY, accelZ };
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		if(event.getAction() == MotionEvent.ACTION_UP)
		{
			if(Math.random() < 0.5)
				thread.addRectangle(
						false,
						event.getX() / 10,
						event.getY() / 10,
						(float)Math.random() * 5 + 1,
						(float)Math.random() * 5 + 1,
						(float)(Math.random() * Math.PI),
						(float)Math.random() * 12 + 3,
						(float)Math.random(),
						(float)Math.random() * 3 + 0.3f,
						Color.rgb(
								(int)(Math.random() * 256),
								(int)(Math.random() * 256),
								(int)(Math.random() * 256)
								)
						);
			else
				thread.addCircle(
						false,
						event.getX() / 10,
						event.getY() / 10,
						(float)Math.random() * 2 + 1,
						(float)Math.random() * 12 + 3,
						(float)Math.random(),
						Color.rgb(
								(int)(Math.random() * 256),
								(int)(Math.random() * 256),
								(int)(Math.random() * 256)
								)
						);
		}
		return true;
		//return false;
	}
}
