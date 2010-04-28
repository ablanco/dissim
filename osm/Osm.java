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

package osm;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import util.HexagonalGrid;
import util.Point;
import util.java.TempFiles;
import util.java.Wget;

public class Osm {

	private static final short smallClass = 10;
	private static final short mediumClass = 50;
	private static final short bigClass = 100;

	public static final short Undefined = -1;
	// Land
	public static final short Raw_Field = 0;
	public static final short Waterway = Raw_Field + smallClass;
	public static final short Barrier = Waterway + smallClass;
	public static final short Natural = Barrier + smallClass;
	public static final short Landuse = Natural + smallClass;
	public static final short Geological = Landuse + smallClass;
	public static final short Land = 200;
	// End Land Types
	// Roads
	public static final short Tracktype = Land + mediumClass;
	public static final short Cycleway = Tracktype + mediumClass;
	public static final short Aerialway = Tracktype + mediumClass;
	public static final short Railway = Aerialway + mediumClass;
	public static final short Highway = Railway + bigClass;
	public static final short Roads = 600;
	// End Roads
	// Safe Points
	// Big Places
	public static final short Man_Made = Roads + smallClass;
	public static final short Shop = Man_Made + smallClass;
	public static final short Tourism = Shop + smallClass;
	public static final short Power = Tourism + smallClass;
	public static final short Leisure = Man_Made + mediumClass;
	public static final short Amenity = Leisure + mediumClass;

	// Goverment
	public static final short Historic = Amenity + smallClass;
	public static final short Military = Historic + smallClass;
	public static final short Building = Military + mediumClass * 4;
	public static final short Aeroway = Building + mediumClass * 5;

	public static final short SafePoint = 1000;
	// End Safe Points
	// Others
	public static final short Boundary = -2;
	public static final short Multipolygon = -3;
	public static final short Route = -4;

