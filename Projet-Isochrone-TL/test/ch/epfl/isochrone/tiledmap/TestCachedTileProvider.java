package ch.epfl.isochrone.tiledmap;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

/**
 * Test de la classe CachedTileProvider
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public class TestCachedTileProvider {

    @Test
    public void testCachedTile() throws IOException{
        CachedTileProvider tileProviderOSM = new CachedTileProvider(new OSMTileProvider(new URL("http://b.tile.openstreetmap.org/")));
        
        Tile tile = tileProviderOSM.tileAt(17, 67927, 46357);
        
        // Si la tuile est bien en cache, la référence doit être la même
        assertTrue( tile.equals(tileProviderOSM.tileAt(17, 67927, 46357)) );
    }
}
