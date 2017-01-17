package nl.camilstaps.rbn;

public class Speed {
    public final int value;
    public final SpeedUnit unit;

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
