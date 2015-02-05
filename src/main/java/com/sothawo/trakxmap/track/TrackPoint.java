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

import com.sothawo.mapjfx.Coordinate;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * TrackPoint in track.
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
public final class TrackPoint {
// ------------------------------ FIELDS ------------------------------

    /** latitude */
    private final Double latitude;
    /** longitude */
    private final Double longitude;
    /** elevation */
    private final Double elevation;
    /** timestamp */
    private final LocalDateTime timestamp;

// --------------------------- CONSTRUCTORS ---------------------------

    public TrackPoint(Double latitude, Double longitude, Double elevation, LocalDateTime timestamp) {
        this.latitude = Objects.requireNonNull(latitude);
        this.longitude = Objects.requireNonNull(longitude);

        this.elevation = elevation;
        this.timestamp = timestamp;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public Double getElevation() {
        return elevation;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
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
     * @return coordinate
     */
    public Coordinate getCoordinate() {
        return new Coordinate(latitude, longitude);
    }
}
