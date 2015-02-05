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
package com.sothawo.trakxmap.db;

import com.sothawo.trakxmap.util.Failure;
import com.sothawo.trakxmap.util.I18N;
import com.sothawo.trakxmap.util.PathTools;
import javafx.concurrent.Task;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Task for updating the database.
 *
 * @author P.J.Meisch (pj.meisch@jaroso.de)
 */
public class DatabaseUpdateTask  extends Task<Optional<Failure>> {
    /** Logger */
    private final static Logger logger = LoggerFactory.getLogger(DatabaseUpdateTask.class);

    @Override
    protected Optional<Failure> call() throws Exception {
        logger.info(I18N.get(I18N.LOG_DB_UPDATE_NECESSARY));
        try {
            DatabaseConnection dbConnection = new JdbcConnection(DriverManager.getConnection(PathTools.getJdbcUrl()));
            Liquibase liquibase = new Liquibase("db/db-changelog.xml", new ClassLoaderResourceAccessor(), dbConnection);
            liquibase.update(new Contexts());
            return Optional.empty();
        } catch (SQLException | LiquibaseException e) {
            logger.error(I18N.get(I18N.LOG_DB_UPDATE_ERROR), e);
            return Optional.of(new Failure(I18N.get(I18N.LOG_DB_UPDATE_ERROR), e));
        }
    }
}
