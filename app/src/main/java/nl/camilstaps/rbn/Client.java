package nl.camilstaps.rbn;

import android.util.Log;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;

public final class Client {
    private final TelnetClient client;
    private final InputStream inputStream;
    private final BufferedReader bufferedReader;
    private final PrintStream outputStream;

    private final Collection<NewRecordListener> listeners = new ArrayList<>();

    public Client(String call, String host, int port) throws IOException {
        client = new TelnetClient();
        client.setConnectTimeout(2000);
        client.connect(host, port);
        client.setKeepAlive(true);

        inputStream = client.getInputStream();
        outputStream = new PrintStream(client.getOutputStream());

        Log.i("RBN", "Connected");

        readUntil("Please enter your call:");
        outputStream.print(call + "\r\n");
        outputStream.flush();

        Log.i("RBN", "logged in");

        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        (new Thread(new ClientThread())).start();
    }

    public void register(NewRecordListener listener) {
        listeners.add(listener);
    }

    private void readUntil(String pattern) throws IOException {
        char lastChar = pattern.charAt(pattern.length() - 1);
        StringBuilder sb = new StringBuilder();
        int c;

        while((c = inputStream.read()) != -1) {
            char ch = (char) c;
            sb.append(ch);
            if(ch == lastChar && sb.toString().endsWith(pattern))
                return;
        }
    }

    class ClientThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    processInput();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void processInput() throws IOException {
            String line = bufferedReader.readLine();
            try {
                Record record = Record.factory(line);
                for (NewRecordListener listener : listeners)
                    listener.receive(record);
                Log.i("RBN", "PIr: " + record);
            } catch (ParseException e) {
                for (NewRecordListener listener : listeners)
                    listener.unparsable(line, e);
                Log.i("RBN", "PIp: " + e.getMessage());
                Log.i("RBN", line);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface NewRecordListener {
        void receive(Record record);
        void unparsable(String line, ParseException e);
    }
}
