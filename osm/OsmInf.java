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
	public static final short Waterway = 1*smallClass;
	public static final short Barrier = 2*smallClass;
	public static final short Natural = 3*smallClass;
	public static final short Landuse = 4*smallClass;
	public static final short Geological = 5*smallClass;
	public static final short Tourism = 6*smallClass;
	public static final short Power = 7*smallClass;
	public static final short Man_Made = 8*smallClass;
	public static final short Leisure = 9*smallClass;
	public static final short Amenity = 10*smallClass;
	public static final short Shop = 11*smallClass;
	// Roads
	private static final short Roads = 600;
	public static final short Cycleway = Roads + mediumClass;
	public static final short Tracktype = Roads + mediumClass *2 ;
	public static final short Aerialway = Roads + mediumClass *3;
	public static final short Railway = Roads + mediumClass * 4;
	public static final short Highway = Roads + mediumClass * 5;
	// safePoints
	private static final short SafePoint = 1000; 
	public static final short Historic = SafePoint;
	public static final short Military = SafePoint + mediumClass *1;
	public static final short Building = SafePoint + mediumClass *2;
	public static final short Aeroway = SafePoint + mediumClass *3;

	public static OsmNodeExtendedInfo getExtendedInfo(Node node) {
		String value = "";
		String name = "";
		String type = "";
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
				} else if (type.equalsIgnoreCase("leisure")) {
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
					key = witchHighway(type, value);
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
					key = witchWaterway(value);
				} else if (type.equalsIgnoreCase("Railway")) {
					value = node.getAttributes().item(1).getNodeValue();
					key = Railway;
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
				} 
			}
			node = node.getNextSibling();
		}
		return new OsmNodeExtendedInfo(key, name, value);
	}

	
	private static short witchWaterway(String value) {
		short key = Waterway;
		if (value.equalsIgnoreCase("riverbank"))
			return key;
		return 0;
	}


	private static short witchHighway(String type, String value) {
		short key = Highway;
		if (type.equalsIgnoreCase("highway")) {
			//Higways
			if (value.equalsIgnoreCase("path"))
				return  key;
			key++;
			if (value.equalsIgnoreCase("track"))
				return key;
			key++;
			if (value.equalsIgnoreCase("cycleway"))
				return key;
			key++;
			if (value.equalsIgnoreCase("residential"))
				return key;
			key++;
			if (value.equalsIgnoreCase("road"))
				return key;
			key++;
			if (value.equalsIgnoreCase("service"))
				return  key;
			key++;
			if (value.contains("trunk"))
				return  key;
			key++;
			if (value.contains("motorway"))
				return  key;
		}else if (type.equalsIgnoreCase("junction")) {

		}else if (type.equalsIgnoreCase("traffic_calming")) {

		}else {
			// service
		}
		return Highway;
	}

	public static short whatType(short type) {
		if (type < Cycleway) {
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
			if (type < Tourism)
				return Geological;
			if (type < Power)
				return Tourism;
			if (type < Man_Made)
				return Power;
			if (type < Leisure)
				return Man_Made;
			if (type < Amenity)
				return Leisure;
			if (type < Shop)
				return Amenity;
			return Shop;
		} else if (type < Historic) {
			// Road type
			if (type < Tracktype)
				return Cycleway;
			if (type < Aerialway)
				return Tracktype;
			if (type < Railway)
				return Aerialway;
			if (type < Highway)
				return Railway;
			return Highway;
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
	
	public static Color witchColor(short type){
		short key = whatType(type);
		int darker = (type - key)*50;
		switch (key) {
		case Highway:
			return new Color (Color.RED.getRGB() + darker );
		case Waterway:
			return new Color (Color.BLUE.getRGB() + darker);
		default:
			return new Color(type*1000);
		}
	}
}
