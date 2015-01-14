/*
 * (c) 2015 P.J. Meisch (pj.meisch@sothawo.com).
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
