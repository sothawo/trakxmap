# trakxmap

A desktop application to manage and display Track data, for example, gpx data from GPS devices. All data is stored
locally, no registration or account needed. Internet connection only is necessary to fetch the map data to display
the tracks.

More Information about the project can be found at [the sothawo website](http://www.sothawo.com/projects/trakxmap/).

## license

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

## building the program

this project is built using maven and needs Java 8. The library jar is created by running `mvn package`. This creates
 as well an application directory structure in _target/trakxmap_. You can copy this whole directory to a machine
 which has Java8 installed and start the program by using the scripts found in the _bin_ directory. Or start it on
 the machine where you did the build  using

_(cd target/trakxmap && ./bin/trakxmap)_

or all in one:

_mvn package && (cd target/trakxmap && ./bin/trakxmap)_


## version history

### 0.4.1

* uses JDK >= 1.8u40, Dialogs

### 0.4.0

* track elevation data is displayed below the map
* track info in the list view now contains name, timestamp, length and duration
* trackpoints contain information about the distance in meters to the track's start
* tracks can be deleted by using the context menu

### 0.3.1

* additional track info in the track list view
* track list is ordered, latest track at top

### 0.3.0.

* route lines are shown along with track lines if present in gpx file
* track data is persisted to the internal database

### 0.2.0.

* drag gpx-files into the application to load the track data or select via filechooser
* the selected track is shown in the map

### 0.1.0

* basic layout

### 0.0.1-SNAPSHOT

* intial version, nothing but a window with some text

