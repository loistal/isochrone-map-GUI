package ch.epfl.isochrone.timetable;
import static ch.epfl.isochrone.math.Math.divF;
import static ch.epfl.isochrone.math.Math.modF;


/**
 * Représentation des dates (calendrier grégorien).
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public final class Date implements Comparable<Date> {
    public enum DayOfWeek {MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY};
    public enum Month {JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER};
    
    private final int day;
    private final Month month;
    private final int year;
    
    /**
     * Construit une date du calendrier grégorien étant donnés un jour, un mois et une année.
     * 
     * @param day Le jour.
     * @param month Le mois.
     * @param year L'année.
     * @throws IllegalArgumentException Si le jour est inférieur à 1 ou supérieur au nombre de jours dans le mois donné.
     */
    public Date(int day, Month month, int year){
        if(day < 1){
            throw new IllegalArgumentException("Le jour (=" + day + ") et inférieur à 1");
        }
        else if(day > daysInMonth(month, year)){
            throw new IllegalArgumentException("Le jour (=" + day + ") est supérieur (=" + daysInMonth(month, year) + ") au nombre de jours dans le mois donnée.");
        }
        else{
            this.day = day;
            this.month = month;
            this.year = year;
        }
    }
    
    /**
     * Construit une date du calendrier grégorien étant donnés un jour, un numéro de mois et une année.
     * 
     * @param day Le jour.
     * @param month Le mois.
     * @param year L'année.
     * @throws IllegalArgumentException Si le jour est inférieur à 1 ou supérieur au nombre de jours dans le mois donné,
     *                                  ou si le numéro de mois est hors de l'intervalle [1;12]
     */
    public Date(int day, int month, int year){
        this(day, intToMonth(month), year);
    }
    
    /**
     * Construit une date en fonction d'une date Java (une instance de la classe java.util.Date).
     * 
     * @param date Une instance de la classe java.util.Date .
     */
    @SuppressWarnings("deprecation")
    public Date(java.util.Date date){
        this(date.getDate(), intToMonth(date.getMonth()+1), date.getYear()+1900);
    }
    
    /**
     * Retourne le jour du mois de la date, compris entre 1 et 31.
     * 
     * @return Le jour du mois de la date.
     */
    public int day(){
        return day;
    }
    
    /**
     * Retourne le mois de la date.
     * 
     * @return Le mois de la date.
     */
    public Month month(){
        return month;
    }
    
    /**
     * Retourne le mois de la date sous forme d'entier (1 pour janvier, 2 pour février, etc.).
     * 
     * @return Le mois de la date sous forme d'entier.
     */
    public int intMonth(){
        return monthToInt(month);
    }
    
    /**
     * Retourne l'année de la date.
     * 
     * @return L'année de la date.
     */
    public int year(){
        return year;
    }
    
    /**
     * Retourne le jour de la semaine de la date.
     * 
     * @throws IndexOutOfBoundsException Si, lors du calcul interne, le reste de la division de la date en entier par 7 n'est pas une valeur attendue ( [0;6] - des entiers ).
     * @return Le jour de la semaine de la date.
     */
    public DayOfWeek dayOfWeek(){
        int dateInt = fixed();
        
        /* Une semaine = 7 jours et 01/01/01 est un lundi
           Avec ces constations on divise la date en entier par 7 et les jours seront attribués selon la valeur du reste */
        int dateMod = dateInt % 7;
        
        switch(dateMod){
            case 1:
                return DayOfWeek.MONDAY;
            case 2:
                return DayOfWeek.TUESDAY;
            case 3:
                return DayOfWeek.WEDNESDAY;
            case 4:
                return DayOfWeek.THURSDAY;
            case 5:
                return DayOfWeek.FRIDAY;
            case 6:
                return DayOfWeek.SATURDAY;
            case 0:
                return DayOfWeek.SUNDAY;
            default :
                throw new IndexOutOfBoundsException("Erreur lors de l'attribution du jour. Le reste de la division de la date en entier par 7 n'est pas une valeur attendue. Valeur rencontrée : " + dateMod + ". Valeur attendue : [0;6] (des entiers).");
        }
    }
    
    /**
     * Retourne la date distante du nombre de jours donnés de la date à laquelle on l'applique. 
     * Par exemple, en appelant relative avec le paramètre 1 on obtient la date du lendemain, avec -1 celle du jour précédent, etc.
     * 
     * @param daysDiff Le nombre exprimant la distance.
     * @return La date distante.
     */
    public Date relative(int daysDiff){
        int dateInt = fixed();
        int newDateInt = dateInt + daysDiff;
        
        return fixedToDate(newDateInt);
    }
    
    /**
     * Retourne la date Java (une instance de java.util.Date) correspondant à cette date.
     * 
     * @return La date Java (une instance de java.util.Date) correspondant à cette date.
     */
    @SuppressWarnings("deprecation")
    public java.util.Date toJavaDate(){
        return new java.util.Date(year-1900, monthToInt(month)-1, day);
    }
    
    /**
     * Retourne la représentation textuelle de la date, qui doit être formée de l'année, du numéro du mois (sur deux chiffres) et du jour (sur deux chiffres), séparés par un tiret (-).
     * 
     * @return La représentation textuelle de la date.
     */
    @Override
    public String toString(){
        String d, m;
        
        // Ajoute un 0 au jour/mois s'il est de longueur 1
        d = (Integer.toString(day).length() == 1) ? "0" + day : Integer.toString(day);
        m = (Integer.toString(monthToInt(month)).length() == 1) ? "0" + monthToInt(month) : Integer.toString(monthToInt(month));

        return year + "-" + m + "-" + d;
    }
    
    /**
     * Compare la date à laquelle on l'applique avec l'objet passé en argument.
     * 
     * @return Vrai si et seulement si cet objet est une date (une instance de ch.epfl.isochrone.timetable.Date) désignant le même jour.
     */
    @Override
    public boolean equals(Object that){
        if(that.getClass() != this.getClass()){
            return false;
        }
        else{
            return ((Date)that).year() == year() && ((Date)that).month() == month() && ((Date)that).day() == day();
        }
    }
    
    /**
     * Retourne l'entier correspondant à la date.
     * 
     * @return L'entier correspondant à la date.
     */
    @Override
    public int hashCode(){
        return fixed();
    }
    
    /**
     * Compare la date à laquelle on l'applique avec la date passée en argument.
     * 
     * @param that La date à comparer.
     * @return -1 lorsque la première est strictement inférieure à la seconde, 0 lorsque les deux sont égales, et 1 lorsque la première est strictement supérieure à la seconde.
     */
    @Override
    public int compareTo(Date that){
        int dateInt = fixed();
        int newDateInt = that.fixed();
        
        if(dateInt < newDateInt){
            return -1;
        }
        else if(dateInt == newDateInt){
            return 0;
        }
        else{
            return 1;
        }
    }
    
    /**
     * Transforme un numéro de mois en mois (Month), avec la convention que l'entier 1 correspond au mois de janvier, l'entier 2 au mois de février, et ainsi de suite.
     * 
     * @param m Le numéro de mois.
     * @throws IllegalArgumentException Si le numéro de mois est hors de l'intervalle [1;12]
     * @return Le numéro de mois en mois (Month)
     */
    private static Month intToMonth(int m){
        switch(m){
            case 1:
                return Month.JANUARY;
            case 2:
                return Month.FEBRUARY;
            case 3:
                return Month.MARCH;
            case 4:
                return Month.APRIL;
            case 5:
                return Month.MAY;
            case 6:
                return Month.JUNE;
            case 7:
                return Month.JULY;
            case 8:
                return Month.AUGUST;
            case 9:
                return Month.SEPTEMBER;
            case 10:
                return Month.OCTOBER;
            case 11:
                return Month.NOVEMBER;
            case 12:
                return Month.DECEMBER;
            default:
                throw new IllegalArgumentException("Le numéro de mois (=" + m + " est hors de l'intervalle [1;12]");
        }
    }
    
    /**
     * Transforme un mois (Month) en numéro de mois.
     * 
     * @param m Le mois (Month).
     * @throws IllegalArgumentException Si le mois m n'est pas une valeur des mois en anglais (JANUARY -> DECEMBER)
     * @return Le mois (Month) en numéro de mois.
     */
    private static int monthToInt(Month m){
        switch(m){
            case JANUARY:
                return 1;
            case FEBRUARY:
                return 2;
            case MARCH:
                return 3;
            case APRIL:
                return 4;
            case MAY:
                return 5;
            case JUNE:
                return 6;
            case JULY:
                return 7;
            case AUGUST:
                return 8;
            case SEPTEMBER:
                return 9;
            case OCTOBER:
                return 10;
            case NOVEMBER:
                return 11;
            case DECEMBER:
                return 12;
            default:
                throw new IllegalArgumentException("Conversion du mois vers le numéro du mois impossible. Valeur rencontrée : " + m + ". Valeur attendue : les mois en anglais (JANUARY -> DECEMBER).");
        }
    }
    
    /**
     * Retourne vrai si et seulement si l'année passée en argument est bissextile.
     * 
     * @param y L'année.
     * @return Vrai si et seulement si l'année passée en argument est bissextile.
     */
    private static boolean isLeapYear(int y){
        return ( modF(y, 4) == 0 && modF(y, 100) != 0 ) || modF(y, 400) == 0;
    }
    
    /**
     * Retourne le nombre de jours dans le mois m de l'année y.
     * 
     * @param m Le mois.
     * @param y L'année.
     * @throws IllegalArgumentException Si le mois m n'est pas une valeur des mois en anglais (JANUARY -> DECEMBER)
     * @return Le nombre de jours dans le mois m de l'année y.
     */
    private static int daysInMonth(Month m, int y){
        switch(m){
            case JANUARY:
                return 31;
            case FEBRUARY:
                return isLeapYear(y) ? 29 : 28;
            case MARCH:
                return 31;
            case APRIL:
                return 30;
            case MAY:
                return 31;
            case JUNE:
                return 30;
            case JULY:
                return 31;
            case AUGUST:
                return 31;
            case SEPTEMBER:
                return 30;
            case OCTOBER:
                return 31;
            case NOVEMBER:
                return 30;
            case DECEMBER:
                return 31;
            default:
                throw new IllegalArgumentException("Attribution du nombre de jours dans le mois m de l'année y impossible. Valeur rencontrée : " + m + ". Valeur attendue : les mois en anglais (JANUARY -> DECEMBER).");
        }
    }
    
    /**
     * Transforme un triplet de valeurs (jour, mois, année) en un entier.
     * 
     * @param d Le jour.
     * @param m Le mois.
     * @param y L'année.
     * @return L'entier représentant le triplet de valeurs (jour, mois, année).
     */
    private static int dateToFixed(int d, Month m, int y){
        int monthInt = monthToInt(m);
        int y0 = y-1;
        int c;
        
        if(monthInt <= 2){
            c = 0;
        }
        else if(monthInt > 2 && isLeapYear(y)){
            c = -1;
        }
        else{
            c = -2;
        }

        return 365*y0 + divF(y0, 4) - divF(y0, 100) + divF(y0, 400) + divF(367*monthInt - 362, 12) + c + d;
    }
    
    /**
     * Transforme un entier en date du calendrier grégorien.
     * 
     * @param n L'entier.
     * @return La date du calendrier grégorien représentant l'entier.
     */
    private static Date fixedToDate(int n){
        int d, m, y;
        int p, c;
        
        int d0   = n -1;
        int n400 = divF(d0, 146097);
        int d1   = modF(d0, 146097);
        int n100 = divF(d1, 36524);
        int d2   = modF(d1, 36524);
        int n4   = divF(d2, 1461);
        int d3   = modF(d2, 1461);
        int n1   = divF(d3, 365);
        int y0   = 400*n400 + 100*n100 + 4*n4 + n1;
        
        y = (n100 == 4 || n1 == 4) ? y0 : y0+1;
        
        p = n - dateToFixed(1, intToMonth(1), y);
        
        if(n < dateToFixed(1, intToMonth(3), y)){
            c = 0;
        }
        else if( n >= dateToFixed(1, intToMonth(3), y) && isLeapYear(y) ){
            c = 1;
        }
        else{
            c = 2;
        }
        
        m = divF(12*(p+c) + 373, 367);
        
        d = n - dateToFixed(1, intToMonth(m), y) + 1;
        
        return new Date(d, intToMonth(m), y);
    }
    
    /**
     * Retourne la date sous forme d'un entier.
     * 
     * @return La date sous forme d'un entier.
     */
    private int fixed(){
        return dateToFixed(day, month, year);
    }
}
