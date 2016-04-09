package ch.epfl.isochrone.tiledmap;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Modélise un « cache » de tuiles, c-à-d une table associative associant des tuiles à leurs coordonnées.
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
@SuppressWarnings("serial")
final class TileCache extends LinkedHashMap<Long, Tile>{
        
    // Taille maximale du cache
    private static final int MAX_SIZE = 100;
    
    // Valeur maximale possible pour X et Y au niveau de zoom maximum ( =2^20 )
    private static final int MAX_XY = (int) java.lang.Math.pow(2, 20);
    
    private final LinkedHashMap<Long, Tile> cache;
     
    /**
     * Construit un « cache » de tuiles.
     */
    public TileCache(){
        cache = new LinkedHashMap<Long, Tile>() {

            @Override
            protected boolean removeEldestEntry(Map.Entry<Long,Tile> e){
                return size() > MAX_SIZE;
            }
        };
    }
    
    /**
     * Ajoute au cache une association entre les coordonnées et la tuile.
     * 
     * @param zoom Le zoom.
     * @param x Coordonnée X.
     * @param y Coordonnée Y.
     * @param tile La tuile.
     */
    void put(int zoom, int x, int y, Tile tile){
        cache.put(packCoordinates(zoom, x, y), tile);
    }
    
    /**
     * Retourne la tuile associée aux coordonnées données, ou null si celle-ci n'est pas présente dans le cache — 
     * soit parce qu'elle n'y a jamais été ajoutée, soit parce qu'elle a été supprimée après que le cache ait atteint sa taille maximale.
     * 
     * @param zoom Le zoom.
     * @param x Coordonnée X.
     * @param y Coordonnée Y.
     * @return La tuile associée aux coordonnées données, ou null si celle-ci n'est pas présente dans le cache.
     */
    Tile get(int zoom, int x, int y){
        return cache.get(packCoordinates(zoom, x, y));
    }
    
    /**
     * Encode les coordonnées (zoom, x, y) dans un type long.<br/>
     * Format de l'encodage: x*1'000'000'000 + y + zoom <br/>
     * Taille maximale de l'encodage: 1'048'576'104'857'620 (1048576|1048576|20 <=> x|y|zoom)
     * 
     * @param zoom Le zoom.
     * @param x Coordonnée X.
     * @param y Coordonnée Y.
     * @throws IllegalArgumentException Si le zoom n'est pas inclu dans l'intervalle [0; 20] ou 
     *                                  si les coordonnées X et Y ne se situent pas dans l'intervalle [0; 1048576]
     * @return Les coordonnées (zoom, x, y) encodées dans un type long.
     */
    private long packCoordinates(int zoom, int x, int y){
        if(zoom < 0 || zoom > 20){
            throw new IllegalArgumentException("Le zoom (=" + zoom + ") doit être dans l'intervalle [0; 20]");
        }
        else if(x < 0 || x > MAX_XY){
            throw new IllegalArgumentException("La coordonée x (=" + x + ") doit être dans l'intervalle [0; 1048576]");   
        }
        else if(y < 0 || y > MAX_XY){
            throw new IllegalArgumentException("La coordonée y (=" + y + ") doit être dans l'intervalle [0; 1048576]");   
        }
        else{
            return x*1000000000 + y*100 + zoom;
        }
    }
}
