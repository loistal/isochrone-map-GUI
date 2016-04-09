package ch.epfl.isochrone.geo;

import static java.lang.Math.PI;
import static java.lang.Math.atan;
import static java.lang.Math.pow;
import static java.lang.Math.round;
import static java.lang.Math.sinh;

/**
 * Un point dans le système de coordonnées OSM.
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public final class PointOSM {
    private final double x;
    private final double y;
    private final int zoom;
    
    /**
     * Construit un point de coordonnées x et y, au niveau de zoom donné.
     * 
     * @param zoom Le niveau de zoom.
     * @param x Coordonnée x
     * @param y Coordonnée y
     * @throws IllegalArgumentException Si le niveau de zoom est négatif ou 
     *                                  si l'une des deux coordonnées n'est pas dans l'intervalle admissible allant de 0 à la valeur maximale déterminée par la formule suivante : 2^(zoom+8).
     */
    public PointOSM(int zoom, double x, double y){
        // Valeur maximale pour x et y selon le niveau de zoom (carte du monde = image carrée de "s" pixels de côté)
        double s = pow(2, zoom+8);
        
        if(zoom < 0){
            throw new IllegalArgumentException("Zoom négatif (=" + zoom + ")");
        }
        else if(x < 0){
            throw new IllegalArgumentException("Coordonée x (=" + x + ") < 0");
        }
        else if(y < 0){
            throw new IllegalArgumentException("Coordonée y (=" + y + ") < 0");
        }
        else if(x > s){
            throw new IllegalArgumentException("Coordonée x (=" + x + ") dépasse la valeur maximale autorisée (=" + s + ")");
        }
        else if(y > s){
            throw new IllegalArgumentException("Coordonée y (=" + y + ") dépasse la valeur maximale autorisée (=" + s + ")");
        }
        else{
            this.x = x;
            this.y = y;
            this.zoom = zoom;
        }
    }
    
    /**
     * Retourne la taille de l'image de la carte du monde au niveau de zoom donné. 
     * Cette taille est également la plus grande coordonnées x ou y admissible au niveau de zomm donné.
     * 
     * @param zoom Le niveau de zoom.
     * @throws IllegalArgumentException Si le zoom est négatif.
     * @return La taille de l'image de la carte selon niveau zoom donné.
     */
    public static int maxXY(int zoom){
        if(zoom < 0){
            throw new IllegalArgumentException("Zoom négatif (=" + zoom + ")");
        }
        else{
            return (int) pow(2, zoom+8);
        }
    }
    
    /**
     * Retourne la coordonnée x du point.
     * 
     * @return La coordonnée x.
     */
    public double x(){
        return x;
    }

    /**
     * Retourne la coordonnée y du point.
     * 
     * @return La coordonnée y.
     */
    public double y(){
        return y;
    }
    
    /**
     * Retourne l'entier le plus proche de la coordonnée x du point.
     * 
     * @return L'entier le plus proche de la coordonnée x du point.
     */
    public int roundedX(){
        return (int) round(x);
    }
    
    /**
     * Retourne l'entier le plus proche de la coordonnée y du point.
     * 
     * @return L'entier le plus proche de la coordonnée y du point.
     */
    public int roundedY(){
        return (int) round(y);
    }
    
    /**
     * Retourne le niveau de zoom du point.
     * 
     * @return Le niveau de zoom.
     */
    public int zoom(){
        return zoom;
    }
    
    /**
     * Retourne ce même point, mais au niveau de zoom passé en argument.
     *
     * @param newZoom Le nouveau niveau de zoom.
     * @throws IllegalArgumentException Si le nouveau niveau de zoom est négatif.
     * @return Le point au niveau de zoom donné.
     */
    public PointOSM atZoom(int newZoom){
        if(newZoom < 0){
            throw new IllegalArgumentException("Zoom négatif (=" + newZoom + ")");
        }
        else{
            /* Exemple d'application qui justifie les formules ci-dessous :
             * Point zoom 00 : (30, 128)
             * zoom 01: (60, 256)
             * zoom 02: (120, 512)
             * zoom 03: (240, 1024)
             * zoom 04: (480, 2048)
             * zoom 05: (960, 4096)
             * etc.
             */
            if(newZoom >= zoom){
                // (02 -> 05) : x = 120*2^(5-2) = 960 , y = 512*2^(5-2) = 4096
                return new PointOSM(newZoom, x*pow(2, newZoom-zoom), y*pow(2, newZoom-zoom));
            }
            // Sinon : newZoom < zoom
            else{
                // (04 -> 00) : x = 480/2^(4-0) = 30 , y = 2048/2^(4-0) = 128
                return new PointOSM(newZoom, x/pow(2, zoom-newZoom), x/pow(2, zoom-newZoom));
            }
        }
    }
    
    /**
     * Retourne le point dans le système de coordonnées WGS 84.
     * 
     * @return Le point dans le système de coordonnées WGS 84.
     */
    public PointWGS84 toWGS84(){
        double s = pow(2, zoom+8);
        
        double longitude = ((2*PI)/s)*x - PI;
        double latitude = atan( sinh( PI - ((2*PI/s)*y) ) );
        
        return new PointWGS84(longitude,latitude);
    }
    
    /**
     * Retourne une représentation textuelle du point, qui est formée du niveau de zoom, de la coordonnée x et de la coordonnée y, séparés par des virgules et entourés d'une paire de parenthèse.
     * C'est-à-dire : (zoom, x, y)
     * 
     * @return Une représentation textuelle du point.
     */
    @Override
    public String toString(){
        return "(" + zoom + ", " + x + ", " + y + ")";
    }
}
