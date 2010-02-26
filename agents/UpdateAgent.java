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
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.lang.reflect.Constructor;

import util.Updateable;
import behaviours.ReceiveUpdateBehav;

@SuppressWarnings("serial")
public class UpdateAgent extends Agent {

	private AID[] envAID = null;
	private Updateable client = null;

	@SuppressWarnings("unchecked")
	@Override
	protected void setup() {
		// Obtener argumentos
		Object[] args = getArguments();
		String[] envs = null;
		if (args.length == 2) {
			try {
				// Carga, y crea un objeto de la clase pasada, por reflexión
				Class cls = Class.forName((String) args[0]);
				Constructor ct = cls.getConstructor(new Class[0]);
				client = (Updateable) ct.newInstance(new Object[0]);

				envs = ((String) args[1]).split(",");
			} catch (Throwable e) {
				e.printStackTrace();
				doDelete();
			}
		} else {
			throw new IllegalArgumentException("Wrong number of arguments.");
		}

		// Obtener agentes entorno
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("syndicate");
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, template);
			if (result.length < 1)
				throw new Exception(
						"Error searching for the enviroment agent. Found "
								+ result.length + " agents.");
			envAID = new AID[envs.length];
			int idx = 0;
			for (DFAgentDescription df : result) {
				String name = df.getName().getLocalName();
				name = name.substring(name.indexOf("-") + 1, name
						.lastIndexOf("-"));
				for (String e : envs) {
					if (e.equals(name)) {
						envAID[idx] = df.getName();
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			doDelete();
		}

		// Sindicarse en el entorno
		ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
		for (int i = 0; i < envAID.length; i++)
			msg.addReceiver(envAID[i]);
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
			for (int i = 0; i < envAID.length; i++)
				msg.addReceiver(envAID[i]);
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
