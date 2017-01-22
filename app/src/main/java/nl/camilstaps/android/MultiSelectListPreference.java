package nl.camilstaps.android;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CheckedTextView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MultiSelectListPreference extends android.preference.MultiSelectListPreference {
	public MultiSelectListPreference(Context context) {
		super(context);
	}

	public MultiSelectListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected List<String> getCurrentEntries() {
		CharSequence[] entries = getEntries();
		CharSequence[] entryValues = getEntryValues();
		List<String> currentEntries = new ArrayList<>();
		Set<String> currentEntryValues = getValues();

		for (int i = 0; i < entries.length; i++)
			if (currentEntryValues.contains(entryValues[i].toString()))
				currentEntries.add(entries[i].toString());

		return currentEntries;
	}

	public void setSummaryLikeEntries() {
		StringBuilder sb = new StringBuilder();
		boolean started = false;

		for (String val : getCurrentEntries()) {
			if (started)
				sb.append(", ");
			sb.append(val);
			started = true;
		}

		setSummary(sb.toString());
	}

	/* Fix: http://stackoverflow.com/a/37466400/1544337 */
	@Override
	protected void showDialog(Bundle state) {
		super.showDialog(state);

		AlertDialog dialog = (AlertDialog) getDialog();
		if (dialog == null)
			return;

		if (Build.VERSION.SDK_INT >= 23) {
			ListView listView = dialog.getListView();

			listView.setOnScrollListener(new AbsListView.OnScrollListener() {
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					int size = view.getChildCount();
					for (int i=0; i<size; i++) {
						View v = view.getChildAt(i);
						if (v instanceof CheckedTextView)
							v.refreshDrawableState();
					}
				}

				@Override
				public void onScroll(AbsListView view,
									 int firstVisible, int visibleCount, int totalCount) {
					int size = view.getChildCount();
					for (int i=0; i<size; i++) {
						View v = view.getChildAt(i);
						if (v instanceof CheckedTextView)
							v.refreshDrawableState();
					}
				}
			});
		}
	}
}