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

package util.stats;

import jade.core.AID;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;

import util.Pedestrian;
import util.Snapshot;
import util.Updateable;

public class Statistics implements Updateable {

	private Snapshot snap = null;
	private CsvWriter csv= null;
	/**
	 * Orden de Columnas
	 */
	private final String[] colums = new String[] {"Still Running","Are Dead","Are Saved","Current Time"};

	@Override
	public void finish() {
		csv.close();
	}

	@Override
	public String getConversationId() {
		return "statistics";
	}

	@Override
	public void init() {
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showSaveDialog(null);
		// Pedimos al usuario que nos diga dónde guardar el fichero
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();		
			// Escribimos el kml
			csv = new CsvWriter(file.getPath());
			//Creo las Columnas
			try {
				csv.writeRecord(colums);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// TODO Cancelado el guardar, la estadistica se pierde
		}
		
	}

	@Override
	public void update(Object obj, AID sender) throws IllegalArgumentException {
		if (!(obj instanceof Snapshot))
			throw new IllegalArgumentException(
					"The argument isn't a Snapshot instance");

		// Sólo se considera el estado final de la simulación
		snap = (Snapshot) obj;
		
		if (snap == null)
			return; // Nada que procesar

		List<Pedestrian> people = snap.getPeople();
		
		int alive =0;
		int dead = 0;
		int safe = 0;
		String time = snap.getDateTime().toString();
		for (Pedestrian p : people) {
			//Por cada pedestrian averiguamos su estado
			switch (p.getStatus()) {
			case Pedestrian.DEAD:
				dead++;
				break;
			case Pedestrian.HEALTHY:
				alive++;
				break;
			case Pedestrian.SAFE:
				safe++;
				break;
			default:
				break;
			}			
		}
		try {
			//Escribimos esta fila, respetando el orden
			csv.write(String.valueOf(alive));		
			csv.write(String.valueOf(dead));
			csv.write(String.valueOf(safe));
			csv.write(time);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
