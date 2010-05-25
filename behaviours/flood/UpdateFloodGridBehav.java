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
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import util.AgentHelper;
import util.Point;
import util.flood.FloodHexagonalGrid;
import util.flood.FloodScenario;
import behaviours.InterGridBehav;

@SuppressWarnings("serial")
public class UpdateFloodGridBehav extends Behaviour {

	private FloodHexagonalGrid grid;
	private FloodScenario scen;
	private Map<String, Object> envs = new Hashtable<String, Object>();
	private Agent agt;

	public UpdateFloodGridBehav(Object[] args) {
		// Agent a, FloodScenario scen, FloodHexagonalGrid grid
		super((Agent) args[0]);
		agt = (Agent) args[0];
		grid = (FloodHexagonalGrid) args[2];
		scen = (FloodScenario) args[1];
	}

	@Override
	public void action() {
		if (myAgent == null)
			myAgent = agt;
		// TODO el 20 es un mangazo
		int times = (scen.getRealTimeTick() * 20) / grid.getTileSize();
		for (int i = 0; i < times; i++) {
			Set<Point> set = grid.getModCoordAndReset();
			Iterator<Point> it = set.iterator();
			// Por cada casilla modificada
			while (it.hasNext()) {
				Point p = it.next();
				int[] coord = new int[] { p.getCol(), p.getRow() };
				ArrayList<int[]> adjacents = grid.getAdjacents(coord[0],
						coord[1]);
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
						water = decrease(adjCoord[0], adjCoord[1], water);
						increase(coord[0], coord[1], water);
					}
					// ELSE Si no la hay es que no hay que hacer nada pues las
					// alturas son las mismas
				}
				// Hay una adyacente más baja, hay que mover agua desde la
				// modificada a la más baja
				else {
					short water = (short) ((value - adjValue) / 2);
					water = decrease(coord[0], coord[1], water);
					increase(adjCoord[0], adjCoord[1], water);
				}
			}
		}

		myAgent.removeBehaviour(this);
	}

	@Override
	public boolean done() {
		return false;
	}

	/**
	 * Decrease the water level of a tile. If the tile is part of other
	 * enviroment it does nothing.
	 * 
	 * @param col
	 * @param row
	 * @param w
	 * @return
	 */
	private short decrease(int col, int row, short w) {
		Object env = getEnv(col, row);
		if (env != null) {
			// x,y pertene al grid de otro entorno y a la corona de éste
			// La corona es parte del grid de otro entorno, así que se encargue
			// dicho entorno de enviar agua al grid de éste
			return 0;
		} else {
			short result = grid.decreaseValue(col, row, w);
			extBorder(col, row);
			return result;
		}
	}

	/**
	 * Increase the water level of a tile. If the tile is part of other
	 * enviroment, then it tells that enviroment to update the tile.
	 * 
	 * @param col
	 * @param row
	 * @param w
	 */
	private void increase(int col, int row, short w) {
		if (w != 0) {
			Object env = getEnv(col, row);
			if (env != null) {
				if (env instanceof AID) {
					// x,y pertene al grid de otro entorno y a la corona de éste
					String content = InterGridBehav.WATER_INCREASE + " "
							+ Integer.toString(col) + " "
							+ Integer.toString(row) + " " + Short.toString(w);
					AgentHelper.send(myAgent, (AID) env, ACLMessage.INFORM,
							"intergrid", content);
					grid.increaseValue(col, row, w);
				}
				// ELSE no existe dicho entorno por lo tanto el agua se pierde
			} else {
				grid.increaseValue(col, row, w);
				extBorder(col, row);
			}
		}
	}

	/**
	 * Search for the enviroment that owns the given tile and returns his AID.
	 * If that enviroment doesn't exists then it return null. And if the
	 * enviroment is the same one that is searching, then it returns a basic
	 * Object instance.
	 * 
	 * @param col
	 * @param row
	 * @return
	 */
	private Object getEnv(int col, int row) {
		// Comprobar si la casilla es de la corona y por lo tanto pertence a
		// otro entorno
		if (col < grid.getOffCol()
				|| (col - grid.getOffCol()) >= grid.getColumns()
				|| row < grid.getOffRow()
				|| (row - grid.getOffRow()) >= grid.getRows()) {
			String env = Integer.toString(scen
					.getEnviromentByPosition(col, row));

			Object returnObj = envs.get(env);
			if (returnObj == null) {
				// Obtener agentes entorno
				DFAgentDescription[] result = AgentHelper.search(myAgent,
						"intergrid");
				for (DFAgentDescription df : result) {
					String name = df.getName().getLocalName();
					name = name.substring(name.indexOf("-") + 1, name
							.lastIndexOf("-"));
					if (name.equals(env)) {
						envs.put(env, df.getName());
						return df.getName();
					}
				}
				// Si no ha encontrado el entorno
				returnObj = new Object();
				envs.put(env, returnObj);
			}
			return returnObj;
		}

		return null;
	}

	/**
	 * Detects if the position is at the exterior border of the enviroment (his
	 * real area, without considering the extra border). If it's at the border,
	 * then the method tells the enviroment that has this position in his extra
	 * border to update it.
	 * 
	 * @param col
	 * @param row
	 */
	private void extBorder(int col, int row) {
		int relCol = col - grid.getOffCol();
		int relRow = row - grid.getOffRow();
		if (relCol == 0 || relCol == (grid.getColumns() - 1) || relRow == 0
				|| relRow == (grid.getRows() - 1)) {
			int otherEnvCol = col;
			int otherEnvRow = row;
			// Hay que avisar a otro entorno para que actualice su corona
			if (relCol == 0)
				otherEnvCol--;
			if (relRow == 0)
				otherEnvRow--;
			if (relCol == (grid.getColumns() - 1))
				otherEnvCol++;
			if (relRow == (grid.getRows() - 1))
				otherEnvRow++;

			Object env = getEnv(otherEnvCol, otherEnvRow);
			if (env instanceof AID) {
				String content = InterGridBehav.WATER_SET + " "
						+ Integer.toString(col) + " " + Integer.toString(row)
						+ " " + Short.toString(grid.getWaterValue(col, row));
				AgentHelper.send(myAgent, (AID) env, ACLMessage.INFORM,
						"intergrid", content);
			}
		}
	}

}
