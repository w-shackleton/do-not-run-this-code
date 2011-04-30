package uk.digitalsquid.spacegame.views;

import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.spacegame.subviews.PlanetaryView;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;

public class MainMenu extends PlanetaryView<MainMenu.ViewWorker>
{
	protected final Handler gameHandler;
	private final InputStream levelData;
	public MainMenu(Context context, AttributeSet attrs, Handler gameHandler, InputStream levelData)
	{
		super(context, attrs);
		this.gameHandler = gameHandler;
		this.levelData = levelData;
		
		initP2();
	}

	@Override
	protected ViewWorker createThread() {
		return new ViewWorker(context, levelData);
	}

	protected class ViewWorker extends PlanetaryView.ViewWorker
	{
		public ViewWorker(Context context, InputStream levelData)
		{
			super(context, levelData);
		}

		@Override
		protected void initialiseOnThread()
		{
			super.initialiseOnThread();
		}
		
		private int oldSW = 0, oldSH = 0;

		@Override
		protected void predraw(GL10 gl)
		{
			level.bounds.x = scaledWidth + 2;
			level.bounds.y = scaledHeight + 2;
			
			if(scaledWidth != oldSW || scaledHeight != oldSH) {
				oldSW = scaledWidth;
				oldSH = scaledHeight;
				levelBorder.setVertices(new float[] {
					(float) -level.bounds.x / 2, (float) -level.bounds.y / 2, 0,
					(float) +level.bounds.x / 2, (float) -level.bounds.y / 2, 0,
					(float) +level.bounds.x / 2, (float) +level.bounds.y / 2, 0,
					(float) -level.bounds.x / 2, (float) +level.bounds.y / 2, 0 });
			}
			
			super.predraw(gl);
		}

		@Override
		protected void scale(GL10 gl)
		{
			gl.glRotatef(warpData.rotation, 0, 0, 1);
			
			gl.glScalef(WORLD_ZOOM_POSTSCALE, WORLD_ZOOM_POSTSCALE, 1);
			gl.glScalef(WORLD_ZOOM_PRESCALE, WORLD_ZOOM_PRESCALE, 1);
		}

		@Override
		protected void onThreadEnd()
		{
			Message msg = Message.obtain();
			msg.what = returnCode;
			MainMenu.this.gameHandler.sendMessage(msg);
		}
	}
}
