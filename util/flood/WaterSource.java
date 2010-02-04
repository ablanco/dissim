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

package util.flood;

import util.jcoord.LatLng;

public class WaterSource {

	private LatLng coord;
	/**
	 * Cantidad de agua que entra en ese punto
	 */
	private short water;
	/**
	 * Periodo en milisegundos entre cada entrada de agua
	 */
	private long rhythm;

	public WaterSource(LatLng coord, short water, long rhythm) {
		this.coord = coord;
		this.water = water;
		this.rhythm = rhythm;
	}

	public LatLng getCoord() {
		return coord;
	}

	public short getWater() {
		return water;
	}

	public long getRhythm() {
		return rhythm;
	}

}
