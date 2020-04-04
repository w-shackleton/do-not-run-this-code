package uk.digitalsquid.llamaorduck;

import java.util.Random;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity implements OnClickListener {
	
	boolean llama = false;
	Drawable img;
	int score = 0;

	Drawable[] llamas = new Drawable[8];
	Drawable[] ducks = new Drawable[7];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		if(savedInstanceState != null) {
			score = savedInstanceState.getInt("score", 0);
		}
		
		findViewById(R.id.llama).setOnClickListener(this);
		findViewById(R.id.duck).setOnClickListener(this);
		
		llamas[0] = getResources().getDrawable(R.drawable.llama_1);
		llamas[1] = getResources().getDrawable(R.drawable.llama_2);
		llamas[2] = getResources().getDrawable(R.drawable.llama_3);
		llamas[3] = getResources().getDrawable(R.drawable.llama_4);
		llamas[4] = getResources().getDrawable(R.drawable.llama_5);
		llamas[5] = getResources().getDrawable(R.drawable.llama_6);
		llamas[6] = getResources().getDrawable(R.drawable.llama_7);
		llamas[7] = getResources().getDrawable(R.drawable.llama_8);
		ducks[0] = getResources().getDrawable(R.drawable.duck_1);
		ducks[1] = getResources().getDrawable(R.drawable.duck_2);
		ducks[2] = getResources().getDrawable(R.drawable.duck_3);
		ducks[3] = getResources().getDrawable(R.drawable.duck_4);
		ducks[4] = getResources().getDrawable(R.drawable.duck_5);
		ducks[5] = getResources().getDrawable(R.drawable.duck_6);
		ducks[6] = getResources().getDrawable(R.drawable.duck_7);
		
		showNextLlama();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("score", score);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}

	AsyncTask<Void, Void, Boolean> task;
	
	Random rand = new Random();
	
	boolean first = true;
	
	int oldNum = -1;
	
	public void showNextLlama() {
		
		int num = rand.nextInt(15);
		while(num == oldNum)
			num = rand.nextInt(15);
		oldNum = num;
		switch(num) {
		case 0: img = ducks[0]; llama = false; break;
		case 1: img = ducks[1]; llama = false; break;
		case 2: img = ducks[2]; llama = false; break;
		case 3: img = ducks[3]; llama = false; break;
		case 4: img = ducks[4]; llama = false; break;
		case 5: img = ducks[5]; llama = false; break;
		case 6: img = ducks[6]; llama = false; break;
		case 7: img = llamas[0]; llama = true; break;
		case 8: img = llamas[1]; llama = true; break;
		case 9: img = llamas[2]; llama = true; break;
		case 10: img = llamas[3]; llama = true; break;
		case 11: img = llamas[4]; llama = true; break;
		case 12: img = llamas[5]; llama = true; break;
		case 13: img = llamas[6]; llama = true; break;
		default:
		case 14: img = llamas[7]; llama = true; break;
		}
		
		
		ImageView image = (ImageView)findViewById(R.id.image);
		image.setImageDrawable(img);
		if(task != null) task.cancel(true);
		task = new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					Thread.sleep(first ? 2000 : 300);
					first = false;
				} catch (InterruptedException e) {
				}
				if(isCancelled()) return true;
				return false;
			}
			protected void onPostExecute(Boolean result) {
				if(!result)
					fail();
			}
		};
		task.execute();
		
		TextView score = (TextView)findViewById(R.id.score);
		score.setText("" + this.score);
	}
	
	public void fail() {
		finish();
		Toast.makeText(this, "Your final score was " + score, Toast.LENGTH_LONG).show();
	}
	
	public void pass() {
		score++;
		showNextLlama();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.llama:
			if(llama) pass();
			else fail();
			break;
		case R.id.duck:
			if(!llama) pass();
			else fail();
			break;
		}
	}
}
