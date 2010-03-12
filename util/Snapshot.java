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

import jade.core.AID;

import java.io.Serializable;
import java.util.Map;

public class Snapshot implements Serializable {

	private static final long serialVersionUID = 1L;

	private HexagonalGrid grid;
	private DateAndTime dateTime;
	private AID envAid;
	private Map<String, Point> people;

	public Snapshot(AID envAid, HexagonalGrid grid, DateAndTime dateTime,
			Map<String, Point> people) {
		this.envAid = envAid;
		this.grid = grid;
		this.dateTime = dateTime;
		this.people = people;
	}

	public AID getEnvAid() {
		return envAid;
	}

	public HexagonalGrid getGrid() {
		return grid;
	}

	public DateAndTime getDateTime() {
		return dateTime;
	}

	public Map<String, Point> getPeople() {
		return people;
	}

}
