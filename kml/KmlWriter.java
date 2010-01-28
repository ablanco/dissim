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

	private double precision = 0.0001;

	/***
	 * Este Constructor creará un kml con ese nombre de fichero
	 * 
	 * @param nombreFichero
	 */
	public KmlWriter(String nombreFichero) {
		kml = new Kml();
		createKmlFile(nombreFichero);
	}

	/***
	 * Este constructor creará un kml que abarque desde los dos puntos de ese
	 * rectángulo
	 * 
	 * @param izqSupLat
	 * @param izqSupLon
	 * @param derInfLat
	 * @param derInfLon
	 */
	public KmlWriter(String nombreFichero, double izqSupLat, double izqSupLon,
			double derInfLat, double derInfLon) {

		kml = new Kml();

		// Creamos una rejilla del tamaño adeacuado
		buildKmlMap(izqSupLat, izqSupLon, derInfLat, derInfLon);
		// Creamos el fichero kml con las coordenadas y las alturas
		createKmlFile(nombreFichero);
	}

	public void buildKmlMap(double izqSupLat, double izqSupLon,
			double derInfLat, double derInfLon) {
		// Tenemos que averiguar el tamaño de la rejilla
		int dimX = (int) Math.abs(Math.abs(izqSupLat / precision)
				- Math.abs(derInfLat / precision));
		int dimY = (int) Math.abs(Math.abs(izqSupLon / precision)
				- Math.abs(derInfLon / precision));
		System.out.println("Tam en x:" + dimX + ", tam en y:" + dimY);
		System.out.println("Coordenadas : ");

		// Ahora creamos el poligono que representa al suelo
		Document document = new Document();
		kml.setFeature(document);
		document.setName("Polygon.kml");
		document.setOpen(false);
		Placemark placemark = new Placemark();
		document.getFeature().add(placemark);
		placemark.setName("Land Map");
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

		int ilongitude = (int) (izqSupLon / precision);
		int ilatitude = (int) (izqSupLat / precision);
		int cont = 0;
		for (int i = 0; i < dimX; i++) {
			for (int j = 0; j < dimY; j++) {
				double dlongitude = ilongitude * precision;
				double dlatitude = ilatitude * precision;
				// Aqui es donde se llama al metodo del Webservice
				double alt = AltitudeWS.getElevation(new LatLng(dlatitude,
						dlongitude));
				System.out.println("Creando punto (" + cont + "):\t"
						+ dlatitude + "," + dlongitude + "," + alt);
				outercoord.add(new Coordinate(dlatitude, dlongitude, alt));
				cont++;
				ilongitude++;
			}
			ilatitude--;
		}
	}

	/**
	 * Crea el Fichero kml de nombre nombreFichero
	 * 
	 * @param nombreFichero
	 */
	private void createKmlFile(String nombreFichero) {
		try {
			kml.marshal(new File(nombreFichero + ".kml"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
