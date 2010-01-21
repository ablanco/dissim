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
