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

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import util.HexagonalGrid;
import util.Scenario;
import util.flood.FloodHexagonalGrid;
import util.flood.FloodScenario;
import behaviours.AdjacentsGridBehav;
import behaviours.QueryGridBehav;
import behaviours.SyndicateVisorBehav;
import behaviours.flood.AddWaterBehav;
import behaviours.flood.RegisterFloodTileBehav;
import behaviours.flood.UpdateFloodGridBehav;

@SuppressWarnings("serial")
public class EnviromentAgent extends Agent {

	private HexagonalGrid grid;

	@Override
	protected void setup() {
		Scenario scen = Scenario.getCurrentScenario();
		// Obtener argumentos
		Object[] args = getArguments();
		if (args.length == 0) {
			grid = scen.getGrid();
		} else {
			System.err.println(getLocalName() + " wrong arguments.");
			doDelete();
		}

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd;

		// Añadir comportamientos
		addBehaviour(new AdjacentsGridBehav(grid));
		addBehaviour(new QueryGridBehav(grid));
		addBehaviour(new SyndicateVisorBehav());

		sd = new ServiceDescription();
		sd.setType("grid-querying");
		sd.setName(getName());
		dfd.addServices(sd);
		sd = new ServiceDescription();
		sd.setType("adjacents-grid");
		sd.setName(getName());
		dfd.addServices(sd);
		sd = new ServiceDescription();
		sd.setType("syndicate-visor");
		sd.setName(getName());
		dfd.addServices(sd);

		// Si es una inundación
		if (scen instanceof FloodScenario) {
			FloodScenario fscen = (FloodScenario) scen;

			// En el caso de que el agua se agentifique
			if (fscen.useWaterAgents()) {
				addBehaviour(new RegisterFloodTileBehav(
						(FloodHexagonalGrid) grid));

				sd = new ServiceDescription();
				sd.setType("flood-registering");
				sd.setName(getName());
				dfd.addServices(sd);
			}
			// Si no se agentifica
			else {
				addBehaviour(new AddWaterBehav(grid));

				sd = new ServiceDescription();
				sd.setType("add-water");
				sd.setName(getName());
				dfd.addServices(sd);

				addBehaviour(new UpdateFloodGridBehav(this, fscen
						.getFloodUpdateTime(), (FloodHexagonalGrid) grid));
			}
		}

		// Registrarse con el agente DF
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			System.err.println(getLocalName()
					+ " registration with DF unsucceeded. Reason: "
					+ e.getMessage());
			e.printStackTrace();
			doDelete();
		}
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
}
