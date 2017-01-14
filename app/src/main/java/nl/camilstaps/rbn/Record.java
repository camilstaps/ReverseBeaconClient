package nl.camilstaps.rbn;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

/**
 * Created by camil on 1/14/17.
 */

public final class Record {
    private final Callsign dx, de;
    private final float frequency;
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
        Scanner sc = new Scanner(logline);

        sc.findInLine("DX de ");
        sc.useDelimiter(":");
        String dx = sc.next();
        sc.useDelimiter("\\s+");
        sc.next(":");

        float frequency = sc.nextFloat();

        String de = sc.next();

        Mode mode = Mode.valueOf(sc.next());

        int strength = sc.nextInt();
        sc.next("dB");

        int speedValue = sc.nextInt();
        Speed.SpeedUnit speedUnit = Speed.SpeedUnit.valueOf(sc.next());
        Speed speed = new Speed(speedValue, speedUnit);

        Type type = Type.valueOf(sc.next());

        sc.useDelimiter("(\\s+|Z)");
        DateFormat df = new SimpleDateFormat("HHmm");
        Date parsed_date = df.parse(sc.next());
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Zulu"));
        cal.set(Calendar.HOUR_OF_DAY, parsed_date.getHours());
        cal.set(Calendar.MINUTE, parsed_date.getMinutes());
        Date date = cal.getTime();

        return new Record(dx, de, frequency, mode, strength, speed, type, date);
    }

    public String toString() {
        DateFormat df = new SimpleDateFormat("HH:mm");
        df.setTimeZone(TimeZone.getTimeZone("Zulu"));
        return df.format(date) + "Z\t" + "\tDX de " + dx + "\t" +
                new Band(frequency) + "\t" + frequency + "\t" + de +
                "\t" + mode + "\t" + strength + " dB\t" + speed + "\t" + type;
    }

    public enum Mode {
        CW, PSK31, RTTY
    }

    public enum Type {
        CQ, BEACON
    }
}