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

import java.util.Hashtable;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

@SuppressWarnings("serial")
public class SyndicateBehav extends CyclicBehaviour {

	Hashtable<AID, Behaviour> behaviours = new Hashtable<AID, Behaviour>();

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.and(MessageTemplate.or(
				MessageTemplate.MatchConversationId("syndicate-visor"),
				MessageTemplate.MatchConversationId("syndicate-kml")),
				MessageTemplate.or(MessageTemplate
						.MatchPerformative(ACLMessage.REQUEST), MessageTemplate
						.MatchPerformative(ACLMessage.CANCEL)));
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
			if (msg.getPerformative() == ACLMessage.REQUEST && aid != null) {
				// TODO periodos
				Behaviour behav = null;
				if (msg.getConversationId().equals("syndicate-visor")) {
					behav = new UpdateVisorSendBehav(myAgent, 1000L, aid);
				} else if (msg.getConversationId().equals("syndicate-kml")) {
					behav = new KMLSnapshotSendBehav(myAgent, 10000L, aid);
				}
				Behaviour previous = behaviours.put(aid, behav);
				if (previous != null)
					myAgent.removeBehaviour(previous);
				myAgent.addBehaviour(behav);
			}
			// Desregistrarse
			else if (msg.getPerformative() == ACLMessage.CANCEL && aid != null) {
				Behaviour behav = behaviours.remove(aid);
				if (behav != null)
					myAgent.removeBehaviour(behav);
			}
		} else {
			block();
		}
	}

}
