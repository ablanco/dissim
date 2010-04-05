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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import util.HexagonalGrid;
import util.Pedestrian;
import util.Snapshot;
import util.Updateable;
import util.flood.FloodHexagonalGrid;
import util.jcoord.LatLng;
import de.micromata.opengis.kml.v_2_2_0.AltitudeMode;
import de.micromata.opengis.kml.v_2_2_0.Coordinate;
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
	protected Folder folder;

	/**
	 * En este kml almacenaremos toda la informacion de las inundaciones
	 */
	private KmlFlood kFlood;
	/**
	 * En este kml almacenaremos toda la informacion de las personas que
	 * afectadas por alguna catastrofe
	 */
	private KmlPeople kPeople;

	/**
	 * Aqui iremos almacenando la informacion que va cambiando para cada
	 * escenario al que estemos subscrito, el map es para organizarlo mejor, el
	 * nombre de cada escenario deberia de ser unico
	 */
	private Map<String, KmlInf> inf;

	public KmlBase() {
		kml = new Kml();
		inf = new TreeMap<String, KmlInf>();
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
		inf = new TreeMap<String, KmlInf>();
	}

	@Override
	public void update(Object obj, AID sender) throws IllegalArgumentException {
		if (!(obj instanceof Snapshot))
			throw new IllegalArgumentException(
					"Object is not an instance of Snapshot");
		Snapshot snap = (Snapshot) obj;

		if (inf.keySet().size() == 0) {
			// No hay ninguna informacion de ningun entorno creamos la base
			setName(snap.getName());
			setDescription(snap.getDescription());
			// Le damos un nombre al Contenedor Principal del KML
			folder = newFolder(kml, snap.getName(), snap.getDescription());

			kFlood = new KmlFlood(addFolder(folder, "Flood", "Flooded Sectors"));

			kPeople = new KmlPeople(addFolder(folder, "People",
					"People Running For their lives"));
		}

		// Aqui iremos almacenando la informacion que cambia para cada escenario
		// al que estemos subscritos
		KmlInf currentEnv = inf.get(sender.getLocalName());

		if (currentEnv == null) {
			// No tenemos informacion para este enviorement
			currentEnv = new KmlInf(sender.getLocalName(), null, snap
					.getDateTime().toString(), snap.getGrid().getIncs());
			if (snap.getGrid() instanceof FloodHexagonalGrid) {
				// Si es tenemos una inundacion inicializamos lo necesario
				currentEnv.setGrid(((FloodHexagonalGrid) snap.getGrid())
						.getGridWater());
			}
			// Almacenamos la informacion concreta que necesitamos
			// TODO quizas sender.getLocalName() no sea unico
			inf.put(sender.getLocalName(), currentEnv);
		} else {
			// Todas las demas iteraciones
			// Actualizamos las marcas de tiempo
			currentEnv.SetNewDate(snap.getDateTime().toString());
			// Si es una inundacion, actualizar la inundacion

			if (snap.getGrid() instanceof FloodHexagonalGrid) {
				FloodHexagonalGrid f = (FloodHexagonalGrid) snap.getGrid();
				// Por cada llamada update lo que tengo que hacer para FLOOD
				kFlood.update(currentEnv.getWaterGrid(), f, currentEnv
						.getName(), currentEnv.getBegin(), currentEnv.getEnd());
			}
			// Obtenemos la lista de personas
			List<Pedestrian> pedestrians = snap.getPeople();
			if (pedestrians != null && pedestrians.size() > 0) {
				// Si hay personas
				HexagonalGrid g = snap.getGrid();
				for (Pedestrian p : pedestrians) {
					// Por cada persona averiguamos su status y su posicion
					p.setPos(g.tileToCoord(p.getPoint()));
				}
				kPeople.update(pedestrians, currentEnv.getName(), currentEnv
						.getBegin(), currentEnv.getEnd(), currentEnv.getIncs());
			}
		}
	}

	public Kml getKml() {
		return kml;
	}

	public void setName(String name) {
		if (folder != null) {
			folder.setName(name);
		}
	}

	public void setDescription(String description) {
		if (folder != null) {
			folder.setDescription(description);
		}
	}

	public String getName() {
		if (folder != null && folder.getName() != null
				&& folder.getName().length() != 0)
			return folder.getName();
		return "DefaultName";
	}

	public String getDescription() {
		if (folder != null && folder.getDescription() != null
				&& folder.getDescription().length() != 0)
			return folder.getDescription();
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

	/**
	 * Adds folder to an existing Folder
	 * 
	 * @param folder
	 * @param name
	 * @param description
	 * @return
	 */
	public static Folder addFolder(Folder folder, String name,
			String description) {
		if (folder != null) {
			return folder.createAndAddFolder().withName(name).withOpen(true)
					.withDescription(description);
		}
		throw new NullPointerException();
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
		if (borderLine == null || borderLine.size() < 1) {
			throw new NullPointerException();
		}
		Polygon polygon = placeMark.createAndSetPolygon().withExtrude(true)
				.withAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);
		LinearRing l = polygon.createAndSetOuterBoundaryIs()
				.createAndSetLinearRing();

		switch (borderLine.size()) {
		case 0:
			throw new IllegalArgumentException("Poligon canot be empty");
		case 1: // Draws Hexagon
			LatLng coord = borderLine.get(0);
			double ilat = (incs[0] * 4) / 6;
			double ilng = incs[1] / 2;

			// Hexagonal
			double[] f = new double[] { 1, 0, 0.5, 1, -0.5, 1, -1, 0, -0.5, -1,
					0.5, -1, 1, 0 };
			double lat = coord.getLat();
			double lng = coord.getLng();
			List<Coordinate> coordinates = new ArrayList<Coordinate>();
			for (int i = 0; i < f.length; i = i + 2) {
				Coordinate c = new Coordinate(f[i + 1] * ilng + lng, f[i]
						* ilat + lat, coord.getAltitude());
				coordinates.add(c);
			}
			l.withCoordinates(coordinates);
			break;
		default: // Draws Polygon
			// Closing the polygon
			borderLine.add(borderLine.get(0));
			for (LatLng c : borderLine) {
				l.addToCoordinates(c.getLng(), c.getLat(), c.getAltitude());
			}
			// Esto solo dibuja los centros del poligono, no es hegagonal
			break;
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
