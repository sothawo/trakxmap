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
package com.sothawo.trakxmap;

import com.sothawo.trakxmap.loader.TrackLoaderGPX;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * reads a gpx file and writes the tracks lat/lon as csv.
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
public class Gpx2csv {
// --------------------------- main() method ---------------------------

    public static void main(String[] args) {
        if (2 != args.length) {
            System.err.println("args: infile outfile");
            return;
        }
        new TrackLoaderGPX().load(new File(args[0])).ifPresent((track) -> {
            try (PrintWriter writer = new PrintWriter(args[1], "UTF-8")) {
                track.getTrackPoints().forEach((trackPoint) -> {
                    writer.print(trackPoint.getCoordinate().getLatitude());
                    writer.print(';');
                    writer.println(trackPoint.getCoordinate().getLongitude());
                });
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
