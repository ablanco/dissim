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

package agents.people;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import util.AgentHelper;
import behaviours.ReceiveScenarioBehav;
import behaviours.RequestScenarioBehav;
import behaviours.people.PacmanBehav;

@SuppressWarnings("serial")
public class DudeAgent extends Agent {

	private int x;
	private int y;
	private int d;

	@Override
	protected void setup() {
		// Obtener argumentos
		Object[] args = getArguments();
		if (args.length == 3) {
			x = Integer.parseInt((String) args[0]);
			y = Integer.parseInt((String) args[1]);
			d = Integer.parseInt((String) args[2]);
		} else {
			throw new IllegalArgumentException(getLocalName()
					+ " - Wrong number of arguments: " + args.length);
		}

		addBehaviour(new RequestScenarioBehav(new ContinueDA()));
	}

	protected class ContinueDA extends ReceiveScenarioBehav {

		@Override
		public void action() {
			String env = Integer.toString(scen.getEnviromentByPosition(x, y));

			// Obtener agentes entorno
			DFAgentDescription[] result = AgentHelper.search(myAgent,
					"adjacents-grid");
			AID envAID = null;
			for (DFAgentDescription df : result) {
				String name = df.getName().getLocalName();
				name = name.substring(name.indexOf("-") + 1, name
						.lastIndexOf("-"));
				if (name.equals(env)) {
					envAID = df.getName();
					break;
				}
			}

			myAgent.addBehaviour(new PacmanBehav(myAgent, scen
					.getUpdatePeople(), envAID, x, y, d));

			done = true;
		}

	}

}
