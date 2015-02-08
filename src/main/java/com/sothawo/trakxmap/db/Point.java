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

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * This Point class is the base class for the different entity classes TrackPoint, WayPoint and RoutePoint. It is not
 * needed or used in JPA queries, it's for the Java side of things.
 *
 * @author P.J.Meisch (pj.meisch@jaroso.de)
 */
@MappedSuperclass
public abstract class Point implements Serializable {
// ------------------------------ FIELDS ------------------------------

    /** latitude */
    protected Double latitude;
    /** longitude */
    protected Double longitude;
    /** elevation */
    protected Double elevation;
    /** timestamp */
    protected LocalDateTime timestamp;
    /** db id of the track */
    private Long id;
    /** the internal order number of the waypoint within the track */
    private Integer sequence;
    /** the track this point belongs to */
    private Track track;

// --------------------------- CONSTRUCTORS ---------------------------

    public Point() {

    }

    Point(Double latitude, Double longitude, Double elevation, LocalDateTime timestamp) {
        this.latitude = Objects.requireNonNull(latitude);
        this.longitude = Objects.requireNonNull(longitude);

        this.elevation = elevation;
        this.timestamp = timestamp;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    @Column(name = "ELEVATION")
    public Double getElevation() {
        return elevation;
    }

    public void setElevation(Double elevation) {
        this.elevation = elevation;
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

    @Column(name = "LATITUDE", nullable = false)
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    @Column(name = "LONGITUDE", nullable = false)
    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Column(name = "SEQUENCE", nullable = false)
    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    @Column(name = "TIMESTAMP", nullable = true)
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @ManyToOne
    @JoinColumn(name = "TRACK_ID", nullable = false)
    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (id != null ? !id.equals(point.id) : point.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Point{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", elevation=" + elevation +
                ", timestamp=" + timestamp +
                '}';
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * gets lat/lon as coordinate object for the MapView.
     *
     * @return coordinate
     */
    @Transient
    public Coordinate getCoordinate() {
        return new Coordinate(latitude, longitude);
    }
}
