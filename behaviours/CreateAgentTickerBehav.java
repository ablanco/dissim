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
import jade.core.behaviours.TickerBehaviour;

public class CreateAgentTickerBehav extends TickerBehaviour {

	private static final long serialVersionUID = 171695826446378597L;

	protected String name;
	protected String agtClass;
	protected int clones;
	protected Object[] arguments;
	protected int count = 0;

	/**
	 * 
	 * @param agt
	 *            Agente que llama a este constructor
	 * @param period
	 *            Tiempo entre agente creado y agente creado
	 * @param name
	 *            Nombre del agente a crear
	 * @param agtClass
	 *            Clase del agente a crear
	 * @param arguments
	 *            Argumentos a pasar al agente a crear
	 */
	public CreateAgentTickerBehav(Agent agt, long period, String name,
			String agtClass, int clones, Object[] arguments) {
		super(agt, period);
		if (clones <= 0)
			throw new IllegalArgumentException(
					"Error: At least there must be 1 clone.");
		this.name = name;
		this.agtClass = agtClass;
		this.clones = clones;
		this.arguments = arguments;
	}

	@Override
	protected void onTick() {
		myAgent.addBehaviour(new CreateAgentBehav(myAgent, name + " " + count,
				agtClass, clones, arguments));
		count++;
	}

	public int getCount() {
		return count;
	}
}
