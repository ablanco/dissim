package kml;

public class KmlInf {
	
	private short[][] waterGrid;
	private String begin;
	private String end;
	private String name;
	private double [] incs;
	
	public KmlInf(String name, String begin, String end, double[] incs){
		this.name = name;
		this.begin = begin;
		this.end = end;
		this.incs = incs;
	}
	
	public String getBegin() {
		return begin;
	}
	
	public String getEnd() {
		return end;
	}
	
	public short[][] getWaterGrid() {
		return waterGrid;
	}
	
	public String getName() {
		return name;
	}

	public double[] getIncs() {
		return incs;
	}
	
	public void setGrid(short[][] grid) {
		this.waterGrid = grid.clone();
	}
	
	/**
	 * Updates Date
	 * @param end time of the events
	 */
	public void SetNewDate(String end){
		begin = this.end;
		this.end = end;
	}
	
	@Override
	public String toString() {
		return name+", From "+begin+" to "+end;
	}
}
