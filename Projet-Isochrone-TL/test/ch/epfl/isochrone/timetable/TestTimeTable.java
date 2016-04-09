package ch.epfl.isochrone.timetable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import ch.epfl.isochrone.geo.PointWGS84;
import ch.epfl.isochrone.timetable.Date.DayOfWeek;
import ch.epfl.isochrone.timetable.Date.Month;

/**
 * Test de la classe TimeTable
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public class TestTimeTable {
    // Le "test" suivant n'en est pas un à proprement parler, raison pour
    // laquelle il est ignoré (annotation @Ignore). Son seul but est de garantir
    // que les noms des classes et méthodes sont corrects.
    @Test
    @Ignore
    public void namesAreOk() {
        TimeTable t = new TimeTable(Collections.<Stop> emptySet(),
                Collections.<Service> emptySet());
        t.stops();
        t.servicesForDate(new Date(1, Month.JANUARY, 2000));

        TimeTable.Builder b = new TimeTable.Builder();
        b.addStop(new Stop("s", new PointWGS84(0, 0)));
        Date d = new Date(1, Month.APRIL, 2000);
        b.addService(new Service("s", d, d, Collections.<DayOfWeek> emptySet(),
                Collections.<Date> emptySet(), Collections.<Date> emptySet()));
        b.build();
    }
    
    @Test
    public void testConstructorImmutableParameters(){
        
        //On initialise un TimeTable avec un set de services et un set de stop.
        //On modifie séparément ces set. Puis on regarde si ca a modifié les attributs de la TimeTable aussi
        
        //Creation et remplissage d'un set de Stop
        Set<Stop> stops = new HashSet<Stop>();
        Stop stop1 = new Stop("t1", new PointWGS84(0, 0));
        stops.add(stop1);
        
        //Creation et remplissage d'un set de Service
        Date startingDate = new Date(20, Month.APRIL, 1990);
        Date endingDate = new Date(25, Month.APRIL, 1990);
        
        Set<DayOfWeek> operatingDays = new HashSet<DayOfWeek>();   
        Set<Date> excludedDates = new HashSet<Date>();
        Set<Date> includedDates = new HashSet<Date>();
        
        Service s1 = new Service("service 1", startingDate, endingDate, operatingDays, excludedDates, includedDates);
        
        Set<Service> services = new HashSet<Service>();
        services.add(s1);
        
        //Initialisation d'un TimeTable avec comme parametre les 2 sets créés précédemment
        TimeTable t = new TimeTable(stops, services);
        
        //Test de l'immuabilité
        Service s2 = new Service("service 2", startingDate, endingDate, operatingDays, excludedDates, includedDates);
        services.add(s2);
        
        Stop stop2 = new Stop("t2", new PointWGS84(0, 0));
        stops.add(stop2);
        
        assertFalse( (t.stops()).equals(stops) );
        assertFalse( (t.servicesForDate(startingDate)).equals(services) );
        
    }
    
    //tester si stop() renvoie une version immuable
    @Test(expected = java.lang.UnsupportedOperationException.class)
    public void testImmutableStops(){
        
        //Creation du set de stops
        Set<Stop> stops = new HashSet<Stop>();
        Stop s = new Stop("t", new PointWGS84(0, 0));
        stops.add(s);
        
        //Creation du set de services
        Collection<Service> services = new HashSet<Service>();
        
        //Creation de la TimeTable
        TimeTable t = new TimeTable(stops, services);
        Set<Stop> stopImmuable = t.stops();
        
        //Test de l'immuabilité
        Stop test = new Stop("t", new PointWGS84(0, 0));
        stopImmuable.add(test);
        
    }
    
    //tester si  servicesForDate renvoie bien tous les services du jour
    @Test
    public void testServicesForDate(){
       
        //Creation du set de stops
        Stop s = new Stop("t", new PointWGS84(0, 0));
        Set<Stop> stops = new HashSet<Stop>();
        stops.add(s);
        
        //Creation du set de services
        Date startingDate = new Date(20, Month.APRIL, 1990);
        Date endingDate = new Date(25, Month.APRIL, 1990);
        
        Set<DayOfWeek> operatingDays = new HashSet<DayOfWeek>();
        operatingDays.add(DayOfWeek.MONDAY);
        operatingDays.add(DayOfWeek.TUESDAY);
        operatingDays.add(DayOfWeek.WEDNESDAY);
        operatingDays.add(DayOfWeek.THURSDAY);
        operatingDays.add(DayOfWeek.FRIDAY); 
        operatingDays.add(DayOfWeek.SATURDAY);
        operatingDays.add(DayOfWeek.SUNDAY);
        
        Set<Date> excludedDates = new HashSet<Date>();
        Set<Date> includedDates = new HashSet<Date>();
        
        Service s1 = new Service("Misc", startingDate, endingDate, operatingDays, excludedDates, includedDates);
        
        Set<Service> services = new HashSet<Service>();
        services.add(s1);
        
        //Creation de la TimeTable
        TimeTable t = new TimeTable (stops , services);
        
        assertEquals(t.servicesForDate(new Date(20, Month.APRIL, 1990)), services);
        assertEquals(t.servicesForDate(new Date(21, Month.APRIL, 1990)), services);
        assertEquals(t.servicesForDate(new Date(22, Month.APRIL, 1990)), services);
        assertEquals(t.servicesForDate(new Date(23, Month.APRIL, 1990)), services);
        assertEquals(t.servicesForDate(new Date(24, Month.APRIL, 1990)), services);
        assertEquals(t.servicesForDate(new Date(25, Month.APRIL, 1990)), services);
        assertNotEquals(t.servicesForDate(new Date(26, Month.APRIL, 1990)), services);
    }
    
    @Test
    public void testBuilderBuild(){
        
        //Creation et remplissage d'un set de services
        Date startingDate = new Date(20, Month.APRIL, 1990);
        Date endingDate = new Date(25, Month.APRIL, 1990);
        
        Set<DayOfWeek> operatingDays = new HashSet<DayOfWeek>();   
        operatingDays.add(DayOfWeek.MONDAY);
        operatingDays.add(DayOfWeek.TUESDAY);
        operatingDays.add(DayOfWeek.WEDNESDAY);
        operatingDays.add(DayOfWeek.THURSDAY);
        operatingDays.add(DayOfWeek.FRIDAY); 
        operatingDays.add(DayOfWeek.SATURDAY);
        operatingDays.add(DayOfWeek.SUNDAY);
        
        Set<Date> excludedDates = new HashSet<Date>();
        Set<Date> includedDates = new HashSet<Date>();
        
        Service s1 = new Service("service 1", startingDate, endingDate, operatingDays, excludedDates, includedDates);
        
        Set<Service> services = new HashSet<Service>();
        services.add(s1);
        
        //Creation et remplissage d'un set de Stop
        Set<Stop> stops = new HashSet<Stop>();
        Stop stop = new Stop("t1", new PointWGS84(0, 0));
        stops.add(stop);
        
        //Initialisation d'un TimeTable avec comme parametre les 2 sets créés précédemment
        TimeTable t1 = new TimeTable(stops, services);
        
        //Initialisation d'un TimeTable.Builder qui contiendra les mêmes données que t1
        TimeTable.Builder tb = new TimeTable.Builder();
        tb.addStop(stop);
        tb.addService(s1);
        
        TimeTable t2 = tb.build();
        
        //t1 et t2 (issu du builder) sont censé être identique
        assertEquals(t1.servicesForDate(startingDate), t2.servicesForDate(startingDate));
        assertEquals(t1.stops(), t2.stops());
    }
}


