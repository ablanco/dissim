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

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Iterator;

import util.flood.FloodHexagonalGrid;

public class AddWaterBehav extends CyclicBehaviour {

	private static final long serialVersionUID = 6693696497776800016L;

	private FloodHexagonalGrid grid;

	public AddWaterBehav(FloodHexagonalGrid grid) {
		this.grid = grid;
	}

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.and(MessageTemplate
				.MatchConversationId("add-water"), MessageTemplate
				.MatchPerformative(ACLMessage.PROPOSE));
		ACLMessage msg = myAgent.receive(mt);
		if (msg != null) {
			// Mensaje CFP recibido, hay que procesarlo
			String[] data = msg.getContent().split(" ");
			int x = Integer.parseInt(data[0]);
			int y = Integer.parseInt(data[1]);
			short water = Short.parseShort(data[2]);

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
