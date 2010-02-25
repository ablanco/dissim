package osm;

import java.util.SortedSet;

import osm.Streets.Way;

public class OsmWay implements Comparable<OsmWay>{
	protected SortedSet<OsmNode> way;
	protected long id;
	protected OsmNode first;
	protected OsmNode last;
	protected String type;
	protected String name;

	public OsmWay(long id, String name, String type) {
		this.name = name;
		this.type = type;
		this.id = id;
	}

	public void addToWay(OsmNode node) {
		way.add(node);
	}

	public boolean containsNode(OsmNode node) {
		return way.contains(node);
	}

	@Override
	public int compareTo(OsmWay w) {
		if (id == w.id)
		return 0;
		if (id > w.id){
			return (int) (id-w.id);
		}else{
			return (int) (w.id - id);
		}
	}
	
	public boolean equals (Object o){
		Way way = (Way) o;
		return id==way.id;
	}
}
