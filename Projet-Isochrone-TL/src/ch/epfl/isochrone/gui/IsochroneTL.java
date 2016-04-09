package ch.epfl.isochrone.gui;

import static java.lang.Math.pow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JViewport;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.epfl.isochrone.geo.PointOSM;
import ch.epfl.isochrone.geo.PointWGS84;
import ch.epfl.isochrone.tiledmap.CachedTileProvider;
import ch.epfl.isochrone.tiledmap.ColorTable;
import ch.epfl.isochrone.tiledmap.IsochroneTileProvider;
import ch.epfl.isochrone.tiledmap.OSMTileProvider;
import ch.epfl.isochrone.tiledmap.TileProvider;
import ch.epfl.isochrone.tiledmap.TransparentTileProvider;
import ch.epfl.isochrone.timetable.Date;
import ch.epfl.isochrone.timetable.Date.Month;
import ch.epfl.isochrone.timetable.FastestPathTree;
import ch.epfl.isochrone.timetable.Graph;
import ch.epfl.isochrone.timetable.SecondsPastMidnight;
import ch.epfl.isochrone.timetable.Service;
import ch.epfl.isochrone.timetable.Stop;
import ch.epfl.isochrone.timetable.TimeTable;
import ch.epfl.isochrone.timetable.TimeTableReader;

/**
 * Modélise la classe principale du programme. 
 * Elle se charge de construire l'interface graphique et d'afficher la fenêtre du programme à l'écran, avec laquelle l'utilisateur peut ensuite interagir.
 *
 * @author Alexandre Simoes Tavares (234563)
 * @author Lois Talagrand (234231)
 */
public final class IsochroneTL {
    private static final String OSM_TILE_URL = "http://b.tile.openstreetmap.org/";
    private static final int INITIAL_ZOOM = 11;
    private static final PointWGS84 INITIAL_POSITION = new PointWGS84(Math.toRadians(6.476), Math.toRadians(46.613));
    private static final String INITIAL_STARTING_STOP_NAME = "Lausanne-Flon";
    private static final int INITIAL_DEPARTURE_TIME = SecondsPastMidnight.fromHMS(6, 8, 0);
    private static final Date INITIAL_DATE = new Date(1, Month.OCTOBER, 2013);
    private static final int WALKING_TIME = 5 * 60;
    private static final double WALKING_SPEED = 1.25;
    private static final double ALPHA_TRANSPARENCY = 0.5;
    
    private final TiledMapComponent tiledMapComponent;
    private final TileProvider tileProviderOSM;
    private final ColorTable colorTable;
    
    private final TimeTable timeTable;
    private final TimeTableReader timeTableReader;
    private final Set<Stop> stops;
    
    private Date  currentDate = INITIAL_DATE;
    private int   currentDepartureTime = INITIAL_DEPARTURE_TIME;
    private Stop  currentStartingStop;
    private Set<Service> currentServices;
    private Graph currentGraph;
    
    private Point mouseLocationOnScreenSaved;
    
    /**
     * Construit une instance d'IsochroneTL avec un état intial spécifié par les précédentes variables statiques privées.
     * 
     * @throws IOException Si une erreur de lecture de fichier intervient.
     * @throws NoSuchElementException Si l'arrêt de départ "INITIAL_STARTING_STOP_NAME" n'a pas été trouvé dans la liste des stops issu de la lecture des fichiers.
     */
    public IsochroneTL() throws IOException, NoSuchElementException {
        timeTableReader = new TimeTableReader("/time-table/");
        timeTable = timeTableReader.readTimeTable();
        stops = java.util.Collections.unmodifiableSet(timeTable.stops());

        //On associe le nom du Stop donné au programme avec le bon stop
        for(Stop stop : stops){
            if(stop.name().equals(INITIAL_STARTING_STOP_NAME)){
                currentStartingStop = stop;
            }
        }
        
        if(currentStartingStop == null){
            throw new NoSuchElementException("L'arrêt de départ " + INITIAL_STARTING_STOP_NAME + " n'a pas pu être trouvé. Initialisation du programme interrompu.");
        }
        
        // Couleur du temps d'accès le plus rapide au plus long (par ex: la première couleur = rouge => "< 5min")
        ArrayList<Color> colorList = new ArrayList<Color>();
        colorList.add(ColorTable.convertIntToColor(1, 0, 0));      // rouge
        colorList.add(ColorTable.convertIntToColor(1, 0.5, 0));    // orange
        colorList.add(ColorTable.convertIntToColor(1, 1, 0));      // jaune
        colorList.add(ColorTable.convertIntToColor(0.5, 1, 0));    // vert clair (jaune-vert)
        colorList.add(ColorTable.convertIntToColor(0, 1, 0));      // vert
        colorList.add(ColorTable.convertIntToColor(0, 0.5, 0.5));  // turquoise
        colorList.add(ColorTable.convertIntToColor(0, 0, 1));      // bleu
        colorList.add(ColorTable.convertIntToColor(0, 0, 0.5));    // bleu foncé
                
        colorTable = new ColorTable(SecondsPastMidnight.fromHMS(0, 5, 0), colorList);
        
        tileProviderOSM = new CachedTileProvider(new OSMTileProvider(new URL(OSM_TILE_URL)));
        tiledMapComponent = new TiledMapComponent(INITIAL_ZOOM);
        
        // On met à jours la l'ensembles des services et le graphe
        updateServicesAndGraph();
        
        // L'arbre des trajets les plus courts est mis à jour et le dessin de la carte effectué
        updateTreeAndDrawMap();
    }
    
