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

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;


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


    /** application configuration */
    private final Config config = ConfigFactory.load().getConfig(TrakxmapApp.class.getCanonicalName());

    /** user preferences */
    private final Preferences prefs = Preferences.userNodeForPackage(TrakxmapApp.class);

// -------------------------- STATIC METHODS --------------------------

    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        logger = LoggerFactory.getLogger(TrakxmapApp.class);
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info(I18N.get(I18N.LOG_START_PROGRAM));

        primaryStage.setTitle(config.getString(CONF_WINDOW_TITLE));
        primaryStage.setScene(setupPrimaryScene());

        primaryStage.show();

        logger.trace(I18N.get(I18N.LOG_START_PROGRAM_FINISHED));
    }

    /**
     * setup the scene for the primary stage.
     *
     * @return Scene
     */
    private Scene setupPrimaryScene() {
        // all elements in a vbox (menu, toolbar, content, statusbar)

        VBox sceneVBox = new VBox();
        // get the windows size from the preferences and add handlers to set size changes in the preferences
        int windowWidth = prefs.getInt(PREF_MAIN_WINDOW_WIDTH, config.getInt(PREF_MAIN_WINDOW_WIDTH));
        int windowHeight = prefs.getInt(PREF_MAIN_WINDOW_HEIGHT, config.getInt(PREF_MAIN_WINDOW_HEIGHT));
        Scene scene = new Scene(sceneVBox, windowWidth, windowHeight);
        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            prefs.putInt(PREF_MAIN_WINDOW_WIDTH, newValue.intValue());
        });
        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            prefs.putInt(PREF_MAIN_WINDOW_HEIGHT, newValue.intValue());
        });

        // create menu
        /*
        MenuBar menuBar = new MenuBar();
        menuBar.setUseSystemMenuBar(true);
        Menu menuFile = new Menu("File");
        menuBar.getMenus().add(menuFile);
        sceneVBox.getChildren().add(menuBar);
        */
        // create content
        Label label = new Label(
                "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt " +
                        "ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo " +
                        "duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum" +
                        " dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy" +
                        " eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. " +
                        "At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, " +
                        " sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur " +
                        "sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam " +
                        "erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita " +
                        "kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");
        label.setWrapText(true);
        sceneVBox.getChildren().add(label);

        return scene;
    }

// --------------------------- main() method ---------------------------

    public static void main(String[] args) {
        launch(args);
    }
}
