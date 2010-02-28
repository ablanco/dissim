package osm;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.w3c.dom.Node;

import util.HexagonalGrid;
import util.Point;
import util.jcoord.LatLng;

public class OsmMap {
	public static final short Raw_Field = 0;
	public static final short Highway = 100;
	public static final short Barrier = 200;
	public static final short Cycleway = 300;
	public static final short Tracktype = 400;
	public static final short Waterway = 500;
	public static final short Railway = 600;
	public static final short Aeroway = 700;
	public static final short Aerialway = 800;
	public static final short Power = 900;
	public static final short Man_Made = 1000;
	public static final short Leisure = 1100;
	public static final short Amenity = 1200;
	public static final short Shop = 1300;
	public static final short Tourism = 1400;
	public static final short Historic = 1500;
	public static final short Landuse = 1600;
	public static final short Military = 1700;
	public static final short Natural = 1800;
	public static final short Geological = 1900;
	public static final short Building = 2000;

	protected String continent;
	protected String name;
	protected String place;
	protected long id;
	protected SortedSet<OsmWay> ways;
	protected SortedSet<OsmNode> specialPlaces;

	public OsmMap(long id) {
		this.id = id;
		ways = new TreeSet<OsmWay>();
		specialPlaces = new TreeSet<OsmNode>();
	}
	
	public void setContinent(String continent) {
		this.continent = continent;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setPlace(String place) {
		this.place = place;
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
				}else if (type.equalsIgnoreCase("Building")) {
					value = node.getAttributes().item(0).getNodeValue();
					key = Building;
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
	
	public void setMapInfo(HexagonalGrid infoGrid){
		
		for (OsmWay way : ways){
			try{
			aproximateWay(way,infoGrid);
			}catch (IllegalArgumentException e) {
				System.err.println("*****No tiene nodos!!! "+way.toString());
			}
		}		
		for (OsmNode node : specialPlaces){
			//System.err.println(node.toString());
			setMapInfoValue(infoGrid, node.coord, node.extendedInfo.getKey());
		}
	}

	private void aproximateWay(OsmWay way, HexagonalGrid grid) {
		if (way.getWay()==null){
			throw new IllegalArgumentException("No puede haber un camino sin nodos");
		}
		List<OsmNode> nodeWay = way.getWay();
		short key = way.getKey();
		OsmNode b = nodeWay.get(0);
		/*
		if(way.getFirsNode()!=null){
			aproximateWay(aproximateNode(way.getFirsNode(),grid),b,grid,key);
		}*/
		//Way from a to b
		for (int x=1;x<nodeWay.size();x++){
			OsmNode a = b;
			b = nodeWay.get(x);
			aproximateWay(a,b,grid,key);
		}
		/*
		if(way.getLastNode()!=null){
			aproximateWay(b, aproximateNode(way.getLastNode(),grid),grid,key);
		}*/
	}


	private void aproximateWay(OsmNode a, OsmNode b, HexagonalGrid grid, short key) {
		Point A = a.getPoint();
		Point B = b.getPoint();
		int x = A.getX();
		int y = A.getY();
		int bX = B.getX();
		int bY = B.getY();
		grid.setTerrainValue(bX, bY, key);
		grid.setTerrainValue(x, y, key);
		while (x != bX || y != bY){
			if(x>bX){
				x--;
			}else if (x<bX){
				x++;
			}else{
				//x==bX
			}
			
			if(y>bY){
				y--;
			}else if (y<bY){
				y++;
			}else{
				//y==bY
			}
			grid.setTerrainValue(x, y, key);
		}
		
	}

	private OsmNode aproximateNode(OsmNode firsNode, HexagonalGrid grid) {
		// TODO Auto-generated method stub
		return null;
	}

	private void setMapInfoValue(HexagonalGrid infoGrid, LatLng coord, short value) {
		try{
			Point point = infoGrid.coordToTile(coord);
			infoGrid.setTerrainValue(point.getX(), point.getY(), value);
//			System.err.println("Valor del grid cambiado con exito");
		}catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("**************Intentando acceder fuera del array");
		}
		
	}
}
