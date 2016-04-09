package ch.epfl.isochrone.tiledmap;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

/**
 * Test de la classe OSMTileProvider
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public class TestOSMTileProvider {

    @Test (expected = IllegalArgumentException.class)
    public void testEmptyServerString() throws IOException{
        new OSMTileProvider("");
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testInvalidServerString() throws IOException{
        new OSMTileProvider("http://www.epfl.ch/");
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testConstructorInvalidURL() throws IOException{
        URL serverUrl = new URL("http://www.epfl.ch/");
        new OSMTileProvider(serverUrl);
    }
}
