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
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import util.Scenario;
import util.flood.FloodScenario;

public class FloodTileBehav extends Behaviour {

	private static final long serialVersionUID = -7362590926527253261L;

	protected int x; // Posición de la unidad de agua
	protected int y;
	protected short water; // Cantidad de agua
	protected AID envAID; // Identificador del agente entorno
	protected Random rnd;
	private boolean stopped = false; // Comportamiento terminado?

	protected short value; // Potencial de la casilla actual
	protected short step = 0;
	protected MessageTemplate mt;

	public FloodTileBehav(Agent a, int x, int y) {
		super(a);
		this.x = x; // Posición inicial
		this.y = y;
		FloodScenario scen = (FloodScenario) Scenario.getCurrentScenario();
		this.water = scen.getWater();
		rnd = new Random(System.currentTimeMillis());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void action() {
		@SuppressWarnings("unused")
		String agent = myAgent.getLocalName(); // TODO DEBUG variable
		ACLMessage msg;

		switch (step) {
		case 0:
			// Obtener agente entorno
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("grid-querying");
			template.addServices(sd);
			sd = new ServiceDescription();
			sd.setType("adjacents-grid");
			template.addServices(sd);
			sd = new ServiceDescription();
			sd.setType("flood-registering");
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

			// Solicitar valor de la casilla inicial
			msg = new ACLMessage(ACLMessage.CFP);
			msg.addReceiver(envAID);
			msg.setContent(Integer.toString(x) + " " + Integer.toString(y));
			msg.setConversationId("query-grid");
			msg.setReplyWith("cfp" + System.currentTimeMillis()); // Valor único
			myAgent.send(msg);
			// Prepara la plantilla para recibir la respuesta
			mt = MessageTemplate.and(MessageTemplate
					.MatchConversationId("query-grid"), MessageTemplate
					.MatchInReplyTo(msg.getReplyWith()));
			step = 1;
			break;
		case 1:
			// Recibir la información de la rejilla
			msg = myAgent.receive(mt);
			if (msg != null) {
				if (msg.getPerformative() == ACLMessage.INFORM) {
					// Es la buscada
					value = Short.parseShort(msg.getContent());
					step = 2;
				}
			} else {
				block();
			}
			break;
		case 2:
			// Solicitar casillas adyacentes
			msg = new ACLMessage(ACLMessage.CFP);
			msg.addReceiver(envAID);
			msg.setContent(Integer.toString(x) + " " + Integer.toString(y));
			msg.setConversationId("adjacents-grid");
			msg.setReplyWith("cfp" + System.currentTimeMillis()); // Valor único
			myAgent.send(msg);
			// Prepara la plantilla para recibir la respuesta
			mt = MessageTemplate.and(MessageTemplate
					.MatchConversationId("adjacents-grid"), MessageTemplate
					.MatchInReplyTo(msg.getReplyWith()));
			step = 3;
			break;
		case 3:
			// Recibir la información de la rejilla
			msg = myAgent.receive(mt);
			ArrayList<int[]> adjacents = null;
			if (msg != null) {
				if (msg.getPerformative() == ACLMessage.INFORM) {
					// Es la buscada
					try {
						adjacents = (ArrayList<int[]>) msg.getContentObject();
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				}
				/*
				 * System.out.println(agent + " -> Adjacents tiles: " +
				 * adjacentsToString(adjacents));
				 */
				// Lista con los índices de las casillas adyacentes de menor
				// potencial
				ArrayList<Integer> tilesIdx = new ArrayList<Integer>(adjacents
						.size());
				int i = 0; // Índice en adjacents
				short oldValue = value;
				// Buscamos las casillas adyacentes de menor potencial
				for (int[] tile : adjacents) {
					// Si es del mismo potencial
					if (((short) tile[2]) == value) {
						tilesIdx.add(new Integer(i));
					}// Un nuevo menor potencial
					else if (((short) tile[2]) < value) {
						// Reiniciamos la lista de índices
						tilesIdx = new ArrayList<Integer>(adjacents.size());
						tilesIdx.add(new Integer(i));
						value = (short) tile[2];
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
					// TODO Escoger casilla en vez de al azar según un vector??
					/*
					 * System.out.println(agent + " -> Moving to tile: " +
					 * tile[0] + " " + tile[1] + " " + tile[2]);
					 */
					x = tile[0];
					y = tile[1];
					// value = tile[2];
					step = 2;
				}
				// Sino significa que ya había llegado a su casilla definitiva
				else {
					// Intentar inundar casilla
					msg = new ACLMessage(ACLMessage.CFP);
					msg.addReceiver(envAID);
					String tile = Integer.toString(x) + " "
							+ Integer.toString(y) + " " + Short.toString(water)
							+ " " + Short.toString(value);
					/*
					 * System.out.println(agent + " -> Trying to flood tile: " +
					 * tile);
					 */
					msg.setContent(tile);
					msg.setConversationId("register-flood");
					// Valor único
					msg.setReplyWith("cfp" + System.currentTimeMillis());
					myAgent.send(msg);
					// Prepara la plantilla para recibir la respuesta
					mt = MessageTemplate.and(MessageTemplate
							.MatchConversationId("register-flood"),
							MessageTemplate.MatchInReplyTo(msg.getReplyWith()));
					step = 4;
				}
			} else {
				block();
			}
			break;
		case 4:
			// Recibir la información de la rejilla
			msg = myAgent.receive(mt);
			if (msg != null) {
				if (msg.getPerformative() == ACLMessage.CONFIRM) {
					// Se inunda la casilla
					stopped = true;
					/*
					 * System.out.println(agent + " -> Trying to flood tile: " +
					 * Integer.toString(x) + " " + Integer.toString(y) + " " +
					 * Integer.toString(water) + " " + value);
					 */
				} else {
					// No se inunda
					value = Short.parseShort(msg.getContent());
					step = 2; // Vuelve a buscar una casilla que inundar
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

	// TODO DEBUG function
	@SuppressWarnings("unused")
	private String adjacentsToString(ArrayList<int[]> adjacents) {
		String result = "";
		Iterator<int[]> it = adjacents.iterator();
		while (it.hasNext()) {
			int[] tile = it.next();
			for (int i = 0; i < tile.length; i++) {
				result += tile[i] + " ";
			}
			result += " - ";
		}
		return result;
	}
}
