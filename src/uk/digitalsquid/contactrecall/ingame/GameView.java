package uk.digitalsquid.contactrecall.ingame;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import uk.digitalsquid.contactrecall.ingame.GameView.ViewWorker;

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

		@Override
		protected void scale(GL10 c) {
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
