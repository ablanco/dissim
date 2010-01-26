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

package agents.flood;

import jade.core.Agent;
import behaviours.flood.FloodTileBehav;

public class WaterAgent extends Agent {

	private static final long serialVersionUID = -8213275953034715877L;

	@Override
	protected void setup() {
		// Obtener argumentos
		Object[] args = getArguments();
		int x; // Posición inicial en la rejilla
		int y;
		if (args != null && args.length == 2) {
			x = Integer.parseInt((String) args[0]);
			y = Integer.parseInt((String) args[1]);
		} else {
			throw new IllegalArgumentException("Wrong arguments.");
		}

		// Añadir comportamientos
		addBehaviour(new FloodTileBehav(this, x, y));
	}
}
