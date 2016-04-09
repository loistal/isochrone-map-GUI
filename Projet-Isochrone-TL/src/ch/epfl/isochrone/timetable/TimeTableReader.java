package ch.epfl.isochrone.timetable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import ch.epfl.isochrone.geo.PointWGS84;
import ch.epfl.isochrone.timetable.Date.DayOfWeek;

/**
 * Modélise un lecteur d'horaires.
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public final class TimeTableReader {
    private final String baseResourceName;
    
    private final Set<Stop> listStops;
    private final Set<Service> listServices;
    
    /* Schema illustrant la composition de la variable ci-dessous
     * 
     *                                               List (taille inconnu)
     *                   -------------------------------------------------------------------------------
     *                  |     ------------------------------           ------------------------------   | A chaque arrêt de départ est associée une heure de départ ( Object[] de taille 2).
     *                  | 1. |       Object[] (taille 2)    |         |       Object[] (taille 2)    |  | A chaque arrêt d'arrivée est associée une heure d'arrivée ( Object[] de taille 2).
     *                  |    |------------------------------|   Map   |---------------------------   |  |
     *                  |    | 1.      Stop (Départ)        | ------> | 1.      Stop (Arrivée)       |  | A chaque couple (arrêt+heure départ) est associé un couple (arrêt+heure d'arrivée).
     *                  |    | -  -  -  -  -  -  -  -  -  - |         | -  -  -  -  -  -  -  -  -  - |  | A chaque service est associé une liste de ces trajets.
     *                  |    | 2.   int (Heure de départ)   |         | 2.   int (Heure d'arrivée)   |  | 
     *            Map   |     ------------------------------           ------------------------------   |
     *  Service ------> | -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  |
     *                  |     ------------------------------           ------------------------------   |
     *                  | 2. |       Object[] (taille 2)    |         |       Object[] (taille 2)    |  |
     *                  |    |------------------------------|   Map   |---------------------------   |  |
     *                  |    | 1.      Stop (Départ)        | ------> | 1.      Stop (Arrivée)       |  |
     *                  |    | -  -  -  -  -  -  -  -  -  - |         | -  -  -  -  -  -  -  -  -  - |  |
     *                  |    | 2.   int (Heure de départ)   |         | 2.   int (Heure d'arrivée)   |  | 
     *                  |     ------------------------------           ------------------------------   |
     *                  | -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  |
     *                  |  Etc.                                                                         |
     *                   -------------------------------------------------------------------------------
     *
     */
    private final Map<Service, List< Map< Object[], Object[] > >> listJourney;
     
    /**
     * Construit un lecteur d'horaire ayant la chaîne donnée comme préfixe des ressources. 
     * Exemple de préfixe: /time-table/ (notez les barres obliques en début et en fin !)
     * 
     * @param baseResourceName
     */
    public TimeTableReader(String baseResourceName){
        this.baseResourceName = baseResourceName;
        
        //Fichiers de données parcouru une seule fois à la création: les données sont stoquées dans des variables
        listStops = readStops("stops.csv");
        listServices = readCalendar("calendar.csv", "calendar_dates.csv");
        listJourney = readStopTimes("stop_times.csv", listStops, listServices);
    }
    
    /**
     * Lit et retourne l'horaire (cet horaire ne contient pas les trajets, qui sont décrits par le graphe des horaires).
     * 
     * @throws IOException En cas d'erreur d'entrée-sortie ou d'autres exceptions en cas d'erreur de format de données.
     * @return L'horaire.
     */
    public TimeTable readTimeTable(){
        return new TimeTable(listStops, listServices);
    }
    
    /**
     * Lit et retourne le graphe des horaires pour les arrêts donnés, en ne considérant que les trajets dont le service fait partie de l'ensemble donné. 
     * Ce graphe inclut également la totalité des trajets à pied entre arrêts qui sont faisables en un temps inférieur ou égal à celui donné (en secondes), à la vitesse de marche donnée (en mètres par seconde).
     * 
     * @param stops L'ensemble d'arrêts.
     * @param services L'ensemble de services.
     * @param walkingTime Le temps de marche.
     * @param walkingSpeed La vitesse de marche (mètres par seconde).
     * @throws IOException En cas d'erreur d'entrée-sortie ou d'autres exceptions en cas d'erreur de format de données.
     * @return Le graphe des horaires.
     */
    public Graph readGraphForServices(Set<Stop> stops, Set<Service> services, int walkingTime, double walkingSpeed){        
        Graph.Builder graphBuilder = new Graph.Builder(stops);

        //Pour chaque service voulu
        for(Service service : services){

            //On récupère la liste des trajets du service
            List<Map<Object[], Object[]>> listOfTrips = listJourney.get(service);

            //Pour chaque trajet du service
            for( Map<Object[], Object[]> trip : listOfTrips){  

                //Parcours l'ensemble des paires (arrêt départ -> arrêt d'arrivée)
                for(Entry<Object[], Object[]> departureMapArrival:trip.entrySet()){

                    Stop departureStop = (Stop)departureMapArrival.getKey()[0];
                    Stop arrivalStop = (Stop)departureMapArrival.getValue()[0];
                    
                    //Si l'arrêt de départ et d'arrivée est contenu dans la liste des stops fourni en paramètre, on construit un arc
                    if( stops.contains(departureStop) && stops.contains(arrivalStop) ){
                        int departureTime = (int)departureMapArrival.getKey()[1];
                        int arrivalTime   = (int)departureMapArrival.getValue()[1];

                        graphBuilder.addTripEdge(departureStop, arrivalStop, departureTime, arrivalTime);
                    }
                }
            }   
        }

        graphBuilder.addAllWalkEdges(walkingTime, walkingSpeed);  
        return graphBuilder.build();
    }
    
    /**
     * Lit les fichiers (.CSV) fournis en paramètre, construit les Service correspondant et retourne la liste des Service construit. Cette méthode fait appel à 
     * la méthode readCalendarDates pour finaliser la construction des services. En l'occurance, la méthode readCalendarDates ajoute les exceptions aux services.
     * 
     * @param fileNameCalendar Le nom du fichier (.CSV) contenant la description des services, leur plage de validité et leurs jours de circulation.
     * @param fileNameCalendarDates Le nom du fichier (.CSV) contenant la description des exceptions aux jours de circulation des services, c-à-d les jours où les services sont exceptionnellement actifs ou inactifs.
     * @throws IOException En cas d'erreur d'entrée-sortie ou d'autres exceptions en cas d'erreur de format de données.
     * @return La liste des Service construit à partir du parcours des fichiers fourni en paramètre.
     */
    private Set<Service> readCalendar(String fileNameCalendar, String fileNameCalendarDates){
        Set<Service.Builder> listServicesBD = new HashSet<Service.Builder>(); // contiendra au fil de la lecture du fichier CSV la liste des bâtisseurs de service
        
        String lineServiceName;
        Date lineServiceStartingDate;
        Date lineServiceEndingDate;
        
        Service.Builder lineServiceBuilder;
        
        // L'idée de stocker l'enum des jours dans un tableau est que compte tenu de la construction du fichier CSV, l'index du jour dans le fichier CSV 
        // correspond à l'index du jour dans le tableau de l'enum des jours -1. Cela évite de faire des if à répétition, par exemple. Voir plus bas pour la mise en oeuvre.
        DayOfWeek[] enumDayOfWeek = Date.DayOfWeek.values();

        InputStream inStream = getClass().getResourceAsStream(baseResourceName + fileNameCalendar);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, StandardCharsets.UTF_8));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                String[] splitLine = line.split(";"); // Contient 10 valeurs: Nom, Lu, Ma, Me, Je, Ve, Sa, Di, Début, Fin
                                                      //               Index:   0,  1,  2,  3,  4,  5,  6,  7,     8,   9
                
                lineServiceName = splitLine[0];
                
                // Example format date CSV: 2013 09 23
                //                  Index : 0123 45 67
                
                lineServiceStartingDate = new Date(Integer.parseInt(splitLine[8].substring(6, 8)), Integer.parseInt(splitLine[8].substring(4, 6)), Integer.parseInt(splitLine[8].substring(0, 4)));
                lineServiceEndingDate = new Date(Integer.parseInt(splitLine[9].substring(6, 8)), Integer.parseInt(splitLine[9].substring(4, 6)), Integer.parseInt(splitLine[9].substring(0, 4)));
            
                lineServiceBuilder = new Service.Builder(lineServiceName, lineServiceStartingDate, lineServiceEndingDate);
                
                // On parcourt les différentes valeurs des jours, si c'est = à 1 (=circulation actif) on ajoute ce jour à la construction du service
                for(int i=1; i<=7; ++i){ // Index 1 à 7 correspond aux valeurs Lundi à Vendredi dans le fichier CSV
                    if(Integer.parseInt(splitLine[i]) == 1){
                        lineServiceBuilder.addOperatingDay(enumDayOfWeek[i-1]); // i-1, car l'index de parcours commence à i=1
                    }
                }
                
                listServicesBD.add(lineServiceBuilder);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Appel à readCalendarDates pour finaliser la construction des services en ajoutant les exceptions
        return readCalendarDates(fileNameCalendarDates, listServicesBD);
    }
    
    /**
     * Méthode destinée à être appelée par la méthode readCalendar. Cette méthode (readCalendarDates) finalise la construction des services, initiallement débutée
     * dans la méthode readCalendar, en leurs ajoutant les exceptions trouvées dans le fichier (.CSV) fourni en paramètre.
     * 
     * @param fileNameCalendarDates Le nom du fichier (.CSV) contenant la description des exceptions aux jours de circulation des services, c-à-d les jours où les services sont exceptionnellement actifs ou inactifs.
     * @param listServicesBD La liste des bâtisseurs de Service (description des services, plage de validité et jours de circulation déjà définis).
     * @throws IOException En cas d'erreur d'entrée-sortie ou d'autres exceptions en cas d'erreur de format de données.
     * @return La liste des Service construit à partir du parcours des fichiers fourni en paramètre.
     */
    private Set<Service> readCalendarDates(String fileNameCalendarDates, Set<Service.Builder> listServicesBD){
        Set<Service> tempListServices = new HashSet<Service>(); // contiendra au fil de la lecture du fichier CSV la liste des services construits
        
        String lineServiceName;
        
        Date lineServiceDate;
        int lineServiceDateType; // 1 = jour actif ; 2 = jour inactif

        InputStream inStream = getClass().getResourceAsStream(baseResourceName + fileNameCalendarDates);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, StandardCharsets.UTF_8));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                String[] splitLine = line.split(";"); // Contient 3 valeurs: Nom, Date exception, Type exception
                                                      //              Index:   0,              1,              2
                
                lineServiceName = splitLine[0];
                
                // Example format date CSV: 2013 09 23
                //                  Index : 0123 45 67
                
                lineServiceDate = new Date(Integer.parseInt(splitLine[1].substring(6, 8)), Integer.parseInt(splitLine[1].substring(4, 6)), Integer.parseInt(splitLine[1].substring(0, 4)));
                lineServiceDateType = Integer.parseInt(splitLine[2]); // 1 = jour actif ; 2 = jour inactif
                
                for(Service.Builder servBD: listServicesBD){
                    
                    if(servBD.name().equals(lineServiceName)){
                        if(lineServiceDateType == 1){
                            servBD.addIncludedDate(lineServiceDate);
                        }
                        else if(lineServiceDateType == 2){
                            servBD.addExcludedDate(lineServiceDate);
                        }
                    }
                    
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Toutes les exceptions ont été ajoutées aux builder correspondant. On fait alors une boucle pour finaliser la construction des builders
        for(Service.Builder servBD: listServicesBD){
            tempListServices.add(servBD.build());
        }
        
        return tempListServices;
    }
    
    /**
     * Lit le fichier fournis en paramètre (.CSV), construit les Stop correspondant et retourne la liste des Stop construit.
     * 
     * @param fileName Le nom du fichier (.CSV) contenant la description des arrêts, c-à-d leur nom et leur position géographique.
     * @throws IOException En cas d'erreur d'entrée-sortie ou d'autres exceptions en cas d'erreur de format de données.
     * @return La liste des Stop construit à partir du parcours du fichier fourni en paramètre.
     */
    private Set<Stop> readStops(String fileName){
        Set<Stop> tempListStops = new HashSet<Stop>(); // contiendra au fil de la lecture du fichier CSV la liste des stops construits

        String lineStopName;
        double lineStopLatitude;
        double lineStopLongitude;
        PointWGS84 linePointWGS84;

        InputStream inStream = getClass().getResourceAsStream(baseResourceName + fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, StandardCharsets.UTF_8));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                String[] splitLine = line.split(";"); // Contient 3 valeurs: nom, latitude, longitude
                                                      //              Index:   0,        1,         2
                
                lineStopName = splitLine[0];
                lineStopLatitude = java.lang.Math.toRadians(Double.parseDouble(splitLine[1]));
                lineStopLongitude = java.lang.Math.toRadians(Double.parseDouble(splitLine[2]));
                
                linePointWGS84 = new PointWGS84(lineStopLongitude, lineStopLatitude);
                
                tempListStops.add(new Stop(lineStopName, linePointWGS84));                
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return tempListStops;
    }
    
    /**
     * Lit le fichier fournis en paramètre (.CSV) et retourne une table associative entre les services et les trajets (trajet = (Arrêt départ/Heure départ --> Arrêt arrivé/Heure arrivée) ) en tenant compte
     * de la liste des services et la liste des stops fournit en paramètre. Voir en début de code pour un schéma des associations.
     *
     * @param fileName Le nom du fichier (.CSV) contenant la description de ce que nous avons appelé les trajets.
     * @param listStops La liste des Stop.
     * @param listServices La liste des Service.
     * @throws IOException En cas d'erreur d'entrée-sortie ou d'autres exceptions en cas d'erreur de format de données.
     * @throws NoSuchElementException Si l'arrêt de départ ou l'arrêt d'arrivée de la ligne courante de lecture n'a pas été trouvé dans la liste des Stop ou 
     *                                si le service de la ligne courante de lecture n'a pas été trouvé dans la liste des Service.
     * @return La table associant les services aux trajets adéquats (trajet = (Arrêt départ/Heure départ --> Arrêt arrivé/Heure arrivée) ).
     */
    private Map<Service, List< Map< Object[], Object[] > >> readStopTimes(String fileName, Set<Stop> listStops, Set<Service> listServices){
        // Contiendra au fil de la lecture du fichier CSV les associations entre services et trajets (voir schema début fichier)
        Map<Service, List< Map< Object[], Object[] > >> tempListJourney = new HashMap<>();
        
        String lineServiceName;   // Contiendra le nom du service
        String lineFromStopStr;   // Contiendra le nom de l'arrêt de départ
        String lineToStopStr;     // Contiendra le nom de l'arrêt d'arrivée
        int    lineDepartureTime; // Contiendra l'heure de départ
        int    lineArrivalTime;   // Contiendra l'heure d'arrivée
        
        Service lineService; // Contiendra le service
        Stop lineFromStop;   // Contiendra l'arrêt de départ
        Stop lineToStop;     // Contiendra l'arrêt d'arrivée
        
        // Contiendra l'association entre l'arrêt de départ (+heure de départ) et l'arrêt d'arrivée (+heure d'arrivée)
        Map< Object[], Object[] > departureMapArrival;
        
        // Contiendra l'arrêt de départ et l'heure de départ
        Object[] departureList;
        
        // Contiendra l'arrêt d'arrivée et l'heure d'arrivée
        Object[] arrivalList;
        
        // Contiendra la liste des associations entre l'arrêt de départ et l'arrêt d'arrivée d'un service
        List< Map< Object[], Object[] > > listOfDepartureMapArrival;
        
        InputStream inStream = getClass().getResourceAsStream(baseResourceName + fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, StandardCharsets.UTF_8));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                // Suppression du pointage à chaque nouvelle ligne de parcours
                // Cela nous servira, entre autre, à savoir si un service/stop d'une ligne du fichier à été trouvé dans nos listes
                lineService = null;
                lineFromStop = null;
                lineToStop = null;
                
                String[] splitLine = line.split(";"); // Contient 5 valeurs: nom du service, nom arrêt de départ, heure de départ (en secondes après minuit), nom arrêt d'arrivée, heure d'arrivée
                                                      //              Index:              0,                   1,                                          2,                   3,               4
                
                lineServiceName = splitLine[0];
                lineFromStopStr = splitLine[1];
                lineToStopStr = splitLine[3];
                lineDepartureTime = Integer.parseInt(splitLine[2]);
                lineArrivalTime = Integer.parseInt(splitLine[4]);
                
                // Parcours la liste des stops pour récupérer le Stop correspondant à la ligne courante
                for(Stop stop: listStops){
                    if(stop.name().equals(lineFromStopStr)){
                        lineFromStop = stop;
                    }
                    if(stop.name().equals(lineToStopStr)){
                        lineToStop = stop;
                    }
                    // Si l'arrêt de départ et d'arrivée ont été trouvés, on arrête la boucle de parcours
                    if(lineFromStop != null && lineToStop != null){
                        break;
                    }
                }
                
                if(lineFromStop == null){
                    throw new NoSuchElementException("L'arrêt de départ (=" + lineFromStopStr + ") du fichier " + fileName + " n'a pas été trouvé dans la liste des arrêts (Stop).");
                }
                else if(lineToStop == null){
                    throw new NoSuchElementException("L'arrêt d'arrivée (=" + lineToStopStr + ") du fichier " + fileName + " n'a pas été trouvé dans la liste des arrêts (Stop).");
                }

                // Parcours la liste des services pour récupérer le Service correspondant à la ligne courante pour ensuite construire les associations entre services et trajets
                for(Service service: listServices){
                    if(service.name().equals(lineServiceName)){
                        lineService = service;
                        
                        departureList = new Object[2];
                        arrivalList = new Object[2];
                        
                        departureList[0] = lineFromStop;
                        departureList[1] = lineDepartureTime;
                        
                        arrivalList[0] = lineToStop;
                        arrivalList[1] = lineArrivalTime;
                        
                        departureMapArrival = new HashMap<Object[], Object[] >();
                        departureMapArrival.put(departureList, arrivalList);
                        
                        // Si le service ne fait pas partit de la construction de la table associative, on crée les associations nécessaires
                        if(!tempListJourney.containsKey(lineService)){
                            
                            listOfDepartureMapArrival = new ArrayList<>();
                            listOfDepartureMapArrival.add(departureMapArrival);
                            
                            tempListJourney.put(lineService, listOfDepartureMapArrival);
                        }
                        // Sinon on récupère la table associative des trajets du service et on ajoute à cette table les associations déclarées par la ligne du fichier
                        else{
                            
                            listOfDepartureMapArrival = tempListJourney.get(lineService);
                            listOfDepartureMapArrival.add(departureMapArrival);
                        }
                        
                        // Le service a été trouvé et les actions adéquates effectuées, la boucle de parcourt peut être arrêté
                        break;
                    }
                }
                
                if(lineService == null){
                    throw new NoSuchElementException("Le service (=" + lineServiceName + ") du fichier " + fileName + " n'a pas été trouvé dans la liste des services (Service).");
                }

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempListJourney;
    }

}

