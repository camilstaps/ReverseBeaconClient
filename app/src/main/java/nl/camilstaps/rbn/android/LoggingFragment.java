package nl.camilstaps.rbn.android;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import nl.camilstaps.android.Util;
import nl.camilstaps.list.EndDiscardingList;
import nl.camilstaps.rbn.Band;
import nl.camilstaps.rbn.Entry;
import nl.camilstaps.rbn.NewRecordListener;
import nl.camilstaps.rbn.R;
import nl.camilstaps.rbn.filter.Filter;

public class LoggingFragment extends Fragment {
	private Activity activity;

	private RecordArrayAdapter adapter;
	private final EndDiscardingList<Entry> entries = new EndDiscardingList<>(100);

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		setRetainInstance(true);

		if (activity == null)
			activity = getActivity();

		adapter = new RecordArrayAdapter(activity, entries);
		registerLogger();
	}

	private void registerLogger() {
		final Filter filter = ((RBNApplication) getActivity().getApplication()).getMainFilter();

		((RBNApplication) activity.getApplication()).registerClientListener(
				new NewRecordListener() {
			@Override
			public void receive(final Entry entry) {
				if (filter.matches(entry)) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							boolean addNew = true;
							int bumpIndex = 0;
							for (Entry otherEntry : entries) {
								if (otherEntry.attemptMerge(entry)) {
									addNew = false;
									break;
								}
								bumpIndex++;
							}

							if (addNew)
								entries.add(entry);
							else
								entries.bumpToEnd(bumpIndex);

							adapter.notifyDataSetChanged();
						}
					});
				}
			}

			@Override
			public void unparsable(String line, ParseException e) {
				feedback(line, e);
			}

			@Override
			public void onDisconnected() {
				feedback("Lost connection to RBN", null);
			}

			@Override
			public void onReconnected() {
				feedback("Reconnected to RBN", null);
			}

			private void feedback(String extra, Exception e) {
				((RBNApplication) getActivity().getApplication())
						.quickToast((e != null ? e.toString() + ":\n" : "") + extra);
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_log, container, false);
		final ListView callListView = (ListView) view.findViewById(R.id.fragment_log_calllist);
		callListView.setAdapter(adapter);

		return view;
	}

	private class RecordArrayAdapter extends ArrayAdapter<Entry> implements Serializable {
		private final Context context;

		RecordArrayAdapter(Context context, List<Entry> objects) {
			super(context, -1, objects);
			this.context = context;
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

				holder.band = (TextView) view.findViewById(R.id.record_item_band_text);
				holder.bandIcon = (ImageView) view.findViewById(R.id.record_item_band_icon);
				holder.callsign = (TextView) view.findViewById(R.id.record_item_callsign);
				holder.description = (TextView) view.findViewById(R.id.record_item_description);
				holder.flag = (ImageView) view.findViewById(R.id.record_item_flag);
				holder.frequency = (TextView) view.findViewById(R.id.record_item_frequency);
				holder.mode = (TextView) view.findViewById(R.id.record_item_mode);
				holder.time = (TextView) view.findViewById(R.id.record_item_time);

				view.setTag(holder);
			} else {
				holder = (RecordItemViewHolder) view.getTag();
			}

			Entry r = getItem(position);

			try {
				holder.flag.setImageResource(activity.getResources().getIdentifier(
						"flag_" + r.getDe().getCountry().toString().toLowerCase(),
						"drawable", "nl.camilstaps.rbn"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			holder.callsign.setText(r.getDe().toString());
			holder.frequency.setText(String.format("%.1f", r.getAvgFrequency()));
			holder.band.setText(r.getBand().toString());
			holder.bandIcon.setColorFilter(bandToColour(r.getBand()), PorterDuff.Mode.SRC);
			holder.mode.setText(r.getMode().toString());

			List<Entry.Record> records = r.getRecords();
			int recordsCount = records.size();
			Entry.Record firstRecord = records.get(0);

			int mindB = r.getMinStrength();
			int maxdB = r.getMaxStrength();

			holder.description.setText(Util.fromHtml(
					(mindB == maxdB ? mindB : mindB + "-" + maxdB) + "dB " +
					(recordsCount == 1 ? "de " + firstRecord.dx : "(" + recordsCount + "x)") +
					" &#8226; " + r.getAvgSpeed() + " &#8226; " + r.getType()));

			DateFormat df = new SimpleDateFormat("HH:mm'Z'");
			df.setTimeZone(TimeZone.getTimeZone("Zulu"));

			if (recordsCount == 1 ||
					r.getFirstDate().getTime() / 60000 == r.getLastDate().getTime() / 60000)
				holder.time.setText(df.format(firstRecord.date));
			else
				holder.time.setText(
						df.format(r.getFirstDate()) + " - " + df.format(r.getLastDate()));

			return view;
		}

		@Override
		public Entry getItem(int position) {
			return super.getItem(getCount() - position - 1);
		}

		private int bandToColour(Band band) {
			switch ((int) (100 * band.getWavelength())) {
				case 16000: return 0xffffe000;
				case  8000: return 0xff093f00;
				case  4000: return 0xffffa500;
				case  3000: return 0xffff0000;
				case  2000: return 0xff800080;
				case  1700: return 0xff0000ff;
				case  1500: return 0xffff00ff;
				case  1200: return 0xff00ffff;
				case  1000: return 0xffaaaaaa;
				case   600: return 0xffffc0cb;
				case   200: return 0xff92ff7f;
			}
			return 0xff888888;
		}
	}

	private static class RecordItemViewHolder {
		private ImageView bandIcon, flag;
		private TextView band, callsign, description, frequency, mode, time;
	}
}
