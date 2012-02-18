package uk.digitalsquid.spacegame.views;

import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.spacegame.subviews.PlanetaryView;
import android.content.Context;
import android.util.AttributeSet;

public class MainMenu extends PlanetaryView<MainMenu.ViewWorker>
{
	private InputStream levelData;
	
	public MainMenu(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	/**
	 * Sets the level. This must be called before create() is called
	 * @param level
	 */
	public void setLevel(InputStream level) {
		this.levelData = level;
	}

	@Override
	protected ViewWorker createThread() {
		return new ViewWorker(getContext(), levelData);
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
		
		@Override
		protected void predraw(GL10 gl)
		{
			level.bounds.x = scaledWidth + 2;
			level.bounds.y = scaledHeight + 2;
			
			super.predraw(gl);
		}
		
		@Override
		protected void onSizeChanged(int w, int h) {
			super.onSizeChanged(w, h);
			
			level.bounds.x = scaledWidth + 2;
			level.bounds.y = scaledHeight + 2;
			
			levelBorder.setVertices(new float[] {
				(float) -level.bounds.x / 2, (float) -level.bounds.y / 2, 0,
				(float) +level.bounds.x / 2, (float) -level.bounds.y / 2, 0,
				(float) +level.bounds.x / 2, (float) +level.bounds.y / 2, 0,
				(float) -level.bounds.x / 2, (float) +level.bounds.y / 2, 0 });
		}

		@Override
		protected void scale(GL10 gl)
		{
			gl.glRotatef(warpData.rotation, 0, 0, 1);
			
			gl.glScalef(WORLD_ZOOM_POSTSCALE, WORLD_ZOOM_POSTSCALE, 1);
			gl.glScalef(WORLD_ZOOM_PRESCALE, WORLD_ZOOM_PRESCALE, 1);
		}

		@Override
		public void wallBounced(float amount) {
			
		}
	}
}
