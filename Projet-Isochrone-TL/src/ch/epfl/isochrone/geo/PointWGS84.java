package ch.epfl.isochrone.geo;

import static ch.epfl.isochrone.math.Math.asinh;
import static ch.epfl.isochrone.math.Math.haversin;
import static java.lang.Math.PI;
import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;
import static java.lang.Math.toDegrees;


/**
 * Un point dans le système de coordonnées WGS 84.
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public final class PointWGS84 {
    private static final int EARTH_RADIUS = 6378137;
    private final double latitude;
    private final double longitude;
    
    /**
     * Construit un point de longitude et latitude données et exprimées en radians.
     * 
     * @param longitude La longitude en radians.
     * @param latitude La latitude en radians.
     * @throws IllegalArgumentException Si la longitude est hors de l'intervalle [-PI, +PI] ou 
     *                                  si la latitude est hors de l'intervalle [-PI/2, +PI/2].
     */
    public PointWGS84(double longitude, double latitude){      
        if(longitude < -PI || longitude > PI){
            throw new IllegalArgumentException("La longitude (=" + longitude +") est hors de l'intervalle [" + -PI + "; " + PI + "]");
        }
        else if(latitude < -(PI/2) || latitude > (PI/2)){
            throw new IllegalArgumentException("La latitude (=" + latitude +") est hors de l'intervalle [" + -PI/2 + "; " + PI/2 + "]");
        }
        else{
            this.longitude = longitude;
            this.latitude = latitude;
        }
    }
    
    /**
     * Retourne la longitude du point en radians.
     * 
     * @return La longitude en radians.
     */
    public double longitude(){
        return longitude;
    }

    /**
     * Retourne la latitude du point en radians.
     * 
     * @return La latitude en radians.
     */
    public double latitude(){
        return latitude;
    }

    /**
     * Retourne la distance, en mètres, séparant le récepteur (c-à-d le point auquel on l'applique) du point passé en argument.
     *
     * @param that  Un point distant.
     * @return La distance, en mètres, entre les deux points.
     */
    public double distanceTo(PointWGS84 that){
        final double longitudeDiff = this.longitude - that.longitude();
        final double latitudeDiff = this.latitude - that.latitude();
        
        final double d = 2 * EARTH_RADIUS * asin(sqrt( haversin(latitudeDiff) + cos(this.latitude)*cos(that.latitude())*haversin(longitudeDiff) ));
        
        return d;
    }
    
    /**
     * Retourne le point dans le système de coordonnées OSM au niveau de zoom passé en argument.
     *
     * @param zoom Le niveau de zoom.
     * @throws IllegalArgumentException Si le zoom est négatif.
     * @return Le point dans le système de coordonnées OSM au  niveau de zoom donné.
     */
    public PointOSM toOSM(int zoom){
        if(zoom < 0){
            throw new IllegalArgumentException("Zoom négatif");
        }
        else{
            double s = pow(2, zoom+8);
            
            double x = (s/(2*PI)) * (longitude + PI);
            double y = (s/(2*PI)) * (PI - asinh(tan(latitude)));
            
            return new PointOSM(zoom, x, y);
        }
    }
    
    /**
     * Retourne une représentation textuelle du point, qui est formée de la longitude et de la latitude en degrés, séparées par une virgule et entourées d'une paire de parenthèses. 
     * C'est-à-dire : (longitude°, latitude°)
     * 
     * @return Une représentation textuelle du point.
     */
    @Override
    public String toString(){
        return "(" + toDegrees(longitude) + ", " + toDegrees(latitude) +")";
    }
}
