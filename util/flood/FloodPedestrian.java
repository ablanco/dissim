package util.flood;

import util.jcoord.LatLng;

public class FloodPedestrian {

	public final static int Healthy = 0;
	public final static int Hurt = 1;
	public final static int Dead = 2;
	
	private LatLng pos;
	private int status;
	
	public FloodPedestrian(LatLng pos, int status){
		this.pos = pos;
		this.status = status;
	}
	
	public LatLng getPos() {
		return pos;
	}
	
	public int getStatus() {
		return status;
	}
	
	public static int getStatus(short waterLevel){
		if (waterLevel > 150)
			return Dead;
		else if (waterLevel > 0)
			return Hurt;
		else
			return Healthy;
	}
}
