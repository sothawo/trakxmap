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

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
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
 * locale property code idea from http://stackoverflow.com/questions/25793841/javafx-bindings-and-localization/25794225#25794225
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
public class I18N {
// ------------------------------ FIELDS ------------------------------

    public static final String EXT_FILE_GPX = "extension.file.gpx";
    public static final String EXT_FILE_ALL = "extension.file.all";
    public static final String LABEL_SWITCH_LOCALE = "label.switch.locale";
    public static final String LABEL_SWITCH_MAPTYPE = "label.switch.maptype";
    public static final String LABEL_TITLE_TRACKLIST = "label.title.tracklist";
    public static final String LABEL_DROP_TRACKFILE_HERE = "label.drop.trackfile.here";
    public static final String LABEL_FILECHOOSER_TRACKS = "label.filechooser.tracks";
    public static final String LABEL_DUMMY_ELEVATION = "label.dummy.elevation";
    public static final String LOG_START_PROGRAM = "log.start.program";
    public static final String LOG_START_PROGRAM_FINISHED = "log.start.program.finished";
    public static final String LOG_SWITCH_LOCALE = "log.switch.locale";
    public static final String LOG_SHOWING_STAGE = "log.showing.stage";
    public static final String LOG_LOADING_TRACK = "log.loading.track";
    public static final String LOG_LOADING_TRACKS = "log.loading.tracks";
    public static final String LOG_MAP_INITIALIZED = "log.map.initialized";
    public static final String TOOLTIP_SWITCH_LOCALE = "tooltip.switch.locale";
    public static final String TOOLTIP_SWITCH_MAPTYPE = "tooltip.switch.maptype";
    public static final String TRACK_NAME_DEFAULT = "track.name.default";
    public static final String ERROR_LOADING_TRACK = "error.loading.track";
    public static final String ERROR_DELETING_TRACK = "error.deleting.track";
    public static final String ERROR_NO_TRACKLOADER_FOR_FILE = "error.no.trackloader.for.file";
    public static final String LOG_DB_UPDATE_NECESSARY = "log.db.update.necessary";
    public static final String LOG_DB_UPDATE_ERROR = "log.db.update.error";
    public static final String LOG_DB_INIT_FINISHED = "log.db.init.finished";
    public static final String LOG_STOP_PROGRAM = "log.stop.program";
    public static final String LABEL_TRACKLISTCELL_DURATIONLENGTH = "label.tracklistcell.durationLength";
    public static final String LOG_DELETE_TRACK = "log.delete.track";

    public static final String CONTEXT_MENU_DELETE_TRACK = "context.menu.delete.track";
    private static final Logger logger = LoggerFactory.getLogger(I18N.class);
    private static final ObjectProperty<Locale> locale;

// -------------------------- STATIC METHODS --------------------------

    static {
        locale = new SimpleObjectProperty<>(getDefaultLocale());
        locale.addListener((observable, oldValue, newValue) -> logger.info(get(LOG_SWITCH_LOCALE, oldValue,
                newValue)));
    }

    public static ObjectProperty<Locale> localeProperty() {
        return locale;
    }

    public static void setLocale(Locale locale) {
        localeProperty().set(locale);
        Locale.setDefault(locale);
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
     *
     * @param key
     *         ResourceBundle key
     * @param args
     *         optional arguments for the message
     * @return Label
     */
    public static Label labelForKey(final String key, final Object... args) {
        Label label = new Label();
        label.textProperty().bind(getStringBinding(key, args));
        return label;
    }

    /**
     * creates a bound Tooltip for the given resourcebundle key
     *
     * @param key
     *         ResourceBundle key
     * @param args
     *         optional arguments for the message
     * @return Label
     */
    public static Tooltip tooltipForKey(final String key, final Object... args) {
        Tooltip tooltip = new Tooltip();
        tooltip.textProperty().bind(getStringBinding(key, args));
        return tooltip;
    }

    /**
     * creates a bound MenuItem for the given resourcebundle key
     *
     * @param key
     *         ResourceBundle key
     * @param args
     *         optional arguments for the message
     * @return Label
     */
    public static MenuItem menuItemForKey(final String key, final Object... args) {
        MenuItem menuItem = new MenuItem();
        menuItem.textProperty().bind(getStringBinding(key, args));
        return menuItem;
    }

    /**
     * creates a String binding to localized String for the given message bundle key
     *
     * @param key
     *         key
     * @param args
     *         optional arguments for the message
     * @return String binding
     */
    public static StringBinding getStringBinding(final String key, final Object... args) {
        return Bindings.createStringBinding(() -> get(key, args), locale);
    }

    /**
     * gets the string with the given key from the resource bundle for the current locale and uses it as first argument
     * to MessageFormat.foramt, passing in the optional args and returning the result.
     *
     * @param key
     *         message key
     * @param args
     *         optional arguments for the message
     * @return localized formatted string
     */
    public static String get(final String key, final Object... args) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", getLocale());
        return MessageFormat.format(bundle.getString(key), args);
    }

    public static Locale getLocale() {
        return locale.get();
    }
}
