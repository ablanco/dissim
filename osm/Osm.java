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

/**
 * This class contains static methods for working with Open Street Maps and
 * managing this data in an easy way
 * 
 * @author Manuel Gomar, Alejandro Blanco
 * 
 */
public class Osm {

	/*
	 * All Static values are for OSM types
	 */

	/**
	 * Small Increment for Small Classes
	 */
	private static final short smallClass = 10;
	/**
	 * Medium Increment for Small Classes
	 */
	private static final short mediumClass = 50;
	/**
	 * Big Increment for Small Classes
	 */
	private static final short bigClass = 100;

	/**
	 * Value for Undefined terrain type
	 */
	public static final short Undefined = -1;
	/**
	 * Value for Raw terrain type
	 */
	public static final short Raw_Field = 0;
	/**
	 * Interval for Water Ways terrain types + smallClass
	 */
	public static final short Waterway = Raw_Field + smallClass;
	/**
	 * Interval for Barriers terrain types + smallClass
	 */
	public static final short Barrier = Waterway + smallClass;
	/**
	 * Interval for Natural terrain types + smallClass
	 */
	public static final short Natural = Barrier + smallClass;
	/**
	 * Interval for Land Uses terrain types + smallClass
	 */
	public static final short Landuse = Natural + smallClass;
	/**
	 * Interval for Geological terrain types + smallClass
	 */
	public static final short Geological = Landuse + smallClass;
	/**
	 * Generic value for Land Types, greater than all terrain natural values
	 */
	public static final short Land = 200;
	/*
	 * End Land Types Begin Road Types
	 */
	/**
	 * Interval for Track types + mediumClass
	 */
	public static final short Tracktype = Land + mediumClass;
	/**
	 * Interval for Cycle Ways types + mediumClass
	 */
	public static final short Cycleway = Tracktype + mediumClass;
	/**
	 * Interval for Aerial Ways types + mediumClass
	 */
	public static final short Aerialway = Tracktype + mediumClass;
	/**
	 * Interval for Rail Ways types + mediumClass
	 */
	public static final short Railway = Aerialway + mediumClass;
	/**
	 * Interval for High Ways types + mediumClass
	 */
	public static final short Highway = Railway + bigClass;
	/**
	 * Generic value for Road Types, greater than all road values
	 */
	public static final short Roads = 600;
	/*
	 * End Roads Begin Safe Points
	 */
	/*
	 * Open Spaces
	 */
	/**
	 * Man Made, Like WindMills, WaterMills ...
	 */
	public static final short Man_Made = Roads + smallClass;
	/**
	 * Shops, Main, ..
	 */
	public static final short Shop = Man_Made + smallClass;
	/**
	 * Turistic Places
	 */
	public static final short Tourism = Shop + smallClass;
	/**
	 * Power Structures
	 */
	public static final short Power = Tourism + smallClass;
	/**
	 * squares, ...
	 */
	public static final short Leisure = Man_Made + mediumClass;
	/**
	 * Parks, natural parks ...
	 */
	public static final short Amenity = Leisure + mediumClass;

	/*
	 * Infrastructures
	 */
	/**
	 * Historic places, ...
	 */
	public static final short Historic = Amenity + smallClass;
	/**
	 * Military structures, places, buildings ...
	 */
	public static final short Military = Historic + smallClass;
	/**
	 * Buildings, hospitals, ...
	 */
	public static final short Building = Military + mediumClass * 4;
	/**
	 * airports, heliports ....
	 */
	public static final short Aeroway = Building + mediumClass * 5;
	/**
	 * Generic value for Safes Places, greater than all places values
	 */
	public static final short SafePoint = 1000;
	/*
	 * End Safe Points begin Others
	 */
	/**
	 * Boundary limits
	 */
	public static final short Boundary = -2;
	/**
	 * Multipolygon OSM class
	 */
	public static final short Multipolygon = -3;
	/**
	 * Man made routes
	 */
	public static final short Route = -4;

