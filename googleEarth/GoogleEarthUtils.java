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

package googleEarth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import util.jcoord.LatLng;
import de.micromata.opengis.kml.v_2_2_0.AltitudeMode;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LinearRing;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Polygon;
import de.micromata.opengis.kml.v_2_2_0.TimeSpan;

public class GoogleEarthUtils {

	/**
	 * Some polygons need diferents names
	 */
	protected static long cont = 0;
	/**
	 * Size in meters of the circunflex circle of the hexagon
	 */
	protected static int tileSize;

	public static void setTileSize(int tileSize) {
		GoogleEarthUtils.tileSize = tileSize;
	}

	/**
	 * New kml file of the current kml
	 * 
	 * @param fileName
	 */
	public static void createKmlFile(Kml kml, String fileName) {
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
	public static void createKmzFile(Kml kml, String fileName) {
		try {
			kml.marshalAsKmz(fileName + ".kmz");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * A folder is a container where you can put several things inside
	 */
	public static Folder newFolder(Kml kml, String name, String description) {
		return kml.createAndSetFolder().withName(name).withOpen(true)
				.withDescription(description);
	}

	/**
	 * A placemark to geolocate things
	 * 
	 * @param name
	 *            of the placemark
	 * @return placeMark to geolocate things
	 */
	public static Placemark newPlaceMark(Folder folder, String name) {
		return folder.createAndAddPlacemark().withName(name);
	}

	/**
	 * Draw a polygon from the sequence of points
	 * 
	 * @param name
	 *            of the polygon
	 * @param borderLine
	 *            borders of the polygon
	 */
	public static void drawPolygon(Folder folder, String name,
			List<LatLng> borderLine) {
		Polygon polygon = newPolygon(newPlaceMark(folder, name));
		switch (borderLine.size()) {
		case 0:
			throw new IllegalArgumentException("Poligon canot be empty");
		case 1:
			drawHexagonBorders(polygon, borderLine.get(0));
			break;
		default:
			drawPolygon(folder, name, borderLine);
			break;
		}
	}

	/**
	 * Draw an Hexagon from the locaion borderline
	 * 
	 * @param name
	 *            of the hexagon
	 * @param borderLine
	 *            coord of the hexagon
	 */
	public void drawHexagon(Folder folder, String name, LatLng borderLine) {
		Polygon polygon = newPolygon(newPlaceMark(folder, name));
		drawHexagonBorders(polygon, borderLine);
	}

	protected static void drawHexagonBorders(Polygon polygon, LatLng coord) {

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

	/**
	 * Creates Polygon Object
	 * 
	 * @param placeMark
	 * @return
	 */
	protected static Polygon newPolygon(Placemark placeMark) {
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

	/**
	 * Sets when the event happends
	 * 
	 * @param placeMark
	 */
	protected static void setTimeSpan(Placemark placeMark, String beginTime, String endTime) {
		TimeSpan t = new TimeSpan();
		if (beginTime != null){
			t.setBegin(beginTime);	
		}
		if (endTime != null){
			t.setEnd(endTime);	
		}
		placeMark.setTimePrimitive(t);
	}

}
