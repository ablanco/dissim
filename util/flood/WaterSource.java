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

import java.io.Serializable;

import util.jcoord.LatLng;

/**
 * Represents an outcoming source of water
 * 
 * @author Manuel Gomar, Alejandro Blanco
 * 
 */
public class WaterSource implements Serializable {

	private static final long serialVersionUID = 1L;

	private LatLng coord;
	/**
	 * Quantity of water that enters at this coordinate
	 */
	private short water;

	/**
	 * New water source in a coordinate, that produces a quantity of water every
	 * tick
	 * 
	 * @param coord
	 *            to place the water outcome
	 * @param water
	 *            quantity of water per tick
	 */
	public WaterSource(LatLng coord, short water) {
		this.coord = coord;
		this.water = water;
	}

	/**
	 * Gets geolocalization of the water source
	 * 
	 * @return coordinate
	 */
	public LatLng getCoord() {
		return coord;
	}

	/**
	 * Gets quantity of water per tick
	 * 
	 * @return water
	 */
	public short getWater() {
		return water;
	}

}
