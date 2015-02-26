/*
 Copyright 2015 Peter-Josef Meisch (pj.meisch@sothawo.com)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.sothawo.trakxmap;

import com.sothawo.mapjfx.Coordinate;
import com.sothawo.mapjfx.CoordinateLine;
import com.sothawo.mapjfx.MapType;
import com.sothawo.mapjfx.MapView;
import com.sothawo.trakxmap.control.TrackListCell;
import com.sothawo.trakxmap.db.DB;
import com.sothawo.trakxmap.db.Track;
import com.sothawo.trakxmap.db.TrackPoint;
import com.sothawo.trakxmap.loader.TrackLoader;
import com.sothawo.trakxmap.loader.TrackLoaderGPX;
import com.sothawo.trakxmap.util.*;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Trakxmap application class.
 */
public class TrakxmapApp extends Application {
// ------------------------------ FIELDS ------------------------------

    /** Logger for the class */
    private static final Logger logger;

    private static final String PREF_MAIN_WINDOW_WIDTH = "mainWindowWidth";
    private static final String PREF_MAIN_WINDOW_HEIGHT = "mainWindowHeight";
    private static final String PREF_SPLIT_1 = "split1FirstDivider";
    private static final String PREF_SPLIT_2 = "split2FirstDivider";
    private static final String PREF_LANGUAGE = "language";
    private static final String PREF_MAPTYPE = "maptype";
    private static final String CONF_WINDOW_TITLE = "windowTitle";


    /** application configuration */
    private final Config config = ConfigFactory.load().getConfig(TrakxmapApp.class.getCanonicalName());

    /** user preferences */
    private final PreferencesBindings prefs = PreferencesBindings.forPackage(TrakxmapApp.class);
    /** the list containing the Tracks */
    private final ObservableList<Track> trackList = FXCollections.observableArrayList();
    /** List with the available TrackLoaders in the order thy are used to load a file */
    private final List<TrackLoader> trackLoaders = new ArrayList<>();
    /** reference to the primary Stage */
    private Stage primaryStage;
    /** the mapView to be used */
    private MapView mapView;

    /** Flag wether the database update is finished */
    private AtomicBoolean dbUpdateFinished = new AtomicBoolean(false);

    /** the database connector object */
    private Optional<DB> db = Optional.empty();

    /** elevation chart object */
    private AreaChart<Number, Number> elevationChart;

// -------------------------- STATIC METHODS --------------------------

    // initialize logging and install Bridge from JUL to SLF4J
    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        logger = LoggerFactory.getLogger(TrakxmapApp.class);
    }

// -------------------------- OTHER METHODS --------------------------

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
        initLanguage();
        trackLoaders.add(new TrackLoaderGPX());
