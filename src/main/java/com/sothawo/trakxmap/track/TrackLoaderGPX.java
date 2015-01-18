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

import com.sothawo.trakxmap.generated.gpx.GpxType;
import com.sothawo.trakxmap.generated.gpx.MetadataType;
import com.sothawo.trakxmap.generated.gpx.TrkType;
import com.sothawo.trakxmap.util.I18N;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * TrackLoader to load data from a GPX track file. If the gpx file contains more than one track, they are
 * concatenated to one.
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
public class TrackLoaderGPX implements TrackLoader {
// ------------------------------ FIELDS ------------------------------

    private final static Logger logger = LoggerFactory.getLogger(TrackLoaderGPX.class);

    /** Unmarshaller, lazy construction */
    private Unmarshaller unmarshaller = null;

// --------------------------- CONSTRUCTORS ---------------------------

    public TrackLoaderGPX() {
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface TrackLoader ---------------------

    @Override
    public Optional<Track> load(File file) {
        try {
            synchronized (this) {
                if (null == unmarshaller) {
                    unmarshaller =
                            JAXBContext.newInstance(GpxType.class.getPackage().getName()).createUnmarshaller();
                }
            }
            @SuppressWarnings("unchecked")
            GpxType gpxType = ((JAXBElement<GpxType>) unmarshaller.unmarshal(file)).getValue();
            return Optional.of(getTrack(file.getName(), gpxType));
        } catch (JAXBException e) {
            logger.info(I18N.get(I18N.ERROR_LOADING_TRACK, file.toString()));
            logger.trace("{}", file.toString(), e);
        }
        return Optional.empty();
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * creates a Track Object from GPX Data
     *
     * @param filename
     *         name of the file where data was loaded from. used as default for the trackname.
     * @param gpxType
     *         the loaded gpx data
     * @return Track object
     */
    private Track getTrack(String filename, GpxType gpxType) {
        Track track = new Track();
        String defaultName = track.getName();

        Optional<MetadataType> optionalMeta = Optional.ofNullable(gpxType.getMetadata());
        // use info from metadata
        optionalMeta.ifPresent((metadata) -> Optional.ofNullable(metadata.getName()).ifPresent(track::setName));

        // process the tracks
        Optional.ofNullable(gpxType.getTrk()).ifPresent((trks) -> trks.forEach((trk) -> addTrkTypeToTrack(trk, track)));

        // if no name was set in the track data, use the filename
        if (defaultName.equals(track.getName())) {
            track.setName(filename);
        }
        return track;
    }

    /**
     * adds the data from the gpx track to the track object
     *
     * @param trkType
     * @param track
     */
    private void addTrkTypeToTrack(TrkType trkType, Track track) {
        StringBuilder sb = new StringBuilder(track.getName());
        Optional.ofNullable(trkType.getName()).ifPresent((name) -> {
            sb.append('/');
            sb.append(name);
        });
        track.setName(sb.toString());
    }
}
