package ch.epfl.isochrone.tiledmap;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * Modélise un fournisseur de tuiles qui obtient ces dernières depuis un serveur utilisant les conventions de nommage des tuiles 
 * du projet OpenStreetMap.
 * 
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public final class OSMTileProvider implements TileProvider{

    private final String baseAddress;
   
    /**
     * Construit un fournisseur de tuiles OpenStreetMap en prenant en paramètre l'adresse du serveur au format String. Les deux adresses 
     * serveurs autorisés sont: http://a.tile.openstreetmap.org/ et http://b.tile.openstreetmap.org/ .
     * 
     * @param baseAddress L'adresse du serveur.
     * @throws IllegalArgumentException Si l'adresse du serveur n'est aucune des adresses connues.
     */
    public OSMTileProvider(String baseAddress){
        
        // On vérifie que l'adresse est une des deux adresses serveurs connues
        if( !baseAddress.equals("http://a.tile.openstreetmap.org/") && !baseAddress.equals("http://b.tile.openstreetmap.org/") ){
            throw new IllegalArgumentException("L'adresse " + baseAddress + " n'est pas une adresse de serveur valide.");
        }
        
        this.baseAddress = baseAddress;   
    }
    
    /**
     * Construit un fournisseur de tuiles OpenStreetMap en prenant en paramètre l'adresse du serveur au format URL. Les deux adresses 
     * serveurs autorisés sont: http://a.tile.openstreetmap.org/ et http://b.tile.openstreetmap.org/ .
     * 
     * @param url L'instance URL de l'adresse du serveur.
     * @throws IllegalArgumentException Si l'adresse du serveur n'est aucune des adresses connues.
     */
    public OSMTileProvider(URL url) {
        this(url.toString());
    }

    @Override
    public Tile tileAt(int zoom, int x, int y){
        
        BufferedImage image = null;
        
        try {
            StringBuilder urlImg = new StringBuilder(baseAddress);
            urlImg.append(zoom).append("/").append(x).append("/").append(y).append(".png");
            
            //On crée l'adresse URL ou se trouve l'image PNG de la tuile
            URL url = new URL(urlImg.toString());
            image = ImageIO.read(url);
        } catch (IOException e) { //Si une erreur se produit, le fournisseur doit produire une tuile dont l'image affiche un message d'erreur

            try {
                image = ImageIO.read(getClass().getResource("/images/error-tile.png"));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            
        }
        
        return new Tile(x, y, zoom, image);
        
    }
    
}
