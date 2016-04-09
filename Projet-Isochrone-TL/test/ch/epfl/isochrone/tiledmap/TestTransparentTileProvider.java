package ch.epfl.isochrone.tiledmap;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test de la classe TransparentTileProvider
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public class TestTransparentTileProvider {

    @Test (expected = IllegalArgumentException.class)
    public void testConstructorOpacityTooLow(){
        new TransparentTileProvider(new OSMTileProvider("http://b.tile.openstreetmap.org/"), -0.1);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testConstructorOpacityTooHigh(){
        new TransparentTileProvider(new OSMTileProvider("http://b.tile.openstreetmap.org/"), 1.1);
    }
    
    @Test
    public void testTransformARGB(){
        TransparentTileProvider transpTileProvider = new TransparentTileProvider(new OSMTileProvider("http://b.tile.openstreetmap.org/"), 0);
        
        // Le canal alpha devrait être complètement supprimé, c'est-à-dire à 0
        assertTrue( 0x00FFFFFF == transpTileProvider.transformARGB(0xFFFFFFFF) );
    }
}
