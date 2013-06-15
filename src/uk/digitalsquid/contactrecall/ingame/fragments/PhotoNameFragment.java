package uk.digitalsquid.contactrecall.ingame.fragments;

import uk.digitalsquid.contactrecall.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PhotoNameFragment extends Fragment {
	public static final String ARG_CONTACT = "contact";
	public static final String ARG_OTHER_NAMES = "othernames";
	public static final String ARG_NUMBER_CHOICES = "numchoices";

    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.photonameview, container, false);
        Bundle args = getArguments();
        
        return rootView;
    }
}
