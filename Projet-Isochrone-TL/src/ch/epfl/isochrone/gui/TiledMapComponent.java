package ch.epfl.isochrone.gui;

import static java.lang.Math.round;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import ch.epfl.isochrone.geo.PointOSM;
import ch.epfl.isochrone.tiledmap.Tile;
import ch.epfl.isochrone.tiledmap.TileProvider;

/**
 * Modélise un composant Swing capable d'afficher une carte en tuiles, ces dernières étant fournies par un ou plusieurs fournisseurs de tuiles.
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
@SuppressWarnings("serial")
public final class TiledMapComponent extends JComponent {    
    public static final int MIN_ZOOM = 10;
    public static final int MAX_ZOOM = 19;
    
    private int zoom;
    private List<TileProvider> tileProviders;
    
    /**
     * Construit un composant Swing capable d'afficher une carte en tuiles en prenant en paramètre le niveau de zoom de la carte à afficher 
     * et la liste des fournisseurs fournissant les tuiles de la carte.
     * 
     * @param zoom Le niveau de zoom de la carte à afficher, compris entre 10 et 19 (inclus).
     * @param tileProviders La liste des fournisseurs fournissant les tuiles de la carte. 
     *                      Les tuiles des différents fournisseurs sont affichées l'une par-dessus l'autre, dans l'ordre des founisseurs
     * @throws IllegalArgumentException Si le niveau de zoom est plus petit que 10 ou plus grand que 19.
     */
    public TiledMapComponent(int zoom, List<TileProvider> tileProviders){
        if(zoom < MIN_ZOOM || zoom > MAX_ZOOM){
            throw new IllegalArgumentException("Le niveau de zoom (=" + zoom + ") n'est pas compris dans l'intervalle [10; 19]");
        }
        this.zoom = zoom;
        this.tileProviders = java.util.Collections.unmodifiableList(tileProviders);
    }
    
    /**
     * Construit un composant Swing capable d'afficher une carte en tuiles en prenant en paramètre le niveau de zoom de la carte à afficher.
     * La liste des fournisseurs fournissant les tuiles de la carte n'étant spécifié avec ce constructeur, la carte sera par défaut "vide" (pas de dessin).
     * 
     * @param zoom Le niveau de zoom de la carte à afficher, compris entre 10 et 19 (inclus).
     * @throws IllegalArgumentException Si le niveau de zoom est plus petit que 10 ou plus grand que 19.
     */
    public TiledMapComponent(int zoom){
        this(zoom, new ArrayList<TileProvider>());
    }
    
    /**
     * Permet de changer les fournisseurs de tuile.
     * 
     * @param newTileProviders La liste des nouveaux fournisseurs de tuile.
     */
    public void setTileProviders(List<TileProvider> newTileProviders){
        tileProviders = java.util.Collections.unmodifiableList(newTileProviders);
    }

    /**
     * Ajoute au niveau de zoom courant un nombre de zoom supplémentaire spécifié en paramètre. Retourne vrai si le nouveau zoom a été appliqué, faux sinon.
     * Si le zoom supplémentaire ammène un nouveau zoom plus bas que la valeur minimale de zoom autorisé, alors le nouveau zoom sera paramétré à ladite valeur minimale autorisé.
     * De manière équivalente, si le zoom supplémentaire ammène un nouveau zoom plus haut que la valeur maximale de zoom autorisé, alors le nouveau zoom sera paramétré à ladite valeur maximale autorisé. 
     * Finalement, si le zoom actuelle se trouve à sa valeur minimale et qu'on souhaite dézoomer: le nouveau zoom ne sera pas appliqué.
     * Si le zoom actuelle se trouve à sa valeur maximale et qu'on souhaite zoomer: le nouveau zoom ne sera pas appliqué.
     * 
     * @param zoom Le zoom à ajouter. Il peut être négatif (dézoom) comme positif (zoom).
     * @return Vrai si le nouveau zoom a été appliqué, faux sinon.
     */
    public boolean addZoom(int zoom){
        int zoomSignum = Integer.signum(zoom);
        
        // Si on veut (dé)zoom alors qu'on est déjà à la limite, ou si on ne zoom pas, alors on ne fait rien
        if( (this.zoom == MIN_ZOOM && zoomSignum == -1) || (this.zoom == MAX_ZOOM && zoomSignum == 1) || zoomSignum == 0){
            return false;
        }

        int newZoom = this.zoom + zoom;

        if(newZoom < MIN_ZOOM){
            this.zoom = MIN_ZOOM;
        }
        else if(newZoom > MAX_ZOOM){
            this.zoom = MAX_ZOOM;
        }
        else{
            this.zoom = newZoom;
        }
        
        return true;
    }
    
    /**
     * Retourne le zoom actuel de la carte.
     * 
     * @return Le zoom actuel de la carte.
     */
    public int zoom(){
        return zoom;
    }

    @Override
    public Dimension getPreferredSize() {
        int size = (int) round( PointOSM.maxXY(zoom) );
        return new Dimension(size, size);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;

        int tileMinX = Tile.convertToTileCoordinate(-getBounds().getX());
        int tileMaxX = Tile.convertToTileCoordinate(-getBounds().getX() + getVisibleRect().getWidth());
        
        int tileMinY = Tile.convertToTileCoordinate(-getBounds().getY());
        int tileMaxY = Tile.convertToTileCoordinate(-getBounds().getY() + getVisibleRect().getHeight());

        for(int i=tileMinX; i <= tileMaxX; ++i){
            for(int j=tileMinY; j <= tileMaxY; ++j){
                
                // Dessine chaque couche de tuiles
                for(int k=0; k < tileProviders.size(); ++k){
                    
                    Tile tile = tileProviders.get(k).tileAt(zoom, i, j);
                    g2d.drawImage(tile.getBufferedImage(), null, tile.getOSMx(), tile.getOSMy());
                    
                }
                
            }
        }
        
        g2d.dispose();
    }
    
}
