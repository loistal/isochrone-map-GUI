package ch.epfl.isochrone.timetable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import ch.epfl.isochrone.timetable.Date.DayOfWeek;
import ch.epfl.isochrone.timetable.Date.Month;

/**
 * Test de la classe Date
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public class TestDate {
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testConstructorDayTooSmall() {
        new Date(0, Month.JANUARY, 1);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testConstructorDayTooBig() {
        new Date(29, Month.FEBRUARY, 2100);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testConstructorIntMonthTooSmall() {
        new Date(1, 0, 1);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testConstructorIntMonthTooBig() {
        new Date(1, 13, 1);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testConstructorFromJavaDate() {
        assertEquals(new Date(17, Month.FEBRUARY, 2014), new Date(new java.util.Date(114, 1, 17)));
    }

    @Test
    public void testDay() {
        for (int d = 1; d <= 31; ++d)
            assertEquals(d, (new Date(d, Month.JANUARY, 2014)).day());
    }

    @Test
    public void testMonth() {
        for (Month m: Month.values())
            assertEquals(m, (new Date(1, m, 2014)).month());
    }

    @Test
    public void testIntMonth() {
        for (int m = 1; m <= 12; ++m)
            assertEquals(m, (new Date(15, m, 2000)).intMonth());
    }

    @Test
    public void testYear() {
        Random rng = new Random();
        for (int i = 0; i < 100; ++i) {
            int y = rng.nextInt();
            assertEquals(y, (new Date(12, Month.MARCH, y)).year());
        }
    }

    @Test
	public void testDayOfWeek() {
	    assertEquals(DayOfWeek.SUNDAY, new Date(24, Month.JULY, -586).dayOfWeek());
	    assertEquals(DayOfWeek.MONDAY, new Date( 1, Month.JANUARY, 1).dayOfWeek());
		assertEquals(DayOfWeek.SUNDAY, new Date( 2, Month.AUGUST, 1953).dayOfWeek());
		assertEquals(DayOfWeek.MONDAY, new Date( 1, Month.OCTOBER, 1973).dayOfWeek());
		assertEquals(DayOfWeek.FRIDAY, new Date(11, Month.OCTOBER, 2013).dayOfWeek());
	}

	@Test
	public void testRelative() {
        assertEquals(new Date(1, Month.MARCH, 2000), new Date(29, Month.FEBRUARY, 2000).relative(1));
        assertEquals(new Date(1, Month.MARCH, 2100), new Date(28, Month.FEBRUARY, 2100).relative(1));
        assertEquals(new Date(1, Month.OCTOBER, 1973), new Date(25, Month.NOVEMBER, 2013).relative(-14665));
        assertEquals(new Date(1, Month.OCTOBER, 1973), new Date(1, Month.OCTOBER, 1973).relative(0));
	}

	@SuppressWarnings("deprecation")
    @Test
	public void testToJavaDate() {
	    assertEquals(new java.util.Date(114, 1, 17), new Date(17, Month.FEBRUARY, 2014).toJavaDate());
	}

	// equals is extensively tested by all the assertEquals calls, we do not test it further here.

	@Test
	public void testToString() {
	    assertEquals("2014-02-17", new Date(17, Month.FEBRUARY, 2014).toString());
	}

	@Test
	public void testCompareTo() {
	    assertTrue(new Date(1, Month.JANUARY, 2014).compareTo(new Date(31, Month.DECEMBER, 2013)) > 0);
        assertTrue(new Date(1, Month.JANUARY, 2014).compareTo(new Date(31, Month.DECEMBER, 2014)) < 0);
        assertTrue(new Date(1, Month.JANUARY, 2014).compareTo(new Date(1, Month.JANUARY, 2014)) == 0);
	}
}