    /**
     * Met à jour la carte isochrone avec le nouvel arrêt de départ spécifié en paramètre. La mise à jour est effectuée si le nouvel 
     * arrêt de départ est différent de l'arrêt de départ courant.
     * 
     * @param newStop Le nouvel arrêt de départ.
     */
    private void setStop(Stop newStop){
        
        if(newStop.equals(currentStartingStop)){
            return;
        }
        
        currentStartingStop = newStop;
        updateTreeAndDrawMap();
    }

    /**
     * Met à jour la carte isochrone avec la nouvelle date spécifié en paramètre. La mise à jour est effectuée si la nouvelle date
     * est différente de la date courante.
     * 
     * @param newDate La nouvelle date.
     */
    private void setDate(Date newDate){
        
        if(newDate.compareTo(currentDate) == 0){
            return;
        }
        
        currentDate = newDate;
        updateServicesAndGraph();
        updateTreeAndDrawMap();
    }
    
    /**
     * Met à jour la carte isochrone avec la nouvelle heure de départ spécifié en paramètre. La mise à jour est effectuée si la nouvelle
     * heure de départ est différent de l'heure de départ courant.
     * 
     * @param newDepartureTime La nouvelle heure de départ
     * @throws IllegalArgumentException Si la nouvelle heure de départ est négatif.
     */
    private void setDepartureTime(int newDepartureTime){
        
        if(newDepartureTime == currentDepartureTime){
            return;
        }
        if(newDepartureTime < 0){
            throw new IllegalArgumentException("L'heure de départ (=" + newDepartureTime + ") ne peut être négative.");
        }
        
        //Si lheure de départ est compris entre 0 et 4 heure du matin
        if(newDepartureTime <= 3600*4){
            currentDepartureTime = 24*3600 + newDepartureTime;
            currentDate = new Date(currentDate.day() - 1, currentDate.month(), currentDate.year());
        }
        else{
            currentDepartureTime = newDepartureTime;
        }
        
        updateTreeAndDrawMap();
    }
    
    /**
     * Met à jour l'ensemble des services et le graphe.
     */
    private void updateServicesAndGraph(){
        
        //On met à jour les services
        Set<Service> newServices = timeTable.servicesForDate(currentDate);
        
        /* Si services == null, c'est qu'on est à la première initialisation
           Si la liste des nouveaux services est de taille 0, c'est qu'il est différent du précédent (containsAll retourne quand même true)
           L'idée de la condition ci-dessous est de vérifier si les nouveaux services différent des précédents */
        if( !(currentServices == null) && newServices.size() != 0 && currentServices.containsAll(newServices)){
            return;
        }
        
        currentServices = newServices;
        
        //On met à jour le graphe
        currentGraph = timeTableReader.readGraphForServices(stops, currentServices, WALKING_TIME, WALKING_SPEED);
    }
    
