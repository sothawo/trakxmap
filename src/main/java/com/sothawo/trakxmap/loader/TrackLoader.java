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
package com.sothawo.trakxmap.loader;

import com.sothawo.trakxmap.db.Track;

import java.io.File;
import java.util.Optional;

/**
 * Implementations of this interface load track files into Track objects.
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
public interface TrackLoader {
// -------------------------- OTHER METHODS --------------------------

    /**
     * Try to load a Track from the given file. If the Track cannot be loaded, the return value should be empty;
     * additional info may be logged. Must be implemented threadsafe.
     *
     * @param file
     *         the file to be opened
     * @return Optional containing the loaded track if successful.
     */
    Optional<Track> load(File file);
}
