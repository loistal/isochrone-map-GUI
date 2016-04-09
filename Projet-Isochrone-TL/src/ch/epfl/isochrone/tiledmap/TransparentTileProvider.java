package ch.epfl.isochrone.tiledmap;


/**
 * Modélise un transformateur de fournisseur de tuiles qui transforme l'opacité de l'image des tuiles de son fournisseur sous-jacent, 
 * pixel par pixel.
 * 
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public final class TransparentTileProvider extends FilteringTileProvider{

    private final double opacity;
    
    /**
     * Construit transformateur de fournisseur de tuiles qui transforme l'opacité de l'image des tuiles de son fournisseur sous-jacent, 
     * pixel par pixel, en prenant en paramètre le fournisseur de tuiles et l'opacité souhaité.
     * @param tileProvider Le fournisseur de tuiles.
     * @param opacity L'opacité à appliquer (valeur entre 0 compris et 1 compris).
     * @throws IllegalArgumentException Si l'ocapité n'est pas comprise dans l'intervalle [0;1].
     */
    public TransparentTileProvider(TileProvider tileProvider, double opacity){
       super(tileProvider);
       
       if( !(opacity <= 1 && opacity >= 0) ){
           throw new IllegalArgumentException("L'opacité (=" + opacity + ") n'est pas comprise dans l'intervalle [0;1].");
       }
       
       this.opacity = opacity;
    }
    
    /**
     * Change l'opacité de la couleur passée en argument par l'opacité donnée lors de la construction du transformateur de fournisseur. 
     * 
     * @param argb La couleur initiale sous forme ARGB.
     * @return La couleur avec une différente opacité.
     */
    @Override
    public int transformARGB(int argb) {
      int newAlpha = (int) java.lang.Math.round(opacity * 255);
      
      // Shift du canal alpha tout à gauche et "concaténation" avec les valeurs rgb
      return (newAlpha << 24) | (argb & 0x00FFFFFF);
        
    }
 
}
