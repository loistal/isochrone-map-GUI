package ch.epfl.isochrone.tiledmap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import ch.epfl.isochrone.geo.PointOSM;
import ch.epfl.isochrone.timetable.FastestPathTree;
import ch.epfl.isochrone.timetable.Stop;

/**
 * Modélise un fournisseur de tuiles pour carte isochrone.
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public final class IsochroneTileProvider implements TileProvider{
    private final ColorTable colorTable;
    private final FastestPathTree pathTree;
    private final double walkingSpeed;
    
    /**
     * Construit un fournisseur de tuiles pour carte isochrone étant donnée un arbre des trajets les plus rapides, 
     * une table de couleur et une vitesse de marche à pied.
     * 
     * @param pathTree L'arbre des trajets les plus rapides.
     * @param colorTable La table de couleur.
     * @param walkingSpeed La vitesse de marche à pied.
     */
    public IsochroneTileProvider(FastestPathTree pathTree, ColorTable colorTable, double walkingSpeed){
        this.pathTree = pathTree;
        this.colorTable = colorTable;
        this.walkingSpeed = walkingSpeed;
    }

    @Override
    public Tile tileAt(int zoom, int x, int y) {
        
        int tileDimension = Tile.DIMENSION;
        
        BufferedImage tileIMG = new BufferedImage(tileDimension, tileDimension, BufferedImage.TYPE_INT_ARGB);
        Graphics2D tileGraphics = tileIMG.createGraphics();

        // Remplit avec un fond noir
        tileGraphics.setColor(Color.BLACK);
        tileGraphics.fillRect(0, 0, tileIMG.getWidth(), tileIMG.getHeight());
        
        // On récupère l'heure de départ maintenant au lieu de le faire à chaque parcours de boucle (tranche de couleurs).
        int startingTime = pathTree.startingTime();

        // Parcours de la ColorTable à l'envers, on dessine de la plus grande tranche à la plus petite
        for(int i=colorTable.getNumberOfTranches(); i >= 1; --i){
            
            Color trancheColor = colorTable.getTrancheColor(i);
            int trancheLength = colorTable.getTrancheLengthOf(i);
            
            tileGraphics.setColor(trancheColor);

            /* Algorithme parcours
            1: pour chaque arrêt atteignable A :
            2:   T = M - (H(A) - Hd)
            3:   si T > 0 :
            4:     R = distance, sur la carte, faisable à pied en un temps T
            5:     dessiner un disque centré en P(A), de couleur C et rayon R
             */
            for(Stop stop: pathTree.stops()){

                int walkingTimeLeft = trancheLength - (pathTree.arrivalTime(stop) - startingTime);
                
                if(walkingTimeLeft > 0){

                    // Résultat en mètre
                    double circleRadius = walkingSpeed * walkingTimeLeft;
                    
                    /* Il faut mettre à l'échelle (pixel) le rayon en mètre. Une carte OSM signifie que le cercle à dessiner n'est pas 
                       forcément un cercle parfait. Il faut alors calculer la mise à l'échelle pour une distance horizontale (x) et verticale (y) séparement.
                       L'idée ici est de prendre un point à droite et un point en bas pour calculer le rapport.
                    */
                    PointOSM currentStopOSM = stop.position().toOSM(zoom);
                    PointOSM right1px = new PointOSM(zoom, currentStopOSM.x() + 1, currentStopOSM.y());
                    PointOSM bottom1px = new PointOSM(zoom, currentStopOSM.x(), currentStopOSM.y() + 1);

                    double distanceMetersHorizontal = stop.position().distanceTo(right1px.toWGS84());
                    double distanceMetersVertical = stop.position().distanceTo(bottom1px.toWGS84());
                    
                    // On fait * 2, car le calcul nous donne un résultat pour un rayon et on a besoin d'un diamètre pour dessiner le cercle
                    double circleDiameterX = (circleRadius/distanceMetersHorizontal) * 2;
                    double circleDiameterY = (circleRadius/distanceMetersVertical) * 2;

                    // Position X et Y de notre stop relatif à la tuile Isochrone qu'on veut dessiner (système de coordonné "local"). 
                    double stopPositionTileX = currentStopOSM.x() - x*tileDimension;
                    double stopPositionTileY = currentStopOSM.y() - y*tileDimension;
                    
                    /* Dessinons le cercle seulement si ce dernier peut être dessiné à l'intérieur de notre tuile: 
                       cela nous évitera de prendre de la mémoire pour créer temporairement une instance de cercle lorsqu'il ne peut de toute façon pas être dessiné.
                       Pour savoir si on doit dessiner un cercle, on prend en compte le rayon du cercle: un point en dehors de la tuile peut avoir un dessin sur la tuile selon le rayon. */
                    if( (stopPositionTileX + circleDiameterX > 0 && stopPositionTileX - circleDiameterX < tileDimension ) && 
                        (stopPositionTileY + circleDiameterY > 0 && stopPositionTileY - circleDiameterY < tileDimension ) ){
                        
                        // On soustrait la moitié du diamètre pour le positionnement pour avoir un cercle centrée sur le point
                        tileGraphics.fill(new Ellipse2D.Double(stopPositionTileX - (circleDiameterX/2), stopPositionTileY - (circleDiameterY/2), circleDiameterX, circleDiameterY));
                    }
                }
            }
        }

        return new Tile(x, y, zoom, tileIMG);
    }
}
