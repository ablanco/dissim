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

package behaviours;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;

public class FloodTileBehav extends SimpleBehaviour {

	private static final long serialVersionUID = -7362590926527253261L;

	protected int x;
	protected int y;
	protected boolean stopped = false;

	public FloodTileBehav(Agent a, int x, int y) {
		super(a);
		this.x = x;
		this.y = y;
	}

	@Override
	public void action() {
		// Obtener agente entorno
		// TODO

		// Solicitar casillas adyacentes
		// TODO

		// Escoger casilla a la que moverse
		// TODO

		// ¿Inundar casilla?
		// TODO

		// Volver a empezar el action
	}

	@Override
	public boolean done() {
		return stopped;
	}

}
