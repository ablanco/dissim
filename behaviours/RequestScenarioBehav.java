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

import agents.AgentUtils;
import agents.CreatorAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import util.Scenario;

/**
 * {@link Behaviour} that asks the {@link CreatorAgent} for the {@link Scenario}
 * and when receives it executes a {@link ReceiveScenarioBehav}.
 * 
 * @author Alejandro Blanco, Manuel Gomar
 * 
 */
@SuppressWarnings("serial")
public class RequestScenarioBehav extends CyclicBehaviour {

	private ReceiveScenarioBehav behav;
	private int step = 0;
	private MessageTemplate mt;

	/**
	 * {@link RequestScenarioBehav} creator
	 * 
	 * @param agt
	 *            {@link Agent}
	 * @param behav
	 *            {@link ReceiveScenarioBehav} that is launched when the
	 *            {@link Scenario} arrives
	 */
	public RequestScenarioBehav(Agent agt, ReceiveScenarioBehav behav) {
		super(agt);
		this.behav = behav;
	}

	@Override
	public void action() {
		ACLMessage msg;
		switch (step) {
		case 0:
			// Obtener agente creador
			DFAgentDescription[] result = AgentUtils
					.search(myAgent, "creator");
			AID creatorAID = result[0].getName();

			// Pedir Scenario
			mt = AgentUtils.send(myAgent, creatorAID, ACLMessage.REQUEST,
					null, null);
			step = 1;
		case 1:
			// Recibir el escenario
			msg = myAgent.receive(mt);
			if (msg != null) {
				if (msg.getPerformative() == ACLMessage.INFORM) {
					try {
						Scenario scen = (Scenario) msg.getContentObject();
						behav.setScenario(scen);
						myAgent.addBehaviour(behav);
						myAgent.removeBehaviour(this);
					} catch (UnreadableException e) {
						e.printStackTrace();
						myAgent.doDelete();
					}
				}
			} else {
				block();
			}
			break;
		}
	}
}
