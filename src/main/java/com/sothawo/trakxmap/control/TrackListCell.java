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
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * ListCell implementation for the Track List View.
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
public class TrackListCell extends ListCell<Track> {
// ------------------------------ FIELDS ------------------------------

    /**
     * the dummy track is for the cells that are displayed to fill the space but contain no real tracks; by using this,
     * the cell size looks better as the cells have a slightly alternating color and empty cells are displayed to the
     * bottom of the list.
     */
    private static Track dummyTrack;

// -------------------------- STATIC METHODS --------------------------

    static {
        dummyTrack = new Track(" ");
        dummyTrack.setFilename(" ");
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    protected void updateItem(Track item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(buildItemForTrack((null == item || empty) ? dummyTrack : item));
        setText(null);
    }

    /**
     * create a Node containing the track's info to display in the list.
     *
     * @param track
     *         thr track, may not be null
     * @return the Node to display
     */
    private Node buildItemForTrack(Track track) {
        VBox vbox = new VBox();
        vbox.getStyleClass().add("tracklist-cell");
        Label labelName = new Label(track.getName());
        labelName.getStyleClass().add("track-name");
        Label labelFilename = new Label(track.getFilename());
        labelFilename.getStyleClass().add("track-filename");
        vbox.getChildren().addAll(labelName, labelFilename);

        TrackStatistics statistics = track.getStatistics();
        statistics.getTrackTimestamp().ifPresent(t -> {
            String dateTime = t.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));

            String duration = "";
            if(statistics.getTrackDuration().isPresent()) {
                Duration d = statistics.getTrackDuration().get();
                long hours = d.toHours();
                d = d.minusHours(hours);
                long minutes = d.toMinutes();
                d = d.minusMinutes(minutes);
                long seconds = d.getSeconds();
                if (hours > 0) {
                    duration = String.format(" %d:%02d:%02d", hours, minutes, seconds);
                }else {
                    duration = String.format(" %d:%02d", minutes, seconds);
                }
            }

            // TODO: add 18n labels for the fields
            Label labelTimestamp =  I18N.labelForKey(I18N.LABEL_TRACKLISTCELL_TIMESTAMP, dateTime, duration);
            labelTimestamp.getStyleClass().add("track-timestamp");
            vbox.getChildren().add(labelTimestamp);
        });

        return vbox;
    }
}
