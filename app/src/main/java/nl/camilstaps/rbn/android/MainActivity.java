package nl.camilstaps.rbn.android;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;

import nl.camilstaps.rbn.Client;
import nl.camilstaps.rbn.R;
import nl.camilstaps.rbn.Record;

public class MainActivity extends AppCompatActivity {

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        quickToast("Connecting...");

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... x) {
                try {
                    Client client = new Client("PD7LOL", "telnet.reversebeacon.net", 7000);
                    client.register(new Client.NewRecordListener() {
                        @Override
                        public void receive (Record record){
                            quickToast(record.toString());
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
}
