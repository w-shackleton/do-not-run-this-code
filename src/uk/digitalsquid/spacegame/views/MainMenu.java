package uk.digitalsquid.spacegame.views;

import java.io.InputStream;

import uk.digitalsquid.spacegame.subviews.PlanetaryView;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

public class MainMenu extends PlanetaryView<MainMenu.ViewThread>
{
	protected Handler gameHandler;
	public MainMenu(Context context, AttributeSet attrs, Handler gameHandler, InputStream levelData)
	{
		super(context, attrs);
		this.gameHandler = gameHandler;
		thread = new ViewThread(context, holder, levelData);// , new Handler()
		// {
		// public void handleMessage(Message m)
		// {
		// if(m.what == ViewThread.MESSAGE_QUIT)
		// {
		// Message newM = new Message();
		// newM.what = Spacegame.MESSAGE_END_LEVEL;
		// MainMenu.this.parentHandler.sendMessage(newM);
		// }
		// }
		// }, holder);
	}

	protected class ViewThread extends PlanetaryView.ViewThread
	{
		public ViewThread(Context context, SurfaceHolder surface, InputStream levelData)
		{
			super(context, surface, levelData);
		}

		@Override
		protected void initialiseOnThread()
		{
			super.initialiseOnThread();
		}

		@Override
		protected void predraw(Canvas c)
		{
			level.bounds.x = c.getWidth() / WORLD_ZOOM + 2;
			level.bounds.y = c.getHeight() / WORLD_ZOOM + 2;
			super.predraw(c);
		}

		@Override
		protected void scale(Canvas c)
		{
			c.rotate(
					warpData.rotation,
					c.getWidth() / 2,
					c.getHeight() / 2);
			c.scale(WORLD_ZOOM_POSTSCALE, WORLD_ZOOM_POSTSCALE, width / 2, height / 2);
			c.translate(c.getWidth() / 2, c.getHeight() / 2);
			c.scale(WORLD_ZOOM_PRESCALE, WORLD_ZOOM_PRESCALE);
		}

		@Override
		protected void onThreadEnd()
		{
			Message msg = Message.obtain();
			msg.what = returnCode;
			MainMenu.this.gameHandler.sendMessage(msg);
		}

		@Override
		protected void postdrawscale(Canvas c) {
			c.scale(WORLD_ZOOM_UNSCALED, WORLD_ZOOM_UNSCALED);
		}
	}
}
