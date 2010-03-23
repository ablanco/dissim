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
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import util.AgentHelper;
import util.DateAndTime;
import util.Point;
import util.flood.FloodHexagonalGrid;
import util.jcoord.LatLng;

@SuppressWarnings("serial")
public class AddWaterBehav extends CyclicBehaviour {

	private FloodHexagonalGrid grid;
	private Map<String, int[]> indexes = new Hashtable<String, int[]>();
	/**
	 * The water source that is the times reference
	 */
	private String timesReference = null;
	/**
	 * True if the enviroment is the clock reference
	 */
	private boolean iAmClock = false;
	/**
	 * Other enviroments AIDs
	 */
	private AID[] otherEnvs = null;
	/**
	 * Simulation date and time
	 */
	private DateAndTime dateTime;
	private int minutes;
	/**
	 * Behaviour that receives the messages from the clock enviroment
	 */
	private WaitForTimeUpdate timeUpdates = new WaitForTimeUpdate();
	/**
	 * Parallel behaviour of the enviroment that processes almost all of the
	 * messages sended to the enviroment
	 */
	private ParallelBehaviour parallel;

	public AddWaterBehav(Agent agt, FloodHexagonalGrid grid,
			DateAndTime dateTime, int minutes, ParallelBehaviour parallel) {
		super(agt);
		this.grid = grid;
		this.dateTime = dateTime;
		this.minutes = minutes;
		this.parallel = parallel;
		parallel.addSubBehaviour(timeUpdates);
	}

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.and(MessageTemplate
				.MatchConversationId("add-water"), MessageTemplate
				.MatchPerformative(ACLMessage.PROPOSE));
		ACLMessage msg = myAgent.receive(mt);
		if (msg != null) {
			// Mensaje recibido, hay que procesarlo
			String[] data = msg.getContent().split(" ");
			double lat = Double.parseDouble(data[0]);
			double lng = Double.parseDouble(data[1]);
			short water = Short.parseShort(data[2]);

			// Calcular posición
			LatLng coord = new LatLng(lat, lng);
			int[] gridCoord = indexes.get(coord.toString());
			if (gridCoord == null) {
				Point p = grid.coordToTile(coord);
				gridCoord = new int[] { p.getCol(), p.getRow() };
				indexes.put(coord.toString(), gridCoord);
			}
			int x = gridCoord[0];
			int y = gridCoord[1];

			// Actualizar tiempo pasado en la simulación
			// La primera entrada de agua en mandar un mensaje será la que
			// marque el tiempo de la simulación
			if (timesReference == null) {
				timesReference = coord.toString();
				// Ofrecernos como entorno reloj
				DFAgentDescription[] result = AgentHelper.search(myAgent,
						"creator");
				AID creator = result[0].getName();
				MessageTemplate mt2 = AgentHelper.send(myAgent, creator,
						ACLMessage.PROPOSE, "clock-env", null);
				myAgent.addBehaviour(new WaitForClockConfirm(myAgent, mt2));
			}

			// Si se trata de agua enviada por la entrada de agua que es la
			// referencia, y si somos el entorno reloj
			if (timesReference.equals(coord.toString()) && iAmClock) {
				if (otherEnvs == null) {
					DFAgentDescription[] result = AgentHelper.search(myAgent,
							"add-water");
					otherEnvs = new AID[result.length - 1];
					int j = 0;
					for (int i = 0; i < result.length; i++) {
						DFAgentDescription df = result[i];
						if (!df.getName().getLocalName().equals(
								myAgent.getLocalName())) {
							otherEnvs[j] = df.getName();
							j++;
						}
					}
				}

				AgentHelper.send(myAgent, otherEnvs, ACLMessage.INFORM,
						"update-time", null);
				dateTime.updateTime(minutes);
			}

			// Máximo nivel que va a alcanzar el agua
			short nivelMax = (short) (grid.getTerrainValue(x, y) + water);
			Iterator<int[]> it = grid.getAdjacents(x, y).iterator();
			short min = Short.MAX_VALUE;
			// Buscamos la casilla adyacente más baja
			while (it.hasNext()) {
				int[] tile = it.next();
				if (tile[2] < min)
					min = (short) tile[2];
			}
			// Si las adyacentes tienen más agua no inundamos
			if (min < nivelMax) {
				grid.increaseValue(x, y, water);
			} else {
				if (water > grid.getWaterValue(x, y))
					grid.setWaterValue(x, y, water);
			}
		} else {
			block();
		}
	}

	protected class WaitForClockConfirm extends CyclicBehaviour {

		private MessageTemplate mt;

		public WaitForClockConfirm(Agent agt, MessageTemplate mt) {
			super(agt);
			this.mt = mt;
		}

		@Override
		public void action() {
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
					iAmClock = true;
					parallel.removeSubBehaviour(timeUpdates);
				}

				myAgent.removeBehaviour(this);
			} else {
				block();
			}
		}

	}

	protected class WaitForTimeUpdate extends CyclicBehaviour {

		private MessageTemplate mt = MessageTemplate
				.MatchConversationId("update-time");

		@Override
		public void action() {
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				dateTime.updateTime(minutes);
			} else {
				block();
			}
		}

	}
}
