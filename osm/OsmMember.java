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

import java.util.Hashtable;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Class for managing related ways, providing a role and a reference
 * 
 * @author Manuel Gomar, Alejandro Blanco
 * 
 */
public class OsmMember {
	private OsmWay way;
	private String role;
	private short type;
	private long ref;

	/**
	 * Private constructor, initializates the {@link OsmMember} by parameters
	 * 
	 * @param type
	 *            defined by {@link Osm} static values
	 * @param ref
	 *            OSM id
	 * @param way
	 *            osmWay
	 * @param role
	 */
	private OsmMember(short type, long ref, OsmWay way, String role) {
		this.type = type;
		this.ref = ref;
		this.way = way;
		this.role = role;
	}

	/**
	 * @return true if the osmWay is empty
	 */
	public boolean isEmpty() {
		return way.getWay().isEmpty();
	}

	public void setWay(OsmWay way) {
		this.way = way;
	}

	/**
	 * Role of the member, given by OSM standart
	 * 
	 * @return the OSM role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * OSM id
	 * 
	 * @return Osm id
	 */
	public long getRef() {
		return ref;
	}

	/**
	 * OSM static value
	 * 
	 * @return type
	 */
	public short getType() {
		return type;
	}

	/**
	 * OSM way containing member
	 * 
	 * @return osmWay containing member
	 */
	public OsmWay getWay() {
		return way;
	}

	/**
	 * Check if the given object is equal to the OSM way that contains member
	 */
	@Override
	public boolean equals(Object obj) {
		return way.equals(obj);
	}

	@Override
	public String toString() {
		return "Member, ref: " + ref + ", type: " + type + ", role: " + role;// +" || "+way.getWay();
	}

	/**
	 * Parse and initalizate the values for managing a OSM member type
	 * 
	 * @param node
	 *            xml node from osm
	 * @param ways
	 *            already parsed and added to the map
	 * @return parsed initializaded osmMember
	 */
	public static OsmMember getMember(Node node, Hashtable<Long, OsmWay> ways) {
		NamedNodeMap attributes = node.getAttributes();
		long ref = Long.parseLong(attributes.item(1).getNodeValue());
		String role = attributes.item(2).getNodeValue();
		OsmWay osmWay = ways.get(ref);
		if (osmWay != null) {
			return new OsmMember(osmWay.getType(), ref, osmWay, role);
		} else {
			return null;
		}
	}

}
