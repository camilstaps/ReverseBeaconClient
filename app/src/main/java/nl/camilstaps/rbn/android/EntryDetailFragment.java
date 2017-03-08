package nl.camilstaps.rbn.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import nl.camilstaps.android.Util;
import nl.camilstaps.rbn.Entry;
import nl.camilstaps.rbn.R;

public class EntryDetailFragment extends DialogFragment {
	private Entry entry;
	private DialogInterface.OnDismissListener listener;

	public void setEntry(Entry entry) {
		this.entry = entry;
	}

	public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
		this.listener = listener;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		if (listener != null)
			listener.onDismiss(dialog);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Activity activity = getActivity();

		LayoutInflater inflater = (LayoutInflater)
				activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View alertView = inflater.inflate(R.layout.entry_detail, null);
		final RecordArrayAdapter adapter = new RecordArrayAdapter(activity, entry);

		final String defaultCallDesc = getResources().getString(R.string.warning_unknown_callsign);

		ListView dxList = (ListView) alertView.findViewById(R.id.record_list);
		dxList.setAdapter(adapter);
		dxList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Entry.Record record = adapter.getItem(position);
				((RBNApplication) activity.getApplication()).quickToast(
						record.dx.getDescription(defaultCallDesc));
			}
		});

		entry.setOnRecordAddedListener(new Entry.OnRecordAddedListener() {
			@Override
			public void onRecordAdded(Entry.Record record) {
				adapter.notifyDataSetChanged();
			}
		});

		((ImageView) alertView.findViewById(R.id.flag)).setImageResource(
				nl.camilstaps.rbn.android.Util.getFlagResource(activity, entry.getDe()));
		((TextView) alertView.findViewById(R.id.callsign)).setText(
				entry.getDe().toString());
		((TextView) alertView.findViewById(R.id.callsign_description)).setText(
				entry.getDe().getDescription(defaultCallDesc));
		((TextView) alertView.findViewById(R.id.main_info)).setText(Util.fromHtml(
				String.format("%.1f", entry.getAvgFrequency()) + " &#8226; " +
						entry.getAvgSpeed() + " &#8226; " +
						entry.getMode() + " &#8226; " + entry.getType()));

		TextView qrzLink = (TextView) alertView.findViewById(R.id.qrz_link);
		qrzLink.setText(Util.fromHtml(String.format(
				getResources().getString(R.string.qrz_link), entry.getDe().toString())));
		qrzLink.setMovementMethod(LinkMovementMethod.getInstance());

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setView(alertView);
		return builder.create();
	}

	private class RecordArrayAdapter extends ArrayAdapter<Entry.Record> {
		private final Context context;
		private final Entry entry;

		RecordArrayAdapter(Context context, Entry entry) {
			super(context, -1, entry.getRecords());
			this.context = context;
			this.entry = entry;
		}

		@Override
		@NonNull
		public View getView(int position, View view, @NonNull ViewGroup parent) {
			RecordItemViewHolder holder;

			if (view == null) {
				holder = new RecordItemViewHolder();

				LayoutInflater inflater = (LayoutInflater)
						context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.record_item, parent, false);

				holder.callsign = (TextView) view.findViewById(R.id.callsign);
				holder.flag = (ImageView) view.findViewById(R.id.flag);
				holder.main_info = (TextView) view.findViewById(R.id.main_info);
				holder.strength = (TextView) view.findViewById(R.id.strength);
				holder.timestamp = (TextView) view.findViewById(R.id.timestamp);

				view.setTag(holder);
			} else {
				holder = (RecordItemViewHolder) view.getTag();
			}

			Entry.Record r = getItem(position);

			holder.flag.setImageResource(
					nl.camilstaps.rbn.android.Util.getFlagResource(getActivity(), r.dx));
			holder.callsign.setText(r.dx.toString());
			holder.strength.setText(String.format("%d dB", r.strength));

			DateFormat df = new SimpleDateFormat("HH:mm'Z'");
			df.setTimeZone(TimeZone.getTimeZone("Zulu"));
			holder.timestamp.setText(df.format(r.date));

			holder.main_info.setText(Util.fromHtml(
					String.format("%.1f", r.frequency) + " &#8226; " +
							r.speed + " " + entry.getSpeedUnit()));

			return view;
		}

		@Override
		public Entry.Record getItem(int position) {
			return super.getItem(getCount() - position - 1);
		}
	}

	private static class RecordItemViewHolder {
		private ImageView flag;
		private TextView callsign, main_info, strength, timestamp;
	}
}
