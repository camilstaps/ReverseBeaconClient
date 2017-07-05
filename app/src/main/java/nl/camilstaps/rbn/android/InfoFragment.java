package nl.camilstaps.rbn.android;

import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.LinkMovementMethod;
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
		String info = getResources().getString(R.string.text_info);
		try {
			Activity activity = getActivity();
			PackageManager pacman = activity.getPackageManager();
			String version = pacman.getPackageInfo(activity.getPackageName(), 0).versionName;
			info = info.replace("VERSION", version);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		View view = inflater.inflate(R.layout.fragment_info, container, false);
		TextView textView = (TextView) view.findViewById(R.id.info_text);
		textView.setText(Util.fromHtml(info));
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		return view;
	}
}
