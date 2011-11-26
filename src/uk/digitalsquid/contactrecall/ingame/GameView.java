package uk.digitalsquid.contactrecall.ingame;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.contactrecall.ingame.GameView.ViewWorker;
import android.content.Context;
import android.graphics.Matrix;
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

		public ViewWorker(Context context) {
			super(context);
		}

		@Override
		protected void initialiseOnThread() {
		}

		@Override
		protected void onThreadEnd() {
		}
		
		final Matrix matrix2d = new Matrix();
		final Matrix matrixInverse = new Matrix();

		@Override
		protected void scale(GL10 c) {
			matrix2d.reset();
			
			matrix2d.postTranslate(-width / 2, -height / 2);
			matrix2d.postScale(scaledWidth / (float)width, scaledHeight / (float)height);
			matrix2d.invert(matrixInverse);
		}

		@Override
		public void saveState(Bundle bundle) {
		}

		@Override
		public void restoreState(Bundle bundle) {
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
		protected void draw(GL10 gl){}
		@Override
		protected void postdraw(GL10 gl){}
		
	}
}
