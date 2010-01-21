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

package pruebas;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Point;

public class HelloKML {

	/**
	 * HelloKML Sample project
	 */
	public static void main(String[] args) throws FileNotFoundException {

		Kml kml = new Kml();
		kml.createAndSetPlacemark().withName("Nueva Orleans, LA, USA")
				.withOpen(Boolean.TRUE).createAndSetPoint().addToCoordinates(
						-90.07507200013812, 29.95464801161122);
		// marshals to console
		kml.marshal();
		// marshals into file
		kml.marshal(new File("HelloKml.kml"));

		kml = Kml.unmarshal(new File("HelloKml.kml"));
		final Placemark placemark = (Placemark) kml.getFeature();
		Point point = (Point) placemark.getGeometry();
		ArrayList<Coordinate> coordinates = (ArrayList<Coordinate>) point
				.getCoordinates();
		for (Coordinate coordinate : coordinates) {
			System.out.println(coordinate.getLatitude());
			System.out.println(coordinate.getLongitude());
			System.out.println(coordinate.getAltitude());
		}

	}
}
