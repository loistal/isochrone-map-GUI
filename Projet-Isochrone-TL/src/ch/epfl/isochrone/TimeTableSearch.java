package ch.epfl.isochrone;

import static ch.epfl.isochrone.timetable.SecondsPastMidnight.fromHMS;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import ch.epfl.isochrone.timetable.Date;
import ch.epfl.isochrone.timetable.FastestPathTree;
import ch.epfl.isochrone.timetable.Graph;
import ch.epfl.isochrone.timetable.SecondsPastMidnight;
import ch.epfl.isochrone.timetable.Service;
import ch.epfl.isochrone.timetable.Stop;
import ch.epfl.isochrone.timetable.TimeTable;
import ch.epfl.isochrone.timetable.TimeTableReader;

/**
 * Permet de recherches les plus courts trajets.
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public class TimeTableSearch {

    /**
     * Permet de rechercher les plus courts trajets entre un arrêt de départ et tous les autres arrêts du réseau des tl, à une date et une heure de départ donnée.
     * 
     * @param args Accepte dans l'ordre les arguments suivants: <br/>
     *              1. Le nom de l'arrêt de départ <br/>
     *              2. La date de départ, composée de trois nombres séparés par un tiret (-) et qui représentent respectivement l'année, le mois et le jour (p.ex. 2013-10-1 pour le 1er octobre 2013) <br/>
     *              3. L'heure de départ, composée de trois nombres séparés par un double point (:) et représentant respectivement l'heure, les minutes et les secondes de l'heure de départ (p.ex. 6:8:0 pour 6h08 précises) <br/>
     */
    public static void main(String[] args) throws IOException{
        //Le nom est le premier argument
        String nameStartingStop = args[0];

        //La date est le 2eme
        String[] dateString = args[1].split("-");
        Date date = new Date(Integer.parseInt(dateString[2]), Integer.parseInt(dateString[1]), Integer.parseInt(dateString[0]));

        //L'heure est est le 3eme: on la met sous la forme de secondsPastMidgnight
        String[] timeString = args[2].split(":");
        int time = fromHMS(Integer.parseInt(timeString[0]), Integer.parseInt(timeString[1]), Integer.parseInt(timeString[2]));

        //Créer un TimeTableReader
        TimeTableReader reader = new TimeTableReader("/time-table/");

        //Collecter les données (stops et services)
        TimeTable timetable = reader.readTimeTable();
        Set<Stop> stops = timetable.stops();
        Set<Service> services = timetable.servicesForDate(date);

        //On associe le nom du Stop donné au programme avec le bon stop
        Stop startingStop = null;
        for(Stop stop : stops){
            if(stop.name().equals(nameStartingStop)){
                startingStop = stop;
            }
        }
        
        if(startingStop == null){
            throw new NoSuchElementException("L'arrêt de départ " + nameStartingStop + " n'a pas été trouvé.");
        }

        //Création d'un Graph avec les données récoltées
        Graph graph = reader.readGraphForServices(stops, services, 300, 1.25);  //300 correspond à 5min

        //Utilisation du Graph pour obtenir le fastestPathTree
        FastestPathTree tree = graph.fastestPaths(startingStop, time);
        
        //Convertit la collection des stops de tree vers un tableau de Stop
        Stop[] treeStopsSorted = tree.stops().toArray(new Stop[0]);
        
        //Classe alphabétiquement les stops du tableau
        Arrays.sort(treeStopsSorted, new Comparator<Stop>() {
            
            @Override
            public int compare(Stop stop1, Stop stop2) {
                return stop1.name().compareTo(stop2.name());
            }
        });
                
        //Parcours alphabétiquement tout les stops qu'on peut atteindre
        for(Stop stop : treeStopsSorted){
            
            List<Stop> intermediaryStops = tree.pathTo(stop); 

            // Pour de raison de formattage d'affichage avec ", " on met l'arrêt de départ avant la boucle de parcours
            String intermediaryStopsString = intermediaryStops.get(0).name();
            
            // On commence à i=1, car l'arrêt de départ est déjà inscrit
            for(int i = 1; i < intermediaryStops.size(); ++i){
                intermediaryStopsString += ", " + intermediaryStops.get(i).name();
            }

            Stop finalStop = intermediaryStops.get(intermediaryStops.size()-1);
            
            System.out.println(finalStop + " : " + SecondsPastMidnight.toString(tree.arrivalTime(finalStop)));
            System.out.println("  via: [" + intermediaryStopsString + "]");
        } 
    }


}
