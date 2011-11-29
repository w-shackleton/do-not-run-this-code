package uk.digitalsquid.contactrecall.ingame;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.contactrecall.game.GameInstance;
import uk.digitalsquid.contactrecall.ingame.GameView.ViewWorker;
import uk.digitalsquid.contactrecall.ingame.gl.QAViewer;
import uk.digitalsquid.contactrecall.ingame.gl.RectMesh;
import uk.digitalsquid.contactrecall.mgr.Contact;
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
		
		/**
		 * A timer for the whole game. In nanoseconds
		 */
		long totalTimer;
		/**
		 * A timer for the current question. In nanoseconds
		 */
		long currentTimer;

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
			bundle.putLong("totalTimer", totalTimer);
			bundle.putLong("currentTimer", currentTimer);
		}

		@Override
		public void restoreState(Bundle bundle) {
			totalTimer = bundle.getLong("totalTimer");
			currentTimer = bundle.getLong("currentTimer");
			oldTime = -1; // TODO: Put this elsewhere as well?
		}
		
		QAViewer odd = new QAViewer(), even = new QAViewer(); // 2 required due to fade in/out
		
		@Override
		protected void draw(GL10 gl){
			if(pointerPos != null) pointerPos.draw(gl);
			odd.draw(gl);
			even.draw(gl);
		}
		
		long oldTime = -1;
		
		@Override
		protected void precalculate() {
			boolean havePreviousTime = oldTime != -1;
			if(havePreviousTime) { // Ignore, as no previous time
				long timeDiff = System.nanoTime() - oldTime;
				totalTimer += timeDiff;
				currentTimer += timeDiff;
			}
			oldTime = System.nanoTime();
		}
		@Override
		protected void calculate(){}
		@Override
		protected void postcalculate(){}
		
		Contact current, next;
		boolean currentIsEven = true;
		
		/**
		 * Moves onto the next contact, which is loaded into the non active buffer.
		 */
		void loadNextContact() {
			next = game.getNext();
			if(game.getProgress() % 2 == 0) { // Even
				
			}
		}
		
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
