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

import agents.UpdateAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import util.Snapshot;
import util.Updatable;

/**
 * {@link Behaviour} that receives a {@link Snapshot} and calls the method
 * update of an {@link Updatable} object. It parallelizes the receiving and the
 * processing.
 * 
 * @author Alejandro Blanco, Manuel Gomar
 * 
 */
@SuppressWarnings("serial")
public class ReceiveUpdateBehav extends ParallelBehaviour {

	/**
	 * Object that processes the {@link Snapshot}
	 */
	private Updatable client;

	/**
	 * {@link ReceiveUpdateBehav} constructor
	 * 
	 * @param a
	 *            Usually an {@link UpdateAgent}
	 * @param client
	 *            {@link Updatable}
	 */
	public ReceiveUpdateBehav(Agent a, Updatable client) {
		super(a, ParallelBehaviour.WHEN_ALL);
		this.client = client;
		addSubBehaviour(new ReceiveBehav(a, this));
	}

	/**
	 * {@link Behaviour} that actually receives the {@link Snapshot}
	 * 
	 * @author Alejandro Blanco, Manuel Gomar
	 * 
	 */
	protected class ReceiveBehav extends CyclicBehaviour {

		private ParallelBehaviour parallel;

		public ReceiveBehav(Agent a, ParallelBehaviour p) {
			super(a);
			parallel = p;
		}

		@Override
		public void action() {
			ACLMessage msg = myAgent.receive(MessageTemplate
					.MatchConversationId("update"));
			if (msg != null) {
				// Mensaje recibido, hay que procesarlo
				try {
					Object content = msg.getContentObject();
					// El procesado pesado se hace un comportamiento paralelo
					// para que no se quede pillado el comportamiento de recibir
					// mensajes
					parallel.addSubBehaviour(new UpdateBehav(this.myAgent,
							content, msg.getSender()));
				} catch (UnreadableException e) {
					System.err.println("Sender: "
							+ msg.getSender().getLocalName() + " - Receiver: "
							+ myAgent.getLocalName() + " - Client: "
							+ client.getType());
					e.printStackTrace();
				}
			} else {
				block();
			}
		}

	}

	/**
	 * {@link Behaviour} that actually calls the update method of the
	 * {@link Updatable} object.
	 * 
	 * @author Alejandro Blanco, Manuel Gomar
	 * 
	 */
	protected class UpdateBehav extends OneShotBehaviour {

		Object content;
		AID sender;

		public UpdateBehav(Agent a, Object content, AID sender) {
			super(a);
			this.content = content;
			this.sender = sender;
		}

		@Override
		public void action() {
			client.update(content, sender);
		}

	}

}