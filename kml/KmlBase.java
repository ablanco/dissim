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

import jade.core.AID;

import java.io.File;
import java.io.IOException;
import java.util.List;

import util.HexagonalGrid;
import util.Pedestrian;
import util.Snapshot;
import util.Updateable;
import util.flood.FloodHexagonalGrid;
import util.jcoord.LatLng;
import de.micromata.opengis.kml.v_2_2_0.AltitudeMode;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LinearRing;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Polygon;
import de.micromata.opengis.kml.v_2_2_0.TimeSpan;

public class KmlBase implements Updateable {
	public final static String folderName = "Kml";
	protected Kml kml;
	private Document doc;

	private boolean init;

	private short[][] oldGrid;
	private KmlFlood kFlood;
	private KmlPeople kPeople;
	private double[] incs;

	private String beginTime;

	public KmlBase() {
		kml = new Kml();
	}

	@Override
	public void finish() {
		// Escribimos el kml
		createKmzFile(kml, getName());
	}

	@Override
	public String getConversationId() {
		return "kml";
	}

	@Override
	public void init() {
		init = false;
	}

	@Override
	public void update(Object obj, AID sender) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		if (!(obj instanceof Snapshot))
			throw new IllegalArgumentException(
					"Object is not an instance of Snapshot");
		Snapshot snap = (Snapshot) obj;
		// Inicizalizacion de KML
		if (!init) {
			beginTime = snap.getDateTime().toString();
			setName(snap.getName());
			setDescription(snap.getDescription());
			// Le damos un nombre al Doc del KML
			doc = kml.createAndSetDocument().withName(snap.getName())
					.withDescription(snap.getDescription());
			incs = snap.getGrid().getIncs();
			if (snap.getGrid() instanceof FloodHexagonalGrid) {
				// Si es tenemos una inundacion inicializamos lo necesario
				FloodHexagonalGrid fgrid = (FloodHexagonalGrid) snap.getGrid();
				oldGrid = new short[fgrid.getColumns()][fgrid.getRows()];
				// Inicializamos la matriz
				for (int c = 0; c < fgrid.getColumns(); c++) {
					for (int r = 0; r < fgrid.getRows(); r++) {
						oldGrid[c][r] = fgrid.getTerrainValue(c, r);
					}
				}
				kFlood = new KmlFlood(
						newFolder(kml, "Flood", "Flooded Sectors"));
			}

			kPeople = new KmlPeople(newFolder(kml, "People",
					"People Running For their lives"), incs);
			// Demas inicializaciones para futuras ampliaziones

			// No necesitamos volver
			init = true;
		} else {
			// Todas las demas iteraciones
			
			//Actualizaciones para las personas
			List<Pedestrian> pedestrians = snap.getPeople();
			if (pedestrians != null) {
				HexagonalGrid g = snap.getGrid();
				for (Pedestrian p : pedestrians) {
					p.setPos(g.tileToCoord(p.getPoint()));
				}
				kPeople.update(pedestrians, beginTime, snap.getDateTime()
						.toString());
			}
			
			//Si es una inundacion, actualizar la inundacion
			if (snap.getGrid() instanceof FloodHexagonalGrid) {
				// Por cada llamada update lo que tengo que hacer para FLOOD
				kFlood.update(oldGrid, (FloodHexagonalGrid) snap.getGrid(), beginTime, snap.getDateTime()
						.toString());
			}

			//Tiempo inicial para la siguiente Iteracion
			beginTime = snap.getDateTime().toString();
		}
	}

	public Kml getKml() {
		return kml;
	}

	public void setName(String name) {
		doc = kml.createAndSetDocument().withName(name);
	}
	
	public void setDescription(String description){
		if (doc != null){
			doc.setDescription(description);
		}
	}
	
	public String getName() {
		if (doc != null && doc.getName() != null && doc.getName().length() != 0)
			return doc.getName();
		return "DefaultName";
	}

	public String getDescription() {
		if (doc != null && doc.getDescription() != null && doc.getDescription().length() != 0)
			return doc.getDescription();
		return "DefaultDescriptor";
	}

	/**
	 * Static methods
	 */

	/**
	 * New kmz file of the current kml
	 * 
	 * @param fileName
	 */
	public static void createKmzFile(Kml kml, String fileName) {
		try {
			File f = new File(fileName + ".kmz");
			kml.marshalAsKmz(f.getPath());
			// For debugg
			kml.marshal(new File(fileName + ".kml"));

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

	public static Folder newFolder(Kml kml) {
		return kml.createAndSetFolder().withOpen(true);
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
	public static void drawPolygon(Placemark placeMark,
			List<LatLng> borderLine, double[] incs) {
		Polygon polygon = newPolygon(placeMark);
		switch (borderLine.size()) {
		case 0:
			throw new IllegalArgumentException("Poligon canot be empty");
		case 1: // Only one hexagon
			drawHexagonBorders(polygon, borderLine.get(0), incs);
			break;
		default:
			// drawPolygon(folder, name, borderLine, incs);
			break;
		}
	}

	public static void drawPolygon(Placemark placeMark, LatLng borderLine,
			double[] incs) {
		Polygon polygon = newPolygon(placeMark);
		if (borderLine != null) {
			drawHexagonBorders(polygon, borderLine, incs);
		} else {
			throw new IllegalArgumentException("Poligon canot be empty");
		}
	}

	protected static void drawHexagonBorders(Polygon polygon, LatLng coord,
			double[] incs) {

		double ilat = (incs[0] * 4) / 6;
		double ilng = incs[1] / 2;

		LinearRing l = polygon.createAndSetOuterBoundaryIs()
				.createAndSetLinearRing();

		l.addToCoordinates(coord.addIncs(ilat, 0).toKmlString());
		l.addToCoordinates(coord.addIncs(ilat / 2, ilng).toKmlString());
		l.addToCoordinates(coord.addIncs(-ilat / 2, ilng).toKmlString());
		l.addToCoordinates(coord.addIncs(-ilat, 0).toKmlString());
		l.addToCoordinates(coord.addIncs(-ilat / 2, -ilng).toKmlString());
		l.addToCoordinates(coord.addIncs(ilat / 2, -ilng).toKmlString());
		l.addToCoordinates(coord.addIncs(ilat, 0).toKmlString());
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
		if (borderLine.size() > 0) {
			LinearRing l = polygon.createAndSetOuterBoundaryIs()
					.createAndSetLinearRing();
			for (LatLng c : borderLine) {
				l.addToCoordinates(c.toKmlString());
			}
			l.addToCoordinates(borderLine.get(0).toKmlString());
		}
	}

	/**
	 * Sets when the event happends
	 * 
	 * @param feature
	 */
	protected static void setTimeSpan(Feature feature, String beginTime,
			String endTime) {
		TimeSpan t = feature.createAndSetTimeSpan();
		if (beginTime != null) {
			t.setBegin(beginTime);
		}
		if (endTime != null) {
			t.setEnd(endTime);
		}
	}

}
