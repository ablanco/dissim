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

import test.Sim1Test;
import util.Scenario;
import util.flood.FloodScenario;
import util.flood.WaterSource;
import behaviours.CreateAgentBehav;
import behaviours.CreateAgentTickerBehav;

public class CreatorAgent extends Agent {

	private static final long serialVersionUID = 1808039536880287251L;

	@Override
	protected void setup() {
		Sim1Test.generateScenario(0); // TODO Debug
		
		Scenario scen = Scenario.getCurrentScenario();
		if (scen != null) {
			Object[] arguments;

			// Enviroment
			arguments = new Object[0];
			addBehaviour(new CreateAgentBehav(this, "Enviroment",
					"agents.EnviromentAgent", 1, arguments));

			// TODO Esperar a que el entorno esté inicializado

			// Si es una inundación
			if (scen instanceof FloodScenario) {
				FloodScenario fscen = (FloodScenario) scen;
				// Si el agua se agentifica
				if (fscen.useWaterAgents()) {
					ListIterator<WaterSource> it = fscen.waterSourcesIterator();
					ArrayList<Behaviour> waterAgents = new ArrayList<Behaviour>(
							fscen.waterSourcesSize());
					while (it.hasNext()) {
						WaterSource ws = it.next();
						int[] tileIdx = scen.coordToTile(ws.getCoord());
						// Calcular cuántos agentes hacen falta para representar
						// esa cantidad de agua
						double water = ws.getWater();
						double scenWater = fscen.getWater();
						int clones = (int) (water / scenWater); // Nº de agentes
						double spare = water - (scenWater * clones);
						arguments = new Object[] { Integer.toString(tileIdx[0]),
								Integer.toString(tileIdx[1]) };
						// Agentes Water
						Behaviour wa = new CreateAgentTickerBehav(this, ws
								.getRythm(), "Water",
								"agents.flood.WaterAgent", clones, arguments);
						addBehaviour(wa);
						waterAgents.add(wa);
						// TODO spare, agua sobrante...
					}
					// TODO cuando parar de crear WaterAgent (usando
					// waterAgents)
				}
			}
		}
	}
}
