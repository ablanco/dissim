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

package behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;
import java.util.Random;

public class FloodTileBehav extends Behaviour {

	private static final long serialVersionUID = -7362590926527253261L;

	protected int x; // Posición de la unidad de agua
	protected int y;
	protected int value; // Potencial de la casilla actual
	protected int water; // Cantidad de agua
	protected AID envAID; // Identificador del agente entorno
	protected Random rnd;
	private boolean stopped = false; // Comportamiento terminado?
	private int step = 0;
	private MessageTemplate mt;

	public FloodTileBehav(Agent a, int x, int y, int value, int water) {
		super(a);
		this.x = x; // Posición inicial
		this.y = y;
		this.value = value;
		this.water = water;
		rnd = new Random(System.currentTimeMillis());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void action() {
		switch (step) {
		case 0:
			// Obtener agente entorno
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("flood-registering");
			template.addServices(sd);
			sd = new ServiceDescription();
			sd.setType("grid-querying");
			template.addServices(sd);
			try {
				DFAgentDescription[] result = DFService.search(myAgent,
						template);
				if (result.length != 1)
					throw new Exception(
							"Error searching for the enviroment agent. Found "
									+ result.length + " agents.");
				envAID = result[0].getName();
			} catch (FIPAException fe) {
				fe.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			step = 1;
			break;
		case 1:
			// Solicitar casillas adyacentes
			ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
			cfp.addReceiver(envAID);
			cfp.setContent(Integer.toString(x) + " " + Integer.toString(y));
			cfp.setConversationId("adjacents-grid");
			cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Valor único
			myAgent.send(cfp);
			// Prepara la plantilla para recibir la respuesta
			mt = MessageTemplate.and(MessageTemplate
					.MatchConversationId("adjacents-grid"), MessageTemplate
					.MatchInReplyTo(cfp.getReplyWith()));
			step = 2;
			break;
		case 2:
			// Recibir la información de la rejilla
			ACLMessage reply = myAgent.receive(mt);
			ArrayList<int[]> adjacents = null;
			if (reply != null) {
				if (reply.getPerformative() == ACLMessage.INFORM) {
					// Es la buscada
					try {
						adjacents = (ArrayList<int[]>) reply.getContentObject();
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				}
				// Lista con los índices de las casillas adyacentes de menor
				// potencial
				ArrayList<Integer> tilesIdx = new ArrayList<Integer>(adjacents
						.size());
				int i = 0; // Índice en adjacents
				int oldValue = value;
				// Buscamos las casillas adyacentes de menor potencial
				for (int[] tile : adjacents) {
					if (tile[2] == value) { // Si es del mismo potencial
						tilesIdx.add(new Integer(i));
					} else if (tile[2] < value) { // Un nuevo menor potencial
						// Reiniciamos la lista de índices
						tilesIdx = new ArrayList<Integer>(adjacents.size());
						tilesIdx.add(new Integer(i));
						value = tile[2];
					}
					i++;
				}
				// Si se ha encontrado una casilla de menor potencial que la
				// casilla actual significa que se debe mover
				if (value < oldValue) {
					int index = tilesIdx.get(rnd.nextInt(tilesIdx.size()))
							.intValue();
					// Escogemos una casilla al azar entre las de menor
					// potencial
					int[] tile = adjacents.get(index);
					x = tile[0];
					y = tile[1];
					// value = tile[2];
					step = 1;
				}
				// Sino significa que ya había llegado a su casilla definitiva
				else {
					// Inundar casilla
					ACLMessage cfp2 = new ACLMessage(ACLMessage.CFP);
					cfp2.addReceiver(envAID);
					cfp2.setContent(Integer.toString(x) + " "
							+ Integer.toString(y) + " "
							+ Integer.toString(water));
					cfp2.setConversationId("register-flood");
					myAgent.send(cfp2);
					stopped = true;
				}
			} else {
				block();
			}
			break;
		}
	}

	@Override
	public boolean done() {
		return stopped;
	}
}
