package uk.digitalsquid.spacegame.subviews;

import java.io.InputStream;

import android.content.Context;
import android.util.AttributeSet;

public abstract class MovingView<VT extends MovingView.ViewWorker> extends PlanetaryView<VT>
{
	public MovingView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public static abstract class ViewWorker extends PlanetaryView.ViewWorker
	{
		public ViewWorker(Context context, InputStream level)
		{
			super(context, level);
		}
		
		@Override
		protected void postcalculate()
		{
			super.postcalculate();
			// Compute move screen
			for(int i = 1; i < screenPos.length; i++)
			{
				screenPos[i - 1].set(screenPos[i]);
			}
			screenPos[screenPos.length - 1].x = p.itemC.x;
			screenPos[screenPos.length - 1].y = p.itemC.y;
			avgPos.setZero(); // Find average into this var
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
