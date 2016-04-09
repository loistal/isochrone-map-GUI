package ch.epfl.isochrone.timetable;

import java.util.HashSet;
import java.util.Set;

import ch.epfl.isochrone.timetable.Date.DayOfWeek;

/**
 * Modélise un service.
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public final class Service {
    private final String name;
    private final Date startingDate;
    private final Date endingDate;
    
    private final Set<DayOfWeek> operatingDays;
    private final Set<Date> excludedDates;
    private final Set<Date> includedDates;
    
    /**
     * Construit un service avec le nom, la plage de validité, les jours de circulation et les exceptions données. A noter que la plage de validité inclut la date de début et la date de fin.
     * 
     * @param name Le nom du service.
     * @param startingDate Date de début de la validité du service.
     * @param endingDate Date de fin de la validité du service.
     * @param operatingDays Jours de la semaine de circulation.
     * @param excludedDates Dates d'inactivités exceptionnelles
     * @param includedDates Dates d'activités exceptionnelles.
     * @throws IllegalArgumentException Si la date de fin est antérieure à la date de début, 
     *                                  si une des dates listées dans les exceptions est en dehors de la plage de validité du service, 
     *                                  ou si l'intersection entre les dates exclues et incluses n'est pas vide.
     */
    public Service(String name, Date startingDate, Date endingDate, Set<Date.DayOfWeek> operatingDays, Set<Date> excludedDates, Set<Date> includedDates){
        if(endingDate.compareTo(startingDate) == -1){
            throw new IllegalArgumentException("La date de fin ne peut être antérieur à la date de début");
        }
        else if( !(isInValidityRange(startingDate, endingDate, excludedDates)) ){
            throw new IllegalArgumentException("Une date d'inactivité exceptionnelle est en dehors de la plage de validité du service.");
        }
        else if( !(isInValidityRange(startingDate, endingDate, includedDates)) ){
            throw new IllegalArgumentException("Une date d'activitié exceptionnelle est en dehors de la plage de validité du service.");
        }
        else if( !(isIntersectionEmpty(excludedDates, includedDates)) ){
            throw new IllegalArgumentException("Une date figure parmis la liste des dates d'inactivités exceptionnelle et d'activités exceptionnelle en même temps.");
        }
        else{
            this.name = name;

            this.startingDate = new Date(startingDate.day(), startingDate.month(), startingDate.year());
            this.endingDate = new Date(endingDate.day(), endingDate.month(), startingDate.year());

            this.operatingDays = new HashSet<DayOfWeek>(operatingDays);
            this.excludedDates = new HashSet<Date>(excludedDates);
            this.includedDates = new HashSet<Date>(includedDates);
        }
    }
    
    /**
     * Définit si toutes les dates d'une collection se trouvent bien dans la plage de validité spécifiée.
     * 
     * @param startingDate Date de début.
     * @param endingDate Date de fin.
     * @param datesSet La collection de dates.
     * @return false si parmis la collection de dates se trouve une date en dehors de la plage de validité spécifié, sinon true .
     */
    private boolean isInValidityRange(Date startingDate, Date endingDate, Set<Date> datesSet){
        Date[] arrayDatesSet = datesSet.toArray(new Date[0]);
        
        for(int i=0; i < arrayDatesSet.length; ++i){
            // Si une des dates listées est en dehors de la plage de validité
            if(startingDate.compareTo(arrayDatesSet[i]) == 1 || endingDate.compareTo(arrayDatesSet[i]) == -1){
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Définit si l'intersection entre deux collections de dates est bien vide.
     * 
     * @param firstDatesSet La première collection de dates.
     * @param secondDatesSet La deuxième collection de dates.
     * @return true si l'intersection est effectivement vide, sinon false .
     */
    private boolean isIntersectionEmpty(Set<Date> firstDatesSet, Set<Date> secondDatesSet){
        Date[] arrayFirstDatesSet = firstDatesSet.toArray(new Date[0]);
        Date[] arraySecondDatesSet = secondDatesSet.toArray(new Date[0]);

        for(int i=0; i < arrayFirstDatesSet.length; ++i){
            for(int j=0; i< arraySecondDatesSet.length; ++i){
                // Si l'intersection entre les dates exclues et incluses n'est pas vide.
                if( arraySecondDatesSet[j].equals(arrayFirstDatesSet[i]) ) {
                    return false;
                }
            }
        }

        return true;
    }
    
    /**
     * Retourne le nom du service.
     * 
     * @return Le nom du service.
     */
    public String name(){
        return name;
    }
    
    /**
     * Retourne vrai si et seulement si le service est actif le jour donné.
     * 
     * @param date Le jour.
     * @return Vrai si et seulement si le service est actif le jour donné.
     */
    public boolean isOperatingOn(Date date){
        if(date.compareTo(startingDate) == -1 || date.compareTo(endingDate) == 1){
            return false;
        }
        else{
            if( ( operatingDays.contains(date.dayOfWeek()) && !(excludedDates.contains(date)) ) || includedDates.contains(date)) {
                return true;
            }
            else{
                return false;
            }
        }
    }
    
    /**
     * Retourne une représentation textuelle du service, en l'occurrence son nom.
     * 
     * @return Le nom du service.
     */
    @Override
    public String toString(){
        return name;
    }
    
    /**
     * Modélise un batisseur pour un service.
     *
     * @author Alexandre Simoes Tavares (234563)
     * @author Lois Talagrand (234231)
     */
    public final static class Builder{
        private final String name;
        private final Date startingDate;
        private final Date endingDate;
        
        private final Set<DayOfWeek> operatingDays;
        private final Set<Date> excludedDates;
        private final Set<Date> includedDates;
        
        /**
         * Construit un nouveau bâtisseur pour un service ayant le nom et la plage de validité donnés.
         * 
         * @param name Le nom.
         * @param startingDate Date de début.
         * @param endingDate Date de fin.
         * @throws IllegalArgumentException Si la date de fin est antérieure à la date de début.
         */
        public Builder(String name, Date startingDate, Date endingDate){
            if(endingDate.compareTo(startingDate) == -1){
                throw new IllegalArgumentException("La date de fin ne peut être antérieur à la date de début.");
            }
            else{
                this.name = name;

                this.startingDate = new Date(startingDate.day(), startingDate.month(), startingDate.year());
                this.endingDate = new Date(endingDate.day(), endingDate.month(), endingDate.year());
                
                // Initialisation des collections qui nous évitera des NullPointerException avec les méthodes add
                this.operatingDays = new HashSet<DayOfWeek>();
                this.excludedDates = new HashSet<Date>();
                this.includedDates = new HashSet<Date>();
            }
        }
        
        /**
         * Retourne le nom du service en cours de construction.
         * 
         * @return Le nom du service en cours de construction.
         */
        public String name(){
            return name;
        }
        
        /**
         * Ajoute le jour de la semaine donné aux jours de circulation. Retourne this afin de permettre les appels chaînés.
         * 
         * @param day Le jour de la semaine.
         * @return this (permet les appels chaînés).
         */
        public Builder addOperatingDay(Date.DayOfWeek day){
            operatingDays.add(day);
            return this;
        }
        
        /**
         * Ajoute la date donnée aux jours exceptionnellement exclus du service. Retourne this afin de permettre les appels chaînés.
         * 
         * @param date La date.
         * @throws IllegalArgumentException Si la date n'est pas dans la plage de validité du service en construction, ou si elle fait partie des dates incluses.
         * @return this (permet les appels chaînés).
         */
        public Builder addExcludedDate(Date date){
            if( (date.compareTo(startingDate) == -1 || date.compareTo(endingDate) == 1) || includedDates.contains(date)){
                throw new IllegalArgumentException("La date n'est pas dans la plage de validité du service en construction ou fait partie des dates incluses.");
            }
            else{
                excludedDates.add(date);
                return this;
            }
        }
        
        /**
         *  Ajoute la date donnée aux jours exceptionnellement inclus au service. Retourne this afin de permettre les appels chaînés.
         *  
         * @param date
         * @throws IllegalArgumentException Si la date n'est pas dans la plage de validité du service en construction, ou si elle fait partie des dates exclues.
         * @return this (permet les appels chaînés).
         */
        public Builder addIncludedDate(Date date){
            if( (date.compareTo(startingDate) == -1 || date.compareTo(endingDate) == 1) || excludedDates.contains(date)){
                throw new IllegalArgumentException("La date n'est pas dans la plage de validité du service en construction ou fait partie des dates exclues.");
            }
            else{
                includedDates.add(date);
                return this;
            }
        }
        
        /**
         * Retourne un nouveau service avec le nom, la plage de validité, les jours de circulation et les exceptions ajoutées jusqu'ici au bâtisseur.
         * 
         * @return Un nouveau service avec le nom, la plage de validité, les jours de circulation et les exceptions ajoutées jusqu'ici au bâtisseur.
         */
        public Service build(){
            return new Service(name, startingDate, endingDate, operatingDays, excludedDates, includedDates);
        }
    }
}
