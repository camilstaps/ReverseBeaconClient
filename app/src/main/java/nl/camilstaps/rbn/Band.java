package nl.camilstaps.rbn;

public class Band {
	private float wavelength;

	public Band(float wavelength) {
		this.wavelength = wavelength;
	}

	public static Band fromFrequency(float frequency) {
		if (frequency < 2500)
			return new Band(160);
		else if (frequency < 5000)
			return new Band(80);
		else if (frequency < 6000)
			return new Band(60);
		else if (frequency < 8500)
			return new Band(40);
		else if (frequency < 12000)
			return new Band(30);
		else if (frequency < 16000)
			return new Band(20);
		else if (frequency < 19500)
			return new Band(17);
		else if (frequency < 22500)
			return new Band(15);
		else if (frequency < 26500)
			return new Band(12);
		else if (frequency < 40000)
			return new Band(10);
		else if (frequency < 65000)
			return new Band(6);
		else if (frequency < 120000)
			return new Band(4);
		else if (frequency < 160000)
			return new Band(2);
		else if (frequency < 300000)
			return new Band(1.25f);
		else if (frequency < 600000)
			return new Band(0.7f);
		else if (frequency < 1000000)
			return new Band(0.33f);
		else if (frequency < 1400000)
			return new Band(0.23f);
		else
			throw new IllegalArgumentException("Unknown frequency " + frequency);
	}

	public String toString() {
		if (wavelength < 1)
			return ((int) (wavelength * 100)) + "cm";
		return ((int) wavelength) + "m";
	}

	public float getWavelength() {
		return wavelength;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Band))
			return false;
		return wavelength == ((Band) obj).wavelength;
	}
}
