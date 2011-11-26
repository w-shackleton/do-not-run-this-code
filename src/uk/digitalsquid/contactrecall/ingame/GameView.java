package uk.digitalsquid.contactrecall.ingame;

import android.content.Context;
import android.util.AttributeSet;
import uk.digitalsquid.contactrecall.ingame.GameView.ViewWorker;

public class GameView extends DrawBaseView<ViewWorker> {

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected ViewWorker createThread() {
		// TODO Auto-generated method stub
		return null;
	}

	public static abstract class ViewWorker extends DrawBaseView.ViewWorker {

		public ViewWorker(Context context) {
			super(context);
		}
	}
}
