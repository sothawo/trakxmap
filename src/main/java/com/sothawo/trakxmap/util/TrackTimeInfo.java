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
 * An object encapsulating the different time informations of a track.
 *
 * @author P.J.Meisch (pj.meisch@jaroso.de)
 */
public class TrackTimeInfo {
// ------------------------------ FIELDS ------------------------------

    private LocalDateTime latestTrackTime;
    private LocalDateTime latestRouteTime;
    private LocalDateTime latestWaypointTime;

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return "TrackTimeInfo{" +
                "latestTrackTime=" + latestTrackTime +
                ", latestRouteTime=" + latestRouteTime +
                ", latestWaypointTime=" + latestWaypointTime +
                '}';
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * checks if the given time is after the already seen latest route time. If so, th given time ist stored
     *
     * @param localDateTime
     *         time to check
     */
    public void addRouteTime(LocalDateTime localDateTime) {
        if (null != localDateTime && (null == latestRouteTime || localDateTime.isAfter(latestRouteTime))) {
            latestRouteTime = localDateTime;
        }
    }

    /**
     * checks if the given time is after the already seen latest track time. If so, th given time ist stored
     *
     * @param localDateTime
     *         time to check
     */
    public void addTrackTime(LocalDateTime localDateTime) {
        if (null != localDateTime && (null == latestTrackTime || localDateTime.isAfter(latestTrackTime))) {
            latestTrackTime = localDateTime;
        }
    }

    /**
     * checks if the given time is after the already seen latest waypoint time. If so, th given time ist stored
     *
     * @param localDateTime
     *         time to check
     */
    public void addWaypointTime(LocalDateTime localDateTime) {
        if (null != localDateTime && (null == latestWaypointTime || localDateTime.isAfter(latestWaypointTime))) {
            latestWaypointTime = localDateTime;
        }
    }

    /**
     * returns the latest LocalDateTime for this track. I a latest track time is set, this is returned. Otherwise if a
     * latest waypoint time is set, this is returned. Otherwise the latest Waypoint time is returned.
     *
     * @return an optional LocalDateTime
     */
    public Optional<LocalDateTime> getLatestTime() {
        Optional<LocalDateTime> oTime = Optional.ofNullable(latestTrackTime);
        if (!oTime.isPresent()) {
            oTime = Optional.ofNullable(latestRouteTime);
            if (!oTime.isPresent()) {
                oTime = Optional.ofNullable(latestWaypointTime);
            }
        }
        return oTime;
    }
}
