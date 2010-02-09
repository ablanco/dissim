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

@SuppressWarnings("serial")
public class CreatorAgent extends Agent {

	@Override
	protected void setup() {
		// TODO DEBUG
		// Obtener argumentos
		Object[] args = getArguments();
		int opt = 1;
		if (args.length == 1) {
			opt = Integer.parseInt((String) args[0]);
		} else if (args.length != 0) {
			throw new IllegalArgumentException("Wrong arguments.");
		}
		Sim1Test.generateScenario(opt);
		// FIN DEBUG

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
				ListIterator<WaterSource> it = fscen.waterSourcesIterator();
				// Si el agua se agentifica
				if (fscen.useWaterAgents()) {
					ArrayList<Behaviour> waterAgents = new ArrayList<Behaviour>(
							fscen.waterSourcesSize());
					while (it.hasNext()) {
						WaterSource ws = it.next();
						int[] tileIdx = scen.coordToTile(ws.getCoord());
						// Calcular cuántos agentes hacen falta para representar
						// esa cantidad de agua
						short water = ws.getWater();
						short scenWater = fscen.getWater();
						int clones = water / scenWater; // Nº de agentes
						// int spare = water - (scenWater * clones); // Resto
						arguments = new Object[] {
								Integer.toString(tileIdx[0]),
								Integer.toString(tileIdx[1]) };
						// Agentes Water
						Behaviour wa = new CreateAgentTickerBehav(this, ws
								.getRhythm(), "Water",
								"agents.flood.WaterAgent", clones, arguments);
						addBehaviour(wa);
						waterAgents.add(wa);
					}
					// TODO cuando parar de crear WaterAgent (usando
					// waterAgents)
				}
				// Si no se agentifica el agua
				else {
					while (it.hasNext()) {
						WaterSource ws = it.next();
						int[] tileIdx = scen.coordToTile(ws.getCoord());
						arguments = new Object[] {
								Integer.toString(tileIdx[0]),
								Integer.toString(tileIdx[1]),
								Short.toString(ws.getWater()),
								Long.toString(ws.getRhythm()) };
						addBehaviour(new CreateAgentBehav(this, "WaterSource",
								"agents.flood.WaterSourceAgent", 1, arguments));
					}
				}
			}

			// TODO DEBUG
			arguments = new Object[0];
			addBehaviour(new CreateAgentBehav(this, "Default Visor",
					"agents.VisorAgent", 1, arguments));
			// FIN DEBUG
		}
	}
}
