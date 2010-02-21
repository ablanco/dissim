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
import java.util.List;

import util.HexagonalGrid;
import util.Logger;
import util.Scenario;
import util.jcoord.LatLng;
import de.micromata.opengis.kml.v_2_2_0.AltitudeMode;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.KmlFactory;
import de.micromata.opengis.kml.v_2_2_0.LinearRing;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Polygon;
import de.micromata.opengis.kml.v_2_2_0.TimeSpan;

public class KmlWriter {

	protected Kml kml;
	// protected Document document;
	protected Folder folder;
	/**
	 * Copy of First Grid, we need it to see changes through time
	 */
	protected HexagonalGrid oldGrid;
	/**
	 * Some polygons need diferents names
	 */
	protected static long cont;
	/**
	 * Size in meters of the circunflex circle of the hexagon
	 */
	protected short tileSize;
	/**
	 * Dimension X from Scenario
	 */
	protected int dimX;
	/**
	 * Dimension Y from Scenario
	 */
	protected int dimY;
	/**
	 * Begin time of the simulation step
	 */
	protected String beginTime;
	/**
	 * End time of the simulation step
	 */
	protected String endTime;
	protected Logger kmlLog = new Logger();

	public KmlWriter() {
		Scenario scene = Scenario.getCurrentScenario();
		kml = KmlFactory.createKml();
		dimX = scene.getGridSize()[0];
		dimY = scene.getGridSize()[1];

		tileSize = scene.getTileSize();

		oldGrid = new HexagonalGrid(dimX, dimY);
		HexagonalGrid grid = scene.getGrid();
		for (int i = 0; i < dimX; i++) {
			for (int j = 0; j < dimY; j++) {
				oldGrid.setTerrainValue(i, j, grid.getTerrainValue(i, j));
			}
		}
		cont = 0;
	}

	/**
	 * New kml file of the current kml
	 * 
	 * @param fileName
	 */
	public void createKmlFile(String fileName) {
		try {
			kml.marshal(new File(fileName + ".kml"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * New kmz file of the current kml
	 * 
	 * @param fileName
	 */
	public void createKmzFile(String fileName) {
		try {
			kml.marshalAsKmz(fileName + ".kmz");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * A folder is a container where you can put several things inside
	 */
	protected void newFolder(String name, String description) {
		folder = kml.createAndSetFolder().withName(name).withOpen(true)
				.withDescription(description);
	}
	protected Placemark newPlaceMark(String name) {
		return folder.createAndAddPlacemark().withName(name);
	}

	public void drawPolygon(String name, List<LatLng> borderLine) {
		Polygon polygon = newPolygon(newPlaceMark(name));
		switch (borderLine.size()) {
		case 0:
			throw new IllegalArgumentException("Poligon canot be empty");
		case 1:
			drawHexagonBorders(polygon, borderLine.get(0));
			break;
		default:
			drawPolygon(name, borderLine);
			break;
		}
	}

	public void drawHexagon(String name, LatLng borderLine) {
		Polygon polygon = newPolygon(newPlaceMark(name));
		drawHexagonBorders(polygon, borderLine);
	}

	protected void drawHexagonBorders(Polygon polygon, LatLng coord) {

		LinearRing l = polygon.createAndSetOuterBoundaryIs()
				.createAndSetLinearRing();

		l.addToCoordinates(coord.metersToDegrees(tileSize / 2, 0)
				.toGoogleString());
		l.addToCoordinates(coord.metersToDegrees(tileSize / 4, tileSize / 2)
				.toGoogleString());
		l.addToCoordinates(coord.metersToDegrees(-tileSize / 4, tileSize / 2)
				.toGoogleString());
		l.addToCoordinates(coord.metersToDegrees(-tileSize / 2, 0)
				.toGoogleString());
		l.addToCoordinates(coord.metersToDegrees(-tileSize / 4, -tileSize / 2)
				.toGoogleString());
		l.addToCoordinates(coord.metersToDegrees(tileSize / 4, -tileSize / 2)
				.toGoogleString());
		l.addToCoordinates(coord.metersToDegrees(tileSize / 2, 0)
				.toGoogleString());
	}



	protected Polygon newPolygon(Placemark placeMark) {
		return placeMark.createAndSetPolygon().withExtrude(true)
				.withAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);
	}

	protected void drawPolygonBorders(Polygon polygon, List<LatLng> borderLine) {
		LinearRing l = polygon.createAndSetOuterBoundaryIs()
				.createAndSetLinearRing();
		LatLng z = borderLine.get(0);
		for (LatLng c : borderLine) {
			l.addToCoordinates(c.toGoogleString());
		}
		l.addToCoordinates(z.toGoogleString());
	}

	protected void setTimeSpan(Placemark placeMark) {
		TimeSpan t = new TimeSpan();
		t.setBegin(beginTime);
		t.setEnd(endTime);

		placeMark.setTimePrimitive(t);
	}

}
