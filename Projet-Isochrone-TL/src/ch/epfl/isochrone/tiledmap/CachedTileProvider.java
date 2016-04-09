package ch.epfl.isochrone.tiledmap;

/**
 * Modélise un transformateur de fournisseur de tuiles qui garde en mémoire un certain nombre de tuiles afin d'accélérer leur obtention.
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public final class CachedTileProvider implements TileProvider{
    private final TileProvider tileProvider;
    private final TileCache cache;

    /**
     * Construit un transformateur de fournisseur de tuiles qui garde en mémoire un certain nombre de tuiles du fournisseur de tuiles 
     * spécifié en paramètre.
     * 
     * @param tileProvider Le fournisseur de tuiles.
     */
    public CachedTileProvider(TileProvider tileProvider){
        this.tileProvider = tileProvider;
        cache = new TileCache();
    }

    @Override
    public Tile tileAt(int zoom, int x, int y) {
        Tile cachedTile = cache.get(zoom, x, y);
        
        // Si le cache ne possède pas la tuile voulue, alors on l'ajoute au cache
        if(cachedTile == null){
            Tile tile = tileProvider.tileAt(zoom, x, y);
            cache.put(zoom, x, y, tile);
            
            return tile;
        }
        else{
            return cachedTile;
        }
    }
}
