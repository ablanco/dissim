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
import util.jcoord.LatLng;
import behaviours.ReceiveClockTickBehav;
import behaviours.ReceiveScenarioBehav;
import behaviours.RequestScenarioBehav;

@SuppressWarnings("serial")
public class PedestrianAgent extends Agent {

	private String behaviour = null;
	Object[] chooseArgs = null;
	private double lat;
	private double lng;
	private int d;
	private int s;

	@Override
	protected void setup() {
		// Obtener argumentos
		Object[] args = getArguments();
		if (args.length == 6) {
			behaviour = (String) args[0];
			chooseArgs = (Object[]) args[1];
			lat = Double.parseDouble((String) args[2]);
			lng = Double.parseDouble((String) args[3]);
			d = Integer.parseInt((String) args[4]);
			s = Integer.parseInt((String) args[5]);
		} else {
			throw new IllegalArgumentException(getLocalName()
					+ " - Wrong number of arguments: " + args.length);
		}

		addBehaviour(new RequestScenarioBehav(new ContinuePA()));
	}

	protected class ContinuePA extends ReceiveScenarioBehav {

		@Override
		public void action() {
			String env = Integer.toString(scen.getEnviromentByCoord(new LatLng(
					lat, lng)));

			// Obtener agente entorno
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

			try {
				myAgent.addBehaviour(new ReceiveClockTickBehav(myAgent, Class
						.forName(behaviour), new Object[] { myAgent, envAID,
						scen, lat, lng, d, s }, null));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				doDelete();
			}

			done = true;
		}

	}

}
