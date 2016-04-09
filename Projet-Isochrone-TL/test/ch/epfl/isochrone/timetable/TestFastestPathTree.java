package ch.epfl.isochrone.timetable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import ch.epfl.isochrone.geo.PointWGS84;
import ch.epfl.isochrone.timetable.FastestPathTree.Builder;

/**
 * Test de la classe FastestPathTree
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public class TestFastestPathTree {
    // Le "test" suivant n'en est pas un à proprement parler, raison pour
    // laquelle il est ignoré (annotation @Ignore). Son seul but est de garantir
    // que les noms des classes et méthodes sont corrects.
    @Test
    @Ignore
    public void namesAreOk() {
        Stop stop = null;
        Map<Stop, Integer> arrivalTimes = null;
        Map<Stop, Stop> predecessors = null;
        FastestPathTree f = new FastestPathTree(stop, arrivalTimes, predecessors);
        Stop s = f.startingStop();
        int i = f.startingTime();
        Set<Stop> ss = f.stops();
        i = f.arrivalTime(stop);
        List<Stop> p = f.pathTo(stop);
        System.out.println("" + s + i + ss + p);

        FastestPathTree.Builder fb = new FastestPathTree.Builder(stop, 0);
        fb.setArrivalTime(stop, 0, stop);
        i = fb.arrivalTime(stop);
        f = fb.build();
    }

    //teste si le constructeur lance bien une exception
   @Test(expected = IllegalArgumentException.class)
   public void testConstructorExceptionNotSameKeys(){
       //methode: on crée 2 Map avec des contenus différents
       
       Map<Stop, Integer> arrivalTime = new HashMap<Stop, Integer>();
       Map<Stop, Stop> predecessor = new HashMap<Stop, Stop>();
       
       Stop stop1 = new Stop("stop1", new PointWGS84(0, 0));
       Stop stop2 = new Stop("stop2", new PointWGS84(0, 0));
       
       arrivalTime.put(stop1, 0);
       predecessor.put(stop2, stop1);
       
       new FastestPathTree(new Stop("stop", new PointWGS84(0, 0)), arrivalTime, predecessor);
   }
   
   @Test(expected = IllegalArgumentException.class)
   public void testConstructorExceptionNotSameStops(){
       
       Map<Stop, Integer> arrivalTime = new HashMap<Stop, Integer>();
       Map<Stop, Stop> predecessor = new HashMap<Stop, Stop>();
       
       Stop stop1 = new Stop("stop1", new PointWGS84(0, 0));
       Stop stop2 = new Stop("stop2", new PointWGS84(0, 0));
       Stop stop3 = new Stop("stop3", new PointWGS84(0, 0));
       Stop stop4 = new Stop("stop4", new PointWGS84(0, 0));
       
       arrivalTime.put(stop1, 0);
       arrivalTime.put(stop2, 0);
       arrivalTime.put(stop3, 0);
       predecessor.put(stop1, stop2);
       predecessor.put(stop2, stop3);
       predecessor.put(stop3, stop4);
       
       new FastestPathTree(new Stop("stop", new PointWGS84(0, 0)), arrivalTime, predecessor);
       
   }
   
   //Teste si on a bien la bonne heure d'arrivée
   @Test
   public void testArrivalTime(){
       Map<Stop, Integer> arrivalTime = new HashMap<Stop, Integer>();
       Map<Stop, Stop> predecessor = new HashMap<Stop, Stop>();
       
       Stop stop1 = new Stop("stop1", new PointWGS84(0, 0));
       Stop stop2 = new Stop("stop2", new PointWGS84(0, 0));
       arrivalTime.put(stop1, 100);
      
       FastestPathTree tree = new FastestPathTree(stop1, arrivalTime, predecessor);
       
       assertEquals(100, tree.arrivalTime(stop1));
       assertEquals(SecondsPastMidnight.INFINITE, tree.arrivalTime(stop2));
   }
   
   //teste si pathTo lance bien une exception
   @Test(expected = IllegalArgumentException.class)
   public void testExceptionPathTo(){
       
       Map<Stop, Integer> arrivalTime = new HashMap<Stop, Integer>();
       Map<Stop, Stop> predecessor = new HashMap<Stop, Stop>();
       
       Stop stop1 = new Stop("stop1", new PointWGS84(0, 0));
       Stop stop2 = new Stop("stop2", new PointWGS84(0, 0));
       arrivalTime.put(stop1, 0);
      
       FastestPathTree tree = new FastestPathTree(stop1, arrivalTime, predecessor);
       
      tree.pathTo(stop2);
   }
   
   // teste si pathTo retourne bien le chemin pour aller de l'arrêt de départ à celui passé en argument
   @Test
   public void testPathTo(){
       
       //methode: On crée un FastestPathTree et on remplit la table des prédécesseurs
       Stop startingStop = new Stop("startingStop", new PointWGS84(0, 0));
       Map<Stop, Integer> arrivalTime = new HashMap<Stop, Integer>();
       Map<Stop, Stop> predecessor = new HashMap<Stop, Stop>();
       
       Stop stop1 = new Stop("stop1", new PointWGS84(0, 0));
       Stop stop2 = new Stop("stop2", new PointWGS84(0, 0));
       Stop stop3 = new Stop("stop3", new PointWGS84(0, 0));
       Stop stop4 = new Stop("stop4", new PointWGS84(0, 0));
       Stop stop5 = new Stop("stop5", new PointWGS84(0, 0));
       
       List<Stop>stops = new ArrayList<Stop>();
       stops.add(startingStop);
       stops.add(stop1);
       stops.add(stop2);
       stops.add(stop3);
       stops.add(stop4);
       stops.add(stop5);
       
       predecessor.put(startingStop, null);
       predecessor.put(stop1, startingStop);
       predecessor.put(stop2, stop1);
       predecessor.put(stop3, stop2);
       predecessor.put(stop4, stop3);
       predecessor.put(stop5, stop4);
       
       arrivalTime.put(startingStop, 0);
       arrivalTime.put(stop1, 1);
       arrivalTime.put(stop2, 2);
       arrivalTime.put(stop3, 3);
       arrivalTime.put(stop4, 4);
       arrivalTime.put(stop5, 5);
       
       FastestPathTree path = new FastestPathTree(startingStop, arrivalTime, predecessor);
       
       List<Stop> pathList = path.pathTo(stop5); //On met le chemin obtenu avec pathTo dans un tableau
       
       for(int i = 0; i < pathList.size(); i++){          
           assertTrue( pathList.get(i).equals(stops.get(i)) );
       }
   }
   
   //teste si le constructeur lance bien une exception
   @Test(expected = IllegalArgumentException.class)
   public void testBuilderConstructorException(){
       
      new FastestPathTree.Builder(new Stop("stop", new PointWGS84(0, 0)), -1);
       
   }
   
   //teste si arrivalTime renvoie bien l'heure de premiere arrivee de l'arrêt qu'on lui a donné en paramètre ou SecondsPastMidnight.INFINITE si aucune heure d'arrivée n'a été attribuée jusque ici. 
   @Test
   public void testBuilderArrivalTime(){
       
       Stop startingStop = new Stop("startingStop", new PointWGS84(0, 0));
       FastestPathTree.Builder builder = new Builder(startingStop, 6);
       
       assertEquals(6, builder.arrivalTime(startingStop));
       
   }
   
    
}
