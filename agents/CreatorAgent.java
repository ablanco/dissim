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
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.util.Iterator;
import java.util.ListIterator;

import util.AgentHelper;
import util.Pedestrian;
import util.Scenario;
import util.flood.FloodScenario;
import util.flood.WaterSource;
import util.java.Logger;
import util.jcoord.LatLng;
import behaviours.CreateAgentBehav;

@SuppressWarnings("serial")
public class CreatorAgent extends Agent {

	private Scenario scen = null;
	private Logger logger = new Logger();

	@Override
	protected void setup() {
		// Obtener argumentos
		Object[] args = getArguments();
		if (args.length != 1)
			throw new IllegalArgumentException("Wrong number of arguments");

		// Cargar el escenario
		try {
			scen = Scenario.loadScenario((String) args[0]);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		if (scen != null) {
			if (!scen.isComplete())
				throw new IllegalArgumentException(
						"There is mandatory information missing in the scenario.");

			// Lo primero es ofrecer el Scenario
			addBehaviour(new SendScenarioBehav());
			AgentHelper.register(this, "creator");

			// Enviroments
			for (int i = 0; i < scen.getNumEnv(); i++) {
				LatLng[] area = scen.getEnvArea(i);
				int[] size = scen.getEnvSize(i);
				Object[] arguments = new Object[] {
						Double.toString(area[0].getLat()),
						Double.toString(area[0].getLng()),
						Double.toString(area[1].getLat()),
						Double.toString(area[1].getLng()),
						Integer.toString(scen.getTileSize()),
						Integer.toString(size[2]), Integer.toString(size[3]) };
				addBehaviour(new CreateAgentBehav(this, "Enviroment-" + i,
						"agents.EnviromentAgent", 1, arguments));
			}

			addBehaviour(new ChooseClockEnviroment());

			// Esperar a que los entornos estén inicializados
			addBehaviour(new WaitForReadyBehav());
		} else {
			System.err.println("Couldn't load scenario " + args[0]
					+ ". Going dead.");
			doDelete();
		}
	}

	protected class WaitForReadyBehav extends CyclicBehaviour {

		/**
		 * Number of enviroments that are ready to go
		 */
		private int count = 0;

		@Override
		public void action() {
			ACLMessage msg = myAgent.receive(MessageTemplate
					.MatchPerformative(ACLMessage.CONFIRM));
			if (msg != null) {
				// Mensaje recibido, hay que procesarlo
				count++;
				// Esperar a que todos los entornos estén listos
				if (count < scen.getNumEnv())
					return;

				Object[] arguments;
				// Si es una inundación
				if (scen instanceof FloodScenario) {
					FloodScenario fscen = (FloodScenario) scen;
					ListIterator<WaterSource> it = fscen.waterSourcesIterator();
					int cont = 0;
					while (it.hasNext()) {
						WaterSource ws = it.next();
						LatLng coord = ws.getCoord();
						arguments = new Object[] {
								Double.toString(coord.getLat()),
								Double.toString(coord.getLng()),
								Short.toString(ws.getWater()),
								Long.toString(fscen.getWaterSourceUpdateTime()) };
						myAgent.addBehaviour(new CreateAgentBehav(myAgent,
								"WaterSource" + cont,
								"agents.flood.WaterSourceAgent", 1, arguments));
						cont++;
					}
				}

				// Agentes humanos
				Iterator<Pedestrian> people = scen.getPeopleIterator();
				int i = 0;
				while (people.hasNext()) {
					Pedestrian p = people.next();
					LatLng pos = p.getPos();
					arguments = new Object[] { p.getBehaviourClass(),
							p.getChooseArgs(), Double.toString(pos.getLat()),
							Double.toString(pos.getLng()),
							Integer.toString(p.getVision()),
							Integer.toString(p.getSpeed()) };
					myAgent.addBehaviour(new CreateAgentBehav(myAgent, "Dude"
							+ i, "agents.people.PedestrianAgent",
							p.getClones(), arguments));
					i++;
				}

				myAgent.removeBehaviour(this);
			} else {
				block();
			}
		}

	}

	protected class SendScenarioBehav extends CyclicBehaviour {

		@Override
		public void action() {
			ACLMessage msg = myAgent.receive(MessageTemplate
					.MatchPerformative(ACLMessage.REQUEST));
			if (msg != null) {
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.INFORM);
				try {
					reply.setContentObject(scen);
					myAgent.send(reply);
				} catch (IOException e) {
					e.printStackTrace(logger.getError());
				}
			} else {
				block();
			}
		}

	}

	protected class ChooseClockEnviroment extends CyclicBehaviour {

		private boolean clockEnviroment = false;
		private int numEnvs = 0;

		@Override
		public void action() {
			ACLMessage msg = myAgent.receive(MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
					MessageTemplate.MatchConversationId("clock-env")));
			if (msg != null) {
				int perf = ACLMessage.REJECT_PROPOSAL;
				// Sólo se le responde que sí al primero
				if (!clockEnviroment) {
					perf = ACLMessage.ACCEPT_PROPOSAL;
					clockEnviroment = true;
				}

				ACLMessage reply = msg.createReply();
				reply.setPerformative(perf);
				myAgent.send(reply);

				// Si todos los enviroments ya se han propuesto como reloj no
				// es necesario este comportamiento
				numEnvs++;
				if (numEnvs == scen.getNumEnv())
					myAgent.removeBehaviour(this);
			} else {
				block();
			}
		}

	}

}
