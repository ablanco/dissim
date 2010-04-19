package osm;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.w3c.dom.Node;

public class OsmRelation {
	private long id;
	private List<OsmMember> members;
	private List<OsmTag> tags;
	private short type = Osm.Undefined;

	private OsmRelation(long id) {
		this.id = id;
		members = new ArrayList<OsmMember>();
		tags = new ArrayList<OsmTag>();
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

	public void setType(short type) {
		this.type = type;
	}

	public List<OsmMember> getMembers() {
		return members;
	}

	public boolean addMember(OsmMember member) {
		if (member != null) {
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

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("Relation " + id + ", " + type + ": ");
		for (OsmTag tag : tags) {
			result.append(tag + ", ");
		}
		for (OsmMember m : members) {
			result.append(m + "\n");
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
			short type = Osm.getNodeType(relation.getTags());
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
