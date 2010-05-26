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

package behaviours.people;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.util.Map;

import util.AgentHelper;
import util.Pedestrian;
import util.Point;
import util.Scenario;
import agents.EnvironmentAgent;
import agents.people.PedestrianAgent;
import behaviours.InterGridBehav;

/**
 * {@link Behaviour} to keep tracking the {@link PedestrianAgent}
 * 
 * @author Alejandro Blanco, Manuel Gomar
 * 
 */
@SuppressWarnings("serial")
public class RegisterPeopleBehav extends CyclicBehaviour {

	/**
	 * Pedestrians registered. Key: Identifier. Value: {@link Pedestrian}
	 */
	private Map<String, Pedestrian> people;
	private Scenario scen;

	/**
	 * {@link RegisterPeopleBehav} constructor
	 * 
	 * @param agt
	 *            Usually an {@link EnvironmentAgent}
	 * @param scen
	 *            {@link Scenario}
	 * @param people
	 *            {@link Map}<{@link String},{@link Pedestrian}> Pedestrians of
	 *            the {@link EnvironmentAgent}
	 */
	public RegisterPeopleBehav(Agent agt, Scenario scen,
			Map<String, Pedestrian> people) {
		super(agt);
		this.scen = scen;
		this.people = people;
	}

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate
				.MatchConversationId("register-people");
		ACLMessage msg = myAgent.receive(mt);
		if (msg != null) {
			if (msg.getPerformative() == ACLMessage.CANCEL) {
				String id = msg.getContent();
				Pedestrian p = people.get(id);
				if (p != null)
					p.setStatus(Pedestrian.DEAD);
			} else {
				String[] pos = msg.getContent().split(" ");
				String id = pos[0];
				Point p = new Point(Integer.parseInt(pos[1]), Integer
						.parseInt(pos[2]));
				int state = Pedestrian.HEALTHY;

				ACLMessage reply = msg.createReply();

				// Tiene información sobre estado, algo especial le pasa
				if (pos.length == 4)
					state = Integer.parseInt(pos[3]);

				int env = isOutsideArea(p);
				if (env >= 0) {
					// Ha cambiado de entorno
					people.remove(id);

					// Obtener agentes entorno
					DFAgentDescription[] result = AgentHelper.search(myAgent,
							"intergrid");
					AID envAID = null;
					for (DFAgentDescription df : result) {
						String name = df.getName().getLocalName();
						name = name.substring(name.indexOf("-") + 1, name
								.lastIndexOf("-"));
						if (name.equals(Integer.toString(env))) {
							envAID = df.getName();
							break;
						}
					}

					// Avisar a entorno de que se le envía una persona
					String content = InterGridBehav.PEOPLE_SET + " " + id + " "
							+ Integer.toString(p.getCol()) + " "
							+ Integer.toString(p.getRow()) + " "
							+ Integer.toString(state);
					AgentHelper.send(myAgent, envAID, ACLMessage.INFORM,
							"intergrid", content);

					// Avisar al agente humano de cuál es su nuevo entorno
					try {
						reply.setContentObject(envAID);
					} catch (IOException e) {
						e.printStackTrace();
					}
					reply.setPerformative(ACLMessage.INFORM);
				} else {
					people.put(id, new Pedestrian(p, state, id));
					reply.setPerformative(ACLMessage.CONFIRM);
				}

				myAgent.send(reply);
			}
		} else {
			block();
		}
	}

	/**
	 * Returns the number of the {@link EnvironmentAgent} that owns the
	 * {@link Point}. -1 if it's the same {@link EnvironmentAgent} that owns
	 * this {@link Behaviour}.
	 * 
	 * @param p
	 *            {@link Point}
	 * @return
	 */
	private int isOutsideArea(Point p) {
		int env = scen.getEnviromentByPosition(p.getCol(), p.getRow());
		String name = myAgent.getLocalName();
		int myEnv = Integer.parseInt(name.substring(name.indexOf("-") + 1, name
				.lastIndexOf("-")));
		if (env == myEnv)
			env = -1;
		return env;
	}

}
