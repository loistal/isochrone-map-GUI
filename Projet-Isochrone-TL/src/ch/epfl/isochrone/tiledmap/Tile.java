package ch.epfl.isochrone.tiledmap;

import static java.lang.Math.floor;

import java.awt.image.BufferedImage;

import ch.epfl.isochrone.geo.PointOSM;

/**
 * Représente une tuile de carte.
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public final class Tile {
    
    public final static int DIMENSION = 256;

    /**
     * Convertit une coordonnée X ou Y (OSM par exemple) en une coordonnée de tuile de dimension spécifié par la variable static DIMENSION. 
     * Par exemple avec tuile dimension 5x5: une coordonnée X OSM de 13 correspond à la coordonnée de tuile 2 ( =floor(13/5)=2 ). On suppose que 
     * les coordonnées de tuiles commencent à 0.
     * 
     * @param value La valeur à convertir.
     * @return La valeur convertit en une coordonnée de tuile.
     */
    public static int convertToTileCoordinate(double value){
        return (int)floor(value/DIMENSION);
    }

    private final int xTile;
    private final int yTile;
    
    private final int xOSM;
    private final int yOSM;
    
    private final int zoom;
    
    private final BufferedImage image;
    
    /**
     * Construit une tuile de carte.
     * 
     * @param x La coordonnée X de la tuile.
     * @param y La coordonnée Y de la tuile.
     * @param zoom Le zoom associé à la tuile. Valeur entre 0 compris et 19 compris.
     * @param image L'image de la tuile.
     * @throws IllegalArgumentException Si le zoom n'est pas dans l'intervalle [0;19], ou 
     *                                  si les coordonnées de X et Y et de la tuile ne possèdent pas des valeurs autorisées (2^zoom+8 / DIMENSION tuile), ou
     *                                  si l'image de la tuile ne possèdent pas une dimension carré définit par la variable static DIMENSION.
     */
    public Tile (int xTile, int yTile, int zoom, BufferedImage image){
        if( !(zoom >= 0 && zoom <= 19) ){
            throw new IllegalArgumentException("Le zoom doit être entre [0; 19]. Elle vaut actuellemment : " + zoom);
        }
        
        int maxTileXY = ( PointOSM.maxXY(zoom) ) / 256;
        if( !(xTile >= 0 && xTile <= maxTileXY) || !(yTile >= 0 && yTile <= maxTileXY) ){
            throw new IllegalArgumentException("La tuile ne peut avoir des coordonnées de tuiles plus grandes que " + maxTileXY + ". "
                                             + "Valeur X : " + xTile + ", Valeur Y : " + yTile);
        }
        
        if( !(image.getWidth() == DIMENSION && image.getHeight() == DIMENSION) ){
            throw new IllegalArgumentException("L'image transmise (" + image.getWidth() + "x" + image.getHeight() + ") n'est pas une image carrée 256x256");
        }
        
        this.xTile = xTile;
        this.yTile = yTile;
        
        this.xOSM = xTile * DIMENSION;
        this.yOSM = yTile * DIMENSION;
        
        this.zoom = zoom;
        this.image = image;
    }
    
    /**
     * Retourne l'image de la tuile.
     * 
     * @return L'image de la tuile.
     */
    public BufferedImage getBufferedImage(){
        return image;
    }
    
    /**
     * Retourne la coordonnée X de la tuile.
     * 
     * @return La coordonnée X de la tuile.
     */
    public int getTileX(){
        return xTile;
    }
    
    /**
     * Retourne la coordonnée Y de la tuile.
     * 
     * @return La coordonnée Y de la tuile.
     */
    public int getTileY(){
        return yTile;
    }
    
    /**
     * Retourne la coordonnée X de la tuile en coordonnée OSM.
     * 
     * @return La coordonnée X de la tuile en coordonnée OSM.
     */
    public int getOSMx(){
        return xOSM;
    }
    
    /**
     * Retourne la coordonnée Y de la tuile en coordonnée OSM.
     * 
     * @return La coordonnée Y de la tuile en coordonnée OSM.
     */
    public int getOSMy(){
        return yOSM;
    }
    
    /**
     * Retourne le niveau de zoom de la tuile.
     * 
     * @return La coordonnée Y de la tuile.
     */
    public int getZoom(){
        return zoom;
    }
}
