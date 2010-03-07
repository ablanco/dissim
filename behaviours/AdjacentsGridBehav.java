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

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.util.ArrayList;

import util.HexagonalGrid;

public class AdjacentsGridBehav extends CyclicBehaviour {

	private static final long serialVersionUID = 150073372111848766L;

	private HexagonalGrid grid;

	public AdjacentsGridBehav(HexagonalGrid grid) {
		this.grid = grid;
	}

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate
				.MatchConversationId("adjacents-grid");
		ACLMessage msg = myAgent.receive(mt);
		if (msg != null) {
			// Mensaje CFP recibido, hay que procesarlo
			String pos = msg.getContent();
			String[] coord = pos.split(" ");
			ArrayList<int[]> adjacents = grid.getAdjacents(Integer
					.parseInt(coord[0]), Integer.parseInt(coord[1]));

			ACLMessage reply = msg.createReply();
			reply.setPerformative(ACLMessage.INFORM);
			try {
				reply.setContentObject(adjacents);
				myAgent.send(reply);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			block();
		}
	}
}
