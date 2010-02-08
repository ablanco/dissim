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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import util.Scenario;
import util.flood.FloodHexagonalGrid;
import util.flood.FloodScenario;
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
	private Document document;
	private Scenario scene;
	private long cont;

	public KmlWriter() {
		kml = new Kml();
		scene = Scenario.getCurrentScenario();
		cont = 0;
	}

	/**
	 * Buil Kml File from Scene
	 * 
	 * @param fileName
	 */

	public void buildKmlAltitudesMap(String fileName) {

		if (scene != null) {
			// Creation of the document
			createDocument("Land Elevation Info", scene.getDescription());
			// Creation of the Polygon
			List<Coordinate>[] polygon = createPolygon("Land Map",
					"Land Map Elevations");
			// Adding coordinates and elevation
			for (int i = 0; i < scene.getGridSize()[0]; i++) {
				for (int j = 0; j < scene.getGridSize()[1]; j++) {
					LatLng aux = scene.tileToCoord(i, j);
					double alt = AltitudeWS.getElevation(aux);
					// System.out.println(cont+") "+aux.toString()+" Altitude :"+alt);
					addCoordinateToPolygon(polygon, aux, alt, true);
				}
			}
			// Now creates the kml File
			createKmzFile(fileName);
		} else {
			System.err.println("No se ha inicializado la escena");
		}

	}

	/**
	 * Crea el Fichero kml de nombre nombreFichero
	 * 
	 * @param nombreFichero
	 */
	public void createKmlFile(String fileName) {
		try {
			kml.marshal(new File(fileName + ".kml"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void createKmzFile(String fileName) {
		try {
			kml.marshalAsKmz(fileName + ".kmz");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void createDocument(String name, String description) {
		document = new Document();
		kml.setFeature(document);
		document.setName(name);
		document.setDescription(description);
		document.setOpen(false);
	}

	/**
	 * Return polygon for adding coordinates [0] Outerlinnerar [1] innerLinear,
	 * default Relative_to_ground
	 * 
	 * @param name
	 * @param description
	 * @return
	 */
	public List<Coordinate>[] createPolygon(String name, String description) {
		// All the steps needed to build a Polygon on KML
		List<Coordinate>[] p = new List[2];
		Placemark placemark = new Placemark();
		document.getFeature().add(placemark);
		placemark.setName(name);
		placemark.setDescription(description);
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
		p[0] = outercoord;
		Boundary innerboundary = new Boundary();
		polygon.getInnerBoundaryIs().add(innerboundary);

		LinearRing innerlinearring = new LinearRing();
		innerboundary.setLinearRing(innerlinearring);

		List<Coordinate> innercoord = new ArrayList<Coordinate>();
		innerlinearring.setCoordinates(innercoord);
		p[1] = innercoord;

		return p;
	}

	/**
	 * Adds coordinate to polygon, True if outer coord, False is inner coord
	 * 
	 * @param polygon
	 * @param coord
	 * @param outer
	 */
	public void addCoordinateToPolygon(List<Coordinate>[] polygon,
			LatLng coord, double altitude, boolean outer) {
		if (outer) {
			polygon[0].add(new Coordinate(coord.getLng(), coord.getLat(),
					altitude));
		} else {
			polygon[1].add(new Coordinate(coord.getLng(), coord.getLat(),
					altitude));
		}
	}

	/**
	 * Create an hexagon with centrum in the coords
	 * 
	 * @param coord
	 * @param alt
	 */
	public void createHexagon(LatLng coord) {
		Polygon polygon = document.createAndAddPlacemark().withName(
				"tile" + cont).createAndSetPolygon().withExtrude(true)
				.withAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);

		double ilat = scene.getLatInc();
		double ilng = scene.getLngInc();

		LatLng WW = new LatLng(coord.getLat(), coord.getLng() + ilng, coord
				.getAltitude());
		LatLng WN = new LatLng(coord.getLat() + ilat,
				coord.getLng() + ilng / 2, coord.getAltitude());
		LatLng EN = new LatLng(coord.getLat() + ilat,
				coord.getLng() - ilng / 2, coord.getAltitude());
		LatLng EE = new LatLng(coord.getLat(), coord.getLng() - ilng, coord
				.getAltitude());
		LatLng ES = new LatLng(coord.getLat() - ilat,
				coord.getLng() - ilng / 2, coord.getAltitude());
		LatLng WS = new LatLng(coord.getLat() - ilat,
				coord.getLng() + ilng / 2, coord.getAltitude());

		polygon.createAndSetOuterBoundaryIs().createAndSetLinearRing()
				.addToCoordinates(WW.toGoogleString()).addToCoordinates(
						WN.toGoogleString()).addToCoordinates(
						EN.toGoogleString()).addToCoordinates(
						EE.toGoogleString()).addToCoordinates(
						ES.toGoogleString()).addToCoordinates(
						WS.toGoogleString());
		System.out.println("Centre :" + coord + ", WN: " + WN + ", ES: " + ES);
		cont++;

	}

	public void createTimeLine() {
		if (scene instanceof FloodScenario) {
			createDocument("Flooding State Level", "RainFalling Motherfuckers");
			FloodScenario flood = (FloodScenario) scene;
			HashSet<int[]> tiles = ((FloodHexagonalGrid)flood.getGrid()).getModCoordAndReset();
			for (int[] tile : tiles){
				LatLng coord = scene.tileToCoord(tile[0], tile[1]);
				createHexagon(new LatLng(coord.getLat(),coord.getLng(),(short)tile[2]));
			}
			createKmlFile(scene.getName());
		}
	}

}
