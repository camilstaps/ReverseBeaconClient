package nl.camilstaps.rbn;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.TimeZone;

public final class Entry implements Serializable {
	private final Callsign de;
	private final Band band;
	private final Mode mode;
	private final Type type;
	private final Speed.SpeedUnit speedUnit;
	private transient final List<Record> records = new ArrayList<>();

	public final static float MAX_MERGE_DIFFERENCE_FREQUENCY = 0.5f;
	public final static int MAX_MERGE_DIFFERENCE_SPEED = 2;
	public final static int MAX_MERGE_DIFFERENCE_SECONDS = 30;

	public Entry(Callsign dx, Callsign de, float frequency, Mode mode, int strength, Speed speed,
				 Type type, Date date) {
		this.de = de;
		this.band = Band.fromFrequency(frequency);
		this.mode = mode;
		this.type = type;
		this.speedUnit = speed.unit;

		records.add(new Record(dx, frequency, strength, speed.value, date));
	}

	public Entry(Callsign dx, Callsign de, float frequency, Mode mode, int strength, Speed speed,
				 Type type) {
		this(dx, de, frequency, mode, strength, speed, type, new Date());
	}

	public Entry(String dx, String de, float frequency, Mode mode, int strength, Speed speed,
				 Type type, Date date) {
		this(new Callsign(dx), new Callsign(de), frequency, mode, strength, speed, type, date);
	}

	public Entry(String dx, String de, float frequency, Mode mode, int strength, Speed speed,
				 Type type) {
		this(new Callsign(dx), new Callsign(de), frequency, mode, strength, speed, type);
	}

	public static Entry factory(String logline) throws ParseException {
		try {
			Scanner sc = new Scanner(logline);

			sc.findInLine("DX de ");
			sc.useDelimiter("-#:");
			String dx = sc.next();
			sc.useDelimiter("\\s*(?=\\d)");
			sc.next("-#:");
			sc.useDelimiter("\\s+");

			float frequency = sc.nextFloat();

			String de = sc.next();

			Mode mode = Mode.valueOf(sc.next());

			int strength = sc.nextInt();
			sc.next("dB");

			int speedValue = sc.nextInt();
			Speed.SpeedUnit speedUnit = Speed.SpeedUnit.valueOf(sc.next());
			Speed speed = new Speed(speedValue, speedUnit);

			StringBuilder typeSb = new StringBuilder();
			do {
				typeSb.append(sc.next());
			} while (!sc.hasNext("\\d{4}Z"));
			Type type = Type.valueOf(typeSb.toString());

			DateFormat df = new SimpleDateFormat("HHmm");
			Date parsed_date = df.parse(sc.next());
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Zulu"));
			cal.set(Calendar.HOUR_OF_DAY, parsed_date.getHours());
			cal.set(Calendar.MINUTE, parsed_date.getMinutes());
			Date date = cal.getTime();

			return new Entry(dx, de, frequency, mode, strength, speed, type, date);
		} catch (IllegalStateException | NoSuchElementException e) {
			throw new ParseException("Internal scanner error", 0);
		} catch (IllegalArgumentException e) {
			throw new ParseException("Unknown value", 0);
		}
	}

	public boolean attemptMerge(Entry entry) {
		if (!de.equals(entry.de)
				|| !mode.equals(entry.mode)
				|| !type.equals(entry.type)
				|| Math.abs(getLastDate().getTime() - entry.getLastDate().getTime())
						> MAX_MERGE_DIFFERENCE_SECONDS * 1000
				|| Math.abs(getAvgFrequency() - entry.getAvgFrequency())
						> MAX_MERGE_DIFFERENCE_FREQUENCY
				|| Math.abs(getAvgSpeed().value - entry.getAvgSpeed().value)
						> MAX_MERGE_DIFFERENCE_SPEED)
			return false;

		for (Record record : entry.records)
			records.add(record);

		return true;
	}

	@NonNull
	public Callsign getDe() {
		return de;
	}

	public Band getBand() {
		return band;
	}

	public Mode getMode() {
		return mode;
	}

	public Type getType() {
		return type;
	}

	public List<Record> getRecords() {
		return records;
	}

	public float getAvgFrequency() {
		float total = 0;
		for (Record record : records)
			total += record.frequency;
		return total / records.size();
	}

	public Speed getAvgSpeed() {
		float total = 0;
		for (Record record : records)
			total += record.speed;
		return new Speed((int) (total / records.size()), speedUnit);
	}

	public int getMinStrength() {
		int min = records.get(0).strength;
		for (Record record : records)
			if (record.strength < min)
				min = record.strength;
		return min;
	}

	public int getMaxStrength() {
		int max = records.get(0).strength;
		for (Record record : records)
			if (record.strength > max)
				max = record.strength;
		return max;
	}

	public Date getFirstDate() {
		Date first = records.get(0).date;
		for (Record record : records)
			if (record.date.before(first))
				first = record.date;
		return first;
	}

	public Date getLastDate() {
		Date last = records.get(0).date;
		for (Record record : records)
			if (record.date.after(last))
				last = record.date;
		return last;
	}

	public static class Record {
		public final Callsign dx;
		public final float frequency;
		public final int strength;
		public final int speed;
		public final Date date;

		public Record(Callsign dx, float frequency, int strength, int speed, Date date) {
			this.dx = dx;
			this.frequency = frequency;
			this.strength = strength;
			this.speed = speed;
			this.date = date;
		}
	}

	public enum Mode implements Serializable {
		CW, PSK125, PSK31, PSK63, RTTY
	}

	public enum Type implements Serializable {
		BEACON, CQ, DX, NCDXF, NCDXFB
	}
}