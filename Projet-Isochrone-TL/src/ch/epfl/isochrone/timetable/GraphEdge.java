package ch.epfl.isochrone.timetable;

import static ch.epfl.isochrone.math.Math.divF;
import static ch.epfl.isochrone.math.Math.modF;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Modélise un arc annoté du graphe des horaires.
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
final class GraphEdge {
    private final Stop destination;
    private final Integer[] arrayPackedTrips;
    private final int walkingTime;
    
    /**
     * Encode en entier un trajet donné par son heure de départ et son heure d'arrivée.
     * 
     * @param departureTime L'heure de départ (en secondes après minuit).
     * @param arrivalTime L'heure d'arrivée (en secondes après minuit).
     * @throws IllegalArgumentException Si l'heure de départ est invalide, c-à-d hors de l'intervalle [0;107'999], ou si la différence entre l'heure d'arrivée et celle de départ est invalide, c-à-d hors de l'intervalle [0;9'999].
     * @return Le trajet encodé en entier contenant l'heure de départ et l'heure d'arrivée.
     */
    public static int packTrip(int departureTime, int arrivalTime){
        int travelTime = arrivalTime - departureTime;
        
        if(departureTime < 0 || departureTime > 107999){
            throw new IllegalArgumentException("L'heure de départ (=" + departureTime + ") est invalide. Une valeur parmis l'intervalle [0;107'999] est attendue.");
        }
        else if(travelTime < 0 || travelTime > 9999){
            throw new IllegalArgumentException("La durée du trajet (=" + travelTime + ") est invalide. Une valeur parmis l'intervalle [0;9'999] est attendue.");
        }
        else{
            return departureTime*10000 + travelTime;
        }
    }

    /**
     * Extrait l'heure de départ d'un trajet encodé.
     * 
     * @param packedTrip Le trajet encodé.
     * @return L'heure de départ d'un trajet encodé.
     */
    public static int unpackTripDepartureTime(int packedTrip){
        return divF(packedTrip, 10000);
    }

    /**
     * Extrait la durée, exprimée en secondes, d'un trajet encodé.
     * 
     * @param packedTrip Le trajet encodé.
     * @return La durée, exprimée en secondes, du trajet encodé.
     */
    public static int unpackTripDuration(int packedTrip){
        return modF(packedTrip, 10000);
    }

    /**
     * Extrait l'heure d'arrivée du trajet encodé passé en argument.
     * 
     * @param packedTrip Le trajet encodé.
     * @return L'heure d'arrivée du trajet encodé.
     */
    public static int unpackTripArrivalTime(int packedTrip){
        int departureTime = divF(packedTrip, 10000);
        int travelTime = modF(packedTrip, 10000);
        int arrivalTime = departureTime + travelTime;
        
        return arrivalTime;
    }

    /**
     * Construit un arc ayant l'arrêt de destination, le temps de marche et les trajets donnés. 
     * Le temps de marche est exprimé en secondes, et peut valoir -1, ce qui signifie qu'il est trop long d'effectuer le trajet à pied.
     * 
     * @param destination L'arrêt de destination.
     * @param walkingTime Le temps de marche (en secondes).
     * @param packedTrips Les trajets encodés en entier.
     * @throws IllegalArgumentException Si le temps de marche est inférieur à -1.
     */
    public GraphEdge(Stop destination, int walkingTime, Set<Integer> packedTrips){
        if(walkingTime < -1){
            throw new IllegalArgumentException("Le temps de marche est inférieur à -1");
        }
        else{
            this.destination = destination;
            this.walkingTime = walkingTime;
            
            // Les trajets dans un tableau trié
            arrayPackedTrips = packedTrips.toArray(new Integer[0]);
            Arrays.sort(arrayPackedTrips); //sort fonctionne ici car le tableau contient des Integer, qui sont comparables

        }
    }
    
    /**
     * Retourne l'arrêt destination de l'arc.
     * 
     * @return L'arrêt destination de l'arc.
     */
    public Stop destination(){
        return destination;
    }
    
