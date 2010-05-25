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

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import util.AgentHelper;
import util.HexagonalGrid;
import util.Point;
import util.Scenario;
import util.jcoord.LatLng;

@SuppressWarnings("serial")
public class AdjacentsGridBehav extends CyclicBehaviour {

	public static final String LAT_LNG = "latlng";
	public static final String POSITION = "pos";
	private static final String OTHER_ENV = "other";

	private Scenario scen;
	private HexagonalGrid grid;
	private MessageTemplate mt = MessageTemplate.and(MessageTemplate
			.MatchConversationId("adjacents-grid"), MessageTemplate
			.MatchPerformative(ACLMessage.REQUEST));

	public AdjacentsGridBehav(Scenario scen, HexagonalGrid grid) {
		this.scen = scen;
		this.grid = grid;
	}

	@Override
	public void action() {
		ACLMessage msg = myAgent.receive(mt);
		if (msg != null) {
			// Mensaje recibido, hay que procesarlo
			String pos = msg.getContent();
			String[] data = pos.split(" ");
			String type = data[0];
			HashSet<Point> adjacents = null;
			int col;
			int row;

			if (data.length <= 1) {
				myAgent.addBehaviour(new SendAdjacentsBehav(myAgent,
						new HashSet<Point>(), msg.createReply()));
				return;
			}

			// Si se trata de coordenadas geográficas las pasamos a coordenadas
			// en la rejilla
			if (type.equals(LAT_LNG)) {
				double lat = Double.parseDouble(data[1]);
				double lng = Double.parseDouble(data[2]);
				Point p = grid.coordToTile(new LatLng(lat, lng));
				col = p.getCol();
				row = p.getRow();
			} else {
				// Si son coordenadas de la rejilla
				col = Integer.parseInt(data[1]);
				row = Integer.parseInt(data[2]);
			}
			int d = 1;
			ArrayList<int[]> otherEnv = new ArrayList<int[]>(4);
			if (data.length > 3) {
				d = Integer.parseInt(data[3]);
				if (!type.equals(OTHER_ENV)) {
					// Averiguamos si se sale del área de este entorno
					if ((col - d) < grid.getOffCol())
						otherEnv.add(new int[] { grid.getOffCol() - 1, row });
					if ((row - d) < grid.getOffRow())
						otherEnv.add(new int[] { col, grid.getOffRow() - 1 });
					if ((grid.getOffCol() + grid.getColumns()) <= col)
						otherEnv.add(new int[] {
								grid.getOffCol() + grid.getColumns(), row });
					if ((grid.getOffRow() + grid.getRows()) <= row)
						otherEnv.add(new int[] { col,
								grid.getOffRow() + grid.getRows() });
				}
			}

			adjacents = grid.getAdjacents(new Point(col, row));
			HashSet<Point> adj1 = adjacents;
			while (d > 1) {
				HashSet<Point> adj2 = new HashSet<Point>(adj1.size() * 6);
				for (Point pt : adj1) {
					adj2.addAll(grid.getAdjacents(pt));
				}
				adjacents.addAll(adj2);
				adj1 = adj2;
				d--;
			}

			ACLMessage reply = msg.createReply();
			if (otherEnv.size() > 0) {
				// Si hay que preguntar a más entornos
				myAgent.addBehaviour(new OtherEnvsAdjacentsBehav(myAgent, col,
						row, Integer.parseInt(data[3]), otherEnv, adjacents,
						reply));
			} else {
				myAgent.addBehaviour(new SendAdjacentsBehav(myAgent, adjacents,
						reply));
			}
		} else {
			block();
		}
	}

	protected class SendAdjacentsBehav extends OneShotBehaviour {

		private HashSet<Point> adjacents;
		private ACLMessage msg;

		public SendAdjacentsBehav(Agent agt, HashSet<Point> adjacents,
				ACLMessage msg) {
			super(agt);
			this.adjacents = adjacents;
			this.msg = msg;
		}

		@Override
		public void action() {
			msg.setPerformative(ACLMessage.INFORM);
			try {
				msg.setContentObject(adjacents);
				myAgent.send(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	protected class OtherEnvsAdjacentsBehav extends CyclicBehaviour {

		private int col;
		private int row;
		private int vision;
		private ArrayList<int[]> outerTiles;
		private HashSet<Point> adjacents;
		private ACLMessage msg;
		private int step;
		private MessageTemplate mtReply;

		public OtherEnvsAdjacentsBehav(Agent agt, int col, int row, int d,
				ArrayList<int[]> outerTiles, HashSet<Point> adjacents,
				ACLMessage msg) {
			super(agt);
			this.col = col;
			this.row = row;
			vision = d;
			this.outerTiles = outerTiles;
			this.adjacents = adjacents;
			this.msg = msg;
			step = 0;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void action() {
			switch (step) {
			case 0:
				if (outerTiles.size() == 0) {
					// No hay más casillas fuera del área, terminamos
					step = 2;
					break;
				}

				int[] outer = outerTiles.remove(0);
				int d = vision
						- HexagonalGrid.distance(col, row, outer[0], outer[1])
						+ 1;
				if (d <= 0) {
					// Si el pedestrian ve 0 adyacentes
					break;
				}

				String env = Integer.toString(scen.getEnviromentByPosition(
						outer[0], outer[1]));
				if (!env.equals("-1")) {
					// Pertenece a otro entorno, le pedimos los adyacentes
					AID envAID = null;
					// Obtener agentes entorno
					DFAgentDescription[] result = AgentHelper.search(myAgent,
							"adjacents-grid");
					for (DFAgentDescription df : result) {
						String name = df.getName().getLocalName();
						name = name.substring(name.indexOf("-") + 1, name
								.lastIndexOf("-"));
						if (name.equals(env)) {
							envAID = df.getName();
							break;
						}
					}

					// Si hemos encontrado el entorno le pedimos los adyacentes
					if (envAID != null) {
						String content = OTHER_ENV + " "
								+ Integer.toString(col) + " "
								+ Integer.toString(row) + " "
								+ Integer.toString(d);

						mtReply = AgentHelper.send(myAgent, envAID,
								ACLMessage.REQUEST, "adjacents-grid", content);
					}

					step = 1;
				}
				break;
			case 1:
				ACLMessage aux = myAgent.receive(mtReply);
				if (aux != null) {
					try {
						Set<Point> newAdjacents = (Set<Point>) aux
								.getContentObject();
						adjacents.addAll(newAdjacents);
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					step = 0;
				} else {
					block();
				}
				break;
			case 2:
				myAgent.addBehaviour(new SendAdjacentsBehav(myAgent, adjacents,
						msg));
				myAgent.removeBehaviour(this);
				break;
			}
		}

	}

}