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

import javafx.beans.property.SimpleStringProperty;
import org.junit.Before;
import org.junit.Test;

import java.util.prefs.Preferences;

import static org.junit.Assert.*;

/**
 * Tests for the PreferenceBindings class. Bindings of the properties are not tested, this is JavaFX stuff.
 */
public class PreferencesBindingsTest {
// ------------------------------ FIELDS ------------------------------

    private static final String KEY_STRING_1 = "k-string1";
    private static final String DEF_STRING_1 = "d-string1";
    private static final String VAL_STRING_1 = "v-string1";

    // the original javaprefs
    private Preferences javaPrefs = Preferences.userNodeForPackage(PreferencesBindingsTest.class);

// -------------------------- OTHER METHODS --------------------------

    @Test
    public void createStringProperty() throws Exception {
        PreferencesBindings prefs = PreferencesBindings.forPackage(PreferencesBindingsTest.class);
        assertNotNull(prefs);
        SimpleStringProperty prop = prefs.simpleStringPropertyFor(KEY_STRING_1, DEF_STRING_1);
        assertNotNull(prop);
        assertEquals(DEF_STRING_1, prop.getValue());
        assertEquals(DEF_STRING_1, javaPrefs.get(KEY_STRING_1, null));
        javaPrefs.remove(KEY_STRING_1);
    }

    @Test
    public void prefIsRemoved() throws Exception {
        PreferencesBindings prefs = PreferencesBindings.forPackage(PreferencesBindingsTest.class);
        SimpleStringProperty prop = prefs.simpleStringPropertyFor(KEY_STRING_1, DEF_STRING_1);
        prefs.remove(KEY_STRING_1);
        assertFalse(prefs.hasAny(KEY_STRING_1));
        for (String key : javaPrefs.keys()) {
            assertFalse(KEY_STRING_1.equals(key));
        }
    }

    @Test
    public void prefsAreCached() throws Exception {
        PreferencesBindings prefs1 = PreferencesBindings.forPackage(PreferencesBindingsTest.class);
        PreferencesBindings prefs2 = PreferencesBindings.forPackage(PreferencesBindingsTest.class);
        assertSame(prefs1, prefs2);
    }

    @Test
    public void prefsAreNotNull() throws Exception {
        assertNotNull(PreferencesBindings.forPackage(PreferencesBindingsTest.class));
    }

    @Before
    public void setup() {
        PreferencesBindings.forPackage(PreferencesBindingsTest.class).remove(KEY_STRING_1);
    }

    @Test
    public void stringPropertyIsCreatedOnlyOnce() throws Exception {
        PreferencesBindings prefs = PreferencesBindings.forPackage(PreferencesBindingsTest.class);
        SimpleStringProperty prop = prefs.simpleStringPropertyFor(KEY_STRING_1, DEF_STRING_1);
        assertSame(prop, prefs.simpleStringPropertyFor(KEY_STRING_1, DEF_STRING_1));
    }

    @Test
    public void stringPropertyValueChangeIsSet() throws Exception {
        PreferencesBindings prefs = PreferencesBindings.forPackage(PreferencesBindingsTest.class);
        SimpleStringProperty prop = prefs.simpleStringPropertyFor(KEY_STRING_1, DEF_STRING_1);
        prop.set(VAL_STRING_1);
        assertEquals(VAL_STRING_1, prop.get());
        assertEquals(VAL_STRING_1, javaPrefs.get(KEY_STRING_1, null));
    }

    @Test
    public void modifyUnderlyingStringChangesProperty() throws Exception {
        PreferencesBindings prefs = PreferencesBindings.forPackage(PreferencesBindingsTest.class);
        SimpleStringProperty prop = prefs.simpleStringPropertyFor(KEY_STRING_1, DEF_STRING_1);
        assertEquals(DEF_STRING_1, prop.get());
        javaPrefs.put(KEY_STRING_1, VAL_STRING_1);
        javaPrefs.flush();
        // need a little time to get the change from the underlying system, atleast on OSX
        Thread.sleep(500);
        assertEquals(VAL_STRING_1, prop.get());
    }
}
