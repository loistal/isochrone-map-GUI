package ch.epfl.isochrone.timetable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Modelise un horaire.
 * 
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public final class TimeTable {

    private final Collection<Service> services;
    private final Set<Stop> stops;
    
    /**
     * Construit un nouvel horaire ayant les arrêts et les services donnés.
     * 
     * @param stops L'ensemble des arrêts.
     * @param services L'ensemble des services.
     */
    public TimeTable(Set<Stop> stops, Collection<Service> services){
        // On prend garde à copier les collections reçues pour garantir l'immuabilité des instances.
        this.stops = java.util.Collections.unmodifiableSet(stops);
        this.services = java.util.Collections.unmodifiableCollection(services);
    }
    
    /**
     * Retourne l'ensemble des arrêts.
     * 
     * @return L'ensemble des arrêts.
     */
    public Set<Stop> stops(){
        return  java.util.Collections.unmodifiableSet(stops);
    }
    
    /**
     * Retourne l'ensemble des services actifs le jour donné.
     * 
     * @param date Le jour.
     * @return L'ensemble des services actifs du jour donné.
     */
    public Set<Service> servicesForDate(Date date){

        Service[] servicesArray = services.toArray(new Service[0]);  //On convertit la collection en tableau pour manipulation plus facile

        Set<Service> servicesForDate = new HashSet<Service>(); 

        for(int i = 0; i < servicesArray.length; i++){
            if( servicesArray[i].isOperatingOn(date) ){
                servicesForDate.add(servicesArray[i]);
            }
        } 
        return java.util.Collections.unmodifiableSet(servicesForDate);
    }
    
    /**
     * Modélise un batisseur pour un horaire.
     *
     * @author Alexandre Simoes Tavares (234563)
     * @author Lois Talagrand (234231)
     */
    public final static class Builder{
        
        private final Collection<Service> services;
        private final Set<Stop> stops;
        
        /**
         * Construit un nouveau bâtisseur pour un horaire.
         */
        public Builder(){
            
            // Initialisation des collections qui nous évitera des NullPointerException avec les méthodes add
            stops = new HashSet<Stop>();
            services = new HashSet<Service>();
        }
        
        /**
         * Ajoute un nouvel arrêt à l'horaire en cours de contruction. Retourne this afin de permettre les appels chaînés.
         * 
         * @param newStop Un arrêt.
         * @return this (permet les appels chaînés).
         */
       public Builder addStop(Stop newStop){
           stops.add(newStop);
           return this;
       }
       
       /**
        * Ajoute un nouveau service à l'horaire en cours de construction. Retourne this afin de permettre les appels chaînés.
        * 
        * @param newService Un service.
        * @return this (permet les appels chaînés).
        */
       public Builder addService(Service newService){
           services.add(newService);
           return this;
       }
       
       /**
        * Retourne un nouvel horaire possédant les arrêts et services ajoutés jusqu'ici au bâtisseur.
        * 
        * @return Un nouvel horaire possédant les arrêts et services ajoutés jusqu'ici au bâtisseur.
        */
       public TimeTable build(){
           return new TimeTable(stops, services);
       }
        
    }
    
}

