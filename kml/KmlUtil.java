package kml;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Point;

public class KmlUtil extends Kml {

	public Kml kml;

	public KmlUtil() {
		kml = new Kml();
	}

	/**
	 * Crea un lugar señalado en el mapa dandole un nombre y unas coordenadas
	 * 
	 * @param nombreCiudad
	 * @param latitud
	 * @param longitud
	 */
	public void setPlacemark(String nombreCiudad, double latitud,
			double longitud) {
		kml.createAndSetPlacemark().withName(nombreCiudad).withOpen(
				Boolean.TRUE).createAndSetPoint().addToCoordinates(latitud,
				longitud);
	}

	/**
	 * Crea un lugar señalado en el mapa dandole un nombre, unas coordenadas y
	 * una altura
	 * 
	 * @param nombreCiudad
	 * @param latitud
	 * @param longitud
	 */
	public void setPlacemark(String nombreCiudad, double latitud,
			double longitud, int altitude) {
		kml.createAndSetPlacemark().withName(nombreCiudad).withOpen(
				Boolean.TRUE).createAndSetPoint().addToCoordinates(latitud,
				longitud, altitude);
	}

	/**
	 * Muestra el archivo kml hasta el momento por consola
	 */
	public void showFile() {
		kml.marshal();
	}

	public void showCoordinates() {
		Placemark placemark = (Placemark) kml.getFeature();
		Point point = (Point) placemark.getGeometry();
		ArrayList<Coordinate> coordinates = (ArrayList<Coordinate>) point
				.getCoordinates();
		for (Coordinate coordinate : coordinates) {
			System.out.println(coordinate.getLatitude());
			System.out.println(coordinate.getLongitude());
			System.out.println(coordinate.getAltitude());
		}
	}

	/**
	 *Escribe el fichero con el nombre que le demos
	 * 
	 * @param nombreFichero
	 */
	public void writeFile(String nombreFichero) {
		try {
			kml.marshal(new File(nombreFichero + ".kml"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
