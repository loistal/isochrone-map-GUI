package ch.epfl.isochrone.timetable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import ch.epfl.isochrone.timetable.Date.DayOfWeek;
import ch.epfl.isochrone.timetable.Date.Month;

/**
 * Test de la classe Service
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public class TestService {
    // Le "test" suivant n'en est pas un à proprement parler, raison pour
    // laquelle il est ignoré (annotation @Ignore). Son seul but est de garantir
    // que les noms des classes et méthodes sont corrects.
    @Test
    @Ignore
    public void namesAreOk() {
        Date d = new Date(1, Month.JANUARY, 2000);
        Service s = new Service("s",
                d, d,
                Collections.<Date.DayOfWeek> emptySet(),
                Collections.<Date> emptySet(),
                Collections.<Date> emptySet());
        s.name();
        s.isOperatingOn(d);

        Service.Builder sb = new Service.Builder("s", d, d);
        sb.name();
        sb.addOperatingDay(DayOfWeek.MONDAY);
        sb.addExcludedDate(d);
        sb.addIncludedDate(d);
        sb.build();
    }
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testConstructorEndDateTooLow(){
        Date startingDate = new Date(20, Month.APRIL, 1990);
        Date endingDate = new Date(19, Month.APRIL, 1990);
        
        Set<DayOfWeek> operatingDays = new HashSet<DayOfWeek>();
        Set<Date> excludedDates = new HashSet<Date>();
        Set<Date> includedDates = new HashSet<Date>();
        
        new Service("Misc", startingDate, endingDate, operatingDays, excludedDates, includedDates);
    }
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testConstructorExcludedDateTooLow(){
        Date startingDate = new Date(20, Month.APRIL, 1990);
        Date endingDate = new Date(25, Month.APRIL, 1990);
        
        Set<DayOfWeek> operatingDays = new HashSet<DayOfWeek>();
        
        Set<Date> excludedDates = new HashSet<Date>();
        excludedDates.add(new Date(19, Month.APRIL, 1990));
        
        Set<Date> includedDates = new HashSet<Date>();
        
        new Service("Misc", startingDate, endingDate, operatingDays, excludedDates, includedDates);
    }
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testConstructorExcludedDateTooBig(){
        Date startingDate = new Date(20, Month.APRIL, 1990);
        Date endingDate = new Date(25, Month.APRIL, 1990);
        
        Set<DayOfWeek> operatingDays = new HashSet<DayOfWeek>();
        
        Set<Date> excludedDates = new HashSet<Date>();
        excludedDates.add(new Date(26, Month.APRIL, 1990));
        
        Set<Date> includedDates = new HashSet<Date>();
        
        new Service("Misc", startingDate, endingDate, operatingDays, excludedDates, includedDates);
    }
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testConstructorIncludedDateTooLow(){
        Date startingDate = new Date(20, Month.APRIL, 1990);
        Date endingDate = new Date(25, Month.APRIL, 1990);
        
        Set<DayOfWeek> operatingDays = new HashSet<DayOfWeek>();
        Set<Date> excludedDates = new HashSet<Date>();
        
        Set<Date> includedDates = new HashSet<Date>();
        includedDates.add(new Date(19, Month.APRIL, 1990));
        
        new Service("Misc", startingDate, endingDate, operatingDays, excludedDates, includedDates);
    }
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testConstructorIncludedDateTooBig(){
        Date startingDate = new Date(20, Month.APRIL, 1990);
        Date endingDate = new Date(25, Month.APRIL, 1990);
        
        Set<DayOfWeek> operatingDays = new HashSet<DayOfWeek>();
        Set<Date> excludedDates = new HashSet<Date>();
        
        Set<Date> includedDates = new HashSet<Date>();
        includedDates.add(new Date(26, Month.APRIL, 1990));
        
        new Service("Misc", startingDate, endingDate, operatingDays, excludedDates, includedDates);
    }
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testConstructorIntersectionIncludedExcludedDates(){
        Date startingDate = new Date(20, Month.APRIL, 1990);
        Date endingDate = new Date(25, Month.APRIL, 1990);
        
        Set<DayOfWeek> operatingDays = new HashSet<DayOfWeek>();
        
        Set<Date> excludedDates = new HashSet<Date>();
        excludedDates.add(new Date(22, Month.APRIL, 1990));
        
        Set<Date> includedDates = new HashSet<Date>();
        includedDates.add(new Date(22, Month.APRIL, 1990));
        
        new Service("Misc", startingDate, endingDate, operatingDays, excludedDates, includedDates);
    }

    @Test
    public void testConstructorImmutableParameters(){
        // 1er Janvier 1990 = Lundi, 7 Janvier 1990 = Dimanche
        Date startingDate = new Date(1, Month.JANUARY, 1990);
        Date endingDate = new Date(7, Month.JANUARY, 1990);
        
        // Jours de circulation : du lundi au vendredi
        Set<DayOfWeek> operatingDays = new HashSet<DayOfWeek>();
        operatingDays.add(Date.DayOfWeek.MONDAY);
        operatingDays.add(Date.DayOfWeek.TUESDAY);
        operatingDays.add(Date.DayOfWeek.WEDNESDAY);
        operatingDays.add(Date.DayOfWeek.THURSDAY);
        operatingDays.add(Date.DayOfWeek.FRIDAY);
        
        Set<Date> excludedDates = new HashSet<Date>();
        Set<Date> includedDates = new HashSet<Date>();
        
        Service service = new Service("Misc", startingDate, endingDate, operatingDays, excludedDates, includedDates);
        
        // On modifie les valeurs pour tester ensuite l'immutabilité
        operatingDays.add(Date.DayOfWeek.SATURDAY);
        assertTrue(!(service.isOperatingOn(new Date(6, Month.JANUARY, 1990)))); // Samedi 6 n'est pas un jour de circulation
        
        excludedDates.add(new Date(1, Month.JANUARY, 1990));
        assertTrue(service.isOperatingOn(new Date(1, Month.JANUARY, 1990))); // Lundi 1 est un jour de circulation
        
        includedDates.add(new Date(6, Month.JANUARY, 1990));
        assertTrue(!(service.isOperatingOn(new Date(6, Month.JANUARY, 1990)))); // Samedi 6 n'est pas un jour de circulation
    }
    
    @Test
    public void testIsOperatingOn(){
        // 1er Janvier 1990 = Lundi, 15 Janvier 1990 = Lundi
        Date startingDate = new Date(1, Month.JANUARY, 1990);
        Date endingDate = new Date(15, Month.JANUARY, 1990);
        
        // Jours de circulation : du lundi au vendredi
        Set<DayOfWeek> operatingDays = new HashSet<DayOfWeek>();
        operatingDays.add(Date.DayOfWeek.MONDAY);
        operatingDays.add(Date.DayOfWeek.TUESDAY);
        operatingDays.add(Date.DayOfWeek.WEDNESDAY);
        operatingDays.add(Date.DayOfWeek.THURSDAY);
        operatingDays.add(Date.DayOfWeek.FRIDAY);
        
        // 8 Janvier 1990 = Lundi
        Set<Date> excludedDates = new HashSet<Date>();
        excludedDates.add(new Date(8, Month.JANUARY, 1990));
        
        // 13 Janvier 1990 = Samedi
        Set<Date> includedDates = new HashSet<Date>();
        includedDates.add(new Date(13, Month.JANUARY, 1990));
        
        Service service = new Service("Misc", startingDate, endingDate, operatingDays, excludedDates, includedDates);

        assertTrue(service.isOperatingOn(new Date(1, Month.JANUARY, 1990)));
        assertTrue(service.isOperatingOn(new Date(2, Month.JANUARY, 1990)));
        assertTrue(service.isOperatingOn(new Date(3, Month.JANUARY, 1990)));
        assertTrue(service.isOperatingOn(new Date(4, Month.JANUARY, 1990)));
        assertTrue(service.isOperatingOn(new Date(5, Month.JANUARY, 1990)));
        assertTrue(!(service.isOperatingOn(new Date(6, Month.JANUARY, 1990)))); // 6 Janvier = Samedi
        assertTrue(!(service.isOperatingOn(new Date(7, Month.JANUARY, 1990)))); // 7 Janvier = Dimanche
        assertTrue(!(service.isOperatingOn(new Date(8, Month.JANUARY, 1990)))); // 8 Janvier = Date exclue
        assertTrue(service.isOperatingOn(new Date(9, Month.JANUARY, 1990)));
        assertTrue(service.isOperatingOn(new Date(10, Month.JANUARY, 1990)));
        assertTrue(service.isOperatingOn(new Date(11, Month.JANUARY, 1990)));
        assertTrue(service.isOperatingOn(new Date(12, Month.JANUARY, 1990)));
        assertTrue(service.isOperatingOn(new Date(13, Month.JANUARY, 1990))); // 13 Janvier = Samedi = Date incluse
        assertTrue(!(service.isOperatingOn(new Date(14, Month.JANUARY, 1990)))); // 14 Janvier = Dimanche
        assertTrue(service.isOperatingOn(new Date(15, Month.JANUARY, 1990)));
    }
    
    @Test
    public void testName(){
        Date startingDate = new Date(20, Month.APRIL, 1990);
        Date endingDate = new Date(25, Month.APRIL, 1990);
        
        Set<DayOfWeek> operatingDays = new HashSet<DayOfWeek>();
        Set<Date> excludedDates = new HashSet<Date>();
        Set<Date> includedDates = new HashSet<Date>();
        
        Service service = new Service("Misc", startingDate, endingDate, operatingDays, excludedDates, includedDates);

        assertEquals("Misc", service.name());
    }
    
    @Test
    public void testToString(){
        Date startingDate = new Date(20, Month.APRIL, 1990);
        Date endingDate = new Date(25, Month.APRIL, 1990);
        
        Set<DayOfWeek> operatingDays = new HashSet<DayOfWeek>();
        Set<Date> excludedDates = new HashSet<Date>();
        Set<Date> includedDates = new HashSet<Date>();
        
        Service service = new Service("Misc", startingDate, endingDate, operatingDays, excludedDates, includedDates);

        assertEquals("Misc", service.toString());
    }
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testBuilderConstructor(){
        new Service.Builder("Misc", new Date(20, Month.APRIL, 1990), new Date(19, Month.APRIL, 1990));
    }
    
    @Test
    public void testBuilderName(){
        Service.Builder sb = new Service.Builder("Misc", new Date(20, Month.APRIL, 1990), new Date(25, Month.APRIL, 1990));
        assertEquals("Misc", sb.name());
    }

    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testBuilderAddExcludedDateTooLow(){
        Service.Builder sb = new Service.Builder("Misc", new Date(20, Month.APRIL, 1990), new Date(25, Month.APRIL, 1990));
        sb.addExcludedDate(new Date(19, Month.APRIL, 1990));
    }
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testBuilderAddExcludedDateTooBig(){
        Service.Builder sb = new Service.Builder("Misc", new Date(20, Month.APRIL, 1990), new Date(25, Month.APRIL, 1990));
        sb.addExcludedDate(new Date(26, Month.APRIL, 1990));
    }
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testBuilderAddExcludedDateIntersectWithIncludedDates(){
        Service.Builder sb = new Service.Builder("Misc", new Date(20, Month.APRIL, 1990), new Date(25, Month.APRIL, 1990));
        sb.addIncludedDate(new Date(22, Month.APRIL, 1990));
        sb.addExcludedDate(new Date(22, Month.APRIL, 1990));
    }
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testBuilderAddIncludedDateTooLow(){
        Service.Builder sb = new Service.Builder("Misc", new Date(20, Month.APRIL, 1990), new Date(25, Month.APRIL, 1990));
        sb.addIncludedDate(new Date(19, Month.APRIL, 1990));
    }
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testBuilderAddIncludedDateTooBig(){
        Service.Builder sb = new Service.Builder("Misc", new Date(20, Month.APRIL, 1990), new Date(25, Month.APRIL, 1990));
        sb.addIncludedDate(new Date(26, Month.APRIL, 1990));
    }
    
    @Test (expected = java.lang.IllegalArgumentException.class)
    public void testBuilderAddIncludedDateIntersectWithExcludedDates(){
        Service.Builder sb = new Service.Builder("Misc", new Date(20, Month.APRIL, 1990), new Date(25, Month.APRIL, 1990));
        sb.addExcludedDate(new Date(22, Month.APRIL, 1990));
        sb.addIncludedDate(new Date(22, Month.APRIL, 1990));
    }
}
