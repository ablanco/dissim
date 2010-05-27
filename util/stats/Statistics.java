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
import jade.core.Agent;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;

import util.DateAndTime;
import util.Pedestrian;
import util.Snapshot;
import util.Updateable;

/**
 * This is for obtaining and storing statics for the disaster, now only collect
 * info of time and pedestrian status, but could be upgraded to collect much
 * more info.
 * 
 * @author Manuel Gomar, Alejandro Blanco
 * 
 */
public class Statistics implements Updateable {

	private CsvWriter csv = null;
	private int lastAlive = 0;
	private int lastDead = 0;
	private int lastSafe = 0;
	private Agent myAgent = null;

	/**
	 * Orden de Columnas
	 */
	private final String[] colums = new String[] { "Current Time", "Alive",
			"Dead", "Safe" };

	@Override
	/**
	 * Close files
	 */
	public void finish() {
		if (csv != null)
			csv.close();
	}

	@Override
	/**
	 * Agent Type, statistics
	 */
	public String getType() {
		return "statistics";
	}

	@Override
	/**
	 * Create, or replace existing files where to save statistics
	 */
	public void init() {
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showSaveDialog(null);
		// Pedimos al usuario que nos diga dónde guardar el fichero
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String path = file.getAbsolutePath();
			if (!path.endsWith(".csv"))
				path += ".csv";
			// Creamos el escritor de CSV
			csv = new CsvWriter(path);
			// Escribimos el nombre de las columnas
			try {
				csv.writeRecord(colums);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			if (myAgent != null)
				myAgent.doDelete();
		}
	}

	@Override
	/**
	 * Insert a new row for the cvs file containin info statistical info form the simulation step, uses Ooo standart.
	 */
	public void update(Object obj, AID sender) throws IllegalArgumentException {
		if (!(obj instanceof Snapshot))
			throw new IllegalArgumentException(
					"The argument isn't a Snapshot instance");

		if (obj == null || csv == null)
			return; // Nada que procesar

		Snapshot snap = (Snapshot) obj;
		List<Pedestrian> people = snap.getPeople();
		int alive = 0;
		int dead = 0;
		int safe = 0;

		for (Pedestrian p : people) {
			// Por cada pedestrian averiguamos su estado
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
			}
		}

		boolean newData = false;
		if (lastAlive != alive) {
			lastAlive = alive;
			newData = true;
		}
		if (lastDead != dead) {
			lastDead = dead;
			newData = true;
		}
		if (lastSafe != safe) {
			lastSafe = safe;
			newData = true;
		}

		if (newData) {
			try {
				// Escribimos esta fila, respetando el orden
				csv.write(DateAndTime.toOooDate(snap.getDateTime()));
				csv.write(String.valueOf(alive));
				csv.write(String.valueOf(dead));
				csv.write(String.valueOf(safe));
				csv.endRecord();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setAgent(Agent agt) {
		myAgent = agt;
	}

}
