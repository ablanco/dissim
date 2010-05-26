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

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import util.Pedestrian;
import util.Scenario;
import util.flood.FloodScenario;
import util.flood.WaterSource;
import util.jcoord.LatLng;
import behaviours.CreateAgentBehav;

/**
 * {@link Agent} that loads and processes a {@link Scenario} file, and launch
 * the simulation that the file describes creating the agents needed.
 * 
 * @author Alejandro Blanco, Manuel Gomar
 * 
 */
@SuppressWarnings("serial")
public class CreatorAgent extends Agent {

	private Scenario scen = null;
	/**
	 * Agents that will receive the {@link ClockAgent} ticks.
	 */
	private ArrayList<AID> clockReceivers = new ArrayList<AID>();

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
			AgentUtils.register(this, "creator");

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
						"agents.EnvironmentAgent", 1, arguments, clockReceivers));
			}

			// Esperar a que los entornos estén inicializados
			addBehaviour(new WaitForReadyBehav());
		} else {
			System.err.println("Couldn't load scenario " + args[0]
					+ ". Going dead.");
			doDelete();
		}
	}

	/**
	 * {@link Behaviour} that continues creating {@link Agent}, it waits until
	 * all the {@link EnvironmentAgent} are ready and then create the others
	 * agents.
	 * 
	 * @author Alejandro Blanco, Manuel Gomar
	 * 
	 */
	protected class WaitForReadyBehav extends CyclicBehaviour {

		/**
		 * Number of {@link EnvironmentAgent} that are ready to go
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
								Short.toString(ws.getWater()) };
						myAgent.addBehaviour(new CreateAgentBehav(myAgent,
								"WaterSource-" + cont,
								"agents.flood.WaterSourceAgent", 1, arguments,
								clockReceivers));
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
					myAgent.addBehaviour(new CreateAgentBehav(myAgent,
							"Pedestrian-" + i, "agents.people.PedestrianAgent",
							p.getClones(), arguments, clockReceivers));
					i++;
				}

				// Lanzar reloj, y por tanto lanzar la simulación
				arguments = new Object[] { scen.getStartTime().toString(),
						scen.getSimulationTick(), scen.getRealTimeTick(),
						clockReceivers };
				myAgent.addBehaviour(new CreateAgentBehav(myAgent, "Clock",
						"agents.ClockAgent", 1, arguments));

				myAgent.removeBehaviour(this);
			} else {
				block();
			}
		}

	}

	/**
	 * {@link Behaviour} that sends the {@link Scenario} instance to others
	 * agents. It receives a request and sends the instance to the sender.
	 * 
	 * @author Alejandro Blanco, Manuel Gomar
	 * 
	 */
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
					e.printStackTrace();
				}
			} else {
				block();
			}
		}

	}

}
