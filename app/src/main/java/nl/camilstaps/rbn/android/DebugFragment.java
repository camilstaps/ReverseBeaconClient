package nl.camilstaps.rbn.android;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import nl.camilstaps.rbn.R;
import nl.camilstaps.util.Logger;

public class DebugFragment extends Fragment implements Logger.LoggerListener, View.OnClickListener {
	private EditText logTextView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
		View view = inflater.inflate(R.layout.fragment_debug, container, false);

		logTextView = (EditText) view.findViewById(R.id.debug_text);
		logTextView.setText(Logger.getInstance().getContent());

		view.findViewById(R.id.debug_share).setOnClickListener(this);

		Logger.getInstance().addListener(this);

		return view;
	}

	@Override
	public void onUpdatedContent() {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (logTextView == null)
					return;
				logTextView.setText(Logger.getInstance().getContent());
				logTextView.setSelection(logTextView.getText().length());
			}
		});
	}

	@Override
	public void onClick(View v) {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, Logger.getInstance().getContent());
		sendIntent.putExtra(Intent.EXTRA_EMAIL, "pd7lol@camilstaps.nl");
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Reverse Beacon Client debug log");
		sendIntent.setType("text/plain");
		startActivity(Intent.createChooser(sendIntent, "Choose an email app"));
	}
}