	/**
	 * Given a proper url for OSM returns a file with the information
	 * 
	 * @param url
	 *            Url for the web service
	 * @param fileName
	 *            Name for the file
	 * @return
	 */
	public static File getOSMXmlFromURL(String url, String fileName) {
		try {
			// creamos un fichero en el directorio temporal
			File dir = TempFiles.getDefaultTempDir();
			File file = new File(dir, fileName);
			if (!file.exists()) {
				if (!Wget.wget(dir.getPath(), url)) {
					System.err
							.println("No se ha podido descargar la informacion");
					return null;
				} else {
					// Si se ha descargado devolvemos el fichero
					file = new File(dir, fileName);
					System.out.println("Se ha descargado el archivo "
							+ file.getPath());
					return file;
				}
			} else {
				// Fichero ya descargado, lo devolvemos
				System.out.println("Peticion ya en disco " + file.getName());
				return file;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		// Unrecheable
		return null;
	}

	/**
	 * Parses XML file and returns XML document.
	 * 
	 * @param fileName
	 *            XML file to parse
	 * @return XML document or <B>null</B> if error occured
	 */
	public static Document parseFile(File sourceFile) {
		System.out.println("Parsing XML file... " + sourceFile.getName());
		DocumentBuilder docBuilder;
		Document doc = null;
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
				.newInstance();
		docBuilderFactory.setIgnoringElementContentWhitespace(true);
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			System.err.println("Wrong parser configuration: " + e.getMessage());
			return null;
		}
		try {
			doc = docBuilder.parse(sourceFile);
		} catch (SAXException e) {
			System.err.println("Wrong XML file structure: " + e.getMessage());
			return null;
		} catch (IOException e) {
			System.err.println("Could not read source file: " + e.getMessage());
		}
		System.out.println("XML file parsed");
		return doc;
	}

	/**
	 * Dado un HexagonalGrid se descarga de internet toda la información desde
	 * OSM y lo actualiza
	 * 
	 * @param osmMap
	 * @param grid
	 */
	public static void setOsmMapInfo(HexagonalGrid grid) {
		OsmMap osmMap = OsmMap.getMap(grid);
		for (OsmRelation r : osmMap.getRelations()) {
			if (r.getType() > Undefined) {
//				System.err.println("**Escribiendo Relations " + r);
				setStreetValue(r, grid);
			} else {
//				System.err.println("No se ha escrito: " + r);
			}
		}

		for (OsmWay w : osmMap.getWays().values()) {
			if (w.getType() > Undefined) {
				setStreetValue(w, grid);
			} else {
//				System.err.println("No se ha escrito: " + w);
			}
		}

		for (OsmNode n : osmMap.getNodes().values()) {
			if (!n.isSimpleNode()) {
				// System.err.println("Escribiendo Nodes: " + n);
				setStreetValue(n, grid);
			}
		}

	}

	/**
	 * Set Street value in point p given the type only is type is greater than
	 * previous value
	 * 
	 * @param p
	 * @param type
	 * @param grid
	 * @return
	 */
	public static boolean setStreetValue(Point p, short type, HexagonalGrid grid) {
		short currType = grid.getStreetValue(p);
		if (type == currType) {
			// Esto quiere decir que es una intersección, luego le damos un
			// valor impar
			try {
				// System.err.println("Escribiendo en " + p + " el valor " +
				// type);
				grid.setStreetValue(p, (short) (type + 1));
			} catch (Exception e) {
				System.err
						.println("Se ha producido un error al intentar insertar "
								+ p + ", del tipo " + (type + 1));
				e.printStackTrace();
			}
			return true;
		} else if (type > currType) {
			// Esto quiere decir que hay algo mas importate que lo actual,
			// sobreescribimos
			try {
				// System.err.println("Escribiendo en " + p + " el valor " +
				// type);
				grid.setStreetValue(p, type);
			} catch (Exception e) {
				System.err
						.println("Se ha producido un error al intentar insertar "
								+ p + ", del tipo " + type);
				e.printStackTrace();
			}
			return true;
		}
		// System.err.println("No se esta escribiendo nada en " + p +
		// " el valor "
		// + type+ " ya tiene "+currType);
		return false;

	}

	/**
	 * Solo para puntos de interes, Añadimos a la matriz de calles del grid el
	 * Nodo Siempre que el valor que metamos sea mayor que el valor que antes
	 * estuviera en la matriz
	 * 
	 * @param n
	 * @param grid
	 * @return
	 */
	public static boolean setStreetValue(OsmNode n, HexagonalGrid grid) {
		if (grid.getBox().contains(n.getCoord()) && !n.isSimpleNode()) {
			// Si esta dentro del grid y es un nodo especial
			Point point = grid.coordToTile(n.getCoord());
			return setStreetValue(point, n.getType(), grid);
		}
		return false;
	}

	/**
	 * Añadimos a la matriz de calles el Way
	 * 
	 * @param w
	 * @param grid
	 */
	public static void setStreetValue(OsmWay w, HexagonalGrid grid) {
		w.getBox().intersection(grid.getBox());
		short type = w.getType();
		for (Point p : w.getLines(grid)) {
			// System.err.println("Escribiendo punto "+p+" de way "+w.getId());
			setStreetValue(p, type, grid);
		}

		if (w.isClosedLine() && !isRoad(type)) {
//			System.err.println("Detectada linea cerrada " + w.getBox() + ", "
//					+ w.getWay());

			Point nW = grid.coordToTile(w.getBox().getNw());
			Point sE = grid.coordToTile(w.getBox().getSe());
			
			for (int col = nW.getCol(); col <= sE.getCol(); col++) {
				for (int row = nW.getRow(); row <= sE.getRow(); row++) {
					Point p = new Point(col, row);
					// System.err.println("Dimesiones lina cerrada "+nW+", "+sE);
					if (w.isIntoPoligon(grid.tileToCoord(p))) {
						// System.err.println("Linea Cerrada punto "+p+" de way "+w.getId());
						setStreetValue(p, type, grid);
					}
				}
			}
		}
	}

	public static void setStreetValue(OsmRelation r, HexagonalGrid grid) {
		// TODO algo cutre, mejorar, asumo que los members vienen ordenados en
		// pares de dos consecutivos, cosa que no creo que sea cierta
//		short type = r.getType();
//		Iterator<OsmMember> m = r.getMembers().iterator();
//		while (m.hasNext()) {
//			OsmMember m1 = m.next();
//			if (m.hasNext()) {
//				OsmMember m2 = m.next();
//				OsmWay w = OsmWay.join(m1.getWay(), m2.getWay(), r.getType(),
//						grid.getBox());
//				setStreetValue(w, grid);
//			}
//		}
	}

	/**
	 * Given a list of tags, retunrs the type
	 * 
	 * @param node
	 * @return
	 */
	public static short getNodeType(List<OsmTag> tags) {
		for (OsmTag tag : tags) {
			String name = tag.getName();
			String value = tag.getValue();
			if (name.equalsIgnoreCase("highway")
					|| name.equalsIgnoreCase("junction")
					|| name.equalsIgnoreCase("traffic_calming")
					|| name.equalsIgnoreCase("service")) {
				return getHighway(name, value);
			} else if (name.equalsIgnoreCase("amenity")) {
				return Amenity;
			} else if (name.equalsIgnoreCase("historic")) {
				return Historic;
			} else if (name.equalsIgnoreCase("leisure")
					|| name.equalsIgnoreCase("sport")) {
				return Leisure;
			} else if (name.equalsIgnoreCase("aeroway")) {
				return Aeroway;
			} else if (name.equalsIgnoreCase("Barrier")) {
				return Barrier;
			} else if (name.equalsIgnoreCase("Cycleway")) {
				return Cycleway;
			} else if (name.equalsIgnoreCase("Tracktype")) {
				return Tracktype;
			} else if (name.equalsIgnoreCase("Waterway")) {
				return getWaterway(value);
			} else if (name.equalsIgnoreCase("Railway")) {
				return getRailway(value);
			} else if (name.equalsIgnoreCase("Aerialway")) {
				return Aerialway;
			} else if (name.equalsIgnoreCase("Power")) {
				return Power;
			} else if (name.equalsIgnoreCase("Shop")) {
				return Shop;
			} else if (name.equalsIgnoreCase("ManMade")) {
				return Man_Made;
			} else if (name.equalsIgnoreCase("Historic")) {
				return Historic;
			} else if (name.equalsIgnoreCase("Military")) {
				return Military;
			} else if (name.equalsIgnoreCase("Natural")) {
				return Natural;
			} else if (name.equalsIgnoreCase("Geological")) {
				return Geological;
			} else if (name.equalsIgnoreCase("Building")) {
				return Building;
			} else if (name.equalsIgnoreCase("Landuse")) {
				return Landuse;
			} else if (name.equalsIgnoreCase("boundary")) {
				return Boundary;
			} else if (name.equalsIgnoreCase("type")) {
				return Multipolygon;
			} else if (name.equalsIgnoreCase("route")) {
				return Route;
			} else {
				// Para depurar tags que no interesan
				// System.err.println("Undefinded Tags " + tag);
			}
		}
		return Undefined;
	}

	private static short getRailway(String value) {
		short key = Railway;
		key += 2;
		if (value.equalsIgnoreCase("rail"))
			return key;
		key += 2;
		if (value.equalsIgnoreCase("tram"))
			return key;
		return Railway;
	}

	private static short getWaterway(String value) {
		short key = Waterway;
		if (value.equalsIgnoreCase("riverbank"))
			return key;
		return 0;
	}

	/**
	 * Given a highway Type Returns a Proper value for this kind of Highway Los
	 * incrementos van de dos en dos, cuando es intersección será impar.
	 * 
	 * @param type
	 * @param value
	 * @return
	 */
	private static short getHighway(String type, String value) {
		short key = Highway;
		if (type.equalsIgnoreCase("highway")) {
			// Higways
			if (value.equalsIgnoreCase("footway")
					|| value.equalsIgnoreCase("path"))
				return key;
			key += 2;
			if (value.equalsIgnoreCase("track"))
				return key;
			key += 2;
			if (value.equalsIgnoreCase("cycleway"))
				return key;
			key += 2;
			if (value.equalsIgnoreCase("residential")
					|| value.contains("parking"))
				return key;
			key += 2;
			if (value.equalsIgnoreCase("road")
					|| value.equalsIgnoreCase("pedestrian"))
				return key;
			key += 2;
			if (value.equalsIgnoreCase("service"))
				return key;
			key += 2;
			if (value.equalsIgnoreCase("tertiary"))
				return key;
			key += 2;
			if (value.equalsIgnoreCase("secondary"))
				return key;
			key += 2;
			if (value.equalsIgnoreCase("primary"))
				return key;
			key += 2;
			if (value.contains("trunk"))
				return key;
			key += 2;
			if (value.contains("motorway"))
				return key;
		} else if (type.equalsIgnoreCase("junction")) {

		} else if (type.equalsIgnoreCase("traffic_calming")) {

		} else {
			// service
		}
		return Highway;
	}

	/**
	 * Given a type, returns Parent Big Type
	 * 
	 * @param type
	 * @return Parent Big Type
	 */
	public static short getBigType(short type) {
		if (type < Raw_Field) {
			return Undefined;
		} else if (type < Land) {
			return Land;
		} else if (type < Roads) {
			return Roads;
		} else if (type < SafePoint) {
			return SafePoint;
		}
		return Undefined;
	}

	/**
	 * Given a type, returns Parent Type
	 * 
	 * @param type
	 * @return Parent Type
	 */
	public static short getType(short type) {
		if (type < Raw_Field) {
			return Undefined;
		} else if (type < Land) {
			// Land Type
			if (type < Waterway)
				return Raw_Field;
			if (type < Barrier)
				return Waterway;
			if (type < Natural)
				return Barrier;
			if (type < Landuse)
				return Natural;
			if (type < Geological)
				return Landuse;
			return Geological;
		} else if (type < Roads) {
			if (type < Cycleway)
				return Tracktype;
			if (type < Aerialway)
				return Cycleway;
			if (type < Railway)
				return Aerialway;
			if (type < Highway)
				return Railway;
			return Highway;
		} else if (type < SafePoint) {
			if (type < Shop)
				return Man_Made;
			if (type < Tourism)
				return Shop;
			if (type < Power)
				return Tourism;
			if (type < Leisure)
				return Power;
			if (type < Amenity)
				return Leisure;
			return Amenity;
		} else {
			// Infrastructura
			if (type < Military)
				return Historic;
			if (type < Building)
				return Military;
			if (type < Aeroway)
				return Building;
			return Aeroway;
		}
	}

	/**
	 * Given a type returns the proper color.
	 * 
	 * @param type
	 * @return
	 */
	public static Color getColor(short type) {
		short key = getType(type);
		int brigther = (type - key) * 10;
		if (key < Raw_Field) {
			// Undefined or Not important
			return new Color(Color.GRAY.getRGB() + brigther);
		} else if (key < Land) {
			// Tipes of land
			switch (key) {
			case Waterway:
				return new Color(Color.BLUE.getRGB() + brigther);
			case Landuse:
				return new Color(Color.YELLOW.getRGB() + brigther);
			default:
				return new Color(Color.CYAN.getRGB() + brigther);
			}
		} else if (key < Roads) {
			// types of roads
			switch (key) {
			case Highway:
				return new Color(Color.RED.getRGB() + brigther);
			case Railway:
				return new Color(Color.ORANGE.getRGB() + brigther);
			default:
				return new Color(Color.MAGENTA.getRGB() + brigther);
			}
		} else {
			// Types of safe Places
			// hay muchos tipos de safe place si le ponemos brighter se sale del
			// verde
			return new Color(Color.GREEN.getRGB());
		}
	}

	/**
	 * Given a Type returns the parents type Name
	 * 
	 * @param type
	 * @return Parent Type Name
	 */
	public static String getName(short type) {
		short key = getType(type);
		switch (key) {
		case Boundary:
			return "Boundary Limit";
		case Highway:
			return "Highway";
		case Railway:
			return "Rail Way";
		case Waterway:
			return "Water Way";
		case Landuse:
			return "Land Use";
		default:
			if (key > Roads) {
				return "Safe Point";
			}
			return "Undefined " + type;
		}

	}

	/**
	 * Tal como esta definido atualmente la forma de escribir las carreteras en
	 * el StreetGrid pues al cruzarse dos caminos iguales, se incrementa el
	 * valor en una unidad, resultando las intersecciones impares.
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isIntersection(short value) {
		return isRoad(value) && (value % 2 != 0);
	}

	/**
	 * Deveuvel true si el value pertenece al intervalo roads
	 */
	public static boolean isRoad(short value) {
		return (value > Land) && (value < Roads);
	}
}
