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
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import util.Scenario;

@SuppressWarnings("serial")
public class SyndicateBehav extends CyclicBehaviour {

	private Scenario scen = Scenario.getCurrentScenario();
	private Map<String, Object[]> subscribers = new Hashtable<String, Object[]>();

	@SuppressWarnings("unchecked")
	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.and(MessageTemplate.or(
				MessageTemplate.MatchConversationId("syndicate-visor"),
				MessageTemplate.MatchConversationId("syndicate-kml")),
				MessageTemplate.or(MessageTemplate
						.MatchPerformative(ACLMessage.SUBSCRIBE),
						MessageTemplate.MatchPerformative(ACLMessage.CANCEL)));
		ACLMessage msg = myAgent.receive(mt);
		if (msg != null) {
			// Mensaje recibido, hay que procesarlo
			AID aid = null;
			try {
				aid = (AID) msg.getContentObject();
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
			// Sindicarse
			if (msg.getPerformative() == ACLMessage.SUBSCRIBE && aid != null) {
				Object[] data = subscribers.get(msg.getConversationId());
				Set<AID> receivers = null;
				if (data != null) {
					myAgent.removeBehaviour((Behaviour) data[0]);
					receivers = (Set<AID>) data[1];
				} else {
					data = new Object[2];
					receivers = new TreeSet<AID>();
				}
				receivers.add(aid);
				Behaviour behav = createBehav(msg.getConversationId(),
						receivers);
				myAgent.addBehaviour(behav);
				data[0] = behav;
				data[1] = receivers;
				subscribers.put(msg.getConversationId(), data);
			}
			// Desregistrarse
			else if (msg.getPerformative() == ACLMessage.CANCEL && aid != null) {
				Object[] data = subscribers.remove(msg.getConversationId());
				if (data != null) {
					myAgent.removeBehaviour((Behaviour) data[0]);
					Set<AID> receivers = (Set<AID>) data[1];
					receivers.remove(aid);
					if (receivers.size() > 0) {
						Behaviour behav = createBehav(msg.getConversationId(),
								receivers);
						myAgent.addBehaviour(behav);
						data[0] = behav;
						data[1] = receivers;
						subscribers.put(msg.getConversationId(), data);
					}
				}
			}
		} else {
			block();
		}
	}

	private Behaviour createBehav(String convId, Set<AID> receivers) {
		Behaviour behav = null;
		if (convId.equals("syndicate-visor")) {
			behav = new UpdateVisorSendBehav(myAgent, scen
					.getUpdateVisorPeriod(), receivers);
		} else if (convId.equals("syndicate-kml")) {
			behav = new UpdateKMLSendBehav(myAgent, scen.getUpdateKMLPeriod(),
					receivers);
		}
		return behav;
	}

}
