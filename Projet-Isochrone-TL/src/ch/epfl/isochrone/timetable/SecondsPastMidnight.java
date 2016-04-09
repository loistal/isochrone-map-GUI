package ch.epfl.isochrone.timetable;

/**
 * Représentation des heures.
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public final class SecondsPastMidnight {

    public static final int INFINITE = 200000;

    private SecondsPastMidnight(){}

    /**
     * Convertit un triplet (heure, minutes, secondes) en un nombre de secondes après minuit.
     * 
     * @param hours L'heure.
     * @param minutes Les minutes.
     * @param seconds Les secondes.
     * @throws IllegalArgumentException Si l'une des trois valeurs est invalide. 
     *                                  Les secondes et les minutes sont valides si elles sont comprises dans l'intervalle [0;60[. 
     *                                  Quant aux heures, elles sont valides si elles sont comprises dans l'intervalle [0;30[.
     * @return Le nombre de secondes après minuit correspondant au triplet (heure, minutes, secondes).
     */
    public static int fromHMS(int hours, int minutes, int seconds){
        if(hours<0 || hours>=30){
            throw new IllegalArgumentException ("Les heures (=" + hours + ") sont invalides. Une valeur parmis l'intervalle [0;30[ est attendue.");
        }
        else if(minutes<0 || minutes>=60){
            throw new IllegalArgumentException ("Les minutes (=" + minutes + ")  sont invalides. Une valeur parmis l'intervalle [0;60[ est attendue.");
        }
        else if(seconds<0 || seconds>=60){
            throw new IllegalArgumentException ("Les secondes (=" + seconds + ") sont invalides.  Une valeur parmis l'intervalle [0;60[ est attendue.");
        }
        
        return (hours*3600) + (minutes*60) + seconds;
    }


    /**
     * Convertit l'heure d'une date Java (une instance de java.util.Date) en un nombre de secondes après minuit.
     * 
     * @param date La date Java (une instance de java.util.Date).
     * @return Le nombre de secondes après minuit correspondant à la date Java (une instance de java.util.Date).
     */
    @SuppressWarnings("deprecation")
    public static int fromJavaDate(java.util.Date date){
        return (date.getHours()*3600) + (date.getMinutes()*60) + (date.getSeconds());
    }

    /**
     * Retourne le nombre d'heures de l'heure (représentée par un nombre de secondes après minuit) passée en argument.
     * 
     * @param spm L'heure en nombre de secondes après minuit.
     * @throws IllegalArgumentException Si le nombre de secondes après minuit passé en argument est négatif ou représente une heure au-delà de 29 h 59 min 59 s.
     * @return Le nombre d'heures de l'heure.
     */
    public static int hours(int spm){
        if(spm<0){
            throw new IllegalArgumentException("L'heure ne peut être négatif.");
        }
        else if(spm>107999){
            throw new IllegalArgumentException("L'heure ne peut être supérieure à 29h 59m 59s.");
        }
        else{
            return (spm/3600);
        }
    }

    /**
     * Retourne le nombre de minutes de l'heure (représentée par un nombre de secondes après minuit) passée en argument.
     * 
     * @param spm L'heure en nombre de secondes après minuit.
     * @throws IllegalArgumentException Si le nombre de secondes après minuit passé en argument est négatif ou représente une heure au-delà de 29 h 59 min 59 s.
     * @return Le nombre de minutes de l'heure.
     */
    public static int minutes(int spm){
        if(spm<0){
            throw new IllegalArgumentException("L'heure ne peut être négatif.");
        }
        else if(spm>107999){
            throw new IllegalArgumentException("L'heure ne peut être supérieure à 29h 59m 59s.");
        }
        else{
         /* 0. L'heure est exprimé en secondes.
            1. (spm / 60) retourne le nombre de minutes de l'heure exprimé en secondes.
            2. (spm / 60) / 60 retourne le nombre d'heures de la valeur précédente avec un reste représentant les
               minutes restantes. De ce fait on utilise le modulo pour récupérer ces minutes. */
            return (spm / 60) % 60;
        }
    }

    /**
     * Retourne le nombre de secondes de l'heure (représentée par un nombre de secondes après minuit) passée en argument.
     * 
     * @param spm L'heure en nombre de secondes après minuit.
     * @throws IllegalArgumentException Si le nombre de secondes après minuit passé en argument est négatif ou représente une heure au-delà de 29 h 59 min 59 s.
     * @return Le nombre de secondes de l'heure.
     */
    public static int seconds(int spm){  
        if(spm<0){
            throw new IllegalArgumentException("L'heure ne peut être négatif.");
        }
        else if(spm>107999){
            throw new IllegalArgumentException("L'heure ne peut être supérieure à 29h 59m 59s.");
        }
        else{
         /* 0. L'heure est exprimé en secondes.
            1. (spm / 60) retourne le nombre de minutes de l'heure exprimé en secondes avec un reste représentant les
               secondes restantes. De ce fait on utilise le modulo pour récupérer ces secondes. */
            return (spm % 60);
        }
    }

    /**
     * Retourne la représentation textuelle du nombre de secondes après minuit passé en argument. Cette représentation consiste en un nombre d'heures, de minutes et de secondes, chacun représenté par deux chiffres, séparés par un double point (:).
     * 
     * @param spm Le nombre de secondes après minuit.
     * @throws IllegalArgumentException Si le nombre de secondes après minuit passé en argument est négatif ou représente une heure au-delà de 29 h 59 min 59 s.
     * @return La représentation textuelle du nombres de secondes après minuit.
     */
    public static String toString(int spm){
        if(spm<0){
            throw new IllegalArgumentException("L'heure ne peut être négatif.");
        }
        else if(spm>107999){
            throw new IllegalArgumentException("L'heure ne peut être supérieure à 29h 59m 59s.");
        }
        else{
            return String.format("%02d:%02d:%02d", hours(spm), minutes(spm), seconds(spm));
        }
    }

}



