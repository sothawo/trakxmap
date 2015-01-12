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

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Utility methods.<br><br>
 *
 * locale property code idea from
 * http://stackoverflow.com/questions/25793841/javafx-bindings-and-localization/25794225#25794225
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
public class I18N {
// ------------------------------ FIELDS ------------------------------

    public final static String LOG_START_PROGRAM = "log.start.program";
    public final static String LOG_START_PROGRAM_FINISHED = "log.start.program.finished";
    public final static String LOG_SWITCH_LOCALE = "log.switch.locale";
    public final static String LABEL_SWITCH_LOCALE = "label.switch.locale";

    private static final Logger logger = LoggerFactory.getLogger(I18N.class);
    private static final ObjectProperty<Locale> locale;

    static {
        locale = new SimpleObjectProperty<>(getDefaultLocale());
        locale.addListener((observable, oldValue, newValue) -> logger.info(get(LOG_SWITCH_LOCALE, oldValue,
        newValue)));
    }

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

    public static String get(String key, Object... args) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", getLocale());
        return MessageFormat.format(bundle.getString(key), args);
    }

    public static List<Locale> getSupportedLocales() {
        List<Locale> locales = new ArrayList<>();
        locales.add(Locale.ENGLISH);
        locales.add(Locale.GERMANY);
        return locales;
    }

    public static Locale getDefaultLocale() {
        Locale sysDefault = Locale.getDefault();
        return getSupportedLocales().contains(sysDefault) ? sysDefault : Locale.ENGLISH;
    }

    /**
     * creates a bound Label for the given resourcebundle key
     * @param key ResourceBundle key
     * @return Label
     */
    public static Label labelForKey(final String key) {
        Label label = new Label();
        label.textProperty().bind(Bindings.createStringBinding(() -> get(key), locale));
        return label;
    }
}
