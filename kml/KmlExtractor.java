package kml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import util.HexagonalGrid;
import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Point;

public class KmlExtractor {

	private Kml kml;

	private int precision = 10000;
	private int offsetLat, offsetLon, tamLat, tamLon;
	ArrayList<Coordinate> coordinates;

	public KmlExtractor(String nombreFichero) {
		kml = Kml.unmarshal(new File(nombreFichero));
	}

	/**
	 * Extrae todas las Coordenadas que se encuentran en el KML
	 */
	public void extractCoordinates() {
		Placemark placemark = (Placemark) kml.getFeature();
		Point point = (Point) placemark.getGeometry();
		coordinates = (ArrayList<Coordinate>) point.getCoordinates();
	}

	/***
	 * Extrae el valor máximo y el valor mínimo de las latitudes longitudes
	 */
	public void extractReferences() {
		double latMax, latMin, lonMax, lonMin;
		latMax = Double.MIN_VALUE;
		latMin = Double.MAX_VALUE;
		lonMax = Double.MIN_VALUE;
		lonMin = Double.MAX_VALUE;
		for (Coordinate coordinate : coordinates) {
			double auxLat, auxLon;
			auxLat = coordinate.getLatitude();
			if (auxLat > latMax) {
				latMax = auxLat;
			}
			if (auxLat < latMin) {
				latMin = auxLat;
			}
			auxLon = coordinate.getLongitude();
			if (auxLon > lonMax) {
				lonMax = auxLon;
			}
			if (auxLon < lonMin) {
				lonMin = auxLon;
			}
		}
		tamLat = Math.abs((int) ((latMax - latMin) * precision));
		tamLon = Math.abs((int) ((lonMax - lonMin) * precision));
		offsetLat = Math.abs((int) (latMin - ((int) latMin)) * precision);
		offsetLon = Math.abs((int) (lonMin - ((int) lonMin)) * precision);
	}

	public HexagonalGrid extractGrid() {
		HexagonalGrid grid = new HexagonalGrid(tamLat, tamLon);

		return grid;
	}

	public String toString() {
		return "OffSet de Latitude :" + offsetLat + ", OffSet de Longitud :"
				+ offsetLon + ", Tamaño en Latitud :" + tamLat
				+ ", Tamaño en Longitud :" + tamLon;
	}

}
