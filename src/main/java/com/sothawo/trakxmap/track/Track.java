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
package com.sothawo.trakxmap.track;

import com.sothawo.trakxmap.util.I18N;
import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * A track that can be displayed on the map with additional data.
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
public class Track {
// ------------------------------ FIELDS ------------------------------

    /** the name of the track */
    private final SimpleStringProperty name = new SimpleStringProperty(I18N.get(I18N.TRACK_NAME_DEFAULT));
    /** the waypoints of the track */
    private final List<WayPoint> wayPoints = new ArrayList<>();
    /** the trackpooints of the track */
    private final List<WayPoint> trackPoints = new ArrayList<>();

    public List<WayPoint> getTrackPoints() {
        return trackPoints;
    }
    // --------------------------- CONSTRUCTORS ---------------------------

    public Track() {
    }

    @Override
    public String toString() {
        return "Track{" +
                "name=" + name +
                ", wayPoints=" + wayPoints +
                ", #trackPoints=" + trackPoints.size() +
                '}';
    }

    public Track(String name) {
        this.name.set(name);
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public List<WayPoint> getWayPoints() {
        return wayPoints;
    }

// ------------------------ CANONICAL METHODS ------------------------

    public String getName() {
        return name.get();
    }

// -------------------------- OTHER METHODS --------------------------

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }
}
