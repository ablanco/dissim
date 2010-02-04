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

package behaviours.flood;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class WaterSourceBehav extends TickerBehaviour {

	private static final long serialVersionUID = 8455503506160028404L;

	private int x;
	private int y;
	private short water;

	private AID envAID;
	private short step;

	public WaterSourceBehav(Agent a, long period, int x, int y, short water) {
		super(a, period);
		this.x = x;
		this.y = y;
		this.water = water;
		step = 0;
	}

	@Override
	protected void onTick() {
		switch (step) {
		case 0:
			// Obtener agente entorno
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("add-water");
			template.addServices(sd);

			try {
				DFAgentDescription[] result = DFService.search(myAgent,
						template);
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
			step = 1;
		case 1:
			// Inundar casilla
			ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
			msg.addReceiver(envAID);
			msg.setContent(Integer.toString(x) + " " + Integer.toString(y)
					+ " " + Short.toString(water));
			msg.setConversationId("add-water");
			myAgent.send(msg);
			break;
		}
	}
}
