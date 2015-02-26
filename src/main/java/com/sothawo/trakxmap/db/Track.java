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

import com.sothawo.mapjfx.Coordinate;
import com.sothawo.mapjfx.CoordinateLine;
import com.sothawo.mapjfx.Extent;
import com.sothawo.trakxmap.util.I18N;
import com.sothawo.trakxmap.util.PathTools;
import com.sothawo.trakxmap.util.TrackStatistics;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A track that can be displayed on the map with additional data.
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
@Entity
@Table(name = "TRACK")
public class Track implements Serializable {
// ------------------------------ FIELDS ------------------------------

    /** the name of the track */
    private final SimpleStringProperty name = new SimpleStringProperty(I18N.get(I18N.TRACK_NAME_DEFAULT));
    /** db id of the track */
    private Long id;
    /** the filename where the track was loaded from (without path) */
    private String filename;
    /** the waypoints of the track */
    private List<WayPoint> wayPoints = new ArrayList<>();
    /** the routepoints of the track */
    private List<RoutePoint> routePoints = new ArrayList<>();
    /** the trackpoints of the track */
    private List<TrackPoint> trackPoints = new ArrayList<>();

    /** the extent of the track */
    private Extent extent = null;
    /** CoordinateLine for the trackpoints */
    private CoordinateLine trackLine = null;
    /** CoordinateLine for the routepoints */
    private CoordinateLine routeLine = null;

    /** the time info for the track */
    private TrackStatistics statistics = null;

// --------------------------- CONSTRUCTORS ---------------------------

    public Track() {
    }

    public Track(String name) {
        this.name.set(name);
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    @Column(name = "FILENAME", length = 255)
    public String getFilename() {
        return filename;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * gets the Coordinateline for the routepoints
     *
     * @return CoordinateLine
     */
    @Transient
    public CoordinateLine getRouteLine() {
        if (null == routeLine) {
            routeLine =
                    new CoordinateLine(routePoints.stream().map(RoutePoint::getCoordinate).collect(Collectors.toList())
                    ).setColor(Color.GREEN).setWidth(3);
        }
        return routeLine;
    }

    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("sequence")
    public List<RoutePoint> getRoutePoints() {
        return routePoints;
    }

    private void setRoutePoints(List<RoutePoint> routePoints) {
        this.routePoints = routePoints;
    }

    /**
     * gets the lazy evaluated TrackStatistics.
     *
     * @return TrackStatistics object
     */
    @Transient
    public TrackStatistics getStatistics() {
        if (null == statistics) {
            statistics = new TrackStatistics();
            trackPoints.stream().forEach(statistics::addTrackPoint);
            routePoints.stream().map(Point::getTimestamp).forEach(statistics::addRouteTime);
            wayPoints.stream().map(Point::getTimestamp).forEach(statistics::addWaypointTime);
        }
        return statistics;
    }

    /**
     * gets the Coordinateline for the trackpoints
     *
     * @return CoordinateLine
     */
    @Transient
    public CoordinateLine getTrackLine() {
        if (null == trackLine) {
            trackLine =
                    new CoordinateLine(trackPoints.stream().map(TrackPoint::getCoordinate).collect(Collectors.toList())
                    ).setColor(Color.RED).setWidth(5);
        }
        return trackLine;
    }

    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("sequence")
    public List<TrackPoint> getTrackPoints() {
        return trackPoints;
    }

    private void setTrackPoints(List<TrackPoint> trackPoints) {
        this.trackPoints = trackPoints;
    }

    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("sequence")
    public List<WayPoint> getWayPoints() {
        return wayPoints;
    }

    private void setWayPoints(List<WayPoint> wayPoints) {
        this.wayPoints = wayPoints;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return "Track{" +
                "name=" + name.getValue() +
                " filename=" + filename +
                ", #wayPoints=" + wayPoints.size() +
                ", #routePoints=" + routePoints.size() +
                ", #trackPoints=" + trackPoints.size() +
                '}';
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * adds a RoutePoint, sets up the entity relationship and the internal sequence number.
     *
     * @param routePoint
     *         the RoutePoint to add
     */
    public void addRoutePoint(RoutePoint routePoint) {
        routePoint.setTrack(this);
        routePoint.setSequence(routePoints.size() + 1);
        routePoints.add(routePoint);
    }

    /**
     * adds a WayPoint, sets up the entity relationship and the internal sequence number.
     *
     * @param trackPoint
     *         the TrackPoint to add
     */
    public void addTrackPoint(TrackPoint trackPoint) {
        trackPoint.setTrack(this);
        trackPoint.setSequence(trackPoints.size() + 1);
        trackPoints.add(trackPoint);
    }

    /**
     * adds a WayPoint, sets up the entity relationship and the internal sequence number.
     *
     * @param wayPoint
     *         the WayPoint to add
     */
    public void addWayPoint(WayPoint wayPoint) {
        wayPoint.setTrack(this);
        wayPoint.setSequence(wayPoints.size() + 1);
        wayPoints.add(wayPoint);
    }

    @Column(name = "NAME", length = 255)
    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    /**
     * recalculates the extent. This is necessary if the trackpoint list was modified after the last call to
     * getExtent();
     *
     * @return the extent
     */
    public Optional<Extent> recalculateExtent() {
        extent = null;
        return getExtent();
    }

    /**
     * gets the extent for the contained trackpoints. If the list of trackpoints is changed after the extent is
     * calculated, the new extent must be calculated by calling recalculateExtent().
     *
     * @return the extent
     */
    @Transient
    public Optional<Extent> getExtent() {
        // extent can only be calculated when more than 2 points are available
        if (null == extent) {
            List<Coordinate> coordinates = trackPoints.stream().map(TrackPoint::getCoordinate).collect(Collectors
                    .toList());
            coordinates.addAll(routePoints.stream().map(RoutePoint::getCoordinate).collect(Collectors.toList()));
            coordinates.addAll(wayPoints.stream().map(WayPoint::getCoordinate).collect(Collectors.toList()));
            if (coordinates.size() >= 2) {
                extent = Extent.forCoordinates(coordinates);
            }
        }
        return Optional.ofNullable(extent);
    }

    public void setFilename(String filename) {
        this.filename = PathTools.getFilenameFromPath(filename);
    }

    public void setName(String name) {
        this.name.set(name);
    }
}
