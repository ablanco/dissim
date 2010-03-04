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
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

import util.AgentHelper;
import util.DateAndTime;
import util.HexagonalGrid;
import util.Logger;
import util.flood.FloodHexagonalGrid;
import util.flood.FloodScenario;
import util.jcoord.LatLng;
import behaviours.AdjacentsGridBehav;
import behaviours.InterGridBehav;
import behaviours.QueryGridBehav;
import behaviours.ReceiveScenarioBehav;
import behaviours.RequestScenarioBehav;
import behaviours.SyndicateBehav;
import behaviours.flood.AddWaterBehav;
import behaviours.flood.UpdateFloodGridBehav;

@SuppressWarnings("serial")
public class EnviromentAgent extends Agent {

	private HexagonalGrid grid = null;
	private Logger logger = new Logger(); // TODO
	private DateAndTime dateTime;

	// TODO Calcular los valores de tiempo en función del agua que haya entrado

	@Override
	protected void setup() {
		// Obtener argumentos
		Object[] args = getArguments();
		if (args.length == 7) {
			LatLng NW = new LatLng(Double.parseDouble((String) args[0]), Double
					.parseDouble((String) args[1]));
			LatLng SE = new LatLng(Double.parseDouble((String) args[2]), Double
					.parseDouble((String) args[3]));
			int tileSize = Integer.parseInt((String) args[4]);
			int offX = Integer.parseInt((String) args[5]);
			int offY = Integer.parseInt((String) args[6]);
			grid = new FloodHexagonalGrid(NW, SE, offX, offY, tileSize); // TODO
			// TODO grid.obtainTerrainElevation();
			dateTime = new DateAndTime(2010, 2, 26, 20, 32);
		} else {
			logger.errorln(getLocalName() + " wrong arguments.");
			doDelete();
		}

		// Obtener Scenario
		addBehaviour(new RequestScenarioBehav(new ContinueEnv()));
	}

	@Override
	protected void takeDown() {
		// Desregistrarse de las páginas amarillas
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace(logger.getError());
		}
		logger.println("Enviroment-agent " + getAID().getName()
				+ " terminating.");
	}

	protected class ContinueEnv extends ReceiveScenarioBehav {

		@Override
		public void action() {
			List<String> services = new ArrayList<String>(5);

			// Si es una inundación
			if (scen instanceof FloodScenario) {
				FloodScenario fscen = (FloodScenario) scen;
				addBehaviour(new AddWaterBehav(myAgent,
						(FloodHexagonalGrid) grid));
				addBehaviour(new InterGridBehav(myAgent, grid));
				services.add("add-water");
				services.add("intergrid");

				// Mover agua por la rejilla
				myAgent
						.addBehaviour(new UpdateFloodGridBehav(myAgent, fscen
								.getFloodUpdateTime(), fscen,
								(FloodHexagonalGrid) grid));
			}

			// Añadir comportamientos
			myAgent.addBehaviour(new AdjacentsGridBehav(grid));
			myAgent.addBehaviour(new QueryGridBehav(grid));
			myAgent.addBehaviour(new SyndicateBehav(myAgent, grid, dateTime));
			services.add("adjacents-grid");
			services.add("grid-querying");
			services.add("syndicate");
			services.add("people");

			// Registrarse con el agente DF
			AgentHelper.register(myAgent, services.toArray(new String[services
					.size()]));

			// Obtener agente creador
			DFAgentDescription[] result = AgentHelper
					.search(myAgent, "creator");
			if (result.length != 1) {
				logger.errorln("Error searching for the creator agent. Found "
						+ result.length + " agents.");
				doDelete();
			}
			AID creatorAID = result[0].getName();
			// Mandar mensaje al agente creador
			AgentHelper.send(myAgent, creatorAID, ACLMessage.CONFIRM, null,
					null);

			done = true;
		}

	}

}
