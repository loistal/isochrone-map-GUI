package ch.epfl.isochrone.timetable;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import ch.epfl.isochrone.geo.PointWGS84;

/**
 * Test de la classe Graph
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public class TestGraph {
    // Le "test" suivant n'en est pas un à proprement parler, raison pour
    // laquelle il est ignoré (annotation @Ignore). Son seul but est de garantir
    // que les noms des classes et méthodes sont corrects.
    @Test
    @Ignore
    public void namesAreOk() {
        // Graph n'a aucune méthode publique à ce stade...

        Set<Stop> stops = null;
        Stop stop = null;
        Graph.Builder gb = new Graph.Builder(stops);
        gb.addTripEdge(stop, stop, 0, 0);
        gb.addAllWalkEdges(0, 0);
        gb.build();
    }
    
    @Test (expected = IllegalArgumentException.class) // Si l'arrêt de départ ne fait pas partie de ceux passés au constructeur
    public void testBuilderAddTripEdgeUnknownFromStop(){
        Stop stop1 = new Stop("Misc1", new PointWGS84(0, 0));
        Stop stop2 = new Stop("Misc2", new PointWGS84(0, 0));
        
        Set<Stop> stopSet = new HashSet<Stop>();
        stopSet.add(stop1);
        stopSet.add(stop2);
        
        Graph.Builder graphBD = new Graph.Builder(stopSet);
        
        Stop unknownStop = new Stop("Misc3", new PointWGS84(0, 0));
        
        graphBD.addTripEdge(unknownStop, stop2, 0, 1);
        
    }
    
    @Test (expected = IllegalArgumentException.class) // Si l'arrêt d'arrivée ne fait pas partie de ceux passés au constructeur
    public void testBuilderAddTripEdgeUnknownToStop(){
        Stop stop1 = new Stop("Misc1", new PointWGS84(0, 0));
        Stop stop2 = new Stop("Misc2", new PointWGS84(0, 0));
        
        Set<Stop> stopSet = new HashSet<Stop>();
        stopSet.add(stop1);
        stopSet.add(stop2);
        
        Graph.Builder graphBD = new Graph.Builder(stopSet);
        
        Stop unknownStop = new Stop("Misc3", new PointWGS84(0, 0));
        
        graphBD.addTripEdge(stop1, unknownStop, 0, 1);
    }
    
    @Test (expected = IllegalArgumentException.class) // Si l'heure de départ est négative
    public void testBuilderAddTripEdgeDepartureTimeNegative(){
        Stop stop1 = new Stop("Misc1", new PointWGS84(0, 0));
        Stop stop2 = new Stop("Misc2", new PointWGS84(0, 0));
        
        Set<Stop> stopSet = new HashSet<Stop>();
        stopSet.add(stop1);
        stopSet.add(stop2);
        
        Graph.Builder graphBD = new Graph.Builder(stopSet);
        
        graphBD.addTripEdge(stop1, stop2, -1, 0);
    }
    
    @Test (expected = IllegalArgumentException.class) // Si l'heure d'arrivée est négative
    public void testBuilderAddTripEdgeArrivalTimeNegative(){
        Stop stop1 = new Stop("Misc1", new PointWGS84(0, 0));
        Stop stop2 = new Stop("Misc2", new PointWGS84(0, 0));
        
        Set<Stop> stopSet = new HashSet<Stop>();
        stopSet.add(stop1);
        stopSet.add(stop2);
        
        Graph.Builder graphBD = new Graph.Builder(stopSet);
        
        graphBD.addTripEdge(stop1, stop2, 0, -1);
    }
    
    @Test (expected = IllegalArgumentException.class) // Si l'heure d'arrivée est antérieure à l'heure de départ
    public void testBuilderAddTripEdgeArrivalTimeTooLow(){
        Stop stop1 = new Stop("Misc1", new PointWGS84(0, 0));
        Stop stop2 = new Stop("Misc2", new PointWGS84(0, 0));
        
        Set<Stop> stopSet = new HashSet<Stop>();
        stopSet.add(stop1);
        stopSet.add(stop2);
        
        Graph.Builder graphBD = new Graph.Builder(stopSet);
        
        graphBD.addTripEdge(stop1, stop2, 2, 1);
    }
    
    @Test (expected = IllegalArgumentException.class) // Si le temps maximum de marche est négatif
    public void testAddAllWalkEdgesMaxWalkingTimeNegative(){
        Stop stop1 = new Stop("Misc1", new PointWGS84(0, 0));
        Stop stop2 = new Stop("Misc2", new PointWGS84(0, 0));
        
        Set<Stop> stopSet = new HashSet<Stop>();
        stopSet.add(stop1);
        stopSet.add(stop2);
        
        Graph.Builder graphBD = new Graph.Builder(stopSet);
        graphBD.addAllWalkEdges(-1, 1);
    }
    
    @Test (expected = IllegalArgumentException.class) // Si la vitesse de marche est négative
    public void testAddAllWalkEdgesWalkingSpeedNegative(){
        Stop stop1 = new Stop("Misc1", new PointWGS84(0, 0));
        Stop stop2 = new Stop("Misc2", new PointWGS84(0, 0));
        
        Set<Stop> stopSet = new HashSet<Stop>();
        stopSet.add(stop1);
        stopSet.add(stop2);
        
        Graph.Builder graphBD = new Graph.Builder(stopSet);
        graphBD.addAllWalkEdges(100, -1);
    }
    
    @Test (expected = IllegalArgumentException.class) // Si la vitesse de marche est nulle
    public void testAddAllWalkEdgesWalkingSpeedZero(){
        Stop stop1 = new Stop("Misc1", new PointWGS84(0, 0));
        Stop stop2 = new Stop("Misc2", new PointWGS84(0, 0));
        
        Set<Stop> stopSet = new HashSet<Stop>();
        stopSet.add(stop1);
        stopSet.add(stop2);
        
        Graph.Builder graphBD = new Graph.Builder(stopSet);
        graphBD.addAllWalkEdges(100, 0);
    }
        
    @Test
    public void testFastestPaths(){
        
        Set<Stop> stops = new HashSet<Stop>();
        Stop belAir = new Stop("Bel-Air", new PointWGS84(java.lang.Math.toRadians(6.62904292724), java.lang.Math.toRadians(46.52236132)));
        Stop stFrancois = new Stop("St-Francois", new PointWGS84(java.lang.Math.toRadians(6.63373679563), java.lang.Math.toRadians(46.519415595)));
        Stop bConstant = new Stop("B.-Constant", new PointWGS84(java.lang.Math.toRadians(6.6374757049), java.lang.Math.toRadians(46.5192386932)));
        Stop georgette = new Stop("Georgette", new PointWGS84(java.lang.Math.toRadians(6.63794733267), java.lang.Math.toRadians(46.5174547958)));
        stops.add(belAir);
        stops.add(stFrancois);
        stops.add(bConstant);
        stops.add(georgette);
        
        Graph.Builder graphBD = new Graph.Builder(stops);
        graphBD.addTripEdge(belAir, stFrancois, SecondsPastMidnight.fromHMS(9, 20, 0), SecondsPastMidnight.fromHMS(9, 23, 0));
        graphBD.addTripEdge(belAir, stFrancois, SecondsPastMidnight.fromHMS(9, 30, 0), SecondsPastMidnight.fromHMS(9, 33, 0));

        graphBD.addTripEdge(stFrancois, belAir, SecondsPastMidnight.fromHMS(9, 20, 0), SecondsPastMidnight.fromHMS(9, 23, 0));
        graphBD.addTripEdge(stFrancois, belAir, SecondsPastMidnight.fromHMS(9, 30, 0), SecondsPastMidnight.fromHMS(9, 33, 0));

        graphBD.addTripEdge(stFrancois, bConstant, SecondsPastMidnight.fromHMS(9, 20, 0), SecondsPastMidnight.fromHMS(9, 21, 0));
        graphBD.addTripEdge(stFrancois, bConstant, SecondsPastMidnight.fromHMS(9, 25, 0), SecondsPastMidnight.fromHMS(9, 27, 0));
        
        graphBD.addTripEdge(bConstant, stFrancois, SecondsPastMidnight.fromHMS(9, 22, 0), SecondsPastMidnight.fromHMS(9, 24, 0));
        graphBD.addTripEdge(bConstant, stFrancois, SecondsPastMidnight.fromHMS(9, 27, 0), SecondsPastMidnight.fromHMS(9, 28, 0));
        
        graphBD.addTripEdge(stFrancois, georgette, SecondsPastMidnight.fromHMS(9, 24, 0), SecondsPastMidnight.fromHMS(9, 28, 0));
        graphBD.addTripEdge(georgette, stFrancois, SecondsPastMidnight.fromHMS(9, 18, 0), SecondsPastMidnight.fromHMS(9, 23, 0));

        graphBD.addAllWalkEdges(1200, 1.04);
        
        // ----------------------- Chemin 1 --------------------------
        List<Stop> path = graphBD.build().fastestPaths(belAir, SecondsPastMidnight.fromHMS(9, 18, 0)).pathTo(georgette);
        
        // D'après ce graphe, le chemin ressemble à: Bel-Air --> St-Francois --> Georgette
        assertTrue(path.size() == 3);
        
        String[] supposedResult = new String[3];
        supposedResult[0] = belAir.name();
        supposedResult[1] = stFrancois.name();
        supposedResult[2] = georgette.name();
        
        Object[] arrayResult = path.toArray();
        
        for(int i=0; i < arrayResult.length; ++i){
            assertTrue( ((Stop)arrayResult[i]).name().equals(supposedResult[i]) );
        }
         
        // ----------------------- Chemin 2 --------------------------
        path = graphBD.build().fastestPaths(bConstant, SecondsPastMidnight.fromHMS(9, 27, 0)).pathTo(belAir);
        
        // D'après ce graphe, le chemin ressemble à: B.-Constant --> St-Francois --> Bel-Air
        assertTrue(path.size() == 3);
        
        supposedResult = new String[3];
        supposedResult[0] = bConstant.name();
        supposedResult[1] = stFrancois.name();
        supposedResult[2] = belAir.name();
        
        arrayResult = path.toArray();
        
        for(int i=0; i < arrayResult.length; ++i){
            assertTrue( ((Stop)arrayResult[i]).name().equals(supposedResult[i]) );
        }
    }
}
