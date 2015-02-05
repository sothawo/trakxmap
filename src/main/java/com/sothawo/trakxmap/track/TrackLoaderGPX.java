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

import com.sothawo.trakxmap.db.Track;
import com.sothawo.trakxmap.generated.gpx.GpxType;
import com.sothawo.trakxmap.generated.gpx.MetadataType;
import com.sothawo.trakxmap.generated.gpx.WptType;
import com.sothawo.trakxmap.util.I18N;
import com.sothawo.trakxmap.util.PathTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * TrackLoader to load data from a GPX track file. If the gpx file contains more than one track, they are concatenated
 * to one.
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
public class TrackLoaderGPX implements TrackLoader {
// ------------------------------ FIELDS ------------------------------

    private final static Logger logger = LoggerFactory.getLogger(TrackLoaderGPX.class);

    /** JAXBContext, lazy construction */
    private JAXBContext jaxbContext;

// --------------------------- CONSTRUCTORS ---------------------------

    public TrackLoaderGPX() {
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface TrackLoader ---------------------

    @Override
    public Optional<Track> load(File file) {
        try {
            synchronized (this) {
                if (null == jaxbContext) {
                    jaxbContext = JAXBContext.newInstance(GpxType.class.getPackage().getName());
                }
            }
            @SuppressWarnings("unchecked")
            GpxType gpxType = ((JAXBElement<GpxType>) jaxbContext.createUnmarshaller().unmarshal(file)).getValue();
            return getTrack(file.getName(), gpxType);
        } catch (JAXBException e) {
            logger.info(I18N.get(I18N.ERROR_LOADING_TRACK, file.toString()));
            logger.trace("{}", file.toString(), e);
        }
        return Optional.empty();
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * creates a WayPoint object from a wptType object
     *
     * @param wptType
     *         JAXB wptType
     * @return WayPoint
     */
    private WayPoint createWayPoint(WptType wptType) {
        Double latitude = Optional.ofNullable(wptType.getLat()).orElse(BigDecimal.ZERO).doubleValue();
        Double longitude = Optional.ofNullable(wptType.getLon()).orElse(BigDecimal.ZERO).doubleValue();
        Double elevation = Optional.ofNullable(wptType.getEle()).orElse(BigDecimal.ZERO).doubleValue();

        ZonedDateTime timestamp = Optional.ofNullable(wptType.getTime())
                .flatMap((xmlGC) -> Optional.of(xmlGC.toGregorianCalendar().toZonedDateTime())).orElse(
                        null);

        String name = Optional.ofNullable(wptType.getName()).orElse("");
        return new WayPoint(latitude, longitude, elevation, timestamp, name);
    }

    /**
     * creates a Track Object from GPX Data
     *
     * @param filename
     *         name of the file where data was loaded from. used as default for the trackname.
     * @param gpxType
     *         the loaded gpx data
     * @return Track object, if it could be read from the gpxType;
     */
    private Optional<Track> getTrack(String filename, GpxType gpxType) {
        if (null == gpxType) {
            return Optional.empty();
        }
        Track track = new Track();

        Optional<MetadataType> optionalMeta = Optional.ofNullable(gpxType.getMetadata());

        // use info from metadata
        String metadataName = optionalMeta.flatMap((metadata) -> Optional.ofNullable(metadata.getName())).orElse("");

        // get the waypoints
        List<WayPoint> wayPoints = track.getWayPoints();
        Optional.ofNullable(gpxType.getWpt())
                .ifPresent((list) -> list.stream().map(this::createWayPoint).forEach(wayPoints::add));

        // process the tracks
        final List<String> trackNames = new ArrayList<>();
        List<WayPoint> trackPoints = track.getTrackPoints();
        Optional.ofNullable(gpxType.getTrk()).ifPresent((trks) -> trks.forEach((trk) -> {
            Optional.ofNullable(trk.getName()).ifPresent(trackNames::add);
            // do other stuff with track
            Optional.ofNullable(trk.getTrkseg()).ifPresent((trkSegs) -> trkSegs.forEach(
                    (trkSeg) -> Optional.ofNullable(trkSeg.getTrkpt()).ifPresent(
                            (trkPts) -> trkPts.stream().map(this::createWayPoint).forEach(trackPoints::add))));
        }));

        track.setName(buildTrackName(metadataName, trackNames, filename));
        track.setFilename(filename);
        logger.debug("{}", track);
        return Optional.of(track);
    }

    /**
     * creates the name for the track from the name elements found in the gpx data
     *
     * @param metadataName
     *         name from metadata section
     * @param trackNames
     *         names from tracks
     * @param filename
     *         to be used as default when no name elements are found
     * @return name for Track Object
     */
    private String buildTrackName(String metadataName, List<String> trackNames, String filename) {
        String nameFromTracks = String.join("/", trackNames);
        String name = metadataName.isEmpty() ? nameFromTracks :
                (metadataName.equals(nameFromTracks) ? metadataName : metadataName + '(' + nameFromTracks + ')');
        return name.isEmpty() ? PathTools.getFilenameFromPath(filename) : name;
    }
}
