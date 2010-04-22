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

import util.HexagonalGrid;
import util.Point;
import util.java.NoDuplicatePointsSet;
import util.jcoord.LatLng;

public class AdjacentsGridBehav extends CyclicBehaviour {

	private static final long serialVersionUID = 150073372111848766L;

	public static final String LAT_LNG = "latlng";
	public static final String POSITION = "pos";

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
			// Mensaje recibido, hay que procesarlo
			String pos = msg.getContent();
			String[] data = pos.split(" ");
			String type = data[0];
			NoDuplicatePointsSet adjacents = null;
			int x;
			int y;

			// Si se trata de coordenadas geográficas las pasamos a coordenadas
			// en la rejilla
			if (type.equals(LAT_LNG)) {
				double lat = Double.parseDouble(data[1]);
				double lng = Double.parseDouble(data[2]);
				Point p = grid.coordToTile(new LatLng(lat, lng));
				x = p.getCol();
				y = p.getRow();
			} else {
				// Si son coordenadas de la rejilla
				x = Integer.parseInt(data[1]);
				y = Integer.parseInt(data[2]);
			}
			int d = 1;
			if (data.length > 3)
				d = Integer.parseInt(data[3]);

			adjacents = grid.getAdjacents(new Point(x, y));
			NoDuplicatePointsSet adj1 = adjacents;
			while (d > 1) {
				NoDuplicatePointsSet adj2 = new NoDuplicatePointsSet(adj1
						.size() * 6);
				for (Point pt : adj1) {
					adj2.addAll(grid.getAdjacents(pt));
				}
				adjacents.addAll(adj2);
				adj1 = adj2;
				d--;
			}

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
