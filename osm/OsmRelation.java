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
import java.util.Hashtable;
import java.util.List;

import org.w3c.dom.Node;

import util.jcoord.LatLngBox;

/**
 * Relations are a list of {@link OsmWay} that forms a complex polygon, such a
 * river, parks ...
 * 
 * @author Manuel Gomar, Alejandro Blanco
 * 
 */
public class OsmRelation {
	private long id;
	private LatLngBox box;
	private List<OsmMember> members;
	private List<OsmTag> tags;
	private short type = Osm.Undefined;

	/**
	 * {@link OsmRelation} constructor
	 * 
	 * @param id
	 */
	private OsmRelation(long id) {
		this.id = id;
		members = new ArrayList<OsmMember>();
		tags = new ArrayList<OsmTag>();
		box = new LatLngBox();
	}

	/**
	 * Gets osm id for the relation
	 * 
	 * @return id of the relation
	 */
	public long getId() {
		return id;
	}

	/**
	 * Gets {@link Osm} static value for the relation, witch is the same than
	 * the {@link OsmWay}
	 * 
	 * @return relation type
	 */
	public short getType() {
		return type;
	}

	/**
	 * Get the list of tags containing all the info collected from OSM
	 * 
	 * @return list of tags
	 */
	public List<OsmTag> getTags() {
		return tags;
	}

	/**
	 * Gets the containing box from the entire relation
	 * 
	 * @return box of coordinates
	 */
	public LatLngBox getBox() {
		return box;
	}

	/**
	 * Sets an specific value for the relation
	 * 
	 * @param type
	 *            static {@link Osm} value
	 */
	public void setType(short type) {
		this.type = type;
	}

	/**
	 * Gets {@link OsmMember} in the relation
	 * 
	 * @return list of members
	 */
	public List<OsmMember> getMembers() {
		return members;
	}

	/**
	 * Add a member to the list of members that defines the relation. Updates
	 * the size of the box
	 * 
	 * @param member
	 *            we want to add to the relation list
	 * @return true if inserted
	 */
	public boolean addMember(OsmMember member) {
		if (member != null) {
			box.addToBox(member.getWay());
			return members.add(member);
		} else {
			return false;
		}
	}

	/**
	 * Adds a tag to the tag list
	 * 
	 * @param tag
	 *            we want to add
	 * @return true if not null
	 */
	public boolean addTag(OsmTag tag) {
		if (tag != null) {
			return tags.add(tag);
		} else {
			return false;
		}
	}

	/**
	 * Check if has any members
	 * 
	 * @return true if member list is not empty
	 */
	public boolean isEmpty() {
		return members.isEmpty();
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("Relation " + id + ", " + type + ": ");
		for (OsmTag tag : tags) {
			result.append(tag + ", ");
		}
		result.append("\n");
		for (OsmMember m : members) {
			result.append("\t" + m + "\n");
		}
		return result.toString();
	}

	/**
	 * Given a xml node from OSM, parse and collect the info and returns a
	 * {@link OsmRelation}, also links {@link OsmWay} to the relation.
	 * 
	 * @param node
	 *            xml node from osm
	 * @param ways
	 *            from the {@link OsmMap}
	 * @return {@link OsmRelation} with the members that contains the box
	 */
	public static OsmRelation getRelation(Node node,
			Hashtable<Long, OsmWay> ways) {
		OsmRelation relation = new OsmRelation(Long.parseLong(node
				.getAttributes().item(0).getNodeValue()));
		// Bajamos un nivel
		node = node.getFirstChild();
		while (node != null) {
			String type = node.getNodeName();
			if (type.equalsIgnoreCase("member")) {
				relation.addMember(OsmMember.getMember(node, ways));

			} else if (type.equalsIgnoreCase("tag")) {
				relation.addTag(OsmTag.getTag(node));
			} else {
				// No deberiamos llegar aqui
			}
			node = node.getNextSibling();
		}
		if (relation.getMembers().size() > 0) {
			// short type = Osm.getNodeType(relation.getTags());
			// Cojemos el tipo del primer member que tenga
			short type = relation.getMembers().get(0).getType();
			if (type == Osm.Undefined) {
				relation.setType(relation.getMembers().get(0).getType());
			} else {
				relation.setType(type);
			}
			return relation;
		} else {
			return null;
		}
	}

}
