package nl.camilstaps.rbn.android;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.util.List;

import nl.camilstaps.android.Util;
import nl.camilstaps.list.EndDiscardingList;
import nl.camilstaps.rbn.Band;
import nl.camilstaps.rbn.Client;
import nl.camilstaps.rbn.R;
import nl.camilstaps.rbn.Record;
import nl.camilstaps.rbn.Speed;
import nl.camilstaps.rbn.filter.AnyOfFilter;
import nl.camilstaps.rbn.filter.Filter;
import nl.camilstaps.rbn.filter.RangeFilter;

public class LoggingFragment extends Fragment {
	private final List<Record> records = new EndDiscardingList<>(100);
	private RecordArrayAdapter adapter;

	public void start(final Activity activity) {
		Util.quickToast(activity, "Connecting...");

		final AnyOfFilter erf1 = AnyOfFilter.just(Filter.Field.Mode, Record.Mode.CW);
		final AnyOfFilter erf2 = AnyOfFilter.just(Filter.Field.Band, new Band(20));
		final RangeFilter rrf1 = new RangeFilter(Filter.Field.Speed, 0, 20, Speed.SpeedUnit.WPM);

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... x) {
				try {
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
					Resources ress = activity.getResources();
					Client client = new Client(
							prefs.getString("callsign", ress.getString(R.string.pref_callsign_default)),
							prefs.getString("host", ress.getString(R.string.pref_host_default)),
							Integer.valueOf(prefs.getString("port", ress.getString(R.string.pref_port_default))));
					Util.quickToast(activity, "Listening...");
					client.register(new Client.NewRecordListener() {
						@Override
						public void receive(final Record record) {
							activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									if (erf1.matches(record) && erf2.matches(record) && rrf1.matches(record)) {
										records.add(record);
										if (adapter != null)
											adapter.notifyDataSetChanged();
									}
								}
							});
						}

						@Override
						public void unparsable(String line, ParseException e) {
							Util.quickToast(activity, e.toString() + ":\n" + line);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					Util.quickToast(activity, "IOException: " + e.getMessage());
				}
				return null;
			}
		}.execute();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_log, container, false);
		final ListView callListView = (ListView) view.findViewById(R.id.fragment_log_calllist);
		adapter = new RecordArrayAdapter(inflater.getContext(), records);
		callListView.setAdapter(adapter);

		return view;
	}

	private class RecordArrayAdapter extends ArrayAdapter<Record> {
		private Context context;

		public RecordArrayAdapter(Context context, List<Record> objects) {
			super(context, -1, objects);
			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Record r = getItem(position);

			LayoutInflater inflater = (LayoutInflater)
					context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.record_item, parent, false);

			try {
				((ImageView) rowView.findViewById(R.id.record_item_flag))
						.setImageResource(getResources().getIdentifier(
								"flag_" + r.getDe().getCountry().toString().toLowerCase(),
								"drawable", "nl.camilstaps.rbn"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			((TextView) rowView.findViewById(R.id.record_item_callsign))
					.setText(r.getDe().toString());
			((TextView) rowView.findViewById(R.id.record_item_frequency))
					.setText(String.format("%.2f", r.getFrequency()));
			((TextView) rowView.findViewById(R.id.record_item_band_text))
					.setText(r.getBand().toString());
			((TextView) rowView.findViewById(R.id.record_item_mode))
					.setText(r.getMode().toString());
			((ImageView) rowView.findViewById(R.id.record_item_band_icon))
					.setColorFilter(bandToColour(r.getBand()), PorterDuff.Mode.SRC);

			((TextView) rowView.findViewById(R.id.record_item_description))
					.setText(Util.fromHtml(r.getStrength() + "dB de " + r.getDx() + " &#8226; " +
							r.getSpeed() + " &#8226; " + r.getType()));

			return rowView;
		}

		@Override
		public Record getItem(int position) {
			return super.getItem(getCount() - position - 1);
		}
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
		return 0xaa888888;
	}
}
