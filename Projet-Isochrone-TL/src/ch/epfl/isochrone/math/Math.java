package ch.epfl.isochrone.math;

import static java.lang.Integer.signum;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 * Offre des méthodes statiques permettant de calculer des fonctions mathématiques diverses qui n'existent pas dans la bibliothèque standard Java.
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public final class Math {

    private Math(){}
    
    /**
     * Calcul le sinus hyperbolique inverse de son argument.
     * 
     * @param x Argument.
     * @return Le sinus hyperbolique inverse de son argument.
     */
    public static double asinh(double x){
        return log(x + sqrt( (1 + pow(x, 2) )));
    }
    
    /**
     * Calcul sin(x/2)^2 , x étant l'argument.
     * 
     * @param x Argument.
     * @return Le résultat de sin(x/2)^2.
     */
    public static double haversin(double x){
        return pow(sin(x/2), 2);
    }

    /**
     * Retourne le quotient de la division par défaut de n par d.
     * 
     * @param n Numérateur.
     * @param d Dénominateur.
     * @return Le quotient de la division par défaut de n par d.
     */
    public static int divF(int n, int d){
        return ((n/d) - I(n%d,d));
        
    }
    
    /**
     * Retourne le reste de la division par défaut de n par d.
     * 
     * @param n Numérateur.
     * @param d Dénominateur.
     * @return Le reste de la division par défaut de n par d.
     */
    public static int modF(int n, int d){
        return((n%d) + I(n%d, d)*d);
    }
    
    /**
     * Formule simple utilisée pour les calculs des méthodes "divF" et "modF".
     * 
     * @param rt Reste de la division tronquée.
     * @param d Dénominateur.
     * @return I Retourne 1 si signum(rt) = - signum(d), ou 0 sinon.
     */
    private static int I(int rt, int d){
        return (signum(rt) == -signum(d)) ? 1 : 0;
    }
}