	/**
	 * Given a proper url for OSM returns a file with the information
	 * 
	 * @param url
	 *            Url for the petition
	 * @param fileName
	 *            Name of the new file
	 * @return File containing OSM data
	 */
	public static File getOSMXmlFromURL(String url, String fileName) {
		try {
			// creamos un fichero en el directorio temporal
			File dir = TempFiles.getDefaultTempDir();
			File file = new File(dir, fileName);
			if (!file.exists()) {
				if (!Wget.wget(dir.getPath(), url)) {
					System.err
							.println("I couldn't download the data from Open Street Maps");
					return null;
				} else {
					// Si se ha descargado devolvemos el fichero
					file = new File(dir, fileName);
					System.out.println("The file is downloaded "
							+ file.getPath());
					return file;
				}
			} else {
				// Fichero ya descargado, lo devolvemos
				System.out.println("File already on disc " + file.getName());
				return file;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		// Unrecheable
		return null;
	}

	/**
	 * Parses XML file and returns XML document
	 * 
	 * @param sourceFile
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
	 * Given an {@link HexagonalGrid}, it downloads from OSM all the street
	 * information and updates the grid
	 * 
	 * @param grid
	 *            {@link HexagonalGrid}
	 */
	public static void setOsmMapInfo(HexagonalGrid grid) {
		OsmMap osmMap = OsmMap.getMap(grid);
		for (OsmRelation r : osmMap.getRelations()) {
			if (r.getType() > Undefined) {
				// System.err.println("**Escribiendo Relations " + r);
				setStreetValue(r, grid);
			} else {
				// System.err.println("No se ha escrito: " + r);
			}
		}

		for (OsmWay w : osmMap.getWays().values()) {
			if (w.getType() > Undefined) {
				setStreetValue(w, grid);
			} else {
				// System.err.println("No se ha escrito: " + w);
			}
		}

		for (OsmNode n : osmMap.getNodes().values()) {
			// Solo safePoints, lo demas no me interesa
			if (getGenericType(n.getType()) == SafePoint) {
				// System.err.println("Escribiendo Nodes: " + n);
				setStreetValue(n, grid);
			}
		}

	}

	/**
	 * Sets street value "type" in point "p", only if type is greater than
	 * previous value. If values are equal, means that there is an intersection,
	 * then add 1 to the value to make it odd
	 * 
	 * @param p
	 *            Point
	 * @param type
	 *            Street Value
	 * @param grid
	 * @return true if the previous value was less or equal than type, false if
	 *         not
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
			} catch (ArrayIndexOutOfBoundsException e) {
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
			} catch (ArrayIndexOutOfBoundsException e) {
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
	 * Only for SafePoints, if they're not accesible (near a street) moves the
	 * point to the nearest street
	 * 
	 * @param n
	 *            {@link OsmNode} SafePoint
	 * @param grid
	 * @return false if is not a safe point, true if has changed value
	 */
	public static boolean setStreetValue(OsmNode n, HexagonalGrid grid) {
		if (grid.getBox().contains(n.getCoord())
				&& Osm.getGenericType(n.getType()) == Osm.SafePoint) {
			// Si esta dentro del grid y es un nodo SafePoint
			Point point = grid.coordToTile(n.getCoord());
			// Ahora tengo que mirar si es accesible
			for (Point p : grid.getAdjacents(point)) {
				// MIro punto por punto a ver si alguno de ellos es carretera
				if (Osm.getGenericType(grid.getStreetValue(p)) == Roads) {
					return setStreetValue(p, n.getType(), grid);
				}
			}
			// Si hemos llegado aqui es que no es accesible
			int maxCol = grid.getColumns();
			int maxRow = grid.getRows();
			int col = point.getCol();
			int row = point.getRow();
			int i = 1;
			while (Math.max(maxCol, maxRow) > i) {
				// Mientras no sea accesible, pues seguimos buscando una casilla
				// accesible, miramos ariba/abajo/izq/der
				if (col + i < maxCol) {
					point = new Point(col + i, row);
					if (Osm.getGenericType(grid.getStreetValue(point)) == Roads) {
						return setStreetValue(point, n.getType(), grid);
					}
				}
				if (col - i > 0) {
					point = new Point(col - i, row);
					if (Osm.getGenericType(grid.getStreetValue(point)) == Roads) {
						return setStreetValue(point, n.getType(), grid);
					}
				}
				if (row + i < maxRow) {
					point = new Point(col, row + i);
					if (Osm.getGenericType(grid.getStreetValue(point)) == Roads) {
						return setStreetValue(point, n.getType(), grid);
					}
				}
				if (row - i > 0) {
					point = new Point(col, row - i);
					if (Osm.getGenericType(grid.getStreetValue(point)) == Roads) {
						return setStreetValue(point, n.getType(), grid);
					}
				}
				i++;
			}
		}
		return false;
	}

	/**
	 * Add a way to the street grid
	 * 
	 * @param w
	 *            Contains the way we want to draw
	 * @param grid
	 *            Grid where we want to draw
	 */
	public static void setStreetValue(OsmWay w, HexagonalGrid grid) {
		w.getBox().intersection(grid.getBox());
		short type = w.getType();
		for (Point p : w.getLines(grid)) {
			// System.err.println("Escribiendo punto "+p+" de way "+w.getId());
			setStreetValue(p, type, grid);
		}

		if (w.isClosedLine() && !isRoad(type)) {
			// System.err.println("Detectada linea cerrada " + w.getBox() + ", "
			// + w.getWay());
			Point nW;
			Point sE;
			// A veces me salgo ... para esos momentos ...
			try {
				nW = grid.coordToTile(w.getBox().getNw());
			} catch (ArrayIndexOutOfBoundsException e) {
				nW = new Point(0 + grid.getOffCol(), 0 + grid.getOffRow());
			}
			try {
				sE = grid.coordToTile(w.getBox().getSe());
			} catch (ArrayIndexOutOfBoundsException e) {
				sE = new Point(grid.getColumns() + grid.getOffCol(), grid
						.getRows()
						+ grid.getOffRow());
			}
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

	/**
	 * Sets relation to a {@link HexagonalGrid}
	 * 
	 * @param r
	 *            A relation is a collection of ways that may be a river, a park
	 *            ...
	 * @param grid
	 *            Grid where we want to draw
	 */
	public static void setStreetValue(OsmRelation r, HexagonalGrid grid) {
		OsmWay way = OsmWay.join(r, grid.getBox());
		setStreetValue(way, grid);
	}

	/**
	 * Given a list of tags, returns the type
	 * 
	 * @param tags
	 *            Tags containing Osm Information about the node
	 * @return Node Type
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

	/**
	 * Given a RailWay "type", returns proper street value
	 * 
	 * @param type
	 *            A railway type
	 * @return Specific Railway
	 */
	private static short getRailway(String type) {
		short key = Railway;
		key += 2;
		if (type.equalsIgnoreCase("rail"))
			return key;
		key += 2;
		if (type.equalsIgnoreCase("tram"))
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
	 * Given a highway "type" and "kind" returns a proper value for the road.
	 * Differences between kinds are made even, because odds value are for
	 * intersections
	 * 
	 * @param type
	 *            Highway, junction, traffic_calming...
	 * @param kind
	 *            Footway, track, cycleway...
	 * @return
	 */
	private static short getHighway(String type, String kind) {
		short key = Highway;
		if (type.equalsIgnoreCase("highway")) {
			// Higways
			if (kind.equalsIgnoreCase("footway")
					|| kind.equalsIgnoreCase("path"))
				return key;
			key += 2;
			if (kind.equalsIgnoreCase("track"))
				return key;
			key += 2;
			if (kind.equalsIgnoreCase("cycleway"))
				return key;
			key += 2;
			if (kind.equalsIgnoreCase("residential")
					|| kind.contains("parking"))
				return key;
			key += 2;
			if (kind.equalsIgnoreCase("road")
					|| kind.equalsIgnoreCase("pedestrian"))
				return key;
			key += 2;
			if (kind.equalsIgnoreCase("service"))
				return key;
			key += 2;
			if (kind.equalsIgnoreCase("tertiary"))
				return key;
			key += 2;
			if (kind.equalsIgnoreCase("secondary"))
				return key;
			key += 2;
			if (kind.equalsIgnoreCase("primary"))
				return key;
			key += 2;
			if (kind.contains("trunk"))
				return key;
			key += 2;
			if (kind.contains("motorway"))
				return key;
		} else if (type.equalsIgnoreCase("junction")) {

		} else if (type.equalsIgnoreCase("traffic_calming")) {

		} else {
			// service
		}
		return Highway;
	}

	/**
	 * Returns Generic Type, from a specific value.
	 * 
	 * @param value
	 *            Specific value
	 * @return General type from value
	 */
	public static short getGenericType(short value) {
		if (value < Raw_Field) {
			return Undefined;
		} else if (value < Land) {
			return Land;
		} else if (value < Roads) {
			return Roads;
		} else if (value < SafePoint) {
			return SafePoint;
		}
		return Undefined;
	}

	/**
	 * Returns Parent Type, from a value
	 * 
	 * @param value
	 * @return Parent Type
	 */
	public static short getType(short value) {
		if (value < Raw_Field) {
			return Undefined;
		} else if (value < Land) {
			// Land Type
			if (value < Waterway)
				return Raw_Field;
			if (value < Barrier)
				return Waterway;
			if (value < Natural)
				return Barrier;
			if (value < Landuse)
				return Natural;
			if (value < Geological)
				return Landuse;
			return Geological;
		} else if (value < Roads) {
			if (value < Cycleway)
				return Tracktype;
			if (value < Aerialway)
				return Cycleway;
			if (value < Railway)
				return Aerialway;
			if (value < Highway)
				return Railway;
			return Highway;
		} else if (value < SafePoint) {
			if (value < Shop)
				return Man_Made;
			if (value < Tourism)
				return Shop;
			if (value < Power)
				return Tourism;
			if (value < Leisure)
				return Power;
			if (value < Amenity)
				return Leisure;
			return Amenity;
		} else {
			// Infrastructura
			if (value < Military)
				return Historic;
			if (value < Building)
				return Military;
			if (value < Aeroway)
				return Building;
			return Aeroway;
		}
	}

	/**
	 * Given a type returns the proper color.
	 * 
	 * @param type
	 *            Osm Type
	 * @return Returns specific color for to this value
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
	 *            Osm Type value
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
	 * Returns true if it's a Road Value and odd, because that means it's an
	 * intersection
	 * 
	 * @param value
	 *            Osm Street Value
	 * @return true is is an odd value and isRoad
	 */
	public static boolean isIntersection(short value) {
		return isRoad(value) && (value % 2 != 0);
	}

	/**
	 * Returns true if value is a Road Value
	 * 
	 * @param value
	 *            Osm Street
	 * @return True if Road > value > Land, false if not.
	 */
	public static boolean isRoad(short value) {
		return (value > Land) && (value < Roads);
	}
}
