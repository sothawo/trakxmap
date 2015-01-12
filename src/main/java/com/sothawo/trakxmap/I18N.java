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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Utility methods.<br><br>
 *
 * locale property code from
 * http://stackoverflow.com/questions/25793841/javafx-bindings-and-localization/25794225#25794225
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
public class I18N {
// ------------------------------ FIELDS ------------------------------

    public final static String LOG_START_PROGRAM = "log.start.program";
    public final static String LOG_START_PROGRAM_FINISHED = "log.start.program.finished";

    private static final ObjectProperty<Locale> locale = new SimpleObjectProperty<>(Locale.getDefault());

// -------------------------- STATIC METHODS --------------------------

    public static ObjectProperty<Locale> localeProperty() {
        return locale;
    }

    public static Locale getLocale() {
        return locale.get();
    }

    public static void setLocale(Locale locale) {
        localeProperty().set(locale);
    }

    public static String get(String key) {
        return ResourceBundle.getBundle("messages", getLocale()).getString(key);
    }
}
