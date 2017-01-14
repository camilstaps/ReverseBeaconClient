package nl.camilstaps.rbn;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        Record r = Record.factory(
                "DX de TF3Y-#:     3507.0  OZ1HDF         CW     5 dB  21 WPM  CQ      2307Z");
        System.out.println(r);
    }
}