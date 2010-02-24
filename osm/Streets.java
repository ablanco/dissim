package osm;

import java.util.List;
import java.util.SortedSet;

import util.Logger;
import util.jcoord.LatLng;

public class Streets {

	private Logger streetsLogger;
	protected String continent;
	protected String name;
	protected String place;
	protected long id;
	protected SortedSet<Way> ways;

	// url="http://api.openstreetmap.org/api/0.6/map?bbox=11.54,48.14,11.543,48.145";
	public Streets(long id, String continent, String name, String place) {
		this.continent = continent;
		this.name = name;
		this.place = place;
		this.id = id;
		ways = new TreeSet<Way>;
	}
	
	public void addWay(Way way){
		ways.add(way);
	}

	protected class Way implements Comparable<Way>{
		protected SortedSet<Node> way;
		protected long id;
		protected Node first;
		protected Node last;
		protected String type;
		protected String name;

		public Way(long id, String name, String type) {
			this.name = name;
			this.type = type;
			this.id = id;
		}

		public void addToWay(Node node) {
			way.add(node);
		}

		public boolean containsNode(Node node) {
			return way.contains(node);
		}

		@Override
		public int compareTo(Way o) {
			if (id == o.id)
			return 0;
			if (id > o.id){
				return (int) (id-o.id);
			}else{
				return (int) (o.id - id);
			}
		}
		
		public boolean equals (Object o){
			Way way = (Way) o;
			return id==way.id;
		}
	}

	protected class Intersection {
		protected List<Node> intersection;

	}

	protected class Node implements Comparable<Node> {

		protected long id;
		protected LatLng coord;

		public Node(long id, LatLng coord) {
			this.id = id;
			this.coord = coord;
		}

		@Override
		public int compareTo(Node o) {
			double alat = coord.getLat();
			double alng = coord.getLng();

			double blat = o.coord.getLat();
			double blng = o.coord.getLng();
			if (alat == blat && alng == blng)
				return 0;

			if (alat >= blat) {
				if (alng >= blng) {
					return -2;
				}
				return -1;
			} else {
				if (alng <= blng) {
					return 2;
				}
				return 1;
			}
		}

		public boolean equals(Object o) {
			Node node = (Node) o;
			return id == node.id;
		}
	}
}
