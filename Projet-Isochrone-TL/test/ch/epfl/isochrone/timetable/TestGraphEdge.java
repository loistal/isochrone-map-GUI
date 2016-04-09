package ch.epfl.isochrone.timetable;

import static ch.epfl.isochrone.timetable.GraphEdge.packTrip;
import static ch.epfl.isochrone.timetable.GraphEdge.unpackTripArrivalTime;
import static ch.epfl.isochrone.timetable.GraphEdge.unpackTripDepartureTime;
import static ch.epfl.isochrone.timetable.GraphEdge.unpackTripDuration;
import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import ch.epfl.isochrone.geo.PointWGS84;

/**
 * Test de la classe GraphEdge
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public class TestGraphEdge {
    // Le "test" suivant n'en est pas un à proprement parler, raison pour
    // laquelle il est ignoré (annotation @Ignore). Son seul but est de garantir
    // que les noms des classes et méthodes sont corrects.
    @Test
    @Ignore
    public void namesAreOk() {
        int i1 = GraphEdge.packTrip(0, 0);
        i1 = GraphEdge.unpackTripDepartureTime(0);
        i1 = GraphEdge.unpackTripDuration(0);
        i1 = GraphEdge.unpackTripArrivalTime(0) + i1;
        Stop s = null;
        GraphEdge e = new GraphEdge(s, 0, Collections.<Integer>emptySet());
        s = e.destination();
        i1 = e.earliestArrivalTime(0);

        GraphEdge.Builder b = new GraphEdge.Builder(s);
        b.setWalkingTime(0);
        b.addTrip(0, 0);
        e = b.build();
    }

    /* Test qui vérifie qu'une exception est bien levee lors si l'heure de départ est invalide, c-à-d hors de l'intervalle [0;107'999], 
       ou si la différence entre l'heure d'arrivée et celle de départ est invalide, c-à-d hors de l'intervalle [0;9'999]. */
    @Test (expected = IllegalArgumentException.class)
    public void testPackTripException(){
    
            packTrip(-1, 1000);       //Heure de départ en dehors de l'intervalle
            packTrip(107998, 108000); //Heure d'arrivée en dehors de l'intervalle
            packTrip(0, 10000);       //Différence en dehors de l'intervalle
    
    }
    
    //Teste que l'encodage du trajet se fait bien correctement
    @Test 
    public void testPackTrip(){
        
        int p = packTrip(100000, 100100);
        assertEquals(p, 1000000100);
        
        int p2 = packTrip(1, 2);
        assertEquals(10001, p2);
    }
    
    //Teste si l'heure de départ est bien extraite correctement
    @Test 
    public void testUnpackTripDepartureTime(){
        
        int p = packTrip(100000, 100100);
        int departure = unpackTripDepartureTime(p);
        assertEquals(departure, 100000);
        
        int p2 = packTrip(1, 2);
        int departure2 = unpackTripDepartureTime(p2);
        assertEquals(departure2, 1);
        
    }
    
  //Teste si la durée du trajet est bien extraite correctement
    @Test
    public void testUnpackTripDuration(){
        
        int p = packTrip(100000, 101000);
        int duration = unpackTripDuration(p);
        assertEquals(duration, 1000);
        
        int p2 = packTrip(1, 2);
        int duration2 = unpackTripDepartureTime(p2);
        assertEquals(duration2, 1);
        
    }
    
  //Teste si l'heure d'arrivée est bien extraite correctement
    @Test
    public void testUnpackTripArrivalTime(){
        
        int p = packTrip(100000, 101000);
        int arrival = unpackTripArrivalTime(p);
        assertEquals(arrival, 101000);
        
        int p2 = packTrip(1, 2);
        int arrival2 = unpackTripArrivalTime(p2);
        assertEquals(arrival2, 2);
        
    }
    
    //Teste si une exception est bien levée lorsque le temps de marche est inférieur à -1
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorJourneyTooLong(){
        
        Stop stop =  new Stop("s", new PointWGS84(0, 0));
        
        Set<Integer> packedTrips = new HashSet<Integer>();
        
        new GraphEdge(stop, -2, packedTrips);
        
    }
    
    /* Trajet possible!
       Scénario où le temps de marche est infini */
    @Test
    public void testEarliestArrivalTimeWalkingTimeInfinite(){
        
        Stop s = new Stop("s", new PointWGS84(0, 0));
        
        GraphEdge.Builder gb = new GraphEdge.Builder(s);
        gb.addTrip(100000, 101000);
        gb.addTrip(100000, 100001);
        gb.addTrip(100000, 100003);
        gb.setWalkingTime(-1);
        GraphEdge g = gb.build();
        
        // earliestArrivalTime(100000) aura seulement l'heure d'arrrivé en trajet à calculer/retourner: doit retourner 100001
        assertEquals(100001, g.earliestArrivalTime(100000));
    }
    
    /* Trajet possible!
       Scénario où heure d'arrivé en trajet est plus rapide que l'heure d'arrivé en marchant */
    @Test
    public void testEarliestArrivalTimeWalkingTimeLongerThanTrip(){
        
        Stop s = new Stop("s", new PointWGS84(0, 0));      
        
        GraphEdge.Builder gb = new GraphEdge.Builder(s);
        gb.addTrip(100000, 101000);
        gb.addTrip(100000, 100001);
        gb.addTrip(100000, 100003);
        gb.setWalkingTime(1000);
        GraphEdge g = gb.build();
        
        /* earliestArrivalTime(100000) aurra comme plus tôt temps d'arrivé en: - marchant: 101000
                                                                               - trajet: 100001
                                                                               ==> Temps d'arrivé en trajet devrait être retourné */
        assertEquals(100001, g.earliestArrivalTime(100000));
    }
    
    /* Trajet possible!
       Scénario où heure d'arrivé en marchant est plus rapide que le meilleur heure d'arrivé en trajet */
    @Test
    public void testEarliestArrivalTimeTripLongerThanWalk(){
        
        Stop s = new Stop("s", new PointWGS84(0, 0));   
        
        GraphEdge.Builder gb = new GraphEdge.Builder(s);
        gb.addTrip(100000, 107000);
        gb.addTrip(100000, 108000);
        gb.addTrip(100000, 109000);
        gb.setWalkingTime(1000);
        GraphEdge g = gb.build();
        
        /* earliestArrivalTime(100000) aurra comme plus tôt temps d'arrivé en: - marchant: 101000
                                                                               - trajet: 107000
                                                                               ==> Temps d'arrivé en marchant devrait être retourné */
        assertEquals(101000, g.earliestArrivalTime(100000));
    }
    
    //teste si le constructeur lève bien des exceptions avec un temps de marche inférieur à -1
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorException(){
        
        Stop s = new Stop("s", new PointWGS84(0, 0));
        Set<Integer> packedTrips = new HashSet<Integer>();
        
        new GraphEdge(s, -2 , packedTrips);
        
    }
    
    //teste si la methode setWalkingTime leve bien une exception quand on lui passe un temps de marche strictement inférieur à -1.
    @Test(expected = IllegalArgumentException.class)
    public void testBuilderSetWalkingTimeException(){
        
        Stop s = new Stop("s", new PointWGS84(0, 0));
        GraphEdge.Builder gb = new GraphEdge.Builder(s);
        
        gb.setWalkingTime(-2);
        
    }

}
