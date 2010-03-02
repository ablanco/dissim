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

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import behaviours.InterGridBehav;

import util.AgentHelper;
import util.Logger;
import util.Point;
import util.flood.FloodHexagonalGrid;
import util.flood.FloodScenario;
import util.jcoord.LatLng;

public class UpdateFloodGridBehav extends TickerBehaviour {

	private static final long serialVersionUID = 8964259995058162322L;

	private FloodHexagonalGrid grid;
	private Logger logger = new Logger();
	private FloodScenario scen;

	public UpdateFloodGridBehav(Agent a, long period, FloodScenario scen,
			FloodHexagonalGrid grid) {
		super(a, period);
		this.grid = grid;
		this.scen = scen;
		// logger = Scenario.getCurrentScenario().getDefaultLogger();
	}

	@Override
	protected void onTick() {
		long time = System.currentTimeMillis();

		Set<Point> set = grid.getModCoordAndReset();
		logger.debugln("Modified tiles set size: " + set.size());
		Iterator<Point> it = set.iterator();
		// Por cada casilla modificada
		while (it.hasNext()) {
			Point p = it.next();
			int[] coord = new int[] { p.getX(), p.getY() };
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
					short water = (short) ((adjValue - value) / 2);
					water = decrease(adjCoord[0], adjCoord[1], coord[0],
							coord[1], water);
					increase(coord[0], coord[1], water);
				}
				// ELSE Si no la hay es que no hay que hacer nada pues las
				// alturas son las mismas
			}
			// Hay una adyacente más baja, hay que mover agua desde la
			// modificada a la más baja
			else {
				short water = (short) ((value - adjValue) / 2);
				water = decrease(coord[0], coord[1], adjCoord[0], adjCoord[1],
						water);
				increase(adjCoord[0], adjCoord[1], water);
			}
		}

		time = System.currentTimeMillis() - time;
		logger.debugln("UpdateFloodGridBehav took " + time + " ms");
	}

	private short decrease(int x, int y, int ix, int iy, short w) {
		Object env = getEnv(x, y);
		if (env != null) {
			if (env instanceof AID) {
				String content = InterGridBehav.WATER_REQUEST + " "
						+ Integer.toString(x) + " " + Integer.toString(y) + " "
						+ Short.toString(w) + " " + Integer.toString(ix) + " "
						+ Integer.toString(iy);
				AgentHelper.send(myAgent, (AID) env, ACLMessage.REQUEST,
						"intergrid", content);
				grid.decreaseValue(x, y, w);
			}
			return 0;
		} else {
			short result = grid.decreaseValue(x, y, w);
			innerBorder(x, y, grid.getWaterValue(x, y));
			return result;
		}
	}

	private void increase(int x, int y, short w) {
		Object env = getEnv(x, y);
		if (env != null) {
			if (env instanceof AID) {
				String content = InterGridBehav.WATER_INCREASE + " "
						+ Integer.toString(x) + " " + Integer.toString(y) + " "
						+ Short.toString(w);
				AgentHelper.send(myAgent, (AID) env, ACLMessage.INFORM,
						"intergrid", content);
				grid.increaseValue(x, y, w);
			}
		} else {
			grid.increaseValue(x, y, w);
			innerBorder(x, y, grid.getWaterValue(x, y));
		}
	}

	private Object getEnv(int x, int y) {
		// Comprobar si la casilla es de la corona y por lo tanto pertence a
		// otro entorno
		if (x < grid.getOffX() || (x - grid.getOffX()) >= grid.getDimX()
				|| y < grid.getOffY() || (y - grid.getOffY()) >= grid.getDimY()) {
			LatLng coord = grid.tileToCoord(x, y);
			String env = Integer.toString(scen.getEnviromentByCoord(coord));

			// Obtener agentes entorno
			DFAgentDescription[] result = AgentHelper.search(myAgent,
					"intergrid");
			for (DFAgentDescription df : result) {
				String name = df.getName().getLocalName();
				name = name.substring(name.indexOf("-") + 1, name
						.lastIndexOf("-"));
				if (name.equals(env)) {
					return df.getName();
				}
			}

			return new Object();
		}

		return null;
	}

	private void innerBorder(int x, int y, short w) {
		int ix = x - grid.getOffX();
		int iy = y - grid.getOffY();
		if (ix == 0 || ix == (grid.getDimX() - 1) || iy == 0
				|| iy == (grid.getDimY() - 1)) {
			int cx = x;
			int cy = y;
			// Hay que avisar a otro entorno para que actualice su corona
			if (ix == 0)
				cx--;
			if (iy == 0)
				cy--;
			if (ix == (grid.getDimX() - 1))
				cx++;
			if (iy == (grid.getDimY() - 1))
				cy++;

			Object env = getEnv(cx, cy);
			if (env instanceof AID) {
				String content = InterGridBehav.WATER_SET + " "
						+ Integer.toString(x) + " " + Integer.toString(y) + " "
						+ Short.toString(w);
				AgentHelper.send(myAgent, (AID) env, ACLMessage.INFORM,
						"intergrid", content);
			}
		}
	}

}
