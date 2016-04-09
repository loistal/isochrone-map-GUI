package ch.epfl.isochrone.tiledmap;

import java.awt.image.BufferedImage;

/**
 * Représente un transformateur de fournisseur de tuiles qui transforme l'image des tuiles de son fournisseur sous-jacent, pixel par pixel.
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public abstract class FilteringTileProvider implements TileProvider{

    private final TileProvider tileProvider;
    
    /**
     * Construit un transformateur du fournisseur de tuiles spécifié en paramètre.
     * 
     * @param tileProvider Le fournisseur de tuiles.
     */
    public FilteringTileProvider(TileProvider tileProvider){
        this.tileProvider = tileProvider;
    }
    
    /**
     * Transforme la couleur passée en argument.
     * 
     * @param argb La couleur du pixel sous forme ARGB
     * @return La couleur transformée du pixel.
     */
    abstract public int transformARGB(int argb);

    @Override
    public Tile tileAt(int zoom, int x, int y) {        
        BufferedImage tempImg = tileProvider.tileAt(zoom, x, y).getBufferedImage();
        
        for(int i=0; i < tempImg.getWidth(); ++i){
            for(int j=0; j < tempImg.getHeight(); ++j){
                tempImg.setRGB(i, j, transformARGB( tempImg.getRGB(i, j) ));
            }
        }
        
        return new Tile(x, y, zoom, tempImg);
    }
}
