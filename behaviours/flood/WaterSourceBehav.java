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
import jade.lang.acl.ACLMessage;

public class WaterSourceBehav extends TickerBehaviour {

	private static final long serialVersionUID = 8455503506160028404L;

	private int x;
	private int y;
	private short water;
	private AID envAID;

	public WaterSourceBehav(Agent a, long period, AID envAID, int x, int y,
			short water) {
		super(a, period);
		this.envAID = envAID;
		this.x = x;
		this.y = y;
		this.water = water;
	}

	@Override
	protected void onTick() {
		// Inundar casilla
		ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
		msg.addReceiver(envAID);
		msg.setContent(Integer.toString(x) + " " + Integer.toString(y) + " "
				+ Short.toString(water));
		msg.setConversationId("add-water");
		myAgent.send(msg);
	}
}
