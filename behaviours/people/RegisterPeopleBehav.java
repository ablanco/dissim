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
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.util.Map;

import behaviours.InterGridBehav;

import util.AgentHelper;
import util.Point;
import util.Scenario;

@SuppressWarnings("serial")
public class RegisterPeopleBehav extends CyclicBehaviour {

	private Map<String, Point> people;
	private Scenario scen;

	public RegisterPeopleBehav(Agent agt, Scenario scen,
			Map<String, Point> people) {
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
			String[] pos = msg.getContent().split(" ");
			String id = pos[0];
			Point p = new Point(Integer.parseInt(pos[1]), Integer
					.parseInt(pos[2]));

			ACLMessage reply = msg.createReply();

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
						+ Integer.toString(p.getRow());
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
				people.put(id, p);
				reply.setPerformative(ACLMessage.CONFIRM);
			}

			myAgent.send(reply);
		} else {
			block();
		}
	}

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
