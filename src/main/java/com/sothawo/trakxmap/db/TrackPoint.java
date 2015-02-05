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
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * TrackPoint in track.
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
@Entity
@Table(name = "TRACKPOINT")
public final class TrackPoint {
// ------------------------------ FIELDS ------------------------------

    /** db id of the track */
    private Long id;
    /** the internal order number of the waypoint within the track */
    private Integer sequence;

    /** latitude */
    private Double latitude;
    /** longitude */
    private Double longitude;
    /** elevation */
    private Double elevation;
    /** timestamp */
    private LocalDateTime timestamp;
    /** the track this trackpoint belongs to */
    private Track track;

// --------------------------- CONSTRUCTORS ---------------------------

    public TrackPoint() {
    }

    public TrackPoint(Double latitude, Double longitude, Double elevation, LocalDateTime timestamp) {
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

        TrackPoint wayPoint = (TrackPoint) o;

        if (elevation != null ? !elevation.equals(wayPoint.elevation) : wayPoint.elevation != null) return false;
        if (latitude != null ? !latitude.equals(wayPoint.latitude) : wayPoint.latitude != null) return false;
        if (longitude != null ? !longitude.equals(wayPoint.longitude) : wayPoint.longitude != null) return false;
        if (timestamp != null ? !timestamp.equals(wayPoint.timestamp) : wayPoint.timestamp != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = latitude != null ? latitude.hashCode() : 0;
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        result = 31 * result + (elevation != null ? elevation.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TrackPoint{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", elevation=" + elevation +
                ", timestamp=" + timestamp +
                '}';
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * gets lat/lon as coordinate object
     *
     * @return coordinate
     */
    @Transient
    public Coordinate getCoordinate() {
        return new Coordinate(latitude, longitude);
    }
}
