package ch.epfl.isochrone.geo;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

/**
 * Test de la classe PointOSM
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public class TestPointOSM {
    private static final double DELTA = 0.000001;

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void textMaxXYInvalidZoom() {
        PointOSM.maxXY(-1);
    }

    @Test
    public void testMaxXY() {
        for (int i = 0; i <= 20; ++i)
            assertEquals(1 << (8 + i), PointOSM.maxXY(i));
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testConstructorInvalidZoom() {
        new PointOSM(-1, 0, 0);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testConstructorTooBigX() {
        new PointOSM(0, 256.1, 0);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testConstructorNegativeX() {
        new PointOSM(0, -0.1, 0);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testConstructorTooBigY() {
        new PointOSM(0, 0, 256.1);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testConstructorNegativeY() {
        new PointOSM(0, 0, -0.1);
    }

    @Test
    public void testX() {
        Random rng = new Random();
        for (int i = 0; i < 100; ++i) {
            double x = rng.nextDouble() * 256.0;
            assertEquals(x, new PointOSM(0, x, 0).x(), DELTA);
        }
    }

    @Test
    public void testY() {
        Random rng = new Random();
        for (int i = 0; i < 100; ++i) {
            double y = rng.nextDouble() * 256.0;
            assertEquals(y, new PointOSM(0, 0, y).y(), DELTA);
        }
    }

    @Test
    public void testRoundedX() {
        Random rng = new Random();
        for (int i = 0; i < 100; ++i) {
            double xy = rng.nextDouble() * 256.0;
            assertEquals((int)Math.round(xy), new PointOSM(0, xy, xy).roundedX());
        }
    }

    @Test
    public void testRoundedY() {
        Random rng = new Random();
        for (int i = 0; i < 100; ++i) {
            double xy = rng.nextDouble() * 256.0;
            assertEquals((int)Math.round(xy), new PointOSM(0, xy, xy).roundedY());
        }
    }

    @Test
    public void testZoom() {
        for (int zoom = 0; zoom < 20; ++zoom) {
            assertEquals(zoom, new PointOSM(zoom, 0, 0).zoom());
        }
    }

    @Test
    public void testAtZoom() {
        Random rng = new Random();
        for (int i = 0; i < 100; ++i) {
            int z = rng.nextInt(19);
            int maxXY = PointOSM.maxXY(z);
            double x = rng.nextDouble() * maxXY, y = rng.nextDouble() * maxXY;
            PointOSM p1 = new PointOSM(z, x, y);
            PointOSM p2 = p1.atZoom(z + 1);
            assertEquals(p1.x(), p2.x() / 2.0, DELTA);
            assertEquals(p1.y(), p2.y() / 2.0, DELTA);
        }
    }

    @Test
    public void testToWGS84() {
        // Test on 3 known points (center and two opposite corners)
        PointWGS84 p1 = new PointOSM(0, 128, 128).toWGS84();
        assertEquals(0, p1.longitude(), DELTA);
        assertEquals(0, p1.latitude(), DELTA);

        PointWGS84 p2 = new PointOSM(0, 0, 0).toWGS84();
        assertEquals(Math.toRadians(-180), p2.longitude(), DELTA);
        assertEquals(Math.toRadians(85.051129), p2.latitude(), DELTA);

        PointWGS84 p3 = new PointOSM(0, 255.9999999999, 255.9999999999).toWGS84();
        assertEquals(Math.toRadians(180), p3.longitude(), DELTA);
        assertEquals(Math.toRadians(-85.051129), p3.latitude(), DELTA);

        // Test that toOSM and toWGS84 are inverse functions on 100 random points
        Random rng = new Random();
        for (int i = 0; i < 100; ++i) {
            int z = rng.nextInt(19);
            double x = rng.nextDouble();
            double y = rng.nextDouble();
            PointOSM p = new PointOSM(z, x, y).toWGS84().toOSM(z);
            assertEquals(x, p.x(), DELTA);
            assertEquals(y, p.y(), DELTA);
        }
    }

    // Do not test toString, given the many possible representations of
    // floating-point values.
}
