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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * RoutePoint in track.
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
@Entity
@Table(name = "ROUTEPOINT")
public final class RoutePoint extends Point {
// ------------------------------ FIELDS ------------------------------

    /** name */
    private String name;

// --------------------------- CONSTRUCTORS ---------------------------

    public RoutePoint() {
    }

    /**
     * constructor to seteverything besides the sequence
     *
     * @param latitude
     *         latitude
     * @param longitude
     *         longitude
     * @param elevation
     *         elevation
     * @param timestamp
     *         timestamp
     * @param name
     *         name
     */
    public RoutePoint(Double latitude, Double longitude, Double elevation, LocalDateTime timestamp, String name) {
        super(latitude, longitude, elevation, timestamp);
        this.name = name;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    @Column(name = "name", nullable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return "RoutePoint{" +
                "name='" + name + '\'' + super.toString() +
                "} ";
    }
}
