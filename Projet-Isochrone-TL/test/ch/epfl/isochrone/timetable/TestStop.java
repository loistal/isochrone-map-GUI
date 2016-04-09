package ch.epfl.isochrone.timetable;

import org.junit.Ignore;
import org.junit.Test;

import ch.epfl.isochrone.geo.PointWGS84;

import static org.junit.Assert.assertEquals;

/**
 * Test de la classe Stop
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public class TestStop {
    // Le "test" suivant n'en est pas un à proprement parler, raison pour
    // laquelle il est ignoré (annotation @Ignore). Son seul but est de garantir
    // que les noms des classes et méthodes sont corrects.
    @Test
    @Ignore
    public void namesAreOk() {
        Stop s = new Stop("invalid", new PointWGS84(6.57, 46.52));
        s.name();
        s.position();
    }

    @Test
    public void testName(){
        Stop stop = new Stop("Misc", new PointWGS84(0, 0));
        
        assertEquals("Misc", stop.name());
    }
    
    @Test
    public void testPosition(){
        PointWGS84 position = new PointWGS84(0, 0);
        Stop stop = new Stop("Misc", position);

        assertEquals(position, stop.position());
    }
    
    @Test
    public void testToString(){
        Stop stop = new Stop("Misc", new PointWGS84(0, 0));
        
        assertEquals("Misc", stop.toString());
    }
}
