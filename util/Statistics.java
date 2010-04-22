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

package util;

import java.util.List;

import jade.core.AID;

public class Statistics implements Updateable {

	private Snapshot snap = null;

	@Override
	public void finish() {
		if (snap == null)
			return; // Nada que procesar

		List<Pedestrian> people = snap.getPeople();

		for (Pedestrian p : people) {
			p.getStatus();
			// TODO Sacar estadísticas
		}

		// TODO Actualizar fichero de stats
	}

	@Override
	public String getConversationId() {
		return "statistics";
	}

	@Override
	public void init() {
		// TODO Escoger fichero de stats, o crear uno nuevo
	}

	@Override
	public void update(Object obj, AID sender) throws IllegalArgumentException {
		if (!(obj instanceof Snapshot))
			throw new IllegalArgumentException(
					"The argument isn't a Snapshot instance");

		// Sólo se considera el estado final de la simulación
		snap = (Snapshot) obj;
	}

}
