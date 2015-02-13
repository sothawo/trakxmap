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
package com.sothawo.trakxmap.control;

import com.sothawo.trakxmap.db.Track;
import com.sothawo.trakxmap.util.I18N;
import com.sothawo.trakxmap.util.TrackStatistics;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;

/**
 * ListCell implementation for the Track List View.
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
public class TrackListCell extends ListCell<Track> {
// ------------------------------ FIELDS ------------------------------


// -------------------------- OTHER METHODS --------------------------

    @Override
    protected void updateItem(Track item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(buildItemForTrack((null == item || empty) ? null : item));
        setText(null);
    }

    /**
     * create a Node containing the track's info to display in the list. the info is made up of three lines:
     *
     * <ul>
     * <li>the track's name</li>
     * <li>the track's date and time</li>
     * <li>the track's duration and length</li>
     * </ul>
     *
     * distance is shown in Meters/kilometers
     *
     * @param track
     *         the track, may not be null
     *
     * @return the Node to display
     */
    private Node buildItemForTrack(Track track) {
        VBox vbox = new VBox();
        vbox.getStyleClass().add("tracklist-cell");
        Label[] labels = buildLabelsForTrack(track);

        Label labelName = labels[0];
        labelName.getStyleClass().add("tracklist-cell-primary");
        Label labelDateAndTime = labels[1];
        labelDateAndTime.getStyleClass().add("tracklist-cell-secondary");
        Label durationAndLength = labels[2];
        durationAndLength.getStyleClass().add("tracklist-cell-secondary");

        vbox.getChildren().addAll(labels);


        return vbox;
    }

    /**
     * creates the 3 Labels for a track.
     *
     * @param track
     *         the track
     *
     * @return the Labels. Always an array with 3 Labels which are not null.
     */
    private Label[] buildLabelsForTrack(Track track) {
        Label[] labels = new Label[3];
        if (null != track) {
            // first Label: name
            labels[0] = new Label(track.getName());

            // Second label: date and time
            TrackStatistics statistics = track.getStatistics();
            Optional<LocalDateTime> trackTimestampOptional = statistics.getTrackTimestamp();
            if (trackTimestampOptional.isPresent()) {
                String dateTime = trackTimestampOptional.get().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle
                        .MEDIUM));
                labels[1] = new Label(dateTime);
            } else {
                labels[1] = new Label();
            }

            // third label: duration and length
            String duration = "--:--";
            if(statistics.getTrackDuration().isPresent()) {
                Duration d = statistics.getTrackDuration().get();
                long hours = d.toHours();
                d = d.minusHours(hours);
                long minutes = d.toMinutes();
                d = d.minusMinutes(minutes);
                long seconds = d.getSeconds();
                if (hours > 0) {
                    duration = String.format("%d:%02d:%02d", hours, minutes, seconds);
                } else {
                    duration = String.format("%d:%02d", minutes, seconds);
                }
            }
            String distance = "--.--";
            if (statistics.getTrackDistance().isPresent()) {
                Double d = statistics.getTrackDistance().get();
                if (d >= 1000.0) {
                    distance = String.format("%.2f km", d / 1000.0);
                } else {
                    distance = String.format("%.0f m", d);
                }
            }
            labels[2]= I18N.labelForKey(I18N.LABEL_TRACKLISTCELL_DURATIONLENGTH, duration, distance);
        } else {
            for (int i = 0; i < labels.length; i++) {
                labels[i] = new Label();
            }
        }
        return labels;
    }
}
