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

import com.sothawo.trakxmap.util.I18N;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Optional;

/**
 * A TrackLoader that returns a track with a name that shows that the track could not be loaded. Last in the
 * TrackLoader chain.
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
public class TrackLoaderFail implements TrackLoader {
    private final static Logger logger = LoggerFactory.getLogger(TrackLoaderFail.class);

    @Override
    public Optional<Track> load(File file) {
        logger.debug("{}", file);
        return Optional.of(new Track(I18N.get(I18N.ERROR_LOADING_TRACK, file)));
    }
}
