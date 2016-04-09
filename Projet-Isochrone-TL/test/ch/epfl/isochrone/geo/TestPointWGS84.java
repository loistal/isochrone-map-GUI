package ch.epfl.isochrone.geo;

import static java.lang.Math.toRadians;
import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

/**
 * Test de la classe PointWGS84
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public class TestPointWGS84 {
    private static final double DELTA = 0.000001;

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testConstructorLongitudeTooSmall() {
        new PointWGS84(-(2.0 * Math.PI + 0.0001), 0);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testConstructorLongitudeTooBig() {
        new PointWGS84(2.0 * Math.PI + 0.0001, 0);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testConstructorLatitudeTooSmall() {
        new PointWGS84(0, -(Math.PI + 0.0001));
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testConstructorLatitudeTooBig() {
        new PointWGS84(0, Math.PI + 0.0001);
    }

    @Test
    public void testLongitude() {
        Random rng = new Random();
        for (int i = 0; i < 100; ++i) {
            double lon = (rng.nextDouble() - 0.5) * 2.0 * Math.PI;
            assertEquals(lon, new PointWGS84(lon, 0).longitude(), 0);
        }
    }

    @Test
    public void testLatitude() {
        Random rng = new Random();
        for (int i = 0; i < 100; ++i) {
            double lat = (rng.nextDouble() - 0.5) * Math.PI;
            assertEquals(lat, new PointWGS84(0, lat).latitude(), 0);
        }
    }

    @Test
    public void testDistanceTo() {
        PointWGS84 p1a = new PointWGS84(toRadians(6.56165), toRadians(46.51823));
        PointWGS84 p1b = new PointWGS84(toRadians(6.56813), toRadians(46.52045));
        assertEquals(555, p1a.distanceTo(p1b), 1);

        PointWGS84 p2a = new PointWGS84(toRadians(6.81648), toRadians(47.02192));
        PointWGS84 p2b = new PointWGS84(toRadians(7.866944), toRadians(45.936667));
        assertEquals(145183, p2a.distanceTo(p2b), 1);

        PointWGS84 p3a = new PointWGS84(toRadians(6.62901), toRadians(46.51658));
        PointWGS84 p3b = new PointWGS84(toRadians(51.397167), toRadians(35.6577167));
        assertEquals(3887564, p3a.distanceTo(p3b), 1);
    }

    @Test
    public void testToOSM() {
        // Test on 3 known points (center and two opposite corners)
        PointOSM p1 = new PointWGS84(0, 0).toOSM(0);
        assertEquals(128.0, p1.x(), DELTA);
        assertEquals(128.0, p1.y(), DELTA);

        PointOSM p2 = new PointWGS84(Math.toRadians(-180), Math.toRadians(85.0511287798)).toOSM(0);
        assertEquals(0, p2.x(), DELTA);
        assertEquals(0, p2.y(), DELTA);

        PointOSM p3 = new PointWGS84(Math.toRadians(180), Math.toRadians(-85.0511287798)).toOSM(0);
        assertEquals(256, p3.x(), DELTA);
        assertEquals(256, p3.y(), DELTA);
    }
}
