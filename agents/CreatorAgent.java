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

import util.Scenario;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import behaviours.CreateAgentBehav;
import behaviours.CreateAgentTickerBehav;

public class CreatorAgent extends Agent {

	private static final long serialVersionUID = 1808039536880287251L;

	@Override
	protected void setup() {
		// TODO Coger los datos que hay que pasarle a cada agente de la GUI
		Scenario scen = Scenario.getCurrentScenario();
		
		Object[] arguments;

		// Enviroment
		arguments = new Object[] { "3", "3" };
		addBehaviour(new CreateAgentBehav(this, "Enviroment",
				"agents.EnviromentAgent", arguments));

		// TODO Esperar a que el entorno esté listo

		// Agentes Water
		arguments = new Object[] { "0", "0", "9", "1" };
		Behaviour waterAgents = new CreateAgentTickerBehav(this, 100L, "Water",
				"agents.WaterAgent", arguments);
		addBehaviour(waterAgents);
		
		// TODO Stop waterAgents
	}
}
