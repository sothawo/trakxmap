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

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * TrackPoint in track.
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
@Entity
@Table(name = "TRACKPOINT")
public final class TrackPoint extends Point {
// --------------------------- CONSTRUCTORS ---------------------------

    public TrackPoint() {
        super();
    }

    public TrackPoint(Double latitude, Double longitude, Double elevation, LocalDateTime timestamp) {
        super(latitude, longitude, elevation, timestamp);
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return "TrackPoint{ " + super.toString() + "}";
    }
}
