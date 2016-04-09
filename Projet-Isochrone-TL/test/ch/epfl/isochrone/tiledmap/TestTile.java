package ch.epfl.isochrone.tiledmap;

import static org.junit.Assert.assertEquals;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.junit.Test;

/**
 * Test de la classe Tile
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public class TestTile {

    @Test (expected = IllegalArgumentException.class)
    public void testZoomTooLow() throws IOException{
        BufferedImage image = new BufferedImage(Tile.DIMENSION, Tile.DIMENSION, BufferedImage.TYPE_INT_ARGB);
        
        new Tile(0, 0, -1, image);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testZoomTooHigh() throws IOException{
        BufferedImage image = new BufferedImage(Tile.DIMENSION, Tile.DIMENSION, BufferedImage.TYPE_INT_ARGB);
        
        new Tile(0, 0, 20, image);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testXtooHigh() throws IOException{
        BufferedImage image = new BufferedImage(Tile.DIMENSION, Tile.DIMENSION, BufferedImage.TYPE_INT_ARGB);
        
        new Tile(2, 1, 0, image);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testYtooHigh() throws IOException{
        BufferedImage image = new BufferedImage(Tile.DIMENSION, Tile.DIMENSION, BufferedImage.TYPE_INT_ARGB);
        
        new Tile(1, 2, 0, image);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testXtooLow() throws IOException{
        BufferedImage image = new BufferedImage(Tile.DIMENSION, Tile.DIMENSION, BufferedImage.TYPE_INT_ARGB);
        
        new Tile(-1, 1, 0, image);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testYtooLow() throws IOException{
        BufferedImage image = new BufferedImage(Tile.DIMENSION, Tile.DIMENSION, BufferedImage.TYPE_INT_ARGB);
        
        new Tile(1, -1, 0, image);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testIncorrectDimensionX() throws IOException{
        BufferedImage image = new BufferedImage(1, Tile.DIMENSION, BufferedImage.TYPE_INT_ARGB);
        
        new Tile(1, 1, 0, image);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testIncorrectDimensionY() throws IOException{
        BufferedImage image = new BufferedImage(Tile.DIMENSION, 1, BufferedImage.TYPE_INT_ARGB);
        
        new Tile(1, 1, 0, image);
    }
    
    @Test
    public void testGetTileXY() throws IOException{
        BufferedImage image = new BufferedImage(Tile.DIMENSION, Tile.DIMENSION, BufferedImage.TYPE_INT_ARGB);
        
        Tile tile = new Tile(1, 1, 0, image);
        
        assertEquals(1, tile.getTileX());
        assertEquals(1, tile.getTileY());
    }
    
    @Test
    public void testGetOSMxy() throws IOException{
        BufferedImage image = new BufferedImage(Tile.DIMENSION, Tile.DIMENSION, BufferedImage.TYPE_INT_ARGB);
        
        Tile tile = new Tile(1, 1, 0, image);
        
        assertEquals(1 * Tile.DIMENSION, tile.getOSMx());
        assertEquals(1 * Tile.DIMENSION, tile.getOSMy());
    }
    
    @Test
    public void testGetZoom() throws IOException{
        BufferedImage image = new BufferedImage(Tile.DIMENSION, Tile.DIMENSION, BufferedImage.TYPE_INT_ARGB);
        
        Tile tile = new Tile(1, 1, 0, image);
        
        assertEquals(0, tile.getZoom());
    }
}
