package ch.epfl.isochrone.timetable;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

/**
 * Test de la classe SecondsPastMidnight
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public class TestSecondsPastMidnight {
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testFromHMSHourTooSmall() {
        SecondsPastMidnight.fromHMS(-1, 0, 0);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testFromHMSHourTooBig() {
        SecondsPastMidnight.fromHMS(35, 0, 0);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testFromHMSMinutesTooSmall() {
        SecondsPastMidnight.fromHMS(0, -1, 0);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testFromHMSMinutesTooBig() {
        SecondsPastMidnight.fromHMS(0, 60, 0);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testFromHMSSecondsTooSmall() {
        SecondsPastMidnight.fromHMS(0, 0, -1);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testFromHMSSecondsTooBig() {
        SecondsPastMidnight.fromHMS(0, 0, 60);
    }

    @Test
    public void testFromHMS() {
        Random rng = new Random();
        for (int i = 0; i < 100; ++i) {
            int h = rng.nextInt(30);
            int m = rng.nextInt(60);
            int s = rng.nextInt(60);
            assertEquals(h * 3600 + m * 60 + s, SecondsPastMidnight.fromHMS(h, m, s));
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testFromJavaDate() {
        Random rng = new Random();
        for (int i = 0; i < 100; ++i) {
            int h = rng.nextInt(24);
            int m = rng.nextInt(60);
            int s = rng.nextInt(60);
            java.util.Date d = new java.util.Date(0, 0, 1, h, m, s);
            assertEquals(SecondsPastMidnight.fromHMS(h, m, s), SecondsPastMidnight.fromJavaDate(d));
        }
    }

    @Test
    public void testHours() {
        Random rng = new Random();
        for (int i = 0; i < 100; ++i) {
            int h = rng.nextInt(30);
            int m = rng.nextInt(60);
            int s = rng.nextInt(60);
            assertEquals(h, SecondsPastMidnight.hours(SecondsPastMidnight.fromHMS(h, m, s)));
        }
    }


    @Test
    public void testMinutes() {
        Random rng = new Random();
        for (int i = 0; i < 100; ++i) {
            int h = rng.nextInt(30);
            int m = rng.nextInt(60);
            int s = rng.nextInt(60);
            assertEquals(m, SecondsPastMidnight.minutes(SecondsPastMidnight.fromHMS(h, m, s)));
        }
    }

    @Test
    public void testSeconds() {
        Random rng = new Random();
        for (int i = 0; i < 100; ++i) {
            int h = rng.nextInt(30);
            int m = rng.nextInt(60);
            int s = rng.nextInt(60);
            assertEquals(s, SecondsPastMidnight.seconds(SecondsPastMidnight.fromHMS(h, m, s)));
        }
    }

    @Test
    public void testToString() {
        assertEquals("09:15:33", SecondsPastMidnight.toString(SecondsPastMidnight.fromHMS(9, 15, 33)));
    }
}
