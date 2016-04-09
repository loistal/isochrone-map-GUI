package ch.epfl.isochrone.timetable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Modélise un graphe d'horaire.
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public final class Graph {
    private final Set<Stop> stops;
    private final Map<Stop, List<GraphEdge>> outgoingEdges;
    
    /**
     * Construit un graphe avec les arrêts donnés comme noeuds et les arcs spécifiés dans la table associative. 
     * Cette table associe à un arrêt—donc un nœud du graphe—la liste des arcs qui partent de ce noeud.
     * 
     * @param stops Les arrêts.
     * @param outgoingEdges Les arcs.
     */
    private Graph(Set<Stop> stops, Map<Stop, List<GraphEdge>> outgoingEdges){
        this.stops = stops;
        this.outgoingEdges = outgoingEdges;
    }
    
    /**
     * Retourne l'arbre des trajets les plus rapides pour l'arrêt et l'heure de départ donnés.
     * 
     * @param startingStop L'arrêt.
     * @param departureTime L'heure de départ.
     * @throws IllegalArgumentException Si l'arrêt donné ne fait pas partie du graphe ou si l'heure de départ est inférieure à zéro.
     * @return L'arbre des trajets les plus rapides pour l'arrêt et l'heure de départ donnés.
     */
    public FastestPathTree fastestPaths(Stop startingStop, int departureTime){
/*      Pseudo-code de l'algorithme :
        
        V = { tous les arrêts du graphe }
        H(Ad) = Hd
        H(x)  = ∞ pour tout arrêt x ≠ Ad
         
        tant que V n'est pas vide :
          A = élément x de V tel que H(x) est minimum
          V = V \ { A }
        
          si H(A) = ∞ :
            stop
        
          pour chaque voisin A' de A :
            H' = heure de première arrivée en A' en partant de A à H(A)
            si H' < H(A') :
              H(A') = H'
*/

        if(departureTime < 0){
            throw new IllegalArgumentException("L'heure de départ (=" + departureTime + ") est inférieure à zéro.");
        }
        else if(startingStop == null){
            throw new IllegalArgumentException("L'arrêt de départ est \"null\"");
        }
        else if(!stops.contains(startingStop)){
            throw new IllegalArgumentException("L'arrêt de départ (=" + startingStop + ") ne fait pas partie du graphe.");
        }
        else{

            // Contiendra les meilleurs heures d'arrivées pour chaque stop avec son prédécesseur qui a provoqué la mise à jour de la meilleur heure d'arrivée
            final FastestPathTree.Builder fastestPathTreeBD = new FastestPathTree.Builder(startingStop, departureTime);

            // Si le stop existe dans la collection, mais n'a aucun arc sortant, alors l'arbre des trajets est trivial: il est le seul dedans
            if(outgoingEdges.get(startingStop) == null){
                return fastestPathTreeBD.build();
            }

            Comparator<Stop> comparator = new Comparator<Stop>() {

                @Override
                public int compare(Stop stop1, Stop stop2) {
                    int bestArrivalTimeStop1 = fastestPathTreeBD.arrivalTime(stop1);
                    int bestArrivalTimeStop2 = fastestPathTreeBD.arrivalTime(stop2);

                    if(bestArrivalTimeStop1 < bestArrivalTimeStop2){
                        return -1;
                    }
                    else if(bestArrivalTimeStop1 == bestArrivalTimeStop2){
                        return 0;
                    }
                    else{
                        return 1;
                    }
                }
            };

            // Queue de priorité qui contiendra l'ensemble des arrêts à visiter
            PriorityQueue<Stop> stopsPriorityQueue = new PriorityQueue<>(stops.size(), comparator);
            stopsPriorityQueue.addAll(stops);


            // ---------------------- Itération ----------------------
            // Tant que la collection n'est pas vide, c'est qu'il reste encore des arrêts à visiter par l'algorithme
            while(!stopsPriorityQueue.isEmpty()){

                // Prochain stop à parcourir tel qu'il possède la meilleur heure d'arrivée minimum parmis ceux connus
                Stop A = stopsPriorityQueue.remove();

                // Meilleure heure d'arrivée au stop courant
                int bestArrivalTimeOfA = fastestPathTreeBD.arrivalTime(A);

                /* Si le stop récupéré de la queue de priorité contient comme meilleur temps INFINI, 
                   on arrête la boucle de parcours car il n'est plus possible d'améliorer les meilleurs heures d'arrivées des arrêts */
                if(bestArrivalTimeOfA == SecondsPastMidnight.INFINITE){
                    break;
                }

                /* Pour chaque voisin A' de A :
                   On regarde s'il est possible d'améliorer leur meilleure heure d'arrivée en s'y rendant depuis l'arrêt actuellement visité */
                for(GraphEdge neighbor:outgoingEdges.get(A)){
                    
                    Stop neighborStop = neighbor.destination();

                    // A chaque voisin: calculer sa meilleure heure d'arrivée depuis A
                    int H2 = neighbor.earliestArrivalTime(bestArrivalTimeOfA);

                    // Si la meilleure heure d'arrivée calculée est meilleure que celle connu, on met à jour
                    if(H2 < fastestPathTreeBD.arrivalTime(neighborStop)){
                        
                        fastestPathTreeBD.setArrivalTime(neighborStop, H2, A);

                        // Suppression/Ajout du stop afin que la queue de priorité se comporte correctement
                        stopsPriorityQueue.remove(neighborStop);
                        stopsPriorityQueue.add(neighborStop);
                    }
                }
            }

            return fastestPathTreeBD.build();
        }
    }
    
    public final static class Builder{
        private final Set<Stop> stops;
        private final Map<Stop, List<GraphEdge>> outgoingEdges;
        private final Map<Stop, Map<Stop, GraphEdge.Builder>> tempOutgoingEdges; // Stock GraphEdge.Builder pour la construction
        
        /**
         * Construit un bâtisseur pour un graphe d'horaire ayant les arrêts passés en argument comme noeuds.
         * 
         * @param stops Les arrêts.
         */
        public Builder(Set<Stop> stops){
            this.stops = java.util.Collections.unmodifiableSet(stops);
            this.outgoingEdges = new HashMap<Stop, List<GraphEdge>>();
            this.tempOutgoingEdges = new HashMap<Stop, Map<Stop, GraphEdge.Builder>>();
        }
        
        /**
         * Ajoute au graphe en construction un trajet entre les arrêts de départ et d'arrivée donnés, aux heures données (en secondes après minuit).
         * 
         * @param fromStop Arrêt de départ.
         * @param toStop Arrêt d'arrivée.
         * @param departureTime Heure de départ (en secondes après minuit).
         * @param arrivalTime Heure d'arrivée (en secondes après minuit).
         * @throws IllegalArgumentException Si l'un des deux arrêts ne fait pas partie de ceux passés au constructeur, 
         *                                  si l'une des deux heures est négative, 
         *                                  ou si l'heure d'arrivée est antérieure à l'heure de départ.
         * @return this (permet les appels chaînés).
         */
        public Builder addTripEdge(Stop fromStop, Stop toStop, int departureTime, int arrivalTime){
            if(!stops.contains(fromStop)){
                throw new IllegalArgumentException("L'arrêt de départ ne fait pas partie de ceux passés au constructeur.");
            }
            else if(!stops.contains(toStop)){
                throw new IllegalArgumentException("L'arrêt d'arrivée ne fait pas partie de ceux passés au constructeur.");
            }
            else if(departureTime < 0){
                throw new IllegalArgumentException("L'heure de départ (=" + departureTime + ") ne peut être négative.");
            }
            else if(arrivalTime < 0){
                throw new IllegalArgumentException("L'heure d'arrivée (=" + arrivalTime + ") ne peut être négative.");
            }
            else if(arrivalTime < departureTime){
                throw new IllegalArgumentException("L'heure d'arrivée (=" + arrivalTime + ") ne peut être antérieure à l'heure de départ (=" + departureTime + ").");
            }
            else{
                GraphEdge.Builder graphEdgeBD = getGraphEdgeBuilder(fromStop, toStop);
                graphEdgeBD.addTrip(departureTime, arrivalTime);
                return this;
            }
        }
        
        /**
         * Ajoute au graphe en construction la totalité des trajets à pied qu'il est possible d'effectuer entre n'importe quelle paire d'arrêts, 
         * en un temps inférieur ou égal au temps maximum de marche passé (en secondes), à la vitesse de marche donnée (en mètres par seconde).
         * 
         * @param maxWalkingTime Le temps maximum de marche (en secondes).
         * @param walkingSpeed La vitesse de marche (en mètres par seconde)
         * @throws IllegalArgumentException Si le temps maximum de marche est négatif, 
         *                                  ou si la vitesse de marche est négative ou nulle.
         * @return this (permet les appels chaînés).
         */
        public Builder addAllWalkEdges(int maxWalkingTime, double walkingSpeed){
            if(maxWalkingTime < 0){
                throw new IllegalArgumentException("Le temps maximum de marche (=" + maxWalkingTime + ") est négatif.");
            }
            else if(walkingSpeed <= 0){
                throw new IllegalArgumentException("La vitesse de marche (=" + walkingSpeed + ") est négative ou nulle.");
            }
            else{
                
                // Distance maximale de marche
                double maxWalkingDistance = walkingSpeed * maxWalkingTime;
                
                List<Stop> arrayStops = new ArrayList<Stop>(stops);
                
                // Parcourt qu'une seule fois chaque paire d'arrêts : double boucle for
                for(int i=0; i<arrayStops.size(); ++i){
                    
                    for(int j=i+1; j<arrayStops.size(); ++j){
                 
                        Stop stop1 = arrayStops.get(i);
                        Stop stop2 = arrayStops.get(j);
                        
                        // Calcule de la distance séparant deux stops de la pair (stop1, stop2)
                        double distanceBetweenStops = stop1.position().distanceTo( stop2.position() );
                        
                        // Si distance séparant deux stops < distance maximale de marche, alors le trajet à pied est calculé et ajouté aux arcs, un pour chaque sens
                        if(distanceBetweenStops < maxWalkingDistance){
                            // walkingTime devant être un int, on fait un arrondi réflétant au mieux le temps de marche obtenu
                            int walkingTime = (int) java.lang.Math.round(distanceBetweenStops / walkingSpeed);
                            
                            // On récupère les arcs dans les deux sens pour l'ajout du temps de marche
                            GraphEdge.Builder GEbuilderStop1 = getGraphEdgeBuilder(stop1, stop2);
                            GraphEdge.Builder GEbuilderStop2 = getGraphEdgeBuilder(stop2, stop1);
                            
                            GEbuilderStop1.setWalkingTime(walkingTime);
                            GEbuilderStop2.setWalkingTime(walkingTime);
                        }
                    }
                }
                
                return this;
            }
        }
        
        /**
         * Retourne le bâtisseur d'arc de l'arrêt d'arrivée correspondant à l'arrêt de départ. 
         * L'arrêt de départ est également utilisé afin de mettre à jour/créer les associations dans "tempOutgoingEdges".
         * 
         * @param fromStop L'arrêt de départ.
         * @param toStop L'arrêt d'arrivée.
         * @return Le bâtisseur d'arc de l'arrêt d'arrivée correspondant à l'arrêt de départ.
         */
        // La conception de la méthode prend comme référence le schema illustré sur le site
        private GraphEdge.Builder getGraphEdgeBuilder(Stop fromStop, Stop toStop){
            
          // Si la table ne contient pas la clé fromStop, c'est qu'il n'y a pour l'instant aucune association à l'arrêt fromStop dans la table associative
          if(!tempOutgoingEdges.containsKey(fromStop)){
              //Dans ce cas on crée cette assoctiation en créant également la table associative de toStop avec un bâtisseur d'arc
              
              GraphEdge.Builder GEbuilder = new GraphEdge.Builder(toStop);
              
              Map<Stop, GraphEdge.Builder> endStopMapBuilder = new HashMap<Stop, GraphEdge.Builder>();
              endStopMapBuilder.put(toStop, GEbuilder);
              
              tempOutgoingEdges.put(fromStop, endStopMapBuilder);
          }
          // Si la clé fromStop n'a pas d'association à la clé toStop
          if(!tempOutgoingEdges.get(fromStop).containsKey(toStop)){
              // Alors on crée cette association en créant également un bâtisseur d'arc
              
              GraphEdge.Builder GEbuilder = new GraphEdge.Builder(toStop);
              tempOutgoingEdges.get(fromStop).put(toStop, GEbuilder);
          }
          
          // Les associations nécessaire étant créées on peut maintenant retourner le bâtisseur d'arc de l'arrêt d'arrivée
          return tempOutgoingEdges.get(fromStop).get(toStop);
        }
        
        /**
         * Construit et retourne un nouveau graphe avec les noeuds passés à la construction du bâtisseur et les arcs ajoutés jusqu'à présent.
         * 
         * @return Un nouveau graphe avec les noeuds passés à la construction du bâtisseur et les arcs ajoutés jusqu'à présent.
         */
        public Graph build(){
            updateOutgoingEdges();
            return new Graph(new HashSet<Stop>(stops), new HashMap<Stop, List<GraphEdge>>(outgoingEdges)); // Assurons nous d'envoyer des paramètres immmuables
        }

        /**
         * Met à jour la variable "outgoingEdges" afin de construire la forme final d'associations "Stop<->Arcs" en se basant sur "tempOutgoingEdges".  
         * Cette Méthode doit être appelée au début de build() et nul part ailleurs.  
         * Jusqu'à maintenant "tempOutgoingEdges" avait comme rôle de "constructeur d'associations".
         */
        private void updateOutgoingEdges(){
            
            // Parcours chaque paire (Stop de départ, GraphEdge.Builder)
            for (Map.Entry<Stop, Map<Stop, GraphEdge.Builder>> entryDepartureStop : tempOutgoingEdges.entrySet())
            {
                List<GraphEdge> listGraphEdge = new ArrayList<GraphEdge>();
                
                // Parcours tout les GraphEdge.Builder (arcs) associés au Stop de départ courant
                for(GraphEdge.Builder entryArrivalStop: entryDepartureStop.getValue().values())
                {
                    // On "build" l'arc courant (à ce stade l'arc est fini et ne sera plus sujet à un changement)
                    GraphEdge currentGraphEdge = entryArrivalStop.build();
                    
                    // Ajout de l'arc construit à la liste d'arcs qui sera attribué au Stop de départ courant
                    listGraphEdge.add(currentGraphEdge);
                }
                
                /* Finalement on associe la liste des arcs construits au Stop de départ courant: cette association s'établit dans "outgoingEdges" qui contiendra 
                                                                                                 la forme finale d'associations "Stop<->Arcs"  */
                outgoingEdges.put(entryDepartureStop.getKey(), listGraphEdge);
                
            }
        }
    }
}
