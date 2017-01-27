package nl.camilstaps.rbn;

import java.text.ParseException;

public interface NewRecordListener {
	void receive(Entry entry);
	void unparsable(String line, ParseException e);
	void onDisconnected();
	void onReconnected();
}