    /**
     * 1. Met à jour l'arbre des trajets les plus court. <br/>
     * 2. Met à jour la carte isochrone. <br/>
     * 3. Met à jour l'affichage Swing. <br/>
     */
    private void updateTreeAndDrawMap(){
        // Mise à jour de l'arbre des trajets les plus court
        FastestPathTree tree = currentGraph.fastestPaths(currentStartingStop, currentDepartureTime); 
        
        // Mise à jour de la carte isochrone
        TileProvider isochroneTileProvider = new IsochroneTileProvider(tree, colorTable, WALKING_SPEED); 
        TileProvider transpIsoTileProvider = new CachedTileProvider(new TransparentTileProvider(isochroneTileProvider, ALPHA_TRANSPARENCY)); 
        
        ArrayList<TileProvider> tileProviders = new ArrayList<TileProvider>();
        tileProviders.add(tileProviderOSM);
        tileProviders.add(transpIsoTileProvider);
        
        // Mise à jour de l'affichage Swing
        tiledMapComponent.setTileProviders(tileProviders); 
        tiledMapComponent.repaint();
    }

    @SuppressWarnings("deprecation")
    private JPanel createTopPanel(){
        
        // Liste des stops trié pour l'affichage
        ArrayList<Stop> sortedStops = new ArrayList<Stop>(stops);
        Collections.sort(sortedStops, new Comparator<Stop>() {
            
            @Override
            public int compare(Stop stop1, Stop stop2) {
                return stop1.name().compareTo(stop2.name());
            }
        });
        
        // Création d'un Vector pour la JComboBox
        final Vector<Stop> listOfDeparture = new Vector<Stop>(sortedStops);
        
        // Création/Initialisation d'une JComboBox
        final JComboBox<Stop> listOfDepartureBox = new JComboBox<Stop>(listOfDeparture);
        listOfDepartureBox.setSelectedItem(currentStartingStop); // Définir le stop affiché par défaut
        
        // Auditeur pour la JComboBox
        listOfDepartureBox.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Stop newStop = (Stop) listOfDepartureBox.getSelectedItem();
                setStop(newStop);
            }
        });
        
        // Création/Initialisation d'un model Spinner (pour les dates + heures)
        final SpinnerDateModel spinnerDateModel = new SpinnerDateModel();
        
        // Pour définir une valeur par défaut, le model doit prendre une instance de java.util.Date qui contiendra la date + l'heure
        final java.util.Date initialJavaDate = INITIAL_DATE.toJavaDate();
        final java.util.Date spinnerDefaultJavaDate = new java.util.Date(initialJavaDate.getYear(), 
                                                                         initialJavaDate.getMonth(), 
                                                                         initialJavaDate.getDate(), 
                                                                         SecondsPastMidnight.hours(INITIAL_DEPARTURE_TIME), 
                                                                         SecondsPastMidnight.minutes(INITIAL_DEPARTURE_TIME), 
                                                                         SecondsPastMidnight.seconds(INITIAL_DEPARTURE_TIME));
        
        spinnerDateModel.setValue(spinnerDefaultJavaDate); // Définir la date affiché par défaut
        
        // Auditeur pour la SpinnerDateModel
        spinnerDateModel.addChangeListener(new ChangeListener() {
            
            @Override
            public void stateChanged(ChangeEvent e) {
                java.util.Date newDate = ( (SpinnerDateModel)e.getSource() ).getDate();
                setDate(new Date(newDate));
                setDepartureTime(SecondsPastMidnight.fromHMS(newDate.getHours(), newDate.getMinutes(), newDate.getSeconds()));
            }
        });
        
        // Création/Initialisation du Spinner (date + heure)
        final JSpinner spinnerDateHour = new JSpinner(spinnerDateModel);
        
        // Création du label "Départ"
        final String departureText = "Départ";
        final JLabel departureLabel = new JLabel(departureText);
        departureLabel.setOpaque(true);
        departureLabel.setForeground(new Color(0f, 0f, 0f, 1f));
        
        // Création du label "Date et heure"
        final String dateHourText = "Date et heure";
        final JLabel dateHourLabel = new JLabel(dateHourText);
        dateHourLabel.setOpaque(true);
        dateHourLabel.setForeground(new Color(0f, 0f, 0f, 1f));
    
        // Création du séparateur invisible
        final JSeparator separator = new JSeparator();
        
        // Finalement: création de l'agencement des différents "composants"
        final JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(departureLabel);
        topPanel.add(listOfDepartureBox);
        topPanel.add(separator);
        topPanel.add(dateHourLabel);
        topPanel.add(spinnerDateHour);
        
        return topPanel;
    }

    private JComponent createCenterPanel() {
        final JViewport viewPort = new JViewport();
        viewPort.setView(tiledMapComponent);
        PointOSM startingPosOSM = INITIAL_POSITION.toOSM(tiledMapComponent.zoom());
        viewPort.setViewPosition(new Point(startingPosOSM.roundedX(), startingPosOSM.roundedY()));

        final JPanel copyrightPanel = createCopyrightPanel();

        final JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(400, 300));

        layeredPane.add(viewPort, new Integer(0));
        layeredPane.add(copyrightPanel, new Integer(1));

        layeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                final Rectangle newBounds = layeredPane.getBounds();
                viewPort.setBounds(newBounds);
                copyrightPanel.setBounds(newBounds);

                viewPort.revalidate();
                copyrightPanel.revalidate();
            }
        });

        // Déplacement de la carte à la souris
        layeredPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseLocationOnScreenSaved = e.getLocationOnScreen();
            }
        });
        
        layeredPane.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

                Point viewPosition = viewPort.getViewPosition();
                Point mouseLocationOnScreenCurrent = e.getLocationOnScreen();
                
                int newViewPositionX = viewPosition.x - (mouseLocationOnScreenCurrent.x - mouseLocationOnScreenSaved.x);
                int newViewPositionY = viewPosition.y - (mouseLocationOnScreenCurrent.y - mouseLocationOnScreenSaved.y);
                
                viewPort.setViewPosition(new Point(newViewPositionX, newViewPositionY));
                
                // Mise à jour de la position de la souris afin que la map suit la souris lors du déplacement
                mouseLocationOnScreenSaved = mouseLocationOnScreenCurrent;
            }
        });
        
        // Zoom de la carte à la souris (molette): Crans positif: dézoom, Crans négatif: zoom
        layeredPane.addMouseWheelListener(new MouseWheelListener() {
            
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                
                int oldZoom = tiledMapComponent.zoom();
                
                // Effectuons les calculs seulement s'il y a réellement un (dé)zoom
                if( tiledMapComponent.addZoom(-e.getWheelRotation()) ){
                    int newZoom = tiledMapComponent.zoom();

                    Point viewPosition = viewPort.getViewPosition();
                    Point mousePosition = e.getPoint();

                    /* Formule déduite après plusieurs expériences et réflexions :
                       A chaque zoom on multiplie par 2 (dézoom: divise par 2) la position (vue + souris) pour se situer dans le nouveau système de coordonnée
                       A la fin il faut corriger le positionnement de la vue en soustrayant la position de la souris! */
                    int newViewPositionX = (int) ( ( viewPosition.x + mousePosition.x )*pow(2, newZoom - oldZoom) ) - mousePosition.x;
                    int newViewPositionY = (int) ( ( viewPosition.y + mousePosition.y )*pow(2, newZoom - oldZoom) ) - mousePosition.y;

                    // Le "redessin" est appelé automatiquement lorsque l'on change la position de la vue
                    viewPort.setViewPosition(new Point(newViewPositionX, newViewPositionY));
                
                }
            }
        });
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(layeredPane, BorderLayout.CENTER);
        return centerPanel;
    }

    private JPanel createCopyrightPanel() {
        Icon tlIcon = new ImageIcon(getClass().getResource("/images/tl-logo.png"));
        String copyrightText = "Données horaires 2013. Source : Transports publics de la région lausannoise / Carte : © contributeurs d'OpenStreetMap";
        JLabel copyrightLabel = new JLabel(copyrightText, tlIcon, SwingConstants.CENTER);
        copyrightLabel.setOpaque(true);
        copyrightLabel.setForeground(new Color(1f, 1f, 1f, 0.6f));
        copyrightLabel.setBackground(new Color(0f, 0f, 0f, 0.4f));
        copyrightLabel.setBorder(BorderFactory.createEmptyBorder(3, 0, 5, 0));

        JPanel copyrightPanel = new JPanel(new BorderLayout());
        copyrightPanel.add(copyrightLabel, BorderLayout.PAGE_END);
        copyrightPanel.setOpaque(false);
        return copyrightPanel;
    }
    
    private void start() {
        JFrame frame = new JFrame("Isochrone TL");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(createTopPanel(), BorderLayout.PAGE_START);
        frame.getContentPane().add(createCenterPanel(), BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new IsochroneTL().start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
