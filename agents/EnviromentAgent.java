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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import behaviours.flood.AddWaterGridBehav;
import behaviours.flood.RegisterFloodTileBehav;
import behaviours.flood.UpdateFloodGridBehav;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import util.HexagonalGrid;
import util.Scenario;
import util.flood.FloodHexagonalGrid;
import util.flood.FloodScenario;
import util.flood.WaterSource;

public class EnviromentAgent extends Agent {

	private static final long serialVersionUID = 9023113144679741543L;

	private HexagonalGrid grid;

	@Override
	protected void setup() {
		Scenario scen = Scenario.getCurrentScenario();
		// Obtener argumentos
		Object[] args = getArguments();
		if (args.length == 0) {
			grid = scen.getGrid();
		} else {
			System.err.println(getLocalName() + " wrong arguments.");
			doDelete();
		}

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd;

		// Añadir comportamientos
		addBehaviour(new AdjacentsGridBehav());
		addBehaviour(new QueryGridBehav());

		sd = new ServiceDescription();
		sd.setType("grid-querying");
		sd.setName(getName());
		dfd.addServices(sd);
		sd = new ServiceDescription();
		sd.setType("adjacents-grid");
		sd.setName(getName());
		dfd.addServices(sd);

		// Si es una inundación
		if (scen instanceof FloodScenario) {
			FloodScenario fscen = (FloodScenario) scen;

			// En el caso de que el agua se agentifique
			if (fscen.useWaterAgents()) {
				addBehaviour(new RegisterFloodTileBehav(
						(FloodHexagonalGrid) grid));

				sd = new ServiceDescription();
				sd.setType("flood-registering");
				sd.setName(getName());
				dfd.addServices(sd);
			}
			// Si no se agentifica
			else {
				Iterator<WaterSource> it = fscen.waterSourcesIterator();
				while (it.hasNext()) {
					WaterSource ws = it.next();
					int[] coord = scen.coordToTile(ws.getCoord());
					addBehaviour(new AddWaterGridBehav(this, ws.getRythm(),
							(FloodHexagonalGrid) grid, coord[0], coord[1], ws
									.getWater()));
				}

				addBehaviour(new UpdateFloodGridBehav(this, fscen
						.getFloodUpdateTime(), (FloodHexagonalGrid) grid, fscen
						.getWater()));
			}
		}

		// Registrarse con el agente DF
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			System.err.println(getLocalName()
					+ " registration with DF unsucceeded. Reason: "
					+ e.getMessage());
			e.printStackTrace();
			doDelete();
		}
	}

	@Override
	protected void takeDown() {
		// Desregistrarse de las páginas amarillas
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		System.out.println("Enviroment-agent " + getAID().getName()
				+ " terminating.");
	}

	protected class AdjacentsGridBehav extends CyclicBehaviour {

		private static final long serialVersionUID = 1045845004140195390L;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate
					.MatchConversationId("adjacents-grid"), MessageTemplate
					.MatchPerformative(ACLMessage.CFP));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// Mensaje CFP recibido, hay que procesarlo
				String pos = msg.getContent();
				String[] coord = pos.split(" ");
				ArrayList<int[]> adjacents = grid.getAdjacents(Integer
						.parseInt(coord[0]), Integer.parseInt(coord[1]));

				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.INFORM);
				try {
					reply.setContentObject(adjacents);
					myAgent.send(reply);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				block();
			}
		}
	}

	protected class QueryGridBehav extends CyclicBehaviour {

		private static final long serialVersionUID = 1045845004140195390L;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate
					.MatchConversationId("query-grid"), MessageTemplate
					.MatchPerformative(ACLMessage.CFP));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// Mensaje CFP recibido, hay que procesarlo
				String pos = msg.getContent();
				String[] coord = pos.split(" ");
				short value = grid.getValue(Integer.parseInt(coord[0]), Integer
						.parseInt(coord[1]));

				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.INFORM);
				reply.setContent(Short.toString(value));
				myAgent.send(reply);
			} else {
				block();
			}
		}
	}
}
