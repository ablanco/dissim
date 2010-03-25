package util;

import java.io.Serializable;

import util.jcoord.LatLng;

public class Pedestrian implements Serializable {

	private static final long serialVersionUID = 1L;

	public final static int HEALTHY = 0;
	public final static int HURT = 1;
	public final static int DEAD = 2;

	private LatLng pos = null;
	private int status = HEALTHY;
	private String id = null;
	private Point point = null;
	
	public Pedestrian(Point point) {
		this.point = point;
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

	public static int getStatus(short waterLevel) {
		if (waterLevel > 150) // TODO Mangazo
			return DEAD;
		else if (waterLevel > 0)
			return HURT;
		else
			return HEALTHY;
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
}
