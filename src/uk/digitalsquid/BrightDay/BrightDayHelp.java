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

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class BrightDayHelp extends Activity implements OnSeekBarChangeListener
{
	private BrightDayGraph graph;
	private SeekBar minSeek, maxSeek, shiftSeek, gammaSeek, stretchSeek;// stretchYSeek;
	private Thread thread;
	private LinearLayout infoM;
	private TextView infoT;
	private Button saveButton;
	
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        graph = new BrightDayGraph(this, dm);
        setContentView(R.layout.main);
        ((LinearLayout)findViewById(R.id.GraphLayoutHolder)).addView(graph);
        graph.showTemp = false;
        
        saveButton = (Button)findViewById(R.id.SaveButton);
        saveButton.setVisibility(View.INVISIBLE);
        
        infoM = (LinearLayout)findViewById(R.id.BdInfo);
        infoT = (TextView)findViewById(R.id.BdInfoText);
        infoM.setBackgroundColor(0xFF000000);
        ((LinearLayout)findViewById(R.id.BdInfoSub)).setBackgroundColor(0xFFFFFFFF);
        infoT.setTextColor(0xFF000000);
        
        minSeek = (SeekBar)findViewById(R.id.MinBrightnessSeekBar);
        maxSeek = (SeekBar)findViewById(R.id.MaxBrightnessSeekBar);
        shiftSeek = (SeekBar)findViewById(R.id.ShiftSeekBar);
        gammaSeek = (SeekBar)findViewById(R.id.GammaSeekBar);
        stretchSeek = (SeekBar)findViewById(R.id.StretchSeekBar);
        //stretchYSeek = (SeekBar)findViewById(R.id.StretchYSeekBar);

        /*minSeek.setProgress(0);
        maxSeek.setProgress(255);
        shiftSeek.setProgress(50);
        gammaSeek.setProgress(0);
        stretchSeek.setProgress(50);
        stretchYSeek.setProgress(50);*/
        
        minSeek.setOnSeekBarChangeListener(this);
        maxSeek.setOnSeekBarChangeListener(this);
        shiftSeek.setOnSeekBarChangeListener(this);
        gammaSeek.setOnSeekBarChangeListener(this);
        stretchSeek.setOnSeekBarChangeListener(this);
        //stretchYSeek.setOnSeekBarChangeListener(this);

        ((TextView)findViewById(R.id.gv1)).setText("");
        ((TextView)findViewById(R.id.gv2)).setText("");
        ((TextView)findViewById(R.id.gv3)).setText("");
        
        thread = new Thread(workThr);
        thread.start();
    }

	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		switch (seekBar.getId())
		{
		case R.id.MinBrightnessSeekBar:
			graph.minBright = progress;
			if(maxSeek.getProgress() < progress)
				maxSeek.setProgress(progress);
			break;
		case R.id.MaxBrightnessSeekBar:
			graph.maxBright = progress;
			if(minSeek.getProgress() > progress)
				minSeek.setProgress(progress);
			break;
		case R.id.ShiftSeekBar:
			graph.shift = progress;
			break;
		case R.id.GammaSeekBar:
			graph.gamma = progress;
			break;
		case R.id.StretchSeekBar:
			graph.stretch = progress;
			break;
		//case R.id.StretchYSeekBar:
		//	graph.stretchY = progress;
		//	break;
		}
		graph.invalidate();
	}
	
	public void onStartTrackingTouch(SeekBar seekBar) {}
	public void onStopTrackingTouch(SeekBar seekBar) {}
	
	private Runnable workThr = new Runnable()
	{
		Message m;
		public void run()
		{
			sleep(1.5f);
			dispText(R.string.tut1, 4);
			dispText(R.string.tut2, 8);
			sleep(1.5f);
			dispText(R.string.tut3, 7);
			
			dispText(R.string.tut_minmax, 4);
			sleep(1);
			slideSlider(1, 0, 128, 1, 0.01f);
			slideSlider(1, 128, 40, 1, 0.01f);
			slideSlider(2, 255, 128, 1, 0.01f);
			slideSlider(2, 128, 216, 1, 0.01f);
			sleep(1.5f);
			
			dispText(R.string.tut_shift, 4);
			sleep(1);
			slideSlider(3, 50, 70, 1, 0.05f);
			slideSlider(3, 70, 30, 1, 0.05f);
			sleep(1.5f);
			
			dispText(R.string.tut_gamma, 4);
			sleep(1);
			slideSlider(4, 0, 70, 1, 0.03f);
			slideSlider(4, 70, 50, 1, 0.03f);
			sleep(1.5f);
			
			dispText(R.string.tut_stretch, 4);
			sleep(1);
			slideSlider(5, 50, 0, 1, 0.04f);
			slideSlider(5, 0, 80, 1, 0.04f);
			sleep(1.5f);
			
			dispText(R.string.tut_save, 4);
			sleep(1);
			dispSave(2);
			sleep(1.5f);
			
			dispText(R.string.tut_comp, 10);
			sleep(.5f);
			m = new Message();
			quitHandler.sendMessage(m);
		}

		private void dispText(int textRes, float time)
		{
			m = new Message();
			
			m.arg1 = 1;
			m.arg2 = textRes;
			textHandler.sendMessage(m);
			try {Thread.sleep((int)(time * 1000));} catch (InterruptedException e) {}
			
			m = new Message();
			
			m.arg1 = 0;
			textHandler.sendMessage(m);
			try {Thread.sleep(400);} catch (InterruptedException e) {} // 400 is time in Anim XML
		}
		private void dispSave(float time)
		{
			m = new Message();
			m.arg1 = 3;
			textHandler.sendMessage(m);
			try {Thread.sleep((int)(time * 1000));} catch (InterruptedException e) {}
			m = new Message();
			m.arg1 = 2;
			textHandler.sendMessage(m);
			try {Thread.sleep(400);} catch (InterruptedException e) {} // 400 is time in Anim XML
		}
		private void sleep(float time)
		{
			try {Thread.sleep((int)(time * 1000));} catch (InterruptedException e) {}
		}
		private void setVal(int slider, int val, float time)
		{
			m = new Message();
			
			m.arg1 = slider;
			m.arg2 = val;
			handler.sendMessage(m);
			
			try {Thread.sleep((int)(time * 1000));} catch (InterruptedException e) {}
		}
		private void slideSlider(int slider, int start, int end, int step, float speed)
		{
			if(start < end)
			{
				for(int i = start; i < end; i += step)
				{
					setVal(slider, i, speed);
				}
			}
			else
			{
				for(int i = start; i > end; i -= step)
				{
					setVal(slider, i, speed);
				}
			}
		}
	};
	
	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			     if(msg.arg1 == 1)
				minSeek.setProgress(msg.arg2);
			else if(msg.arg1 == 2)
				maxSeek.setProgress(msg.arg2);
			else if(msg.arg1 == 3)
				shiftSeek.setProgress(msg.arg2);
			else if(msg.arg1 == 4)
				gammaSeek.setProgress(msg.arg2);
			else if(msg.arg1 == 5)
				stretchSeek.setProgress(msg.arg2);
		}
	};
	
	private Handler textHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if(msg.arg1 == 0)
			{
				infoM.setVisibility(View.INVISIBLE);
				infoM.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.fadeout));
			}
			/*else if(msg.arg1 == 2)
			{
				infoT.setText(msg.arg2);
			}*/
			else if(msg.arg1 == 1)
			{
				infoM.setVisibility(View.VISIBLE);
				infoM.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.fadein));
				infoT.setText(msg.arg2);
			}
			else if(msg.arg1 == 2)
			{
				saveButton.setVisibility(View.INVISIBLE);
				saveButton.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.fadeout));
			}
			else
			{
				saveButton.setVisibility(View.VISIBLE);
				saveButton.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.fadein));
			}
		}
	};
	
	private Handler quitHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			BrightDayHelp.this.finish();
		}
	};
}