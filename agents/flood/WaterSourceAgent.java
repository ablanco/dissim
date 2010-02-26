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

package agents.flood;

import behaviours.flood.WaterSourceBehav;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class WaterSourceAgent extends Agent {

	private static final long serialVersionUID = -901992561566307027L;
	private AID envAID;
	
	@Override
	protected void setup() {
		// Obtener argumentos
		Object[] args = getArguments();
		double lat;
		double lng;
		short water;
		long rhythm;
		if (args.length == 4) {
			lat = Double.parseDouble((String) args[0]);
			lng = Double.parseDouble((String) args[1]);
			water = Short.parseShort((String) args[2]);
			rhythm = Long.parseLong((String) args[3]); 
		} else {
			throw new IllegalArgumentException("Wrong arguments.");
		}
		
		// Obtener agentes entorno
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("syndicate");
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, template);
			if (result.length < 1)
				throw new Exception(
						"Error searching for the enviroment agent. Found "
								+ result.length + " agents.");
			envAID = result[0].getName();
			ACLMessage msg;
		} catch (Exception e) {
			e.printStackTrace();
			doDelete();
		}
			
		// TODO Encontrar qué agente entorno gestiona esas coord y preguntarle a qué casilla corresponden 
		
		int x = 0;
		int y = 0;
		
		addBehaviour(new WaterSourceBehav(this, rhythm, envAID, x, y, water));
	}

}
