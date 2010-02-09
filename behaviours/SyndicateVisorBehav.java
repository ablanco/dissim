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
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

@SuppressWarnings("serial")
public class SyndicateVisorBehav extends CyclicBehaviour {

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.and(MessageTemplate
				.MatchConversationId("syndicate-visor"), MessageTemplate
				.MatchPerformative(ACLMessage.REQUEST));
		ACLMessage msg = myAgent.receive(mt);
		if (msg != null) {
			// Mensaje recibido, hay que procesarlo
			try {
				AID visor = (AID) msg.getContentObject();
				// TODO 500L ???
				myAgent.addBehaviour(new UpdateVisorSendBehav(myAgent, 1000L,
						visor));
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
		} else {
			block();
		}
	}

}
