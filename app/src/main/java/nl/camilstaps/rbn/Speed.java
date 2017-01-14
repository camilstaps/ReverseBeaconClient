package nl.camilstaps.rbn;

/**
 * Created by camil on 1/15/17.
 */

public class Speed {
    public int value;
    public SpeedUnit unit;

    public Speed(int value, SpeedUnit unit) {
        this.value = value;
        this.unit = unit;
    }

    public String toString() {
        return this.value + " " + this.unit;
    }

    public enum SpeedUnit {
        BPS, WPM
    }
}
