package nl.camilstaps.rbn;

public class Band {
    private float wavelength;

    public Band(float frequency) {
        if (frequency < 2500)
            wavelength = 160;
        else if (frequency < 5000)
            wavelength = 80;
        else if (frequency < 6000)
            wavelength = 60;
        else if (frequency < 8500)
            wavelength = 40;
        else if (frequency < 12000)
            wavelength = 30;
        else if (frequency < 16000)
            wavelength = 20;
        else if (frequency < 19500)
            wavelength = 17;
        else if (frequency < 22500)
            wavelength = 15;
        else if (frequency < 26500)
            wavelength = 12;
        else if (frequency < 40000)
            wavelength = 10;
        else if (frequency < 65000)
            wavelength = 6;
        else if (frequency < 120000)
            wavelength = 4;
        else if (frequency < 160000)
            wavelength = 2;
        else if (frequency < 300000)
            wavelength = 1.25f;
        else if (frequency < 600000)
            wavelength = 0.7f;
        else if (frequency < 1000000)
            wavelength = 0.33f;
        else if (frequency < 1400000)
            wavelength = 0.23f;
        else
            throw new IllegalArgumentException("Unknown frequency " + frequency);
    }

    public String toString() {
        if (wavelength < 1)
            return ((int) (wavelength * 100)) + "cm";
        return ((int) wavelength) + "m";
    }
}
