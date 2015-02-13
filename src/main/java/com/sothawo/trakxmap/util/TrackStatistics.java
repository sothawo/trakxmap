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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * An object with several statistical informations about a track.
 *
 * @author P.J.Meisch (pj.meisch@jaroso.de)
 */
public class TrackStatistics {
// ------------------------------ FIELDS ------------------------------

    /** the timestamp of the first trackpoint in the track */
    private LocalDateTime trackStartTime;
    /** the timestamp of the last trackpoint in the track */
    private LocalDateTime trackEndTime;
    /** the timestamp of the first routepoint in the track */
    private LocalDateTime routeStartTime;
    /** the timestamp of the first waypoint in the track */
    private LocalDateTime firstWaypointTime;
    /** the track's distance */
    private Double trackDistance;

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        Optional<Duration> duration = getTrackDuration();
        return "TrackStatistics{" +
                "trackStartTime=" + trackStartTime +
                ", routeStartTime=" + routeStartTime +
                ", firstWaypointTime=" + firstWaypointTime +
                (duration.isPresent() ? (", duration=" + duration.toString()) : "") +
                '}';
    }

    /**
     * get the duration of this track. Only defined if trackStartTime and trackEndTime are set
     *
     * @return optional duration
     */
    public Optional<Duration> getTrackDuration() {
        Duration duration = null;
        if (null != trackStartTime && null != trackEndTime) {
            duration = Duration.between(trackStartTime, trackEndTime);
        }
        return Optional.ofNullable(duration);
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * if this is the first route timstamp it is kept, otherwise ignored.
     *
     * @param localDateTime
     *         time to check
     */
    public void addRouteTime(LocalDateTime localDateTime) {
        if (null != localDateTime && null == routeStartTime) {
            routeStartTime = localDateTime;
        }
    }

    /**
     * if this is the first trackpoint, the timestamp is kept as starttime. If it is after the start time, it is kept
     * as
     * end time.
     *
     * @param localDateTime
     *         time to check
     */
    public void addTrackTime(LocalDateTime localDateTime) {
        if (null != localDateTime) {
            if (null == trackStartTime) {
                trackStartTime = localDateTime;
            }
            if (null == trackEndTime || localDateTime.isAfter(trackEndTime)) {
                trackEndTime = localDateTime;
            }
        }
    }

    /**
     * if this is the first waypoint timestamp it is kept, otherwise ignored
     *
     * @param localDateTime
     *         time to check
     */
    public void addWaypointTime(LocalDateTime localDateTime) {
        if (null != localDateTime && null == firstWaypointTime) {
            firstWaypointTime = localDateTime;
        }
    }

    /**
     * adds a given distance to the track's distance.
     *
     * @param distance
     *         the distance to add
     */
    public void adddTrackpointDistance(Double distance) {
        if (null != distance) {
            // distance is accumulated from track start
            trackDistance = distance;
        }
    }

    /**
     * get the track's distance
     *
     * @return distance if set
     */
    public Optional<Double> getTrackDistance() {
        return Optional.ofNullable(trackDistance);
    }

    /**
     * returns the latest LocalDateTime for this track. I a track start time is set, this is returned. Otherwise if a
     * first waypoint time is set, this is returned. Otherwise the routeStartTime is returned if set.
     *
     * @return an optional LocalDateTime
     */
    public Optional<LocalDateTime> getTrackTimestamp() {
        Optional<LocalDateTime> oTime = Optional.ofNullable(trackStartTime);
        if (!oTime.isPresent()) {
            oTime = Optional.ofNullable(firstWaypointTime);
            if (!oTime.isPresent()) {
                oTime = Optional.ofNullable(routeStartTime);
            }
        }
        return oTime;
    }
}
