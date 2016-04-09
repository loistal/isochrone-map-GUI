package ch.epfl.isochrone.tiledmap;

import static java.lang.Math.round;

import java.awt.Color;
import java.util.List;

/**
 * Modélise une table de couleurs à utiliser pour dessiner une carte isochrone.
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public final class ColorTable {

    /**
     * Convertit un quadruplet (alpha, red, green, blue), chaque valeur étant comprise entre 0 et 1, en une instance de la classe Color.
     * 
     * @param alpha Valeur alpha.
     * @param red Valeur rouge.
     * @param green Valeur verte.
     * @param blue Valeur bleu.
     * @throws IllegalArgumentException Si la valeur alpha n'est pas comprise entre [0; 1] ou
     *                                  si la valeur red n'est pas comprise entre [0; 1] ou
     *                                  si la valeur green n'est pas comprise entre [0; 1] ou
     *                                  si la valeur blue n'est pas comprise entre [0; 1].
     * @return Une instance de la classe Color.
     */
    public static Color convertIntToColor(double alpha, double red, double green, double blue){
        if(alpha < 0 || alpha > 1){
            throw new IllegalArgumentException("La valeur alpha (=" + alpha + ") n'est pas comprise dans l'intervalle [0; 1]");
        }
        else if(red < 0 || red > 1){
            throw new IllegalArgumentException("La valeur rouge (=" + alpha + ") n'est pas comprise dans l'intervalle [0; 1]");
        }
        else if(green < 0 || green > 1){
            throw new IllegalArgumentException("La valeur verte (=" + alpha + ") n'est pas comprise dans l'intervalle [0; 1]");
        }
        else if(blue < 0 || blue > 1){
            throw new IllegalArgumentException("La valeur bleu (=" + alpha + ") n'est pas comprise dans l'intervalle [0; 1]");
        }
        
        return new Color( (int) round(red * 255), (int) round(green * 255), (int) round(blue * 255), (int) round(alpha * 255));
    }

    /**
     * Convertit un triplet (red, green, blue), chaque valeur étant comprise entre 0 et 1, en une instance de la classe Color.
     *
     * @param red Valeur rouge.
     * @param green Valeur verte.
     * @param blue Valeur bleu.
     * @throws IllegalArgumentException Si la valeur red n'est pas comprise entre [0; 1] ou
     *                                  si la valeur green n'est pas comprise entre [0; 1] ou
     *                                  si la valeur blue n'est pas comprise entre [0; 1].
     * @return Une instance de la classe Color.
     */
    public static Color convertIntToColor(double red, double green, double blue){
        return convertIntToColor(1, red, green, blue);
    }

    private final int trancheLength;
    private final List<Color> colorList;
    
    /**
     * Construit une table de couleurs à partir d'une durée des tranches et d'une liste de couleurs.
     * 
     * @param trancheLength La durée (en secondes) des tranches.
     * @param colorList La liste de couleurs (doit contenir au moins un élément).
     * @throws IllegalArgumentException Si la durée des tranches est égal ou plus petite que 0 ou
     *                                  si la liste des couleurs est vide.
     */
    public ColorTable(int trancheLength, List<Color> colorList){
        
        if(colorList.isEmpty()){
            throw new IllegalArgumentException("La liste des couleurs est vide.");
        }
        if(trancheLength <= 0){
            throw new IllegalArgumentException("La durée des tranches (=" + trancheLength + ") ne peut être égal ou plus petite que 0.");
        }
        
        this.trancheLength = trancheLength;
        this.colorList = java.util.Collections.unmodifiableList(colorList);
    
    }
    
    /**
     * Permet d'obtenir la durée de la tranche associée à une tranche.
     * 
     * @param trancheNumber Le numéro de tranche. L'index commence à 1.
     * @throws IllegalArgumentException Si le numéro de tranche qu'on veut accéder est plus petit que 1 ou plus grand que le nombre de tranches total.
     * @return La durée de la tranche associée à la tranche.
     */
    public int getTrancheLengthOf(int trancheNumber){
        
        if(trancheNumber < 1 || trancheNumber > getNumberOfTranches()){
            throw new IllegalArgumentException("Vous essayez d'accéder à la tranche n°" + trancheNumber + " alors qu'il y a " + getNumberOfTranches() + " tranches.");
        }
        
        return trancheLength * trancheNumber;
        
    }
    
    /**
     * Permet d'obtenir le nombre de tranches total.
     * 
     * @return Le nombre de tranches.
     */
    public int getNumberOfTranches(){
        
        return colorList.size();
        
    }
    
    /**
     * Permet d'obtenir la couleur associée à une tranche.
     * 
     * @param trancheNumber Le numéro de la tranche dont laquelle on veut la couleur. L'index commence à 1.
     * @throws IllegalArgumentException Si le numéro de tranche qu'on veut accéder est plus petit que 1 ou plus grand que le nombre de tranches total.
     * @return La couleur associée à la tranche.
     */
    public Color getTrancheColor(int trancheNumber){
        
        if(trancheNumber < 1 || trancheNumber > getNumberOfTranches()){
            throw new IllegalArgumentException("La tranche n° " + trancheNumber + " est plus petit que 1 ou plus grand que le nombre de tranches total (=" + getNumberOfTranches() + ").");
        }
        
        return colorList.get(trancheNumber - 1);
    
    }
    
}