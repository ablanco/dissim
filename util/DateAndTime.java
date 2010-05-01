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
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateAndTime implements Serializable {

	private static final long serialVersionUID = 1L;

	private GregorianCalendar g;

	public DateAndTime(String date, String hour) {
		String[] d = date.split("/");
		String[] h = hour.split(":");
		g = new GregorianCalendar(Integer.parseInt(d[2]), Integer
				.parseInt(d[1]), Integer.parseInt(d[0]),
				Integer.parseInt(h[0]), Integer.parseInt(h[1]), Integer
						.parseInt(h[2]));
	}

	public DateAndTime(int year, int month, int dayOfMonth, int hourOfDay,
			int minute) {
		g = new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute, 0);
	}

	/**
	 * Increase the date and time in given minutes
	 * 
	 * @param minutes
	 */
	public DateAndTime updateTime(int minutes) {
		GregorianCalendar previous = (GregorianCalendar) g.clone();
		g.add(Calendar.MINUTE, minutes);
		return new DateAndTime(previous.get(Calendar.YEAR), previous
				.get(Calendar.MONTH), previous.get(Calendar.DAY_OF_MONTH),
				previous.get(Calendar.HOUR_OF_DAY), previous
						.get(Calendar.MINUTE));
	}

	public String toOooDate() {
		return g.get(Calendar.DAY_OF_MONTH) + "/" + g.get(Calendar.MONTH) + "/"
				+ g.get(Calendar.YEAR) + " " + g.get(Calendar.HOUR_OF_DAY)
				+ ":" + g.get(Calendar.MINUTE);
	}

	@Override
	public String toString() {
		// dateTime (AAAA-MM-DDThh:mm:ssZ)
		String month = "";
		String day = "";
		String hour = "";
		String minute = "";
		int m = g.get(Calendar.MONTH);
		int da = g.get(Calendar.DAY_OF_MONTH);
		int h = g.get(Calendar.HOUR_OF_DAY);
		int mm = g.get(Calendar.MINUTE);
		if (m < 10) {
			month = "0" + m;
		} else {
			month = "" + m;
		}
		if (da < 10) {
			day = "0" + da;
		} else {
			day = "" + da;
		}
		if (h < 10) {
			hour = "0" + h;
		} else {
			hour = "" + h;
		}
		if (mm < 10) {
			minute = "0" + mm;
		} else {
			minute = "" + mm;
		}
		return g.get(Calendar.YEAR) + "-" + month + "-" + day + "T" + hour
				+ ":" + minute + ":00";
	}

}
