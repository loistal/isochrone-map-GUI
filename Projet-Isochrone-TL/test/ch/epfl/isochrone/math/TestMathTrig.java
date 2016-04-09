package ch.epfl.isochrone.math;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

/**
 * Test de la classe Math
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public class TestMathTrig {
    private final static double DELTA = 0.0000001;

    @Test
    public void testASinH() {
        // Test that asinh and sinh are inverse on 100 random values
        // between -10 and +10.
        Random rng = new Random();
        for (int i = 0; i < 100; ++i) {
            double x = (rng.nextDouble() - 0.5) * 20.0;
            assertEquals(x, Math.asinh(java.lang.Math.sinh(x)), DELTA);
            assertEquals(x, java.lang.Math.sinh(Math.asinh(x)), DELTA);
        }
    }

    @Test
    public void testHaversin() {
        // Test result on 3 known values
        assertEquals(0, Math.haversin(0), DELTA);
        assertEquals(0.708073418274, Math.haversin(2), DELTA);
        assertEquals(1, Math.haversin(java.lang.Math.PI), DELTA);

        // Test property that
        //   haversin(-x) = haversin(x)
        // on 100 random values between -10 and +10
        Random rng = new Random();
        for (int i = 0; i < 100; ++i) {
            double x = (rng.nextDouble() - 0.5) * 20.0;
            assertEquals(Math.haversin(x), Math.haversin(-x), DELTA);
        }

        // Test periodicity, i.e. the property that
        //   haversin(x) = haversin(x + k*2π)
        // on 100 random x,k pairs (x between -10 and +10, k integer between -10 and +10)
        for (int i = 0; i < 100; ++i) {
            double x = (rng.nextDouble() - 0.5) * 20.0;
            int k = rng.nextInt(21) - 10;
            assertEquals(Math.haversin(x), Math.haversin(x + k * 2.0 * java.lang.Math.PI), DELTA);
        }
    }
}
