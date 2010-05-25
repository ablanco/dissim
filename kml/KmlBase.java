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
import jade.core.Agent;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JFileChooser;

import util.HexagonalGrid;
import util.Pedestrian;
import util.Scenario;
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

/**
 * Is the base class from managing the info from the updates and parsin them
 * into kml friendly
 * 
 * @author Manuel Gomar, Alejandro Blanco
 * 
 */
public class KmlBase implements Updateable {
	public final static String folderName = "Kml";
	protected Kml kml;
	protected Folder folder;

	/*
	 * En este kml almacenaremos toda la información de las inundaciones
	 */
	private KmlFlood kFlood;
	/*
	 * En este kml almacenaremos toda la información de las personas afectadas
	 * por alguna catastrofe
	 */
	private KmlPeople kPeople;

	/*
	 * Aquí iremos almacenando la información que va cambiando para cada
	 * escenario al que estemos subscrito, el map es para organizarlo mejor, el
	 * nombre de cada escenario deberia de ser único
	 */
	private Map<String, KmlInf> inf;
	private Agent myAgent = null;

	/**
	 * Public constructor, initiazlizes the kml and organizes all the listening
	 * enviorements
	 */
	public KmlBase() {
		kml = new Kml();
		inf = new TreeMap<String, KmlInf>();
	}

	/**
	 * Sets the agent from KML
	 * 
	 * @param agt
	 *            new Agent
	 */
	@Override
	public void setAgent(Agent agt) {
		myAgent = agt;
	}

	/**
	 * Creates the kml archives
	 */
	@Override
	public void finish() {
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showSaveDialog(null);
		// Pedimos al usuario que nos diga dónde guardar el fichero
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			// Escribimos el kml
			createKmzFile(kml, file.getAbsolutePath());
		} else {
			if (myAgent != null)
				myAgent.doDelete();
		}
	}

	/**
	 * Get type of the agent
	 */
	@Override
	public String getType() {
		return "kml";
	}

	/**
	 * Initializes the map for the kmls agents
	 */
	@Override
	public void init() {
		inf = new TreeMap<String, KmlInf>();
	}

	/**
	 * for each new scenario, it find the diferences and print them into the kml
	 * 
	 * @param obj
	 *            snapshot
	 * @param sender
	 *            sender name
	 */
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
		// System.err.println(sender.getLocalName()+" "+snap.getDateTime().toString());
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
			// TODO quizas sender.getLocalName() no sea unico, usar getName()
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
				short precision = snap.getGrid().getPrecision();
				HexagonalGrid g = snap.getGrid();
				for (Pedestrian p : pedestrians) {
					// Por cada persona averiguamos su status y su posicion
					// Tenemos que pasar a alura real
					LatLng c = g.tileToCoord(p.getPoint());
					// Supongo que la persona tiene 2 metros de altura, para que
					// se vea bien
					// TODO Quizás algo más de altura? 5 metros?
					c.setAltitude(Scenario.innerToDouble(precision, (short) (p
							.getPoint().getZ())) + 2.0);
					p.setPos(c);
				}
				kPeople.update(pedestrians, currentEnv.getName(), currentEnv
						.getBegin(), currentEnv.getEnd(), currentEnv.getIncs());
			}
		}
	}

	/**
	 * Gets kml
	 * 
	 * @return kml
	 */
	public Kml getKml() {
		return kml;
	}

	/**
	 * Sets a new name from the kml folder
	 * 
	 * @param name
	 *            new
	 */
	public void setName(String name) {
		if (folder != null) {
			folder.setName(name);
		}
	}

	/**
	 * Sets a new description to the kml
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		if (folder != null) {
			folder.setDescription(description);
		}
	}

	/**
	 * Gets the name from the kml, if not defined, returns DefaultName
	 * 
	 * @return name, or DefaultName if not defined
	 */
	public String getName() {
		if (folder != null && folder.getName() != null
				&& folder.getName().length() != 0)
			return folder.getName();
		return "DefaultName";
	}

	/**
	 * Gets the description from the kml, if not defined, returns
	 * DefaultDescriptor
	 * 
	 * @return name, or DefaultDescriptor if not defined
	 */
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
	 * @param kml
	 * @param name
	 * @param description
	 * @return
	 */
	public static Folder newFolder(Kml kml, String name, String description) {
		return kml.createAndSetFolder().withName(name).withOpen(false)
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
			return folder.createAndAddFolder().withName(name).withOpen(false)
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
	 * @param kpolygon
	 *            borders of the polygon
	 */
	public static void drawPolygon(Placemark placeMark, Kpolygon kpolygon) {
		if (kpolygon == null || kpolygon.getOuterLine().size() == 0) {
			throw new IllegalArgumentException(
					"El borde exterior del poligono no puede ser 0");
		}
		Polygon kmlPolygon = placeMark.createAndSetPolygon().withExtrude(true)
				.withAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);
		LinearRing outer = kmlPolygon.createAndSetOuterBoundaryIs()
				.createAndSetLinearRing();

		// Pintamos la linea exterior del poligono
		outer.withCoordinates(kpolygon.getOuterLine());

		// Si el poligono tiene huecos los pintamos
		for (List<Coordinate> inn : kpolygon.getInnerLines()) {
			LinearRing inner = kmlPolygon.createAndAddInnerBoundaryIs()
					.createAndSetLinearRing();
			inner.withCoordinates(inn);
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
