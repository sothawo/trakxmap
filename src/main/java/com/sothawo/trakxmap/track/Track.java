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

import com.sothawo.mapjfx.CoordinateLine;
import com.sothawo.mapjfx.Extent;
import com.sothawo.trakxmap.util.I18N;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    /** the extent of the track */
    private Extent extent = null;

    /** CoordinateLine representig this track */
    private CoordinateLine coordinateLine = null;

// --------------------------- CONSTRUCTORS ---------------------------

    public Track() {
    }

    public Track(String name) {
        this.name.set(name);
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public CoordinateLine getCoordinateLine() {
        if (null == coordinateLine) {
            coordinateLine =
                    new CoordinateLine(trackPoints.stream().map(WayPoint::getCoordinate).collect(Collectors.toList())
                    ).setColor(Color.RED).setWidth(5);
        }
        return coordinateLine;
    }

    /**
     * gets the extent for the contined trackpoints. If the list of trackpoints is changed after the extent is
     * calculated, the new extent must be calculated by calling recalculateExtent().
     *
     * @return the extent
     */
    public Extent getExtent() {
        // extent can only be calculated when more than 2 points are available
        if (null == extent && trackPoints.size() >= 2) {
            extent = Extent.forCoordinates(
                    trackPoints.stream().map(WayPoint::getCoordinate).collect(Collectors.toList()));
        }
        return extent;
    }

    public List<WayPoint> getTrackPoints() {
        return trackPoints;
    }

    public List<WayPoint> getWayPoints() {
        return wayPoints;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return "Track{" +
                "name=" + name +
                ", wayPoints=" + wayPoints +
                ", #trackPoints=" + trackPoints.size() +
                ", extent=" + getExtent().toString() +
                '}';
    }

// -------------------------- OTHER METHODS --------------------------

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    /**
     * recalculates the etxent. This is necessary if the trackpoint list was modified after the last call to
     * getExtent();
     *
     * @return the extent
     */
    public Extent recalculateExtent() {
        extent = null;
        return getExtent();
    }

    public void setName(String name) {
        this.name.set(name);
    }
}