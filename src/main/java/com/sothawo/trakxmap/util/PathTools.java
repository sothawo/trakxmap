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
package com.sothawo.trakxmap.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Utility Class with methods related to paths.
 *
 * @author P.J.Meisch (pj.meisch@jaroso.de)
 */
public final class PathTools {
// ------------------------------ FIELDS ------------------------------

    private static final String SYSPROP_OS_NAME = "os.name";
    private static final String SYSPROP_USER_HOME = "user.home";
    private static final String OS_WIN = "win";
    private static final String OS_MAC = "mac";
    private static final String ENV_WIN_APPDATA = "APPDATA";
    private static final String DATAPATH_MAC = "/Library/Application Support";
    private static final String DATAPATH_NIX = "/.local/share";

    /** Logger for the class */
    private static final Logger logger = LoggerFactory.getLogger(PathTools.class);

// -------------------------- STATIC METHODS --------------------------

    /**
     * get the application data directory for the different os systems.
     *
     * @return data directory if it can be determined.  may be null when in windows environment is not set
     */
    public static Optional<String> getApplicationDataDirectory() {
        String os = System.getProperty(SYSPROP_OS_NAME).toLowerCase();
        return Optional.ofNullable(os.contains(OS_WIN) ? System.getenv(ENV_WIN_APPDATA) :
                System.getProperty(SYSPROP_USER_HOME) + (os.contains(OS_MAC) ? DATAPATH_MAC : DATAPATH_NIX));
    }

    /**
     * gets the JDBC URL for the database.
     *
     * @return JDBC URL
     */
    public static String getJdbcUrl() {
        String jdbcUrl = "jdbc:h2:" + getDatabaseDirectory().toString() + "/trakxmap-db";
        logger.debug("jdbc url: {}", jdbcUrl);
        return jdbcUrl;
    }

    /**
     * getst the directory to crate the database in.
     *
     * @return directory Path
     */
    public static Path getDatabaseDirectory() {
        String dir = getApplicationDataDirectory().orElse(".") + "/trakxmap/database";
        logger.debug("database directory: {}", dir);
        return Paths.get(dir);
    }

// --------------------------- CONSTRUCTORS ---------------------------

    /**
     * private constructor. the class only contains static methods.
     */
    private PathTools() {
    }
}
