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
import util.Scenario;
import util.jcoord.LatLng;
import de.micromata.opengis.kml.v_2_2_0.AltitudeMode;
import de.micromata.opengis.kml.v_2_2_0.ColorMode;
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
	protected HexagonalGrid oldGrid;
	protected long cont;
	protected double ilat;
	protected double ilng;
	protected short tileSize;
	protected int dimX;
	protected int dimY;
	protected String beginTime;
	protected String endTime;

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
	 * Crea el Fichero kml de nombre nombreFichero
	 * 
	 * @param nombreFichero
	 */
	public void createKmlFile(String fileName) {
		try {
			kml.marshal(new File(fileName + ".kml"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

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
			folder.createAndAddStyle().withId("examplePolyStyle")
					.createAndSetPolyStyle().withColor("ff0000cc")
					.withColorMode(ColorMode.RANDOM);

			Placemark placeMark = folder.createAndAddPlacemark().withName(
					name + " " + cont);

			setTimeSpan(placeMark);
			
			Polygon polygon = placeMark.createAndSetPolygon().withExtrude(true)
					.withAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);

			LinearRing l = polygon.createAndSetOuterBoundaryIs()
					.createAndSetLinearRing();
			for (LatLng c : borderLine) {
				l.addToCoordinates(c.toGoogleString());
			}
			l.addToCoordinates(borderLine.get(0).toGoogleString());
		}
	}
	
	
	protected void setTimeSpan(Placemark placeMark){
		TimeSpan t = new TimeSpan();
		t.setBegin(beginTime);
		t.setEnd(endTime);
		
		placeMark.setTimePrimitive(t);
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
		
		setTimeSpan(placeMark);

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
