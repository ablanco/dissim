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
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

import java.lang.reflect.Constructor;

import util.Snapshot;
import util.Updateable;
import behaviours.ReceiveUpdateBehav;

/**
 * {@link Agent} that subcribes to an {@link EnvironmentAgent} and receives a
 * {@link Snapshot} on a regular period, and passes it to a client that must
 * implement the {@link Updateable} interface.
 * 
 * @author Alejandro Blanco, Manuel Gomar
 * 
 */
@SuppressWarnings("serial")
public class UpdateAgent extends Agent {

	private AID[] envAID = null;

	/**
	 * Object that processes the {@link Snapshot}, it must implement the
	 * {@link Updateable} interface
	 */
	private Updateable client = null;

	@Override
	protected void setup() {
		// Obtener argumentos
		Object[] args = getArguments();
		String[] envs = null;
		if (args.length == 2) {
			try {
				// Carga, y crea un objeto de la clase pasada, por reflexión
				Class<?> cls = Class.forName((String) args[0]);
				Constructor<?> ct = cls.getConstructor(new Class[0]);
				client = (Updateable) ct.newInstance(new Object[0]);

				envs = ((String) args[1]).split(",");
			} catch (Throwable e) {
				e.printStackTrace();
				doDelete();
			}
		} else {
			throw new IllegalArgumentException("Wrong number of arguments.");
		}

		if (client == null)
			doDelete();

		client.setAgent(this);

		// Obtener agentes entorno
		DFAgentDescription[] result = AgentUtils.search(this, "syndicate");
		envAID = new AID[envs.length];

		for (int i = 0; i < envs.length; i++) {
			String env = envs[i];
			for (DFAgentDescription df : result) {
				String name = df.getName().getLocalName();
				name = name.substring(name.indexOf("-") + 1, name
						.lastIndexOf("-"));
				if (name.equals(env)) {
					envAID[i] = df.getName();
				}
			}
		}

		// Añadir comportamiento de actualización del objeto cliente
		addBehaviour(new ReceiveUpdateBehav(this, client));

		client.init();

		// Sindicarse en el entorno
		AgentUtils.send(this, envAID, ACLMessage.SUBSCRIBE, "syndicate",
				new Object[] { client.getType(), getAID() });
	}

	@Override
	protected void takeDown() {
		if (envAID != null) {
			// Desregistrarse en el entorno
			AgentUtils.send(this, envAID, ACLMessage.CANCEL, "syndicate",
					new Object[] { client.getType(), getAID() });
		}
		if (client != null) {
			client.finish();
		}
	}

}
