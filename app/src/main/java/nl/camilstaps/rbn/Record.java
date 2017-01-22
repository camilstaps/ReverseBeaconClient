package nl.camilstaps.rbn;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.TimeZone;

public final class Record implements Serializable {
	private final Callsign dx;
	private final Callsign de;
	private final float frequency;
	private final Band band;
	private final Mode mode;
	private final int strength;
	private final Speed speed;
	private final Type type;
	private final Date date;

	public Record(Callsign dx, Callsign de, float frequency, Mode mode, int strength, Speed speed,
				  Type type, Date date) {
		this.dx = dx;
		this.de = de;
		this.frequency = frequency;
		this.band = Band.fromFrequency(frequency);
		this.mode = mode;
		this.strength = strength;
		this.speed = speed;
		this.type = type;
		this.date = date;
	}

	public Record(Callsign dx, Callsign de, float frequency, Mode mode, int strength, Speed speed,
				  Type type) {
		this(dx, de, frequency, mode, strength, speed, type, new Date());
	}

	public Record(String dx, String de, float frequency, Mode mode, int strength, Speed speed,
				  Type type, Date date) {
		this(new Callsign(dx), new Callsign(de), frequency, mode, strength, speed, type, date);
	}

	public Record(String dx, String de, float frequency, Mode mode, int strength, Speed speed,
				  Type type) {
		this(new Callsign(dx), new Callsign(de), frequency, mode, strength, speed, type);
	}

	public static Record factory(String logline) throws ParseException {
		try {
			Scanner sc = new Scanner(logline);

			sc.findInLine("DX de ");
			sc.useDelimiter("-#:");
			String dx = sc.next();
			sc.useDelimiter("\\s+");
			sc.next("-#:");

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

			return new Record(dx, de, frequency, mode, strength, speed, type, date);
		} catch (IllegalStateException | NoSuchElementException e) {
			throw new ParseException("Internal scanner error", 0);
		} catch (IllegalArgumentException e) {
			throw new ParseException("Unknown value", 0);
		}
	}

	public String toString() {
		DateFormat df = new SimpleDateFormat("HH:mm");
		df.setTimeZone(TimeZone.getTimeZone("Zulu"));
		return df.format(date) + "Z\t" + "\tDX de " + dx + "\t" +
				band + "\t" + frequency + "\t" + de + "\t" +
				mode + "\t" + strength + " dB\t" + speed + "\t" + type;
	}

	@NonNull
	public Callsign getDx() {
		return dx;
	}

	@NonNull
	public Callsign getDe() {
		return de;
	}

	public float getFrequency() {
		return frequency;
	}

	public Band getBand() {
		return band;
	}

	public Mode getMode() {
		return mode;
	}

	public int getStrength() {
		return strength;
	}

	public Speed getSpeed() {
		return speed;
	}

	public Type getType() {
		return type;
	}

	public Date getDate() {
		return date;
	}

	public enum Mode implements Serializable {
		CW, PSK125, PSK31, PSK63, RTTY
	}

	public enum Type implements Serializable {
		BEACON, CQ, DX, NCDXF, NCDXFB
	}
}