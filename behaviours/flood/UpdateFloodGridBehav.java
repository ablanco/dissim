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

package behaviours.flood;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

import java.util.ArrayList;
import java.util.Iterator;

import util.flood.FloodHexagonalGrid;

public class UpdateFloodGridBehav extends TickerBehaviour {

	private static final long serialVersionUID = 8964259995058162322L;

	protected FloodHexagonalGrid grid;
	protected short water;

	public UpdateFloodGridBehav(Agent a, long period, FloodHexagonalGrid grid,
			short water) {
		super(a, period);
		this.grid = grid;
		this.water = water;
	}

	@Override
	protected void onTick() {
		Iterator<int[]> it = grid.getModCoordAndReset().iterator();
		// Por cada casilla modificada
		while (it.hasNext()) {
			int[] coord = it.next();
			ArrayList<int[]> adjacents = grid.getAdjacents(coord[0], coord[1]);
			short value = grid.getValue(coord[0], coord[1]);

			int[] adjCoord = coord;
			short adjValue = value;
			Iterator<int[]> itadj = adjacents.iterator();
			// Buscamos un casilla más baja que la modificada
			while (itadj.hasNext()) {
				int[] tile = itadj.next();
				if (((short) tile[2]) < adjValue) {
					adjValue = (short) tile[2];
					adjCoord = new int[] { tile[0], tile[1] };
				}
			}
			// Si no la hay es que la modificada es más baja o igual
			if (adjValue == value) {
				itadj = adjacents.iterator();
				// Buscamos una casilla más alta que la modificada
				while (itadj.hasNext()) {
					int[] tile = itadj.next();
					if (((short) tile[2]) > adjValue) {
						adjValue = (short) tile[2];
						adjCoord = new int[] { tile[0], tile[1] };
					}
				}
				// Hay una adyacente más alta, hay que mover agua desde la
				// adyacente a la modificada
				if (adjValue != value) {
					short volume = grid.decreaseValue(adjCoord[0], adjCoord[1],
							water);
					volume = grid.increaseValue(coord[0], coord[1], volume);
					if (volume > 0) {
						// TODO Agua sobrante
					}
				}
				// ELSE Si no la hay es que no hay que hacer nada pues las
				// alturas son las mismas
			}
			// Hay una adyacente más baja, hay que mover agua desde la
			// modificada a la más baja
			else {
				short volume = grid.decreaseValue(coord[0], coord[1], water);
				volume = grid.increaseValue(adjCoord[0], adjCoord[1], volume);
				if (volume > 0) {
					// TODO Agua sobrante
				}
			}
		}
	}

}
