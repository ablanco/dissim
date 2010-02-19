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
import java.util.SortedSet;
import java.util.TreeSet;

import util.HexagonalGrid;
import util.Logger;
import util.Scenario;
import util.jcoord.LatLng;
import de.micromata.opengis.kml.v_2_2_0.AltitudeMode;
import de.micromata.opengis.kml.v_2_2_0.ColorMode;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.KmlFactory;
import de.micromata.opengis.kml.v_2_2_0.LinearRing;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.PolyStyle;
import de.micromata.opengis.kml.v_2_2_0.Polygon;
import de.micromata.opengis.kml.v_2_2_0.Style;
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

	protected SortedSet<Short> altitudes;

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
		altitudes = new TreeSet<Short>();
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

	/*
	 * public void createDocument(String name, String description) { document =
	 * new Document(); kml.setFeature(document); document.setName(name);
	 * document.setDescription(description); document.setOpen(false); }
	 */
	/**
	 * A folder is a container where you can put several things inside
	 */
	public void openFolder(String name, String description) {
		folder = kml.createAndSetFolder().withName(name).withOpen(true)
				.withDescription(description);
	}

	/**
	 * Return polygon for adding coordinates [0] Outerlinnerar [1] innerLinear,
	 * default Relative_to_ground
	 * 
	 * @param name
	 * @param description
	 * @return
	 */
	public void createPolygon(String name, List<LatLng> borderLine) {
		if (borderLine.size() < 0) {
			throw new IllegalArgumentException("Poligon canot be empty");
		}

		if (borderLine.size() == 1) {
			createHexagon(name, borderLine.get(0));
		} else {

			LatLng z = borderLine.get(0);
			createWaterStyleAndColor(z.getAltitude());

			Placemark placeMark = folder.createAndAddPlacemark().withName(
					name + " " + cont);

			setTimeSpan(placeMark);
			setWaterColorToPlaceMark(placeMark, z.getAltitude());

			Polygon polygon = placeMark.createAndSetPolygon().withExtrude(true)
					.withAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);

			LinearRing l = polygon.createAndSetOuterBoundaryIs()
					.createAndSetLinearRing();

			for (LatLng c : borderLine) {
				l.addToCoordinates(c.toGoogleString());
			}
			l.addToCoordinates(z.toGoogleString());
		}
	}

	/**
	 * Creates a new Water color for this altitude
	 * @param z
	 */
	protected void createWaterStyleAndColor(short z) {

		if (!altitudes.contains(z)) {
			Style style = new Style();
			folder.getStyleSelector().add(style);
			style.setId("BLUE" + z);

			PolyStyle polyStyle = new PolyStyle();
			style.setPolyStyle(polyStyle);
			//TODO por ahora solo permite 16 tonalizades de azul
			polyStyle.setColor("ffff"+Integer.toHexString(z* 16)+"00");
			polyStyle.setColorMode(ColorMode.NORMAL);
			altitudes.add(z);
		}
	}

	protected void setTimeSpan(Placemark placeMark) {
		TimeSpan t = new TimeSpan();
		t.setBegin(beginTime);
		t.setEnd(endTime);

		placeMark.setTimePrimitive(t);
	}

	protected void setWaterColorToPlaceMark(Placemark placeMark, short z) {
		// Adding to BLUE
		placeMark.setStyleUrl("BLUE" + z);
	}

	/**
	 * Create an hexagon with centrum in the coords
	 * 
	 * @param coord
	 * @param alt
	 */
	public void createHexagon(String name, LatLng coord) {

		Placemark placeMark = folder.createAndAddPlacemark().withName(
				name + " " + cont);

		createWaterStyleAndColor(coord.getAltitude());
		setTimeSpan(placeMark);
		setWaterColorToPlaceMark(placeMark, coord.getAltitude());

		Polygon polygon = placeMark.createAndSetPolygon().withExtrude(true)
				.withAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);
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

}
