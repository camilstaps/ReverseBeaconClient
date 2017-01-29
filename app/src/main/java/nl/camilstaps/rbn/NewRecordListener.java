package nl.camilstaps.rbn;

import java.text.ParseException;

public interface NewRecordListener {
	boolean receivesAll();
	void receive(Entry entry, boolean matchesFilter);
	void unparsable(String line, ParseException e);
	void onDisconnected();
	void onReconnected();
}
