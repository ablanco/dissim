package util;

import util.jcoord.LatLng;

public class Pedestrian {

	public final static int Healthy = 0;
	public final static int Hurt = 1;
	public final static int Dead = 2;
	
	private LatLng pos;
	private int status;
	private String id; 
	private Point point;
	
	public Pedestrian(LatLng pos, int status, String id){
		this.pos = pos;
		this.status = status;
		this.id = id;
	}
	
	public Pedestrian(Point point, int status, String id){
		this.point = point;
		this.status = status;
		this.id = id;
	}
	
	public Pedestrian(){};
	
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
	
	public static int getStatus(short waterLevel){
		if (waterLevel > 150)
			return Dead;
		else if (waterLevel > 0)
			return Hurt;
		else
			return Healthy;
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
