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

import com.sothawo.trakxmap.track.Track;
import javafx.scene.control.ListCell;

/**
 * ListCell implementation for the Track List View.
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
public class TrackListCell extends ListCell<Track> {
// -------------------------- OTHER METHODS --------------------------

    @Override
    protected void updateItem(Track item, boolean empty) {
        super.updateItem(item, empty);
        if (null == item || empty) {
            setGraphic(null);
            setText(null);
        }else {
            setText(item.getName());
        }
    }
}
