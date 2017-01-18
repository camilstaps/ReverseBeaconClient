package nl.camilstaps.rbn;

import java.io.Serializable;

public class Speed implements Serializable {
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
