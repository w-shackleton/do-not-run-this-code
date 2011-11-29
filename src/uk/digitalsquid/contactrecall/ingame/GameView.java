package uk.digitalsquid.contactrecall.ingame;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.contactrecall.game.GameInstance;
import uk.digitalsquid.contactrecall.ingame.GameView.ViewWorker;
import uk.digitalsquid.contactrecall.ingame.gl.RectMesh;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;

public class GameView extends DrawBaseView<ViewWorker> {

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initP2();
	}

	@Override
	protected ViewWorker createThread() {
		return new ViewWorker(context);
	}

	public static class ViewWorker extends DrawBaseView.ViewWorker {
		
		public GameInstance game;
		
		public boolean running = false;

		public ViewWorker(Context context) {
			super(context);
		}

		@Override
		protected void initialiseOnThread() {
			if(DEBUG) {
				pointerPos = new RectMesh(0, 0, 2, 2, 1, 0, 0, 1);
				pointerPos.setVisible(false);
			}
		}

		@Override
		protected void onThreadEnd() {
		}
		
		@Override
		protected void scale(GL10 c) {
			matrix2d.reset();
			
			matrix2d.postTranslate(-width / 2, -height / 2);
			matrix2d.postScale(scaledWidth / (float)width, -scaledHeight / (float)height);
			matrix2d.invert(matrixInverse);
		}
		
		private RectMesh pointerPos;

		@Override
		public void saveState(Bundle bundle) {
		}

		@Override
		public void restoreState(Bundle bundle) {
		}
		
		@Override
		protected void draw(GL10 gl){
			if(pointerPos != null) pointerPos.draw(gl);
		}
		
		@Override
		protected void precalculate(){}
		@Override
		protected void calculate(){}
		@Override
		protected void postcalculate(){}
		
		@Override
		protected void predraw(GL10 gl){
			gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		}
		@Override
		protected void postdraw(GL10 gl){}

		@Override
		protected void onTouchDown(float x, float y) {
			if(pointerPos != null) pointerPos.setVisible(true);
			if(pointerPos != null) pointerPos.setXY(x, y);
		}

		@Override
		protected void onTouchMove(float x, float y) {
			if(pointerPos != null) pointerPos.setVisible(true);
			if(pointerPos != null) pointerPos.setXY(x, y);
		}

		@Override
		protected void onTouchUp(float x, float y) {
			if(pointerPos != null) pointerPos.setVisible(false);
		}
	}
	
	public void setGame(GameInstance instance) {
		thread.game = instance;
	}
	
	public void stopGame() {
		thread.game = null;
	}
	
	public void pause() {
		thread.running = false;
	}
	
	public void resume() {
		thread.running = true;
	}
}
