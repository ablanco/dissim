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

package agents;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

import java.util.ArrayList;
import java.util.ListIterator;

import util.Scenario;
import util.flood.FloodScenario;
import behaviours.CreateAgentBehav;
import behaviours.CreateAgentTickerBehav;

public class CreatorAgent extends Agent {

	private static final long serialVersionUID = 1808039536880287251L;

	@Override
	protected void setup() {
		Scenario scen = Scenario.getCurrentScenario();
		if (scen != null) {
			Object[] arguments;

			// Enviroment
			int[] grid = scen.getGridSize();
			arguments = new Object[] { new Integer(grid[0]),
					new Integer(grid[1]) };
			addBehaviour(new CreateAgentBehav(this, "Enviroment",
					"agents.EnviromentAgent", arguments));

			// TODO Esperar a que el entorno esté inicializado

			// Si es una inundación
			if (scen instanceof FloodScenario) {
				FloodScenario fscen = (FloodScenario) scen;
				if (fscen.useWaterAgents()) {
					ListIterator<double[]> it = fscen.waterSourcesIterator();
					ArrayList<Behaviour> waterAgents = new ArrayList<Behaviour>(
							fscen.waterSourcesSize());
					while (it.hasNext()) {
						double[] waterSource = it.next();
						// Agentes Water
						grid = scen.coordToTile(waterSource[0], waterSource[1]);
						arguments = new Object[] { Integer.toString(grid[0]),
								Integer.toString(grid[1]),
								Double.toString(waterSource[2]) };
						Behaviour wa = new CreateAgentTickerBehav(this,
								(long) waterSource[3], "Water",
								"agents.flood.WaterAgent", arguments);
						addBehaviour(wa);
						waterAgents.add(wa);
					}
					// TODO parar de crear WaterAgent (usando waterAgents)
				}
			}
		} else { // TODO Borrar este código (DEBUG)
			scen = new FloodScenario();
			scen.complete();
			Object[] arguments;

			// Enviroment
			arguments = new Object[] { "3", "3" };
			addBehaviour(new CreateAgentBehav(this, "Enviroment",
					"agents.EnviromentAgent", arguments));

			// Agentes Water
			arguments = new Object[] { "0", "0", "1" };
			Behaviour waterAgents = new CreateAgentTickerBehav(this, 100L,
					"Water", "agents.flood.WaterAgent", arguments);
			addBehaviour(waterAgents);
		} // FIN DEBUG
	}
}
