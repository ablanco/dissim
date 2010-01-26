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

import util.flood.FloodHexagonalGrid;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class AddWaterGridBehav extends TickerBehaviour {

	private static final long serialVersionUID = 8455503506160028404L;

	protected FloodHexagonalGrid grid;
	protected int x;
	protected int y;
	protected double water;

	public AddWaterGridBehav(Agent a, long period, FloodHexagonalGrid grid,
			int x, int y, double water) {
		super(a, period);
		this.grid = grid;
		this.x = x;
		this.y = y;
		this.water = water;
	}

	@Override
	protected void onTick() {
		double spare = grid.increaseValue(x, y, water);
		if (spare > 0) {
			grid.increaseValue(x, y, spare); // TODO
		}
	}

}
