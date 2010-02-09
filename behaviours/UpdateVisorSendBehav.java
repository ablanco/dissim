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

import java.io.IOException;

import util.Scenario;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class UpdateVisorSendBehav extends TickerBehaviour {

	private AID to;

	public UpdateVisorSendBehav(Agent a, long period, AID to) {
		super(a, period);
		this.to = to;
	}

	@Override
	protected void onTick() {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(to);
		msg.setConversationId("update-visor");
		try {
			msg.setContentObject(Scenario.getCurrentScenario().getGrid());
			myAgent.send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
