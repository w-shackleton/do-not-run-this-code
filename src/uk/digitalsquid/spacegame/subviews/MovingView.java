package uk.digitalsquid.spacegame.subviews;

import java.io.InputStream;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

public abstract class MovingView<VT extends MovingView.ViewThread> extends PlanetaryView<VT>
{
	public MovingView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public static abstract class ViewThread extends PlanetaryView.ViewThread
	{
		public ViewThread(Context context, SurfaceHolder surface, InputStream level)
		{
			super(context, surface, level);
		}
		
		@Override
		protected void postcalculate()
		{
			super.postcalculate();
			// Compute move screen
			for(int i = 1; i < screenPos.length; i++)
			{
				screenPos[i - 1] = screenPos[i];
			}
			screenPos[screenPos.length - 1].x = p.itemC.x - (width / 2 / WORLD_ZOOM_UNSCALED);
			screenPos[screenPos.length - 1].y = p.itemC.y - (height / 2 / WORLD_ZOOM_UNSCALED);
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
		}
	}
}
