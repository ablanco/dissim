package osm;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.w3c.dom.Node;

import util.jcoord.LatLngBox;

public class OsmRelation {
	private long id;
	private LatLngBox box;
	private List<OsmMember> members;
	private List<OsmTag> tags;
	private short type = Osm.Undefined;

	private OsmRelation(long id) {
		this.id = id;
		members = new ArrayList<OsmMember>();
		tags = new ArrayList<OsmTag>();
		box = new LatLngBox();
	}

	public long getId() {
		return id;
	}

	public short getType() {
		return type;
	}

	public List<OsmTag> getTags() {
		return tags;
	}
	
	public LatLngBox getBox() {
		return box;
	}

	public void setType(short type) {
		this.type = type;
	}

	public List<OsmMember> getMembers() {
		return members;
	}

	public boolean addMember(OsmMember member) {
		if (member != null) {
			box.addToBox(member.getWay());
			return members.add(member);
		} else {
			return false;
		}
	}

	public boolean addTag(OsmTag tag) {
		if (tag != null) {
			return tags.add(tag);
		} else {
			return false;
		}
	}

	public boolean isEmpty() {
		return members.isEmpty();
	}
	
	/**
	 * Devuelve una lista de joins de memebers, el resultado de emparejar a cada way con su pareja.
	 * @param gridBox
	 * @return
	 */
	public List<OsmWay> matchMembers(LatLngBox gridBox){
		//TODO emparejarlos ... match.com funcionara??
		List<OsmWay> list = new ArrayList<OsmWay>();
		list.add(OsmWay.join(members.get(1).getWay(), members.get(2).getWay(),type,gridBox));
		return null;
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
			result.append("\t"+m + "\n");
		}
		return result.toString();
	}

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
//			short type = Osm.getNodeType(relation.getTags());
			//Cojemos el tipo del primer member que tenga
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
