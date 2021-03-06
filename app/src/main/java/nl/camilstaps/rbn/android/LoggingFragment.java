package nl.camilstaps.rbn.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Build;
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
import nl.camilstaps.rbn.Entry;
import nl.camilstaps.rbn.NewRecordListener;
import nl.camilstaps.rbn.R;

public class LoggingFragment extends Fragment implements AdapterView.OnItemClickListener {
	private Activity activity;

	private EntryArrayAdapter adapter;
	private final EndDiscardingList<Entry> entries = new EndDiscardingList<>(100);

	private boolean isRegistered = false;
	private Entry openedEntryDetails;

	// See https://stackoverflow.com/a/33655722/1544337
	@TargetApi(23)
	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		onAttachToContext(context);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (Build.VERSION.SDK_INT < 23)
			onAttachToContext(activity);
	}

	private void onAttachToContext(Context context) {
		if (activity == null)
			activity = getActivity();

		if (!isRegistered) {
			adapter = new EntryArrayAdapter(activity, entries);
			registerLogger();
			isRegistered = true;
		}
	}

	private void registerLogger() {
		((RBNApplication) activity.getApplication()).registerClientListener(
				new NewRecordListener() {
			@Override
			public boolean receivesAll() {
				return false;
			}

			@Override
			public void receive(final Entry entry, boolean matchesFilter) {
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

			@Override
			public void unparsable(String line, ParseException e) {
				feedback(line, e);
			}

			@Override
			public void onDisconnected() {
				if (!isAdded())
					return;

				feedback(getResources().getString(R.string.connection_lost), null);
			}

			@Override
			public void onReconnected() {
				if (!isAdded())
					return;

				feedback(getResources().getString(R.string.reconnected), null);
			}

			private void feedback(final String extra, final Exception e) {
				if (!isAdded())
					return;

				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						((RBNApplication) getActivity().getApplication())
								.quickToast((e != null ? e.toString() + ":\n" : "") + extra);
					}
				});
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

		if (openedEntryDetails != null)
			openEntryDetails(openedEntryDetails);

		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Entry entry = adapter.getItem(position);
		openEntryDetails(entry);
	}

	private void openEntryDetails(Entry entry) {
		EntryDetailFragment fragment = new EntryDetailFragment();
		fragment.setEntry(entry);
		fragment.setRetainInstance(true);
		getActivity().getFragmentManager()
				.beginTransaction()
				.add(fragment, "entry")
				.addToBackStack(null)
				.commit();

		openedEntryDetails = entry;

		fragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				openedEntryDetails = null;
			}
		});
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
				holder.flag.setImageResource(
						nl.camilstaps.rbn.android.Util.getFlagResource(activity, entry.getDe()));
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
}
