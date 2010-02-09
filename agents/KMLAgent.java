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

import java.io.IOException;

import behaviours.KMLSnapshotReceiveBehav;

import util.Scenario;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import kml.flood.FloodKml;

@SuppressWarnings("serial")
public class KMLAgent extends Agent {

	@Override
	protected void setup() {
		FloodKml kml = new FloodKml(Scenario.getCurrentScenario());

		// Obtener agente entorno
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("syndicate");
		template.addServices(sd);
		AID envAID = null;
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
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(envAID);
		msg.setConversationId("syndicate-kml");
		try {
			msg.setContentObject(getAID());
			send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Añadir comportamiento para la creación del KML
		addBehaviour(new KMLSnapshotReceiveBehav(this, kml));
	}

}
