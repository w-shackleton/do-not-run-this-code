package uk.digitalsquid.contactrecall.ingame;

import uk.digitalsquid.contactrecall.misc.Config;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TableLayout;

/**
 * Shows pairs of questions and answers on-screen and allows the user to draw
 * lines between them.
 * @author william
 *
 */
public class PairingLayout extends TableLayout implements Config {
	
	Context context;

	public PairingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}
}
