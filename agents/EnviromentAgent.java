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

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import util.HexagonalGrid;

public class EnviromentAgent extends Agent {

	private static final long serialVersionUID = 9023113144679741543L;

	private HexagonalGrid grid;

	@Override
	protected void setup() {
		// Obtener argumentos
		Object[] args = getArguments();
		if (args != null && args.length >= 2) {
			int x = Integer.parseInt((String) args[0]);
			int y = Integer.parseInt((String) args[1]);
			grid = new HexagonalGrid(x, y);
			// TODO introducir las alturas en grid
		} else {
			System.err.println(getLocalName() + " wrong arguments.");
			doDelete();
		}

		// Añadir comportamientos
		addBehaviour(new RegisterFloodTileBehav());
		addBehaviour(new QueryGridBehav());

		// Registrarse con el agente DF
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("flood-registering");
		sd.setName(getName());
		dfd.addServices(sd);
		sd = new ServiceDescription();
		sd.setType("grid-querying");
		sd.setName(getName());
		dfd.addServices(sd);
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

	protected class RegisterFloodTileBehav extends CyclicBehaviour {

		private static final long serialVersionUID = -3677388777435949196L;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate
					.MatchConversationId("register-flood"), MessageTemplate
					.MatchPerformative(ACLMessage.CFP));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// CFP Message received. Process it
				String pos = msg.getContent();
				String[] coord = pos.split(" ");
				grid.increaseValue(Integer.parseInt(coord[0]), Integer
						.parseInt(coord[1]), 1); // TODO 1?
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
				// CFP Message received. Process it
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
}
