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
