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

import util.HexagonalGrid;
import util.Scenario;
import util.flood.FloodHexagonalGrid;
import util.flood.FloodScenario;
import util.jcoord.LatLng;
import de.micromata.opengis.kml.v_2_2_0.Boundary;
import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LinearRing;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Polygon;

public class KmlReader extends Kml {

	private Kml kml;

	/**
	 * Opens a kml file for data extraction
	 * 
	 * @param fileName
	 * @throws FileNotFoundException
	 */
	public KmlReader(String fileName) {
		if (fileName.contains(".kmz")){
			try {
				kml = Kml.unmarshalFromKmz(new File(fileName))[0];
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			kml = Kml.unmarshal(new File(fileName));	
		}
		

		Scenario scene = Scenario.getCurrentScenario();
		if (scene != null) {
			if (kml == null) {
				try {
					throw new FileNotFoundException();
				} catch (FileNotFoundException e) {
					System.err.println("No se puede encontrar " + fileName);
					e.printStackTrace();
				}
			} else {
				getSceneInfo(scene);
			}
		}
	}

	/**
	 * Get the scene info from the kml File
	 * 
	 * @param scene
	 */
	// Nota: Al ser un método privado no importa que el Scenario se le pase como
	// parámetro
	private void getSceneInfo(Scenario scene) {
		// Geting Document
		Document doc = (Document) kml.getFeature();
		// Getin description Info
		String s = doc.getDescription();
		String info[] = s.split(",");
		// Setting Scenario Atributes
		scene.setDescription(s);
		scene.setGeoData(new LatLng(Double.parseDouble(info[1]), Double
				.parseDouble(info[2])), new LatLng(Double.parseDouble(info[3]),
				Double.parseDouble(info[4])), Integer.parseInt(info[0]));
	}

	/**
	 * Retuns hexGrid form the kmlFile
	 * 
	 * @param scene
	 * 
	 * @return
	 */
	public HexagonalGrid getHexagonalGrid() {
		Scenario scene = Scenario.getCurrentScenario();
		HexagonalGrid hexGrid = null;
		if (scene != null) {
			int gridSize[] = scene.getGridSize();

			if (scene instanceof FloodScenario) {
				hexGrid = new FloodHexagonalGrid(gridSize[0], gridSize[1], true);
				// TODO - Leer boolean del kml
			} else {
				hexGrid = new HexagonalGrid(gridSize[0], gridSize[1]);
			}

			// Begins the extraction of points
			Document doc = (Document) kml.getFeature();
			// Placemark placemark = (Placemark) doc.getFeature();
			Placemark place = (Placemark) (doc.getFeature()).get(0);
			Polygon pol = (Polygon) place.getGeometry();
			Boundary bound = pol.getOuterBoundaryIs();
			LinearRing lr = bound.getLinearRing();
			for (Coordinate coordinate : lr.getCoordinates()) {
				LatLng coord = new LatLng(coordinate.getLatitude(), coordinate
						.getLongitude());
				int pos[] = scene.coordToTile(coord);
				System.out.println(pos[0]+","+pos[1]+" ("+coordinate.getLatitude()+", "+coordinate.getLongitude()+") "+coordinate.getAltitude()+"m");
				hexGrid.setTerrainValue(pos[0], pos[1], coordinate
						.getAltitude());
			}
		}
		return hexGrid;
	}

	/**
	 * Muestra el archivo kml hasta el momento por consola
	 */
	public void showFile() {
		kml.marshal();
	}

}
