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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Snapshot implements Serializable {

	private static final long serialVersionUID = 1L;

	private HexagonalGrid grid;
	private DateAndTime dateTime;
	private List<Pedestrian> people;
	private String name;
	private String description;

	public Snapshot(String name, String description, HexagonalGrid grid,
			DateAndTime dateTime, Map<String, Pedestrian> people) {
		this.grid = grid;
		this.dateTime = dateTime;
		this.name = name;
		this.description = description;
		this.people = new ArrayList<Pedestrian>(people.size());
		Iterator<String> it = people.keySet().iterator();
		while (it.hasNext()) {
			String id = it.next();
			Pedestrian p = people.get(id);
			this.people.add(new Pedestrian(p.getPoint(), p.getStatus(), id));
		}
	}

	public HexagonalGrid getGrid() {
		return grid;
	}

	public DateAndTime getDateTime() {
		return dateTime;
	}

	public List<Pedestrian> getPeople() {
		return people;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public DateAndTime updateTime(int minutes) {
		return dateTime.updateTime(minutes);
	}

}
