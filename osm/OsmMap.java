package osm;

import java.util.SortedSet;
import java.util.TreeSet;

import org.w3c.dom.Node;

public class OsmMap {
	public static final short Raw_Field = 0;
	public static final short Highway = 1;
	public static final short Barrier = 2;
	public static final short Cycleway = 3;
	public static final short Tracktype = 4;
	public static final short Waterway = 5;
	public static final short Railway = 6;
	public static final short Aeroway = 7;
	public static final short Aerialway = 8;
	public static final short Power = 9;
	public static final short Man_Made = 10;
	public static final short Leisure = 11;
	public static final short Amenity = 12;
	public static final short Shop = 13;
	public static final short Tourism = 14;
	public static final short Historic = 15;
	public static final short Landuse = 16;
	public static final short Military = 17;
	public static final short Natural = 18;
	public static final short Geological = 19;

	protected String continent;
	protected String name;
	protected String place;
	protected long id;
	protected SortedSet<OsmWay> ways;
	protected SortedSet<OsmNode> specialPlaces;

	public OsmMap(long id, String continent, String name, String place) {
		this.continent = continent;
		this.name = name;
		this.place = place;
		this.id = id;
		ways = new TreeSet<OsmWay>();
		specialPlaces = new TreeSet<OsmNode>();
	}

	public void addWay(OsmWay way) {
		ways.add(way);
	}

	public void addSpecialPlace(OsmNode node) {
		specialPlaces.add(node);
	}

	public static OsmNodeExtendedInfo getExtendedInfo(Node node) {
		String value = "";
		String name = "";
		String type = "";
		short key = 0;
		while (node!=null){
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
				} else if (type.equalsIgnoreCase("highway")) {
					value = node.getAttributes().item(1).getNodeValue();
					key = Highway;
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
					key = Waterway;
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
				}
			}
			node = node.getNextSibling();
		}
		return new OsmNodeExtendedInfo(key, name, value);
	}
	
	@Override
	public String toString() {
		String result = continent +" "+place+" "+name+"\n";
		for (OsmWay w : ways){
			result += w.toString()+"\n";
		}
		
		for (OsmNode n : specialPlaces){
			result += n.toString()+"\n";
		}
		return result;
	}
}
