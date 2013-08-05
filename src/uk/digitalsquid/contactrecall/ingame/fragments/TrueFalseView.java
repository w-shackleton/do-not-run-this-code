package uk.digitalsquid.contactrecall.ingame.fragments;

import uk.digitalsquid.contactrecall.R;
import android.view.View;
import android.widget.Button;

public abstract class TrueFalseView<QView extends View> extends MultiChoiceView<QView, Button> {

	@Override
	protected Button[] getChoiceButtons(View rootView) {
		return new Button[] {
				(Button)rootView.findViewById(R.id.choice1),
				(Button)rootView.findViewById(R.id.choice2),
		};
	}
}
