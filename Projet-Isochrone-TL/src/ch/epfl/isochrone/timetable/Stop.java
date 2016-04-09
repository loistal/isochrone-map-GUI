package ch.epfl.isochrone.timetable;

import ch.epfl.isochrone.geo.PointWGS84;

/**
 * Modélise un arrêt nommé et positionné dans l'espace.
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public final class Stop {
    private final String name;
    private final PointWGS84 position;
    
    public Stop(String name, PointWGS84 position){
        this.name = name;
        this.position = position;
    }
    
    /**
     * Retourne le nom de l'arrêt.
     * 
     * @return Le nom de l'arrêt.
     */
    public String name(){
        return name;
    }
    
    /**
     * Retourne la position de l'arrêt.
     * 
     * @return La position de l'arrêt.
     */
    public PointWGS84 position(){
        return position;
    }
    
    /**
     * Retourne une représentation textuelle de l'arrêt, en l'occurrence son nom.
     * 
     * @return La représentation textuelle de l'arrêt.
     */
    @Override
    public String toString(){
        return name;
    }
}
