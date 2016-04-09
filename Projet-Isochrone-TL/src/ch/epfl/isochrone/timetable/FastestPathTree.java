package ch.epfl.isochrone.timetable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Modélise un arbre de trajets les plus rapides.
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public final class FastestPathTree {

    private final Stop startingStop;
    private final Map<Stop, Integer> arrivalTime;
    private final Map<Stop, Stop> predecessor;
    
    /**
     * Construit un arbre des trajets les plus rapides avec l'arrêt de départ, la table des heures d'arrivée et la table des prédécesseurs donnés. 
     * La table des heures d'arrivée associe à un certain nombre d'arrêts l'heure de première arrivée à cet arrêt (en secondes après minuit). 
     * La table des prédécesseurs associe à chaque arrêt son prédécesseur dans l'arbre.
     * 
     * @param startingStop L'arrêt de départ.
     * @param arrivalTime La table des heures d'arrivée.
     * @param predecessor La table des prédécesseurs.
     * @throws IllegalArgumentException Si l'ensemble des clefs de la table des heures n'est pas égal à celui des clefs de la tables des prédécesseurs plus l'arrêt de départ.
     */
    public FastestPathTree(Stop startingStop, Map<Stop, Integer> arrivalTime, Map<Stop, Stop> predecessor){
        
        // ATTENTION: Dans la Map predecessor, les values représentent les arrêts précédents les clefs
        // Illustration de la Map:  
        // Stop | Predecessor
        predecessor.put(startingStop, null);
        
        if(arrivalTime.keySet().size() != predecessor.keySet().size()){
            throw new IllegalArgumentException("Il n'y a pas le même nombre de clefs entre la table des heures d'arrivée et la table des prédécesseurs");     
        }
        
        
        Stop[] arrayPredecessorKeys = predecessor.keySet().toArray(new Stop[0]);
        for(int j = 0; j < predecessor.keySet().size(); j++){
            
            if( !(arrivalTime.keySet().contains(arrayPredecessorKeys[j])) ){
                throw new IllegalArgumentException("La clef de la table des prédécesseurs (=" + arrayPredecessorKeys[j].name() + ") n'est pas contenu dans la table des heures d'arrivée");
            }

        }
        
        this.startingStop = startingStop;
        this.arrivalTime = java.util.Collections.unmodifiableMap(arrivalTime);
        this.predecessor = java.util.Collections.unmodifiableMap(predecessor);
    }
    
    /**
     * Retourne l'arrêt de départ.
     * 
     * @return L'arrêt de départ.
     */
    public Stop startingStop(){
        return startingStop; //Stop est une classe immuable donc on a pas besoin de faire de copie
    }
    
   
    /**
     * Retourne l'heure de départ, qui n'est autre que l'heure de première arrivée à l'arrêt de départ.
     * 
     * @return L'heure de départ, qui n'est autre que l'heure de première arrivée à l'arrêt de départ.
     */
    public int startingTime(){
        return arrivalTime.get(startingStop);
    }
    
    /**
     * Retourne l'ensemble des arrêts pour lesquels une heure de première arrivée existe.
     * 
     * @return L'ensemble des arrêts pour lesquels une heure de première arrivée existe.
     */
    public Set<Stop> stops(){
        return java.util.Collections.unmodifiableSet(arrivalTime.keySet()); // On fait attention à faire une copie pour l'immuabilité
    }
    
    
    /**
     * Retourne l'heure d'arrivée à l'arrêt donné ou SecondsPastMidnight.INFINITE si l'arrêt donné n'est pas dans la table des heures d'arrivée passée au constructeur.
     * 
     * @param stop L'arrêt.
     * @return L'heure d'arrivée à l'arrêt donné ou SecondsPastMidnight.INFINITE si l'arrêt donné n'est pas dans la table des heures d'arrivée passée au constructeur.
     */
    public int arrivalTime(Stop stop){
        return arrivalTime.containsKey(stop) ? arrivalTime.get(stop) : SecondsPastMidnight.INFINITE;
    }
    
    /**
     * Retourne le chemin pour aller de l'arrêt de départ à celui passé en argument.
     * 
     * @param stop L'arrêt de destination.
     * @throws IllegalArgumentException Si l'arrêt passé n'est pas présent dans la table des heures d'arrivée.
     * @return Le chemin pour aller de l'arrêt de départ à celui passé en argument.
     */
    public List<Stop> pathTo(Stop stop){
                   
        if(!(arrivalTime.containsKey(stop))){
            throw new IllegalArgumentException("L'arrêt \"" + stop.name() + "\" n'est pas dans la table des heures d'arrivée.");
        }
            
        List<Stop> path = new ArrayList<Stop>();  
        
        path.add(stop);
        while( !(path.contains(startingStop)) ){
            Stop stopPredecessor = predecessor.get(stop);
            path.add(stopPredecessor);
            stop = stopPredecessor;
        }
        
        Collections.reverse(path); // Inversion pour avoir le chemin dans un ordre logique
        
        return path;
    }
    
    /**
     * Modélise un batisseur pour un arbre de trajets les plus rapides
     *
     * @author Alexandre Simoes Tavares (234563)
     * @author Lois Talagrand (234231)
     */
    public static final class Builder{
        
        private final Stop startingStop;
        private final int startingTime;
        private final Map<Stop, Integer> arrivalTimeMap;
        private final Map<Stop, Stop> predecessorMap;
        
        /**
         * Construit un bâtisseur pour un arbre des trajets les plus rapides avec l'arrêt et l'heure de départ donnés. 
         * Dans cet arbre en construction, l'heure de première arrivée de l'arrêt de départ doit être l'heure de départ.
         * 
         * @param startingStop L'arrêt de départ.
         * @param startingTime L'heure de départ.
         * @throws IllegalArgumentException Si l'heure de départ est négative.
         */
        public Builder(Stop startingStop, int startingTime){
            
            if(startingTime < 0){
                throw new IllegalArgumentException(" L'heure de départ (=" + startingTime + ") doit être positive.");
            }
            
            this.startingStop = startingStop;
            this.startingTime = startingTime;
            arrivalTimeMap = new HashMap<Stop, Integer>();
            predecessorMap = new HashMap<Stop, Stop>();
            
            arrivalTimeMap.put(startingStop, startingTime);
        }
        
        /**
         * (Re)Définit l'heure de première arrivée et le prédécesseur de l'arrêt donné dans l'arbre en construction.
         * 
         * @param stop L'arrêt.
         * @param time L'heure.
         * @param predecessor Le prédécesseur
         * @throws IllegalArgumentException Si l'heure donnée est antérieure à l'heure de départ.
         * @return this (permet les appels chaînés).
         */
        public Builder setArrivalTime(Stop stop, int time, Stop predecessor){
            
            if(time < startingTime){
                throw new IllegalArgumentException("L'heure en argument: " + time + " est antérieure à l'heure de départ.");
            }
                   
            // On (re)defitnit l'heure de première arrivée
            arrivalTimeMap.put(stop, time); 
                    
            // On (re)definit le predecesseur
            predecessorMap.put(stop, predecessor); 
               
            return this;
        }
        
        /**
         * Retourne l'heure de première arrivée à l'arrêt donné, ou SecondsPastMidnight.INFINITE si aucune heure d'arrivée n'a été attribuée à cet arrêt jusqu'ici.
         * 
         * @param stop L'arrêt.
         * @return L'heure de première arrivée à l'arrêt donné, ou SecondsPastMidnight.INFINITE si aucune heure d'arrivée n'a été attribuée à cet arrêt jusqu'ici.
         */
        public int arrivalTime(Stop stop){
            return arrivalTimeMap.containsKey(stop) ? arrivalTimeMap.get(stop) : SecondsPastMidnight.INFINITE;
        }
        
        /**
         * Construit l'arbre des trajets les plus rapides avec les nœuds ajoutés jusqu'ici.
         * 
         * @return L'arbre des trajets les plus rapides avec les nœuds ajoutés jusqu'ici.
         */
        public FastestPathTree build(){
            return new FastestPathTree(startingStop, arrivalTimeMap, predecessorMap);
        }
    }
}
