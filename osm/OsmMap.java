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

import java.util.ArrayList;
import java.util.List;

import util.HexagonalGrid;
import util.Point;
import util.jcoord.LatLng;

public class OsmMap {
	

	protected String continent;
	protected String name;
	protected String place;
	protected long id;
	protected List<OsmWay> ways;
	protected List<OsmNode> specialPlaces;

	public OsmMap(long id) {
		this.id = id;
		ways = new ArrayList<OsmWay>();
		specialPlaces = new ArrayList<OsmNode>();
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

	@Override
	public String toString() {
		String result = "";
		if (continent!=null)
			result += "Continent: "+continent+" ";
		if (place!= null)
			result += "Place: "+place +" ";
		if(name!= null){
			result +="Name: "+ name + "\n";
		}
		for (OsmWay w : ways) {
			result += w.toString() + "\n";
		}
		
		for (OsmNode n : specialPlaces) {
			result += n.toString() + "\n";
		}
		return result;
	}

	public void setMapInfo(HexagonalGrid infoGrid) {

		for (OsmWay way : ways) {
			try {
				setMapInfoWay(way, infoGrid);
			} catch (IllegalArgumentException e) {
				// System.err.println("*****No tiene nodos!!! " +
				// way.toString());
			}
		}
		for (OsmNode node : specialPlaces) {
			// System.err.println(node.toString());
			setMapInfoValue(infoGrid, node.coord, node.extendedInfo.getKey());
		}
	}

	private void setMapInfoWay(OsmWay way, HexagonalGrid grid) {
		List<OsmNode> nodeWay = way.getWay();
		if (nodeWay.size() == 0) {
			throw new IllegalArgumentException(
					"No puede haber un camino sin nodos");
		}
		List<Point> road = new ArrayList<Point>();
		OsmNode b = nodeWay.get(0);
		if (way.getFirsNode() != null) {
			road.addAll(aproximateWayPoints(way.getFirsNode(), b));
		}
		// Way from a to b
		for (int x = 1; x < nodeWay.size(); x++) {
			OsmNode a = b;
			b = nodeWay.get(x);
			road.addAll(aproximateWayPoints(a, b));
		}
		if (way.getLastNode() != null) {
			road.addAll(aproximateWayPoints(b, way.getLastNode()));
		}
		short key = way.getKey();
//		System.err.println(way.toString());
//		System.err.print("Way: "+way.getId()+", Nodes: ");
		for (Point p : road) {
//			System.err.print(p.toString()+", ");
			grid.setStreetValue(p, key);
		}
//		System.err.println();

	}

	private List<Point> aproximateWayPoints(OsmNode nodeA, OsmNode nodeB) {
		List<Point> road = new ArrayList<Point>();
		Point pointA = nodeA.getPoint();
		Point pointB = nodeB.getPoint();
		road.add(pointA);
		while (!pointA.equals(pointB)) {
			//Mientras no hayamos llegado al destino
			pointA = HexagonalGrid.nearestHexagon(pointA, pointB);
			//Añadimos punto a la carretera
			road.add(pointA);
		}
		return road; 
	}

	private void setMapInfoValue(HexagonalGrid infoGrid, LatLng coord,
			short value) {
		try {
			Point point = infoGrid.coordToTile(coord);
			infoGrid.setStreetValue(point.getCol(), point.getRow(), value);
			// System.err.println("Valor del grid cambiado con exito");
		} catch (IndexOutOfBoundsException e) {
			System.err
					.println("**************Intentando acceder fuera del array");
		}

	}
}
