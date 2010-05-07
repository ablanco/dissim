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
