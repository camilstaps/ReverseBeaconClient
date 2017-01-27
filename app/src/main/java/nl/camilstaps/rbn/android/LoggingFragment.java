package nl.camilstaps.rbn.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import nl.camilstaps.android.Util;
import nl.camilstaps.list.EndDiscardingList;
import nl.camilstaps.rbn.Band;
import nl.camilstaps.rbn.Callsign;
import nl.camilstaps.rbn.Entry;
import nl.camilstaps.rbn.NewRecordListener;
import nl.camilstaps.rbn.R;
import nl.camilstaps.rbn.filter.Filter;

public class LoggingFragment extends Fragment implements AdapterView.OnItemClickListener {
	private Activity activity;

	private EntryArrayAdapter adapter;
	private final EndDiscardingList<Entry> entries = new EndDiscardingList<>(100);

	private boolean isRegistered = false;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		setRetainInstance(true);

		if (activity == null)
			activity = getActivity();

		if (!isRegistered) {
			adapter = new EntryArrayAdapter(activity, entries);
			registerLogger();
			isRegistered = true;
		}
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
							for (Entry otherEntry : entries) synchronized (entries) {
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

		callListView.setOnItemClickListener(this);

		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Entry entry = adapter.getItem(position);

		LayoutInflater inflater = (LayoutInflater)
				activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View alertView = inflater.inflate(R.layout.entry_detail, parent, false);
		final RecordArrayAdapter adapter = new RecordArrayAdapter(activity, entry);
		((ListView) alertView.findViewById(R.id.record_list)).setAdapter(adapter);

		entry.setOnRecordAddedListener(new Entry.OnRecordAddedListener() {
			@Override
			public void onRecordAdded(Entry.Record record) {
				adapter.notifyDataSetChanged();
			}
		});

		((ImageView) alertView.findViewById(R.id.flag)).setImageResource(
				getFlagResource(entry.getDe()));
		((TextView) alertView.findViewById(R.id.callsign)).setText(
				entry.getDe().toString());
		((TextView) alertView.findViewById(R.id.callsign_description)).setText(
				"Description");
		((TextView) alertView.findViewById(R.id.main_info)).setText(Util.fromHtml(
				String.format("%.1f", entry.getAvgFrequency()) + " &#8226; " +
				entry.getAvgSpeed() + " &#8226; " +
				entry.getMode() + " &#8226; " + entry.getType()));

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setView(alertView);
		builder.show();
	}

	private int getFlagResource(Callsign call) {
		return activity.getResources().getIdentifier(
				"flag_" + call.getCountry().toString().toLowerCase(),
				"drawable", "nl.camilstaps.rbn");
	}

	private class EntryArrayAdapter extends ArrayAdapter<Entry> {
		private final Context context;

		EntryArrayAdapter(Context context, List<Entry> objects) {
			super(context, -1, objects);
			this.context = context;
		}

		@Override
		@NonNull
		public View getView(int position, View view, @NonNull ViewGroup parent) {
			EntryItemViewHolder holder;

			if (view == null) {
				holder = new EntryItemViewHolder();

				LayoutInflater inflater = (LayoutInflater)
						context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.entry_item, parent, false);

				holder.band = (TextView) view.findViewById(R.id.band);
				holder.bandIcon = (ImageView) view.findViewById(R.id.band_icon);
				holder.callsign = (TextView) view.findViewById(R.id.callsign);
				holder.description = (TextView) view.findViewById(R.id.main_info);
				holder.flag = (ImageView) view.findViewById(R.id.flag);
				holder.frequency = (TextView) view.findViewById(R.id.frequency);
				holder.mode = (TextView) view.findViewById(R.id.mode);
				holder.time = (TextView) view.findViewById(R.id.timestamp);

				view.setTag(holder);
			} else {
				holder = (EntryItemViewHolder) view.getTag();
			}

			Entry entry = getItem(position);

			try {
				holder.flag.setImageResource(activity.getResources().getIdentifier(
						"flag_" + entry.getDe().getCountry().toString().toLowerCase(),
						"drawable", "nl.camilstaps.rbn"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			holder.callsign.setText(entry.getDe().toString());
			holder.frequency.setText(String.format("%.1f", entry.getAvgFrequency()));
			holder.band.setText(entry.getBand().toString());
			holder.bandIcon.setColorFilter(bandToColour(entry.getBand()), PorterDuff.Mode.SRC);
			holder.mode.setText(entry.getMode().toString());

			List<Entry.Record> records = entry.getRecords();
			int recordsCount = records.size();
			Entry.Record firstRecord = records.get(0);

			int mindB = entry.getMinStrength();
			int maxdB = entry.getMaxStrength();

			holder.description.setText(Util.fromHtml(
					(mindB == maxdB ? mindB : mindB + "-" + maxdB) + "dB " +
					(recordsCount == 1 ? "de " + firstRecord.dx : "(" + recordsCount + "x)") +
					" &#8226; " + entry.getAvgSpeed() + " &#8226; " + entry.getType()));

			DateFormat df = new SimpleDateFormat("HH:mm'Z'");
			df.setTimeZone(TimeZone.getTimeZone("Zulu"));

			if (recordsCount == 1 ||
					entry.getFirstDate().getTime() / 60000 == entry.getLastDate().getTime() / 60000)
				holder.time.setText(df.format(firstRecord.date));
			else
				holder.time.setText(
						df.format(entry.getFirstDate()) + " - " + df.format(entry.getLastDate()));

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

	private static class EntryItemViewHolder {
		private ImageView bandIcon, flag;
		private TextView band, callsign, description, frequency, mode, time;
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

			holder.flag.setImageResource(getFlagResource(r.dx));
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
