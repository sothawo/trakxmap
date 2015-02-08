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

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * An object with several statistical informations about a track.
 *
 * @author P.J.Meisch (pj.meisch@jaroso.de)
 */
public class TrackStatistics {
// ------------------------------ FIELDS ------------------------------

    /** hte timestamp of the first trackpoint in the track */
    private LocalDateTime trackStartTime;
    /** the timestamp of the first routepoint in the track */
    private LocalDateTime routeStartTime;
    /** the timestamp of the first waypoint in the track */
    private LocalDateTime firstWaypointTime;

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return "TrackStatistics{" +
                "trackStartTime=" + trackStartTime +
                ", routeStartTime=" + routeStartTime +
                ", firstWaypointTime=" + firstWaypointTime +
                '}';
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
     * if this is the first trackpoint, the timestamp is kept otherwise it is ignored.
     *
     * @param localDateTime
     *         time to check
     */
    public void addTrackTime(LocalDateTime localDateTime) {
        if (null != localDateTime && null == trackStartTime) {
            trackStartTime = localDateTime;
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
     * returns the latest LocalDateTime for this track. I a track start time is set, this is returned. Otherwise if a
     *  first waypoint time is set, this is returned. Otherwise the routeStartTime is returned if set.
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
