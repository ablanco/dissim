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
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import util.Scenario;

@SuppressWarnings("serial")
public class RequestScenarioBehav extends CyclicBehaviour {

	private ReceiveScenarioBehav behav;
	private int step = 0;
	private MessageTemplate mt;

	public RequestScenarioBehav(ReceiveScenarioBehav behav) {
		this.behav = behav;
	}

	@Override
	public void action() {
		ACLMessage msg;
		switch (step) {
		case 0:
			// Obtener agente creador
			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("creator");
			dfd.addServices(sd);
			DFAgentDescription[] result;
			AID creatorAID = null;
			try {
				result = DFService.search(myAgent, dfd);
				if (result.length != 1) {
					System.err
							.println("Error searching for the creator agent. Found "
									+ result.length + " agents.");
					myAgent.doDelete();
				}
				creatorAID = result[0].getName();
			} catch (FIPAException e) {
				e.printStackTrace();
				myAgent.doDelete();
			}
			// Pedir Scenario
			msg = new ACLMessage(ACLMessage.REQUEST);
			msg.addReceiver(creatorAID);
			msg.setReplyWith("scen" + System.currentTimeMillis());
			myAgent.send(msg);
			mt = MessageTemplate.MatchInReplyTo(msg.getReplyWith());
			step = 1;
		case 1:
			// Recibir la información de la rejilla
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
