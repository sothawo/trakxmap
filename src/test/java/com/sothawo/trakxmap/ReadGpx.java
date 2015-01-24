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

import com.sothawo.trakxmap.generated.gpx.GpxType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * test program to read a gpx track file.
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
public class ReadGpx {
    public static void main(String[] args) {
        if (args.length > 0) {

            try {
                Unmarshaller unmarshaller =
                        JAXBContext.newInstance(GpxType.class.getPackage().getName()).createUnmarshaller();
                Path path = Paths.get(args[0]);
                @SuppressWarnings("unchecked")
                GpxType gpxType = ((JAXBElement<GpxType>) unmarshaller.unmarshal(path.toFile())).getValue();
                System.out.println(gpxType);
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
    }
}
