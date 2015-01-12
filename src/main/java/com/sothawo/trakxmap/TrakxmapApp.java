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
import com.sothawo.mapjfx.MapView;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.Locale;
import java.util.Optional;
import java.util.prefs.Preferences;

/**
 * Trakxmap application class.
 */
public class TrakxmapApp extends Application {
// ------------------------------ FIELDS ------------------------------

    /** Logger for the class */
    private static final Logger logger;

    private static final String PREF_MAIN_WINDOW_WIDTH = "mainWindowWidth";
    private static final String PREF_MAIN_WINDOW_HEIGHT = "mainWindowHeight";
    private static final String CONF_WINDOW_TITLE = "windowTitle";
    private static final String PREF_LANGUAGE = "language";


    /** application configuration */
    private final Config config = ConfigFactory.load().getConfig(TrakxmapApp.class.getCanonicalName());

    /** user preferences */
    private final Preferences prefs = Preferences.userNodeForPackage(TrakxmapApp.class);

    /** the mapView to be used */
    private MapView mapView;

// -------------------------- STATIC METHODS --------------------------

    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        logger = LoggerFactory.getLogger(TrakxmapApp.class);
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    public void start(Stage primaryStage) throws Exception {
        Optional.ofNullable(prefs.get(PREF_LANGUAGE, null))
                .ifPresent(lang -> I18N.setLocale(Locale.forLanguageTag(lang)));
        logger.info(I18N.get(I18N.LOG_START_PROGRAM));

        primaryStage.setTitle(config.getString(CONF_WINDOW_TITLE));
        primaryStage.setScene(setupPrimaryScene());

        logger.trace(I18N.get(I18N.LOG_SHOWING_STAGE));
        primaryStage.show();

        logger.trace(I18N.get(I18N.LOG_START_PROGRAM_FINISHED));
    }

    @Override
    public void stop() throws Exception {
        prefs.put(PREF_LANGUAGE, I18N.getLocale().toLanguageTag());
        super.stop();
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


        // create content
        BorderPane content = new BorderPane();
        // on top the toolbar (maybe a menu later)
        content.setTop(createToolbar());

        //in the center a splitpane for the tracks on the left and the map and the elevation diagram on the right
        SplitPane splitPane1 = new SplitPane();
        splitPane1.setOrientation(Orientation.HORIZONTAL);
        SplitPane splitPane2 = new SplitPane();
        splitPane2.setOrientation(Orientation.VERTICAL);


        // initialize the track view
        splitPane1.getItems().add(I18N.labelForKey(I18N.LABEL_DUMMY_TRACKLIST));

        // initialize the map view
        setupMapView();
        splitPane2.getItems().add(mapView);

        Label label = I18N.labelForKey(I18N.LABEL_DUMMY_ELEVATION);
        label.setMinHeight(100.0);
        splitPane2.getItems().add(label);

        splitPane1.getItems().add(splitPane2);

        content.setCenter(splitPane1);

        // get the windows size from the preferences and add handlers to set size changes in the preferences
        int windowWidth = prefs.getInt(PREF_MAIN_WINDOW_WIDTH, config.getInt(PREF_MAIN_WINDOW_WIDTH));
        int windowHeight = prefs.getInt(PREF_MAIN_WINDOW_HEIGHT, config.getInt(PREF_MAIN_WINDOW_HEIGHT));
        Scene scene = new Scene(content, windowWidth, windowHeight);
        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            prefs.putInt(PREF_MAIN_WINDOW_WIDTH, newValue.intValue());
        });
        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            prefs.putInt(PREF_MAIN_WINDOW_HEIGHT, newValue.intValue());
        });


        return scene;
    }

    /**
     * sets up and initializes the map view.
     */
    private void setupMapView() {
        mapView = new MapView();
        mapView.initializedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                logger.trace(I18N.get(I18N.LOG_MAP_INITIALIZED));
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
        // Label for the Language change with automatic change
        Label labelLanguages = I18N.labelForKey(I18N.LABEL_SWITCH_LOCALE);
        // combobox for switching the locale
        ComboBox<Locale> comboLanguages = new ComboBox<>();
        comboLanguages.setTooltip(I18N.tooltipForKey(I18N.TOOLTIP_SWITCH_LOCALE));
        comboLanguages.setEditable(false);
        comboLanguages.getItems().addAll(I18N.getSupportedLocales());
        comboLanguages.setConverter(new StringConverter<Locale>() {
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
        comboLanguages.getSelectionModel().select(I18N.getLocale());
        I18N.localeProperty().bindBidirectional(comboLanguages.valueProperty());

        return new ToolBar(labelLanguages, comboLanguages);
    }

// --------------------------- main() method ---------------------------

    public static void main(String[] args) {
        launch(args);
    }
}
