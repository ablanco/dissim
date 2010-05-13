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

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import util.Point;
import util.flood.FloodHexagonalGrid;
import util.jcoord.LatLng;

@SuppressWarnings("serial")
public class AddWaterBehav extends CyclicBehaviour {

	private FloodHexagonalGrid grid;
	private Map<String, int[]> indexes = new Hashtable<String, int[]>();

	public AddWaterBehav(Agent agt, FloodHexagonalGrid grid) {
		super(agt);
		this.grid = grid;
	}

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.and(MessageTemplate
				.MatchConversationId("add-water"), MessageTemplate
				.MatchPerformative(ACLMessage.PROPOSE));
		ACLMessage msg = myAgent.receive(mt);
		if (msg != null) {
			// Mensaje recibido, hay que procesarlo
			String[] data = msg.getContent().split(" ");
			double lat = Double.parseDouble(data[0]);
			double lng = Double.parseDouble(data[1]);
			short water = Short.parseShort(data[2]);

			// Calcular posición
			LatLng coord = new LatLng(lat, lng);
			int[] gridCoord = indexes.get(coord.toString());
			if (gridCoord == null) {
				Point p = grid.coordToTile(coord);
				gridCoord = new int[] { p.getCol(), p.getRow() };
				indexes.put(coord.toString(), gridCoord);
			}
			int x = gridCoord[0];
			int y = gridCoord[1];

			// Máximo nivel que va a alcanzar el agua
			short nivelMax = (short) (grid.getTerrainValue(x, y) + water);
			Iterator<int[]> it = grid.getAdjacents(x, y).iterator();
			short min = Short.MAX_VALUE;
			// Buscamos la casilla adyacente más baja
			while (it.hasNext()) {
				int[] tile = it.next();
				if (tile[2] < min)
					min = (short) tile[2];
			}
			// Si las adyacentes tienen más agua no inundamos
			if (min < nivelMax) {
				grid.increaseValue(x, y, water);
			} else {
				if (water > grid.getWaterValue(x, y))
					grid.setWaterValue(x, y, water);
			}
		} else {
			block();
		}
	}

}
