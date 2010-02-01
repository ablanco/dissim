//    Flood and evacuation simulator using multi-agent technology
//    Copyright (C) 2010 Alejandro Blanco and Manuel Gomar
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package kml;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import util.Scenario;
import util.jcoord.LatLng;
import webservices.AltitudeWS;
import de.micromata.opengis.kml.v_2_2_0.AltitudeMode;
import de.micromata.opengis.kml.v_2_2_0.Boundary;
import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LinearRing;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Polygon;

public class KmlWriter {

	private Kml kml;

	public KmlWriter() {
		kml = new Kml();
	}

	/**
	 * Buil Kml File from Scene
	 * 
	 * @param fileName
	 */
	public void buildKmlAltitudesMap(String fileName) {
		Scenario scene = Scenario.getCurrentScenario();

		if (scene != null) {
			// All the steps needed to build a Polygon on KML
			Document document = new Document();
			kml.setFeature(document);
			document.setName("Land Elevation Info");
			document.setDescription(scene.getDescription());
			document.setOpen(false);
			Placemark placemark = new Placemark();
			document.getFeature().add(placemark);
			placemark.setName("Coordinates");
			Polygon polygon = new Polygon();
			placemark.setGeometry(polygon);

			polygon.setExtrude(true);
			polygon.setAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);
			Boundary outerboundary = new Boundary();
			polygon.setOuterBoundaryIs(outerboundary);

			LinearRing outerlinearring = new LinearRing();
			outerboundary.setLinearRing(outerlinearring);

			List<Coordinate> outercoord = new ArrayList<Coordinate>();
			outerlinearring.setCoordinates(outercoord);

			// Now iterate on the coords and get altitudes
			int cont=0;
			for (int i = 0; i < scene.getGridSize()[0]; i++) {
				for (int j = 0; j < scene.getGridSize()[1]; j++) {
					LatLng aux = scene.tileToCoord(i, j);
					double alt = AltitudeWS.getElevation(aux);
					System.out.println(cont+") "+aux.toString()+" Altitude :"+alt);
					outercoord.add(new Coordinate(aux.getLng(), aux.getLat(),
							alt));
					cont++;
				}
			}
			// Now creates the kml File
			createKmlFile(fileName);
		}
	}

	/**
	 * Crea el Fichero kml de nombre nombreFichero
	 * 
	 * @param nombreFichero
	 */
	private void createKmlFile(String fileName) {
		try {
			kml.marshal(new File(fileName + ".kml"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
