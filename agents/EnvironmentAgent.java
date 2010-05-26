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
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import util.DateAndTime;
import util.HexagonalGrid;
import util.Pedestrian;
import util.flood.FloodHexagonalGrid;
import util.flood.FloodScenario;
import util.jcoord.LatLng;
import agents.people.PedestrianAgent;
import behaviours.AdjacentsGridBehav;
import behaviours.InterGridBehav;
import behaviours.QueryGridBehav;
import behaviours.ReceiveClockTickBehav;
import behaviours.ReceiveScenarioBehav;
import behaviours.RequestScenarioBehav;
import behaviours.SyndicateBehav;
import behaviours.flood.AddWaterBehav;
import behaviours.flood.UpdateFloodGridBehav;
import behaviours.people.RegisterPeopleBehav;

/**
 * {@link Agent} that manages the terrain ({@link HexagonalGrid}) of a
 * simulation's area.
 * 
 * If the disaster is a flood, then the agent manages and simulates the movement
 * of the water around the terrain.
 * 
 * @author Alejandro Blanco, Manuel Gomar
 * 
 */
@SuppressWarnings("serial")
public class EnvironmentAgent extends Agent {

	/**
	 * Object that holds all the information about the terrain and water.
	 */
	private HexagonalGrid grid = null;
	/**
	 * Simulation time.
	 */
	private DateAndTime dateTime = null;
	/**
	 * {@link PedestrianAgent} that are moving in the area ofthis
	 * {@link EnvironmentAgent}
	 */
	private Map<String, Pedestrian> people = new Hashtable<String, Pedestrian>();

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
			grid = new FloodHexagonalGrid(NW, SE, offX, offY, tileSize);
		} else {
			System.err.println(getLocalName() + " wrong arguments.");
			doDelete();
		}

		// Obtener Scenario
		addBehaviour(new RequestScenarioBehav(this, new ContinueEnv()));
	}

	@Override
	protected void takeDown() {
		// Desregistrarse de las páginas amarillas
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		System.out.println("Enviroment-agent " + getAID().getName()
				+ " terminating.");
	}

	protected class ContinueEnv extends ReceiveScenarioBehav {

		@Override
		public void action() {
			grid.setPrecision(scen.getPrecision());

			System.out.println(getLocalName()
					+ " - Obtaining terrain elevation data");
			try {
				grid.obtainTerrainElevation(scen.getRandomAltitudes(), scen
						.getDbServer(), scen.getDbPort(), scen.getDbDb(), scen
						.getDbUser(), scen.getDbPass(), scen.getDbDriver());
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}

			System.out.println(getLocalName() + " - Obtaining street data");
			grid.obtainStreetInfo();

			dateTime = scen.getStartTime();

			List<String> services = new ArrayList<String>(10);

			ParallelBehaviour parallel = new ParallelBehaviour(
					ParallelBehaviour.WHEN_ALL);
			// Añadir comportamientos
			parallel
					.addSubBehaviour(new AdjacentsGridBehav(myAgent, scen, grid));
			parallel.addSubBehaviour(new QueryGridBehav(myAgent, grid));
			parallel.addSubBehaviour(new SyndicateBehav(myAgent, grid,
					dateTime, scen, people));
			parallel.addSubBehaviour(new InterGridBehav(myAgent, grid, people));
			parallel.addSubBehaviour(new RegisterPeopleBehav(myAgent, scen,
					people));
			myAgent.addBehaviour(parallel);
			services.add("adjacents-grid");
			services.add("grid-querying");
			services.add("syndicate");
			services.add("intergrid");

			// Si es una inundación
			if (scen instanceof FloodScenario) {
				FloodScenario fscen = (FloodScenario) scen;
				myAgent.addBehaviour(new AddWaterBehav(myAgent,
						(FloodHexagonalGrid) grid));
				services.add("add-water");

				// Mover agua por la rejilla
				myAgent.addBehaviour(new ReceiveClockTickBehav(myAgent,
						UpdateFloodGridBehav.class, new Object[] { myAgent,
								fscen, (FloodHexagonalGrid) grid }, dateTime));
			}

			// Registrarse con el agente DF
			AgentUtils.register(myAgent, services.toArray(new String[services
					.size()]));

			// Obtener agente creador
			DFAgentDescription[] result = AgentUtils
					.search(myAgent, "creator");
			if (result.length != 1) {
				System.err
						.println("Error searching for the creator agent. Found "
								+ result.length + " agents.");
				doDelete();
			}
			AID creatorAID = result[0].getName();
			// Mandar mensaje al agente creador
			AgentUtils.send(myAgent, creatorAID, ACLMessage.CONFIRM, null,
					null);

			done = true;
		}

	}

}
