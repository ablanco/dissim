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

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import util.Updateable;

@SuppressWarnings("serial")
public class UpdateReceiveBehav extends CyclicBehaviour {

	private Updateable obj;

	public UpdateReceiveBehav(Agent a, Updateable obj) {
		super(a);
		this.obj = obj;
	}

	@Override
	public void action() {
		ACLMessage msg = myAgent.receive();
		if (msg != null) {
			// Mensaje recibido, hay que procesarlo
			try {
				Object content = msg.getContentObject();
				// El procesado pesado se hace un comportamiento paralelo para
				// que no se quede pillado el comportamiento de recibir mensajes
				ParallelBehaviour parBehav = new ParallelBehaviour(
						ParallelBehaviour.WHEN_ALL);
				parBehav.addSubBehaviour(new UpdateParallelBehav(this.myAgent,
						content));
				myAgent.addBehaviour(parBehav);
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
		} else {
			block();
		}
	}

	protected class UpdateParallelBehav extends OneShotBehaviour {

		Object content;

		public UpdateParallelBehav(Agent a, Object content) {
			super(a);
			this.content = content;
		}

		@Override
		public void action() {
			obj.update(content);
		}

	}

}