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

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.concurrent.ConcurrentHashMap;
import java.util.prefs.Preferences;

/**
 * Exposes bindable properties that are persisted in User Preferences. Instances are created for classes and cached.
 * Changes to the underlying Preferences object are NOT monitored.
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
public class PreferencesBindings {
// ------------------------------ FIELDS ------------------------------

    /** concurrent map for caching */
    private final static ConcurrentHashMap<Class<?>, PreferencesBindings> cache = new ConcurrentHashMap<>();

    /** the Java preferences */
    private final Preferences prefs;

    /** cache for SimpleDoubleProperties */
    private final ConcurrentHashMap<String, SimpleDoubleProperty> simpleDoubleProperties = new ConcurrentHashMap<>();

    /** cache for SimpleStringProperties */
    private final ConcurrentHashMap<String, SimpleStringProperty> simpleStringProperties = new ConcurrentHashMap<>();

// -------------------------- STATIC METHODS --------------------------

    /**
     * returns the PreferencesBindings for the package of the given class. See java.util.prefs.Preferences doc for the
     * conventions.
     *
     * @param c
     *         class
     * @return PreferencesBindings instance
     * @throws java.lang.NullPointerException
     *         if c is null
     */
    public static PreferencesBindings forPackage(Class<?> c) {
        return cache.computeIfAbsent(c, PreferencesBindings::new);
    }

// --------------------------- CONSTRUCTORS ---------------------------

    /**
     * initializes the internal Preferences object.
     *
     * @param c
     *         class for package node
     * @throws java.lang.NullPointerException
     *         if c is null
     */
    private PreferencesBindings(Class<?> c) {
        prefs = Preferences.userNodeForPackage(c);
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * creates a SimpleDoubleProperty for the given preference key.
     *
     * @param key
     *         Preference key
     * @param def
     *         default value when not in preferences
     * @return SimpleDoubleProperty
     * @throws java.lang.NullPointerException
     *         if key is null
     */
    public SimpleDoubleProperty simpleDoublePropertyFor(String key, double def) {
        if (null == key) {
            throw new NullPointerException();
        }
        return simpleDoubleProperties.computeIfAbsent(key, (k) -> {
            SimpleDoubleProperty prop = new SimpleDoubleProperty();
            prop.setValue(prefs.getDouble(k, def));
            prop.addListener((observable, oldValue, newValue) -> {
                        prefs.putDouble(k, newValue.doubleValue());
                    }
            );
            return prop;
        });
    }
    /**
     * creates a SimpleStringProperty for the given preference key.
     *
     * @param key
     *         Preference key
     * @param def
     *         default value when not in preferences
     * @return SimpleStringProperty
     * @throws java.lang.NullPointerException
     *         if key is null
     */
    public SimpleStringProperty simpleStringPropertyFor(String key, String def) {
        if (null == key) {
            throw new NullPointerException();
        }
        return simpleStringProperties.computeIfAbsent(key, (k) -> {
            SimpleStringProperty prop = new SimpleStringProperty();
            prop.setValue(prefs.get(k, def));
            prop.addListener((observable, oldValue, newValue) -> {
                        prefs.put(k, newValue);
                    }
            );
            return prop;
        });
    }
}
