package nl.camilstaps.rbn.android;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nl.camilstaps.android.Util;
import nl.camilstaps.rbn.R;

public class InfoFragment extends Fragment {
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
		View view = inflater.inflate(R.layout.fragment_info, container, false);
		TextView textView = (TextView) view.findViewById(R.id.info_text);
		textView.setText(Util.fromHtml(getResources().getString(R.string.text_info)));
		textView.setMovementMethod(new ScrollingMovementMethod());
		return view;
	}
}