//        trackLoaders.add(new TrackLoaderFail());
    }

    /**
     * initialize language settings
     */
    private void initLanguage() {
        // normally the bindBidirectional should be called on the object that should be set with the initial value
        // from the other property. This is not possible here, as we need the mapping between Locale and String and
        // therefore must call bindBidirectional on the StringProperty. So we store the value from the prefs and set
        // it after the binding
        SimpleStringProperty prop =
                prefs.simpleStringPropertyFor(PREF_LANGUAGE, I18N.getDefaultLocale().toLanguageTag());

        String lang = prop.getValue();
        prop.bindBidirectional(I18N.localeProperty(), new StringConverter<Locale>() {
            @Override
            public String toString(Locale object) {
                return object.toLanguageTag();
            }

            @Override
            public Locale fromString(String string) {
                return Locale.forLanguageTag(string);
            }
        });
        prop.setValue(lang);
    }

    /**
     * Tries to load the given track files. First the track file is loaded, then the distances in the track are
     * calculated. After that step, the data is persisted in the database and added to the trackList.
     *
     * @param files
     *         file names
     */
    private void loadTrackFiles(List<File> files) {
        if (null == files || 0 == files.size()) {
            return;
        }
        files.parallelStream().forEach(file -> {
            logger.info(I18N.get(I18N.LOG_LOADING_TRACK, file.toString()));
            Optional<Track> optionalTrack = Optional.empty();
            Iterator<TrackLoader> trackLoaderIterator = trackLoaders.iterator();
            while (!optionalTrack.isPresent() && trackLoaderIterator.hasNext()) {
                optionalTrack = trackLoaderIterator.next().load(file);
            }
            if (optionalTrack.isPresent()) {
                Track track = optionalTrack.get();
                Geo.updateTrackDistances(track);
                // store in db and trackList
                db.ifPresent(d -> d.store(track));
                Platform.runLater(() -> {
                    trackList.add(track);
                    sortTrackList();
                });
            } else {
                logger.warn(I18N.get(I18N.ERROR_NO_TRACKLOADER_FOR_FILE, file.toString()));
            }
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        logger.info(I18N.get(I18N.LOG_START_PROGRAM));
        initializeDatabase();


        primaryStage.setTitle(config.getString(CONF_WINDOW_TITLE));
        primaryStage.setScene(setupPrimaryScene());

        logger.trace(I18N.get(I18N.LOG_SHOWING_STAGE));
        primaryStage.show();

        logger.debug(I18N.get(I18N.LOG_START_PROGRAM_FINISHED));
    }

    /**
     * initializes the database by firing off the update in a different thread and creating the DB object when the
     * update is finished. After that the stored Tracks are loaded.
     */
    private void initializeDatabase() {
        // create an update task
        Supplier<Optional<Failure>> dbUpdate = () -> {
            logger.info(I18N.get(I18N.LOG_DB_UPDATE_NECESSARY));
            try {
                DatabaseConnection dbConnection =
                        new JdbcConnection(DriverManager.getConnection(PathTools.getJdbcUrl()));
                Liquibase liquibase =
                        new Liquibase("db/db-changelog.xml", new ClassLoaderResourceAccessor(), dbConnection);
                liquibase.update(new Contexts());
                return Optional.empty();
            } catch (SQLException | LiquibaseException e) {
                logger.error(I18N.get(I18N.LOG_DB_UPDATE_ERROR), e);
                return Optional.of(new Failure(I18N.get(I18N.LOG_DB_UPDATE_ERROR), e));
            }
        };

        CompletableFuture.supplyAsync(dbUpdate).thenAcceptAsync(failure -> {
            dbUpdateFinished.set(true);
            logger.info(I18N.get(I18N.LOG_DB_INIT_FINISHED));
            if (!failure.isPresent()) {
                db = Optional.of(new DB());
                db.get()
                        .loadTrackIds()
                        .parallelStream()
                        .forEach(id -> db.get()
                                .loadTrackWithId(id)
                                .ifPresent(
                                        t -> Platform.runLater(() -> {
                                            trackList.add(t);
                                            sortTrackList();
                                        })));
            }
        });
    }

    /**
     * sorts the track list. default is the latest track first.
     */
    private void sortTrackList() {
        Collections.sort(trackList, new Comparator<Track>() {
            @Override
            public int compare(Track t1, Track t2) {
                int result;
                Optional<LocalDateTime> optLatestTime1 = t1.getStatistics().getTrackTimestamp();
                Optional<LocalDateTime> optLatestTime2 = t2.getStatistics().getTrackTimestamp();
                if (optLatestTime1.isPresent()) {
                    if (optLatestTime2.isPresent()) {
                        // 1 is set, 2 is set
                        result = optLatestTime2.get().compareTo(optLatestTime1.get());
                    } else {
                        // 1 is set, 2 not
                        result = -1;
                    }
                } else {
                    // 1 is not set
                    if (optLatestTime2.isPresent()) {
                        // 1 is not set, 2 is set
                        result = 1;
                    } else {
                        // 1 is not set 2 is not set
                        result = 0;
                    }
                }
                return result;
            }
        });
    }

    /**
     * setup the scene for the primary stage.
     *
     * @return Scene
     */
    private Scene setupPrimaryScene() {
        // create menu, must be added to top of the content if needed
        /*
        MenuBar menuBar = new MenuBar();
        menuBar.setUseSystemMenuBar(true);
        Menu menuFile = new Menu("File");
        menuBar.getMenus().add(menuFile);
        sceneVBox.getChildren().add(menuBar);
        */

        // initialize the map view; do it first, so that other components referencing the MapView have something to
        // reference
        createMapView();

        // create content
        BorderPane content = new BorderPane();
        // on top the toolbar (maybe a menu later)
        content.setTop(createToolbar());

        //in the center a splitpane for the tracks on the left and the map and the elevation diagram on the right
        SplitPane splitPane1 = new SplitPane();
        splitPane1.setOrientation(Orientation.HORIZONTAL);

        SplitPane splitPane2 = new SplitPane();
        splitPane2.setOrientation(Orientation.VERTICAL);

        splitPane2.getItems().add(mapView);

        // initialize the loadTrackFiles view
        splitPane1.getItems().add(createTracksViewNode());

        splitPane2.getItems().add(createElevationViewNode());

        splitPane1.getItems().add(splitPane2);

        SimpleDoubleProperty slider1Prop = prefs.simpleDoublePropertyFor(PREF_SPLIT_1, 0.25);
        splitPane1.setDividerPositions(slider1Prop.get());
        slider1Prop.bind(splitPane1.getDividers().get(0).positionProperty());

        SimpleDoubleProperty slider2Prop = prefs.simpleDoublePropertyFor(PREF_SPLIT_2, 0.75);
        splitPane2.setDividerPositions(slider2Prop.get());
        slider2Prop.bind(splitPane2.getDividers().get(0).positionProperty());

        content.setCenter(splitPane1);

        // get the windows size from the preferences and add handlers to set size changes in the preferences
        SimpleDoubleProperty widthProperty =
                prefs.simpleDoublePropertyFor(PREF_MAIN_WINDOW_WIDTH, config.getInt(PREF_MAIN_WINDOW_WIDTH));
        SimpleDoubleProperty heightProperty =
                prefs.simpleDoublePropertyFor(PREF_MAIN_WINDOW_HEIGHT, config.getInt(PREF_MAIN_WINDOW_HEIGHT));
        Scene scene = new Scene(content, widthProperty.get(), heightProperty.get());
        widthProperty.bind(scene.widthProperty());
        heightProperty.bind(scene.heightProperty());

        scene.getStylesheets().add("/css/trakxmap.css");

        return scene;
    }

    /**
     * creates the Node that contans the elevation data.
     *
     * @return
     */
    private Node createElevationViewNode() {
        // in front of the label is the chart
        NumberAxis xAxis = new NumberAxis();
        xAxis.setForceZeroInRange(false);
        xAxis.setAnimated(false);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setForceZeroInRange(false);
        yAxis.setAnimated(false);
        elevationChart = new AreaChart<>(xAxis, yAxis);
        elevationChart.setCreateSymbols(false);
        // if the chart is set to animated, we get error message son the console when adding data
        elevationChart.setAnimated(false);
        return elevationChart;
    }

    /**
     * sets up and initializes the map view. the MapView object is stored in a field as it is needed in different places
     * of the application.
     */
    private void createMapView() {
        mapView = new MapView();
        mapView.initializedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                logger.trace(I18N.get(I18N.LOG_MAP_INITIALIZED));
                mapView.setAnimationDuration(1000);
                // show Europe
                mapView.setCenter(new Coordinate(46.67959446564012, 5.537109374999998));
                mapView.setZoom(5);
            }
        });
        mapView.initialize();
    }

    /**
     * creates the applications toolbar.
     *
     * @return ToolBar
     */
    private Node createToolbar() {
        SimpleStringProperty maptypePref = prefs.simpleStringPropertyFor(PREF_MAPTYPE, MapType.OSM.name());
        String actMapType = maptypePref.get();
        // Label for the map type selection
        Label labelMapType = I18N.labelForKey(I18N.LABEL_SWITCH_MAPTYPE);
        // ComboBox for switching the MapType
        ComboBox<String> mapTypeComboBox = new ComboBox<>();
        mapTypeComboBox.setTooltip(I18N.tooltipForKey(I18N.TOOLTIP_SWITCH_MAPTYPE));
        mapTypeComboBox.setEditable(false);
        mapTypeComboBox.getItems().addAll(
                Arrays.stream(MapType.values()).map(MapType::name).collect(Collectors.toList()));
        mapTypeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (null != newValue) {
                mapView.setMapType(MapType.valueOf(newValue));
            }
        });
        mapTypeComboBox.getSelectionModel().select(actMapType);
        maptypePref.bind(mapTypeComboBox.getSelectionModel().selectedItemProperty());

        // Label for the Language change with automatic change
        Label labelLanguages = I18N.labelForKey(I18N.LABEL_SWITCH_LOCALE);
        // combobox for switching the locale
        ComboBox<Locale> languageComboBox = new ComboBox<>();
        languageComboBox.setTooltip(I18N.tooltipForKey(I18N.TOOLTIP_SWITCH_LOCALE));
        languageComboBox.setEditable(false);
        languageComboBox.getItems().addAll(I18N.getSupportedLocales());
        languageComboBox.setConverter(new StringConverter<Locale>() {
            @Override
            public String toString(Locale l) {
                return l.getDisplayLanguage(l);
            }

            @Override
            public Locale fromString(String s) {
                // only really needed if combo box is editable
                return Locale.forLanguageTag(s);
            }
        });
        languageComboBox.getSelectionModel().select(I18N.getLocale());
        I18N.localeProperty().bindBidirectional(languageComboBox.valueProperty());

        return new ToolBar(labelMapType, mapTypeComboBox, labelLanguages, languageComboBox);
    }

    /**
     * builds the Node to contain the track list together with the add button/drop area at the top
     *
     * @return Node
     */
    private Node createTracksViewNode() {
        BorderPane borderPane = new BorderPane();

        borderPane.getStyleClass().add("track-view-node");
        borderPane.setTop(createTrackFileDropArea());
        borderPane.setCenter(createTrackListNode());
        return borderPane;
    }

    /**
     * creates the Node that is used as a drop area to add new files; contains an add-button as well.
     *
     * @return Node
     */
    private Node createTrackFileDropArea() {
        HBox dropArea = new HBox();
        Button buttonAdd = new Button("+");
        buttonAdd.setOnAction((evt) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.titleProperty().bind(I18N.getStringBinding(I18N.LABEL_FILECHOOSER_TRACKS));
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(I18N.get(I18N
                    .EXT_FILE_GPX), "*.gpx"), new FileChooser.ExtensionFilter(I18N.get(I18N.EXT_FILE_ALL), "*.*"));
            fileChooser.setSelectedExtensionFilter(fileChooser.getExtensionFilters().get(0));
            Optional.ofNullable(fileChooser.showOpenMultipleDialog(primaryStage))
                    .ifPresent(this::loadTrackFiles);
        });

        Label labelDropHere = I18N.labelForKey(I18N.LABEL_DROP_TRACKFILE_HERE);
        dropArea.getChildren().addAll(buttonAdd, labelDropHere);
        dropArea.getStyleClass().add("track-droparea");
        dropArea.setOnDragOver((evt) -> {
                    if (evt.getGestureSource() != dropArea && evt.getDragboard().hasFiles()) {
                        evt.acceptTransferModes(TransferMode.COPY);
                    }
                    evt.consume();
                }
        );
        dropArea.setOnDragDropped((evt) -> {
            Optional.ofNullable(evt.getDragboard().getFiles()).ifPresent(this::loadTrackFiles);
            evt.consume();
        });
        return dropArea;
    }

    /**
     * creates the node containing the track list.
     *
     * @return Node
     */
    private Node createTrackListNode() {
        TitledPane titledPane = new TitledPane();
        titledPane.setCollapsible(false);
        titledPane.setExpanded(true);
        titledPane.textProperty().bind(I18N.getStringBinding(I18N.LABEL_TITLE_TRACKLIST));

        ListView<Track> trackListView = new ListView<>(trackList);

        // Create a MenuItem and place it in a ContextMenu
        MenuItem menuItemDelete = I18N.menuItemForKey(I18N.CONTEXT_MENU_DELETE_TRACK);
        ContextMenu contextMenu = new ContextMenu(menuItemDelete);
        menuItemDelete.setOnAction(evt -> Optional.ofNullable(trackListView.getSelectionModel().getSelectedItem())
                .ifPresent(track -> {
                    trackListView.getSelectionModel().select(null);
                    deleteTrack(track);
                }));

        trackListView.setCellFactory((listView -> {
            TrackListCell listCell = new TrackListCell();
            listCell.setContextMenu(contextMenu);
            return listCell;
        }));
        trackListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        trackListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            trackSelectionChanged(oldValue, newValue);
        });

        AnchorPane anchorPane = new AnchorPane(trackListView);
        anchorPane.setId("track-list-anchorpane");
        AnchorPane.setTopAnchor(trackListView, 0.0);
        AnchorPane.setBottomAnchor(trackListView, 0.0);
        AnchorPane.setLeftAnchor(trackListView, 0.0);
        AnchorPane.setRightAnchor(trackListView, 0.0);
        anchorPane.setPrefHeight(Double.MAX_VALUE);

        titledPane.setContent(anchorPane);
        return titledPane;
    }

    /**
     * deletes the given track from the database and the listview
     *
     * @param track
     *         the track to delete
     */
    private void deleteTrack(Track track) {
        // TODO: confirm deletion
        logger.info(I18N.get(I18N.LOG_DELETE_TRACK, track));
        db.ifPresent(d -> {
            Optional<Failure> optFailure = d.deleteTrack(track);
            if (!optFailure.isPresent()) {
                trackList.remove(track);
            }
        });
    }

    /**
     * hides the old track from the map, show the new track and zooms to the new track's extent, update the elevation
     * chart.
     *
     * @param oldTrack
     *         the old track if any
     * @param newTrack
     *         the new track
     */
    private void trackSelectionChanged(Track oldTrack, Track newTrack) {
        if (null != oldTrack) {
            mapView.removeCoordinateLine(oldTrack.getTrackLine());
            mapView.removeCoordinateLine(oldTrack.getRouteLine());
        }
        if (null != newTrack) {
            CoordinateLine trackLine = newTrack.getTrackLine();
            mapView.addCoordinateLine(trackLine);
            trackLine.setVisible(true);

            CoordinateLine routeLine = newTrack.getRouteLine();
            mapView.addCoordinateLine(routeLine);
            routeLine.setVisible(true);

            newTrack.getExtent().ifPresent(mapView::setExtent);
            logger.debug("{}", newTrack.getStatistics().toString());
        }
        updateElevationChartWithTrack(newTrack);
    }

    /**
     * updates the elevation chart with the data from the given track.
     *
     * @param track
     *         the track containing the new data
     */
    private void updateElevationChartWithTrack(Track track) {
        // clear the old data from the chart
        elevationChart.getData().clear();

        if (null != track) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("distance (meters)");
            elevationChart.getData().add(series);
            ObservableList<XYChart.Data<Number, Number>> dataList = series.getData();
            for (TrackPoint trackPoint : track.getTrackPoints()) {
                dataList.add(new XYChart.Data<>(trackPoint.getDistance(), trackPoint.getElevation()));
            }
        }
    }

// --------------------------- main() method ---------------------------

    @Override
    public void stop() throws Exception {
        super.stop();
        db.ifPresent(DB::close);
        logger.info(I18N.get(I18N.LOG_STOP_PROGRAM));
    }
}
