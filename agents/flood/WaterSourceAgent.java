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

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import util.AgentHelper;
import util.jcoord.LatLng;
import behaviours.ReceiveClockTickBehav;
import behaviours.ReceiveScenarioBehav;
import behaviours.RequestScenarioBehav;

@SuppressWarnings("serial")
public class WaterSourceAgent extends Agent {

	private AID envAID;
	private LatLng coord;
	private short water;

	@Override
	protected void setup() {
		// Obtener argumentos
		Object[] args = getArguments();
		if (args.length == 3) {
			double lat = Double.parseDouble((String) args[0]);
			double lng = Double.parseDouble((String) args[1]);
			coord = new LatLng(lat, lng);
			water = Short.parseShort((String) args[2]);
		} else {
			throw new IllegalArgumentException("Wrong arguments.");
		}

		addBehaviour(new RequestScenarioBehav(new ContinueWS()));
	}

	protected class ContinueWS extends ReceiveScenarioBehav {

		@Override
		public void action() {
			String env = Integer.toString(scen.getEnviromentByCoord(coord));

			// Obtener agentes entorno
			DFAgentDescription[] result = AgentHelper.search(myAgent,
					"add-water");
			for (DFAgentDescription df : result) {
				String name = df.getName().getLocalName();
				name = name.substring(name.indexOf("-") + 1, name
						.lastIndexOf("-"));
				if (name.equals(env)) {
					envAID = df.getName();
					break;
				}
			}

			myAgent.addBehaviour(new ReceiveClockTickBehav(myAgent,
					WaterSourceBehav.class, new Object[] { myAgent, envAID,
							coord, water }, null));
			done = true;
		}

	}

	protected class WaterSourceBehav extends Behaviour {

		private LatLng coord;
		private short water;
		private AID envAID;

		public WaterSourceBehav(Object[] args) {
			super((Agent) args[0]);
			// Agent a, AID envAID, LatLng coord, short water
			envAID = (AID) args[1];
			coord = (LatLng) args[2];
			water = (Short) args[3];
		}

		@Override
		public void action() {
			// Inundar casilla
			String content = Double.toString(coord.getLat()) + " "
					+ Double.toString(coord.getLng()) + " "
					+ Short.toString(water);
			AgentHelper.send(myAgent, envAID, ACLMessage.PROPOSE, "add-water",
					content);

			// TODO - Remove Behav
			myAgent.removeBehaviour(this);
		}

		@Override
		public boolean done() {
			return false;
		}
	}

}
