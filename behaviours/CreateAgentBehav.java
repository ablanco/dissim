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

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

public class CreateAgentBehav extends OneShotBehaviour {

	private static final long serialVersionUID = -1618019490277716693L;

	private Object[] arguments;
	private String name;
	private String agtClass;
	private int clones;

	/**
	 * 
	 * @param agt
	 *            Agente que llama a este constructor
	 * @param name
	 *            Nombre del agente a crear
	 * @param agtClass
	 *            Clase del agente a crear
	 * @param arguments
	 *            Argumentos a pasar al agente a crear
	 */
	public CreateAgentBehav(Agent agt, String name, String agtClass,
			int clones, Object[] arguments) {
		super(agt);
		if (clones <= 0)
			throw new IllegalArgumentException(
					"Error: At least there must be 1 clone.");
		this.name = name;
		this.agtClass = agtClass;
		this.clones = clones;
		this.arguments = arguments;
	}

	@Override
	public void action() {
		for (int i = 1; i <= clones; i++) {
			AgentContainer container = myAgent.getContainerController();
			AgentController agtctrl;
			try {
				agtctrl = container.createNewAgent(name + "-"
						+ Integer.toString(i), agtClass, arguments);
				agtctrl.start();
			} catch (ControllerException e) {
				System.err.println(myAgent.getLocalName()
						+ " -> Error creating an agent of type " + agtClass
						+ " with name " + name);
				e.printStackTrace();
			}
		}
	}
}