    /**
     * Retourne la première heure d'arrivée possible à la destination de l'arc, étant donnée l'heure de départ. 
     * Cette heure est SecondsPastMidnight.INFINITE s'il n'est pas possible d'effectuer le trajet à l'heure de départ donnée (c'est-à-dire si le temps de marche est -1 et l'heure de départ du dernier trajet est antérieure à l'heure de départ donnée).
     * 
     * @param departureTime L'heure de départ (en secondes après minuit).
     * @return S'il est possible d'effectuer le trajet : La première heure d'arrivée possible à la destination de l'arc. S'il est impossible d'effectuer le trajet : SecondsPastMidnight.INFINITE .
     */
    public int earliestArrivalTime(int departureTime){
        int walkEarliestArrivalTime = departureTime + walkingTime;

        // S'il n'y a pas de trajet sur l'arc
        if(arrayPackedTrips.length == 0){
            
            // Et si en plus la marche est impossible ==> impossible d'effectuer le trajet
            if(walkingTime < -1){
                return SecondsPastMidnight.INFINITE;
            }
            // Si marche possible, alors on retourne l'heure d'arrivée en marchant
            else{
                return walkEarliestArrivalTime;
            }
        }
        // Sinon il y a des trajets sur l'arc et il faut déterminer la meilleur heure d'arrivée parmis les trajets
        else{

            // On "pack" l'heure de départ pour se conformer au format du tableau trié lors de la recherche dichotomique ci-dessous
            // On décide de mettre l'heure d'arrivée à 0. On tirera nos conclusion lors des comparaisons à partir de ce fait.
            int departureTimePacked = packTrip(departureTime, departureTime);

            int binarySearchResult = Arrays.binarySearch(arrayPackedTrips, departureTimePacked);

            /* Si le résultat est négatif c'est que l'élément ne se trouve pas dans la liste et que la recherche dichotomique
           nous a retourné l'endroit où l'élément pourrait être inséré. Il nous faut faire un petit calcul pour récpérer l'index. */
            if(binarySearchResult < 0){
                binarySearchResult = -(binarySearchResult) - 1;
            }

            // Trajet impossible
            if(binarySearchResult == arrayPackedTrips.length){
                
                // Marche impossible ==> impossible d'effectuer le trajet
                if(walkingTime == -1){
                    return SecondsPastMidnight.INFINITE;
                }
                // Marche possible ==> retourner l'heure d'arrivée en marchant
                else{
                    return walkEarliestArrivalTime;
                }
            }
            // Trajet possible
            else{
                int tripEarliestArrivalTime = unpackTripArrivalTime( (int)arrayPackedTrips[binarySearchResult] );

                // Marche impossible ==> retourner l'heure d'arrivée de la meilleure heure d'arrivée des trajets
                if(walkingTime == -1){
                    return tripEarliestArrivalTime;
                }
                // Marche possible ==> déterminer qui entre l'heure du trajet et l'heure de marche est le plus rapide (en supposant qu'à heure égale on préféra le véhicule)
                else{
                    return (tripEarliestArrivalTime <= walkEarliestArrivalTime) ? tripEarliestArrivalTime : walkEarliestArrivalTime;
                }
            }
        }
    }
    
    /**
     * Modélise un batisseur pour un arc annoté du graphe des horaires.
     *
     * @author Alexandre Simoes Tavares (234563)
     * @author Lois Talagrand (234231)
     */
    public final static class Builder{
        private final Stop destination;
        private final Set<Integer> packedTrips;
        private int walkingTime;
       
        /**
         * Construit un bâtisseur pour un arc ayant l'arrêt donné comme destination, aucun trajet et un temps de marche de -1, signifiant qu'il est trop long d'effectuer le trajet à pied.
         * 
         * @param destination L'arrêt de destination.
         */
        public Builder(Stop destination){
            this.destination = destination;
            packedTrips = new HashSet<Integer>();
            walkingTime = -1;
        }
        
        /**
         * Change le temps de marche pour qu'il soit égal à la valeur passée en argument.
         * 
         * @param newWalkingTime Le nouveau temps de marche.
         * @throws IllegalArgumentException Si l'argument est inférieur à -1. 
         * @return this (permet les appels chaînés).
         */
        public GraphEdge.Builder setWalkingTime(int newWalkingTime){
            if(newWalkingTime < -1){
                throw new IllegalArgumentException("Le temps de marche: " + newWalkingTime + " est doit être supérieur ou égal à -1.");
            }
            walkingTime = newWalkingTime;
            return this;
        }
        
        /**
         * Ajoute un trajet avec les heures de départ et d'arrivée données et exprimées en nombre de secondes après minuit.
         * 
         * @param departureTime L'heure de départ.
         * @param arrivalTime L'heure d'arrivée.
         * @throws IllegalArgumentException Si l'heure de départ est invalide, c-à-d hors de l'intervalle [0;107'999], ou si la différence entre l'heure d'arrivée et celle de départ est invalide, c-à-d hors de l'intervalle [0;9'999].
         * @return this (permet les appels chaînés).
         */
        public GraphEdge.Builder addTrip(int departureTime, int arrivalTime){
            
            int packedTrip = packTrip(departureTime, arrivalTime);
            packedTrips.add(packedTrip);
            
            return this;
        }
        
        /**
         * Retourne un nouvel arc avec la destination, le temps de marche et les trajets ajoutés jusqu'ici au bâtisseur.
         * 
         * @return Un nouvel arc avec la destination, le temps de marche et les trajets ajoutés jusqu'ici au bâtisseur.
         */
        public GraphEdge build(){
            return new GraphEdge(destination, walkingTime, packedTrips);
        }
    }
}
