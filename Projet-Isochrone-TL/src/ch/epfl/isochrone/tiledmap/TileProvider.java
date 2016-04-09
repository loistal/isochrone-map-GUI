package ch.epfl.isochrone.tiledmap;

/**
 * Représente un fournisseur de tuile.
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public interface TileProvider {
    
    /**
     * Fournit la tuile de coordonnées données.
     * 
     * @param zoom Le niveau de Zoom.
     * @param x Coordonnée X.
     * @param y Coordonnée Y.
     * @return La tuile de coordonnées données.
     */
    public Tile tileAt(int zoom, int x, int y);
}