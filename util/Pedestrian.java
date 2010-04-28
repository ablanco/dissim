package util;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import util.jcoord.LatLng;

public class Pedestrian implements Serializable {

	private static final long serialVersionUID = 1L;

	public final static int HEALTHY = 0;
	public final static int DEAD = 1;
	public final static int HURT = 2;
	public final static int SAFE = 3;

	private LatLng pos = null;
	private int status = HEALTHY;
	private String id = null;
	private Point point = null;
	private String behaviour = "behaviours.people.flood.KnownSafepointPedestrianBehav";
	private int vision = -1;
	private int speed = -1;
	private int clones = 1;
	private Set<LatLng> objectives = null;

	public Pedestrian(Point point) {
		this.point = point;
	}

	public Pedestrian(LatLng pos) {
		this.pos = pos;
	}

	public Pedestrian(LatLng pos, int status, String id) {
		this.pos = pos;
		this.status = status;
		this.id = id;
	}

	public Pedestrian(Point point, int status, String id) {
		this.point = point;
		this.status = status;
		this.id = id;
	}

	public Pedestrian() {
	};

	public LatLng getPos() {
		return pos;
	}

	public int getStatus() {
		return status;
	}

	public String getId() {
		return id;
	}

	public Point getPoint() {
		return point;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public void setPos(LatLng pos) {
		this.pos = pos;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setBehaviourClass(String behaviour) {
		this.behaviour = behaviour;
	}

	public String getBehaviourClass() {
		return behaviour;
	}

	public int getVision() {
		return vision;
	}

	public int getSpeed() {
		return speed;
	}

	public int getClones() {
		return clones;
	}

	public void setScenData(int vision, int speed, int clones) {
		this.vision = vision;
		this.speed = speed;
		this.clones = clones;
	}

	public boolean addObjective(LatLng coord) {
		if (objectives == null)
			objectives = new HashSet<LatLng>();

		return objectives.add(coord);
	}

	public Set<LatLng> getObjectives() {
		return objectives;
	}

	public Object[] getChooseArgs() {
		if (behaviour
				.equals("behaviours.people.flood.KnownSafepointPedestrianBehav")) {
			return new Object[] { objectives };
		} else {
			return new Object[0];
		}
	}
}
