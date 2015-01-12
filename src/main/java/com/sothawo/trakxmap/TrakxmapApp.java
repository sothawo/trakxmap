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

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Trakxmap application class.
 */
public class TrakxmapApp extends Application {
    /** Logger for the class */
    private static final Logger logger;

    // -------------------------- STATIC METHODS --------------------------
    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        logger = LoggerFactory.getLogger(TrakxmapApp.class);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("starting trakxmap program...");

        // show the whole thing
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
        Scene scene = new Scene(label, 800, 600);

        primaryStage.setTitle("sothawo trakxmap ");
        primaryStage.setScene(scene);
        primaryStage.show();

        logger.trace("application started.");

    }
}
