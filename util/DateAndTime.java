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

/**
 * Custom date, because KML, OpenOffice, and Java uses diferents ways to show
 * time, so this is a facade
 * 
 * @author Manuel Gomar, Alejandro Blanco
 * 
 */
public class DateAndTime implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Actual date and time object
	 */
	private GregorianCalendar g;

	/**
	 * Uses {@link GregorianCalendar} constructor
	 * 
	 * @param date
	 *            With format DD/MM/AAAA
	 * @param hour
	 *            With format HH:MM:SS
	 */
	public DateAndTime(String date, String hour) {
		String[] d = date.split("/");
		String[] h = hour.split(":");
		g = new GregorianCalendar(Integer.parseInt(d[2]), Integer
				.parseInt(d[1]), Integer.parseInt(d[0]),
				Integer.parseInt(h[0]), Integer.parseInt(h[1]), Integer
						.parseInt(h[2]));
	}

	/**
	 * Uses {@link GregorianCalendar} constructor
	 * 
	 * @param year
	 * @param month
	 * @param dayOfMonth
	 * @param hourOfDay
	 * @param minute
	 */
	public DateAndTime(int year, int month, int dayOfMonth, int hourOfDay,
			int minute) {
		g = new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute, 0);
	}

	/**
	 * Uses {@link GregorianCalendar} constructor
	 * 
	 * @param dateTime
	 *            With format AAAA-MM-DDThh:mm:ss
	 */
	public DateAndTime(String dateTime) {
		g = new GregorianCalendar();
		parseAndSetTime(dateTime);
	}

	/**
	 * Parses the parameter dateTime and change the date and time of the object
	 * to the one parsed. dateTime must have this format: AAAA-MM-DDThh:mm:ss
	 * 
	 * @param dateTime
	 *            With format AAAA-MM-DDThh:mm:ss
	 */
	public void parseAndSetTime(String dateTime) {
		String[] data = dateTime.split("T");
		String[] date = data[0].split("-");
		String[] hour = data[1].split(":");
		g.set(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer
				.parseInt(date[2]), Integer.parseInt(hour[0]), Integer
				.parseInt(hour[1]), Integer.parseInt(hour[2]));
	}

	/**
	 * Increase the date and time in given minutes
	 * 
	 * @param minutes
	 * @return previous {@link DateAndTime}
	 */
	public DateAndTime updateTime(int minutes) {
		GregorianCalendar previous = (GregorianCalendar) g.clone();
		g.add(Calendar.MINUTE, minutes);
		return new DateAndTime(previous.get(Calendar.YEAR), previous
				.get(Calendar.MONTH), previous.get(Calendar.DAY_OF_MONTH),
				previous.get(Calendar.HOUR_OF_DAY), previous
						.get(Calendar.MINUTE));
	}

	/**
	 * Convert from KML string to Open Office Calc string
	 * 
	 * @param dateTime
	 *            With format AAAA-MM-DDThh:mm:ss
	 * @return {@link String} with this format: DD/MM/AAAA HH:MM
	 */
	public static String toOooDate(String dateTime) {
		String[] dates = dateTime.split("T");
		String[] date = dates[0].split("-");
		String[] time = dates[1].split(":");
		return date[2] + "/" + date[1] + "/" + date[0] + " " + time[0] + ":"
				+ time[1];
	}

	@Override
	/**
	 * String representation of this object with KML format: AAAA-MM-DDThh:mm:ss
	 */
	public String toString() {
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
