/*
 * This file is part of Bright Day.
 * 
 * Bright Day is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Bright Day is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Bright Day.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.digitalsquid.BrightDay;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

public class BrightDayGraph extends View
{
	public int minBright = 0, maxBright = 255, shift = 50, gamma = 0, stretch = 50;// stretchY = 50;
	public boolean showTemp = true;
	public int minBright2 = 0, maxBright2 = 255, shift2 = 50, gamma2 = 0, stretch2 = 50;
	private final float TEXT_SIZE;
	private final static int TEXT_UNSCALED_SIZE = 12;
	private ValCalc valcalc;
	
	public BrightDayGraph(Context context, DisplayMetrics dm)
	{
		super(context);
		TEXT_SIZE = TEXT_UNSCALED_SIZE * dm.density;
		Log.v("BrightDay", "Density size scale: " + dm.density);
	}
	
	@Override
    protected void onDraw(Canvas canvas)
    {
    	super.onDraw(canvas);
    	final int width = getWidth();
    	final int height = getHeight();
    	
    	canvas.drawColor(Color.WHITE);
    	Paint paint = new Paint();
    	paint.setStyle(Paint.Style.STROKE);
    	paint.setARGB(0xFF, 128, 128, 128);
    	paint.setAntiAlias(false);
    	
    	Paint paintFill = new Paint();
    	paintFill.setStyle(Paint.Style.FILL);
    	paintFill.setARGB(0xFF, 128, 128, 128);
    	paintFill.setAntiAlias(true);
    	paintFill.setTextSize(TEXT_SIZE);
    	
    	// Draw horizontal lines
    	Path path = new Path();
    	path.moveTo(0, 0);
    	path.lineTo(width, 0);
    	for(int i = 0; i < 3; i++)
    	{
    		path.offset(0, height / 4);
    		canvas.drawPath(path, paint);
        	canvas.drawText(String.valueOf(75 - (i * 25)) + "%", 2, height / 4 * (i + 1) - 2, paintFill);
    	}
    	canvas.drawText("100%", 2, 2 + TEXT_SIZE, paintFill);

    	// Draw vertical lines
    	path = new Path();
    	path.moveTo(0, 0);
    	path.lineTo(0, height);
    	for(int i = 0; i < 4; i++)
    	{
    		path.offset(width / 4, 0);
    		canvas.drawPath(path, paint);
    	}
    	canvas.drawText("0% 12am", 2, height - 2, paintFill);
    	canvas.drawText("6am", width / 4 + 2, height - 2, paintFill);
    	canvas.drawText("12pm", width / 4 * 2 + 2, height - 2, paintFill);
    	canvas.drawText("6pm", width / 4 * 3 + 2, height - 2, paintFill);
    	
    	// Draw time of day line
    	paint.setARGB(0xFF, 0, 128, 0);
    	paint.setStrokeWidth(2);
    	path = new Path();
		Date time = Calendar.getInstance().getTime();
		path.moveTo(0, 0);
		path.lineTo(0, height);
		path.offset((float)(time.getHours() * 60 + time.getMinutes()) / 1440f * width, 0);
		canvas.drawPath(path, paint);

		paint.setAntiAlias(true);
		if(showTemp)
		{
			// Draw curve 2
			paint.setARGB(0xFF, 128, 0, 0);
    		path = new Path();
    		
	    	valcalc = new ValCalc(minBright2, maxBright2, shift2, gamma2, stretch2, width, height);
	    	path.moveTo(0, valcalc.getPos(0));
    		for(int i = 0; i < width + 4; i += 4)
    		{
    			path.lineTo(i, valcalc.getPos(i));
    		}
    		canvas.drawPath(path, paint);
		}
		
    	// Draw curve 1
    	paint.setARGB(0xFF, 0, 0, 0);
    	path = new Path();
    	
    	valcalc = new ValCalc(minBright, maxBright, shift, gamma, stretch, width, height);
    	
    	path.moveTo(0, valcalc.getPos(0));
    	for(int i = 0; i < width + 4; i += 4)
    	{
    		path.lineTo(i, valcalc.getPos(i));
    	}
    	//path.offset(0, height - ((float)minBright / 255f * (float)height) - curveHeight2);
    	canvas.drawPath(path, paint);
    }
	
	public void setToCurrent()
	{
		minBright2 = minBright;
		maxBright2 = maxBright;
		shift2 = shift;
		gamma2 = gamma;
		stretch2 = stretch;
		invalidate();
	}
}
