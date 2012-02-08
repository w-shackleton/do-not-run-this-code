package uk.digitalsquid.contactrecall.ingame;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.contactrecall.game.GameInstance;
import uk.digitalsquid.contactrecall.ingame.GameView.ViewWorker;
import uk.digitalsquid.contactrecall.ingame.gl.QAViewer;
import uk.digitalsquid.contactrecall.ingame.gl.RectMesh;
import uk.digitalsquid.contactrecall.ingame.gl.objects.Timer;
import uk.digitalsquid.contactrecall.mgr.Contact;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;

public class GameView extends DrawBaseView<ViewWorker> {

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initP2();
	}

	@Override
	protected ViewWorker createThread() {
		return new ViewWorker(context);
	}
	
	GameCallbacks gameCallbacks = GameCallbacks.EMPTY_CALLBACKS;

	void setGameCallbacks(GameCallbacks gameCallbacks) {
		this.gameCallbacks = gameCallbacks;
		if(this.gameCallbacks == null) this.gameCallbacks = GameCallbacks.EMPTY_CALLBACKS;
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
		
		long timeDiffNano;
		float timeDiff;
		
		/**
		 * The amount of time allowed per question, in nanoseconds
		 */
		long questionTimeAllowance = 4000L * 1000000L;
		
		static final int STATUS_SHOWING = 1;
		static final int STATUS_CHANGING = 2;
		static final int STATUS_FINISHING = 3;
		int status;

		public ViewWorker(Context context) {
			super(context);
		}

		@Override
		protected void initialiseOnThread() {
			if(DEBUG) {
				pointerPos = new RectMesh(0, 0, 2, 2, 1, 0, 0, 1);
				pointerPos.setVisible(false);
			}
			
			loadNextContact();
			beginShowNextQuestion();
		}
		
		protected void onSizeChanged(float width, float height) {
			even.setOrientation(landscape, width, height);
			odd.setOrientation(landscape, width, height);
			
			// Top right
			timeDisplay.setXYZ(
					width / 2 - 3,
					height / 2 - 3,
					0);
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
			bundle.putInt("position", currentPosition);
		}

		@Override
		public void restoreState(Bundle bundle) {
			totalTimer = bundle.getLong("totalTimer");
			currentTimer = bundle.getLong("currentTimer");
			game.windTo(bundle.getInt("position"));
			oldTime = -1; // TODO: Put this elsewhere as well?
		}
		
		QAViewer odd = new QAViewer(), even = new QAViewer(); // 2 required due to fade in/out
		Timer timeDisplay = new Timer(0, 0, 2, 2);
		
		@Override
		protected void draw(GL10 gl){
			if(pointerPos != null) pointerPos.draw(gl);
			odd.draw(gl);
			even.draw(gl);
			timeDisplay.draw(gl);
		}
		
		long oldTime = -1;
		
		@Override
		protected void precalculate() {
			boolean havePreviousTime = oldTime != -1;
			if(havePreviousTime) { // Ignore, as no previous time
				timeDiffNano = System.nanoTime() - oldTime;
				timeDiff = timeDiffNano / 1000000;
				totalTimer += timeDiffNano;
				currentTimer += timeDiffNano;
			}
			oldTime = System.nanoTime();
		}
		@Override
		protected void calculate() {
			even.move(timeDiff);
			odd.move(timeDiff);
			
			// Update onscreen timer
			timeDisplay.setProgress((float)currentTimer / (float)questionTimeAllowance);
			
			//TODO: Implement this properly!
			if(currentTimer > questionTimeAllowance) {
				currentTimer = 0;
				beginShowNextQuestion();
			}
		}
		@Override
		protected void postcalculate(){}
		
		Contact current, next;
		int currentPosition = 0, nextPosition = 0;
		
		/**
		 * Begin animation between questions.
		 */
		void beginShowNextQuestion() {
			if(next != null) {
				current = next;
				Log.i(TAG, "Current contact is " + current.getDisplayName());
				currentPosition = nextPosition;
				next = null;
				if(currentPosition % 2 == 0) { // Even
					 even.animateIn(null);
					 odd.animateOut(new Runnable() {
						@Override
						public void run() {
							// Finished animating out
							onNextQuestionShown();
						}
					});
				} else {
					 odd.animateIn(null);
					 even.animateOut(new Runnable() {
						@Override
						public void run() {
							// Finished animating out
							onNextQuestionShown();
						}
					});
				}
			}
		}
		
		void onNextQuestionShown() {
			loadNextContact();
		}
		
		void onQuestionsFinishing() {
			
		}
		
		/**
		 * Moves onto the next contact, which is loaded into the non active buffer.
		 */
		void loadNextContact() {
			next = game.getNext();
			nextPosition = game.getProgress();
			if(next == null) {
				onQuestionsFinishing();
				return;
			}
			if(game.getProgress() % 2 == 0) { // Even
				// (IGNORE?) Next, so load into ODD buffer
				even.setQuestion(game.getFromMode(), game.getFromObject());
				even.setAnswer(game.getToMode(), game.getToObjects());
			} else {
				odd.setQuestion(game.getFromMode(), game.getFromObject());
				even.setAnswer(game.getToMode(), game.getToObjects());
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
			if(pointerPos != null) pointerPos.setXYZ(x, y, 0);
		}

		@Override
		protected void onTouchMove(float x, float y) {
			if(pointerPos != null) pointerPos.setVisible(true);
			if(pointerPos != null) pointerPos.setXYZ(x, y, 0);
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
		setRenderMode(RENDERMODE_WHEN_DIRTY); // Save a bit of CPU
		gameCallbacks.onGamePaused();
	}
	
	public void resume() {
		thread.running = true;
		thread.oldTime = -1; // So we don't get a big time jump
		setRenderMode(RENDERMODE_CONTINUOUSLY);
		gameCallbacks.onGameResumed();
	}
	
	public void cancelGame() {
		gameCallbacks.onGameCancelled();
	}
	
	public boolean isRunning() {
		return thread.running;
	}
}
