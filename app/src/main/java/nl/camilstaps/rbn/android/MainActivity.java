package nl.camilstaps.rbn.android;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import nl.camilstaps.rbn.Band;
import nl.camilstaps.rbn.Client;
import nl.camilstaps.rbn.R;
import nl.camilstaps.rbn.Record;

public class MainActivity extends AppCompatActivity {

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView callListView = (ListView) findViewById(R.id.activity_main_calllist);
        final ArrayList<Record> records = new ArrayList<>();
        final RecordArrayAdapter adapter = new RecordArrayAdapter(this, records);
        callListView.setAdapter(adapter);

        quickToast("Connecting...");

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... x) {
                try {
                    Client client = new Client("PD7LOL", "telnet.reversebeacon.net", 7000);
                    client.register(new Client.NewRecordListener() {
                        @Override
                        public void receive (final Record record){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    records.add(record);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }

                        @Override
                        public void unparsable (String line, ParseException e){
                            quickToast(e.toString() + ":\n" + line);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    quickToast("IOException: " + e.getMessage());
                }
                return null;
            }
        }.execute();
    }

    private void quickToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (toast == null)
                    toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
                else
                    toast.setText(text);
                toast.show();
            }
        });
    }

    private class RecordArrayAdapter extends ArrayAdapter<Record> {
        private Context context;
        private List<Record> objects;

        public RecordArrayAdapter(Context context, List<Record> objects) {
            super(context, -1, objects);
            this.context = context;
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Record r = getItem(position);

            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.record_item, parent, false);

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
            return super.getItem(super.getCount() - position - 1);
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
