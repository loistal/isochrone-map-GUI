package ch.epfl.isochrone.tiledmap;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.util.ArrayList;

import org.junit.Test;

import ch.epfl.isochrone.timetable.SecondsPastMidnight;

/**
 * Test de la classe ColorTable
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public class TestColorTable {
    
    private final int trancheLength = SecondsPastMidnight.fromHMS(0, 5, 0);

    @Test
    public void testConvertIntToColor(){
        Color aColor = ColorTable.convertIntToColor(0.5, 1, 0.3, 0.7);
        
        assertEquals( 128, aColor.getAlpha() ); // 128 = 0.5 * 255 (arrondi)
        assertEquals( 255, aColor.getRed() );   // 255 = 1 * 255   (arrondi)
        assertEquals( 77, aColor.getGreen() );  // 77  = 0.3 * 255 (arrondi)
        assertEquals( 179, aColor.getBlue() );  // 179 = 0.7 * 255 (arrondi)
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testColorListEmpty(){
        ArrayList<Color> colorList = new ArrayList<Color>();
        
        new ColorTable(trancheLength, colorList);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testTrancheLengthTooLow(){
        ArrayList<Color> colorList = new ArrayList<Color>();
        colorList.add(ColorTable.convertIntToColor(1, 0, 0));
        
        new ColorTable(0, colorList);
    }
    
    @Test
    public void testTrancheLengthOf(){
        ArrayList<Color> colorList = new ArrayList<Color>();
        colorList.add(ColorTable.convertIntToColor(1, 0, 0));      // rouge
        colorList.add(ColorTable.convertIntToColor(1, 0.5, 0));    // orange
                
        ColorTable colorTable = new ColorTable(trancheLength, colorList);
        
        // trancheLength = 300 seconds, 600 = 300 * 2 
        assertEquals(600, colorTable.getTrancheLengthOf(2));
    }
    
    @Test
    public void testNumberOfTranches(){
        ArrayList<Color> colorList = new ArrayList<Color>();
        colorList.add(ColorTable.convertIntToColor(1, 0, 0));      // rouge
        colorList.add(ColorTable.convertIntToColor(1, 0.5, 0));    // orange
                
        ColorTable colorTable = new ColorTable(trancheLength, colorList);
        
        assertEquals(2, colorTable.getNumberOfTranches());
    }
    
    @Test
    public void testTrancheColor(){
        Color aColor = ColorTable.convertIntToColor(1, 1, 0, 0); // rouge
        
        ArrayList<Color> colorList = new ArrayList<Color>();
        colorList.add(aColor);
                
        ColorTable colorTable = new ColorTable(trancheLength, colorList);
        Color colorBack = colorTable.getTrancheColor(1);
        
        assertEquals(255, colorBack.getAlpha());
        assertEquals(255, colorBack.getRed());
        assertEquals(0, colorBack.getGreen());
        assertEquals(0, colorBack.getBlue());
    }
}
