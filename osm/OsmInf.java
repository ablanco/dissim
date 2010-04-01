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

import org.w3c.dom.Node;

public class OsmInf {

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
	private static final short Roads = 600;
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

	private static final short SafePoint = 1000;
	// End Safe Points
	// Others
	public static final short Boundary = -2;

	/**
	 * Given a Node Returns a Full of Info Node
	 * @param node
	 * @return
	 */
	public static OsmNodeExtendedInfo getExtendedInfo(Node node) {
		String value = null;
		String name = null;
		String type = null;
		short key = Undefined;
		while (node != null) {
			if (node.getNodeName().equalsIgnoreCase("tag")) {
				type = node.getAttributes().item(0).getNodeValue();
				// osmLog.debug("value: " + value + " | ");
				// Getting Name
				if (type.equalsIgnoreCase("name")) {
					name = node.getAttributes().item(1).getNodeValue();
					// Getting use of the buildin, building value: yes/no
				} else if (type.equalsIgnoreCase("amenity")) {
					value = node.getAttributes().item(1).getNodeValue();
					key = Amenity;
				} else if (type.equalsIgnoreCase("historic")) {
					value = node.getAttributes().item(1).getNodeValue();
					key = Historic;
				} else if (type.equalsIgnoreCase("leisure")
						|| type.equalsIgnoreCase("sport")) {
					value = node.getAttributes().item(1).getNodeValue();
					key = Leisure;
				} else if (type.equalsIgnoreCase("aeroway")) {
					value = node.getAttributes().item(1).getNodeValue();
					key = Aeroway;
				} else if (type.equalsIgnoreCase("highway")
						|| type.equalsIgnoreCase("junction")
						|| type.equalsIgnoreCase("traffic_calming")
						|| type.equalsIgnoreCase("service")) {
					value = node.getAttributes().item(1).getNodeValue();
					key = getHighway(type, value);
				} else if (type.equalsIgnoreCase("Barrier")) {
					value = node.getAttributes().item(1).getNodeValue();
					key = Barrier;
				} else if (type.equalsIgnoreCase("Cycleway")) {
					value = node.getAttributes().item(1).getNodeValue();
					key = Cycleway;
				} else if (type.equalsIgnoreCase("Tracktype")) {
					value = node.getAttributes().item(1).getNodeValue();
					key = Tracktype;
				} else if (type.equalsIgnoreCase("Waterway")) {
					value = node.getAttributes().item(1).getNodeValue();
					key = getWaterway(value);
				} else if (type.equalsIgnoreCase("Railway")) {
					value = node.getAttributes().item(1).getNodeValue();
					key = getRailway(value);
				} else if (type.equalsIgnoreCase("Aerialway")) {
					value = node.getAttributes().item(1).getNodeValue();
					key = Aerialway;
				} else if (type.equalsIgnoreCase("Power")) {
					value = node.getAttributes().item(1).getNodeValue();
					key = Power;
				} else if (type.equalsIgnoreCase("Shop")) {
					value = node.getAttributes().item(1).getNodeValue();
					key = Shop;
				} else if (type.equalsIgnoreCase("ManMade")) {
					value = node.getAttributes().item(1).getNodeValue();
					key = Man_Made;
				} else if (type.equalsIgnoreCase("Historic")) {
					value = node.getAttributes().item(1).getNodeValue();
					key = Historic;
				} else if (type.equalsIgnoreCase("Military")) {
					value = node.getAttributes().item(1).getNodeValue();
					key = Military;
				} else if (type.equalsIgnoreCase("Natural")) {
					value = node.getAttributes().item(1).getNodeValue();
					key = Natural;
				} else if (type.equalsIgnoreCase("Geological")) {
					value = node.getAttributes().item(1).getNodeValue();
					key = Geological;
				} else if (type.equalsIgnoreCase("Building")) {
					value = node.getAttributes().item(0).getNodeValue();
					key = Building;
				} else if (type.equalsIgnoreCase("Landuse")) {
					value = node.getAttributes().item(0).getNodeValue();
					key = Landuse;
				} else if (type.equalsIgnoreCase("boundary")) {
					value = node.getAttributes().item(0).getNodeValue();
					key = Boundary;
				}

			}
			node = node.getNextSibling();
		}
		return new OsmNodeExtendedInfo(key, name, value);
	}

	private static short getRailway(String value) {
		short key = Railway;
		key++;
		if (value.equalsIgnoreCase("rail"))
			return key;
		key++;
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
	 * Given a highway Type Returns a Proper value for this kind of Highway
	 * @param type
	 * @param value
	 * @return 
	 */
	private static short getHighway(String type, String value) {
		short key = Highway;
		if (type.equalsIgnoreCase("highway")) {
			// Higways
			if (value.equalsIgnoreCase("footway"))
				return key;
			if (value.equalsIgnoreCase("path"))
				return key;
			key++;
			if (value.equalsIgnoreCase("track"))
				return key;
			key++;
			if (value.equalsIgnoreCase("cycleway"))
				return key;
			key++;
			if (value.equalsIgnoreCase("residential")
					|| value.contains("parking"))
				return key;
			key++;
			if (value.equalsIgnoreCase("road")
					|| value.equalsIgnoreCase("pedestrian"))
				return key;
			key++;
			if (value.equalsIgnoreCase("service"))
				return key;
			key++;
			if (value.equalsIgnoreCase("tertiary"))
				return key;
			key++;
			if (value.equalsIgnoreCase("secondary"))
				return key;
			key++;
			if (value.equalsIgnoreCase("primary"))
				return key;
			key++;
			if (value.contains("trunk"))
				return key;
			key++;
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
	 * Given a type, returns Parent Type
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
	
	
}
