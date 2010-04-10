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

package agents.people;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;

import java.lang.reflect.Constructor;

import util.AgentHelper;
import util.jcoord.LatLng;
import behaviours.ReceiveScenarioBehav;
import behaviours.RequestScenarioBehav;
import behaviours.people.RunawayBehav;
import behaviours.people.ranking.Ranking;

@SuppressWarnings("serial")
public class PedestrianAgent extends Agent {

	private Ranking rank = null;
	private double lat;
	private double lng;
	private int d;
	private int s;

	@SuppressWarnings("unchecked")
	@Override
	protected void setup() {
		// Obtener argumentos
		Object[] args = getArguments();
		if (args.length == 5) {
			try {
				// Carga, y crea un objeto de la clase pasada, por reflexión
				Class cls = Class.forName((String) args[0]);
				Constructor ct = cls.getConstructor(new Class[0]);
				rank = (Ranking) ct.newInstance(new Object[0]);
			} catch (Throwable e) {
				e.printStackTrace();
				doDelete();
			}

			lat = Double.parseDouble((String) args[1]);
			lng = Double.parseDouble((String) args[2]);
			d = Integer.parseInt((String) args[3]);
			s = Integer.parseInt((String) args[4]);
		} else {
			throw new IllegalArgumentException(getLocalName()
					+ " - Wrong number of arguments: " + args.length);
		}

		addBehaviour(new RequestScenarioBehav(new ContinuePA()));
	}

	protected class ContinuePA extends ReceiveScenarioBehav {

		@Override
		public void action() {
			String env = Integer.toString(scen.getEnviromentByCoord(new LatLng(
					lat, lng)));

			// Obtener agentes entorno
			DFAgentDescription[] result = AgentHelper.search(myAgent,
					"adjacents-grid");
			AID envAID = null;
			for (DFAgentDescription df : result) {
				String name = df.getName().getLocalName();
				name = name.substring(name.indexOf("-") + 1, name
						.lastIndexOf("-"));
				if (name.equals(env)) {
					envAID = df.getName();
					break;
				}
			}

			myAgent.addBehaviour(new RunawayBehav(myAgent, scen
					.getPeopleUpdateTime(), envAID, lat, lng, d, s, rank));

			done = true;
		}

	}

}
