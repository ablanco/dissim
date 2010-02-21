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
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

import java.io.IOException;

import util.Updateable;
import behaviours.ReceiveUpdateBehav;

@SuppressWarnings("serial")
public class UpdateAgent extends Agent {

	private AID envAID = null;
	private Updateable client = null;

	@Override
	protected void setup() {
		// Obtener argumentos
		Object[] args = getArguments();
		if (args.length == 1) {
			if (args[0] instanceof Updateable)
				client = (Updateable) args[0];
			else
				doDelete();
		} else {
			throw new IllegalArgumentException("Wrong number of arguments.");
		}

		// Obtener agente entorno
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("syndicate");
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, template);
			if (result.length != 1)
				throw new Exception(
						"Error searching for the enviroment agent. Found "
								+ result.length + " agents.");
			envAID = result[0].getName();
		} catch (FIPAException fe) {
			fe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Sindicarse en el entorno
		ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
		msg.addReceiver(envAID);
		msg.setConversationId("syndicate-" + client.getConversationId());
		try {
			msg.setContentObject(getAID());
			send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Añadir comportamiento de actualización del objeto cliente
		addBehaviour(new ReceiveUpdateBehav(this, client));

		client.init();
	}

	@Override
	protected void takeDown() {
		if (envAID != null) {
			// Desregistrarse en el entorno
			ACLMessage msg = new ACLMessage(ACLMessage.CANCEL);
			msg.addReceiver(envAID);
			msg.setConversationId("syndicate-" + client.getConversationId());
			try {
				msg.setContentObject(getAID());
				send(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (client != null) {
			client.finish();
		}
	}

}
