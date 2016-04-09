package ch.epfl.isochrone.timetable;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Test de la classe TimeTableReader
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public class TestTimeTableReader {
    TimeTableReader reader = new TimeTableReader("/time-table/");
    
    // Le "test" suivant n'en est pas un à proprement parler, raison pour
    // laquelle il est ignoré (annotation @Ignore). Son seul but est de garantir
    // que les noms des classes et méthodes sont corrects.
    @Test
    @Ignore
    public void namesAreOk() throws IOException {
        TimeTableReader r = new TimeTableReader("");
        TimeTable t = r.readTimeTable();
        Graph g = r.readGraphForServices(t.stops(), Collections.<Service>emptySet(), 0, 0d);
        System.out.println(g); // Evite l'avertissement que g n'est pas utilisé
    }
    
    @Test
    public void testTimeTable(){
        TimeTable timeTable = reader.readTimeTable();
        
        Set<Stop> listStops = timeTable.stops();
        
        boolean foundBruyere,foundBurenoz, foundCaudoz, foundChaffeises, foundMalavaux;
        
        foundBruyere = foundBurenoz = foundCaudoz = foundChaffeises = foundMalavaux = false;
        
        for(Stop stop:listStops){
            switch(stop.name()){
                case "Bruyère":
                    foundBruyere  = true;
                case "Burenoz":
                    foundBurenoz  = true;
                case "Caudoz":
                    foundCaudoz   = true;
                case "Chaffeises":
                    foundChaffeises = true;
                case "Malavaux":
                    foundMalavaux = true;
            }
        }

     // Testons arbitrairement une liste de stops devant être inclu...
        assertTrue(foundBruyere);
        assertTrue(foundBurenoz);
        assertTrue(foundChaffeises);
        assertTrue(foundCaudoz);
        assertTrue(foundMalavaux);
    }
}
