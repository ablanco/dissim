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

import java.io.PrintStream;

public class Logger {

	private PrintStream log = System.out;
	private PrintStream debug = System.err;
	private boolean disabled = false;

	public Logger() {
		// empty
	}

	public Logger(PrintStream log) {
		if (log != null) {
			this.log = log;
			this.debug = log;
		}
	}

	public Logger(PrintStream log, PrintStream debug) {
		if (log != null)
			this.log = log;
		if (debug != null)
			this.debug = debug;
	}

	// Métodos de escritura

	public void print(String s) {
		if (disabled)
			return;
		log.print(s);
	}

	public void println(String s) {
		if (disabled)
			return;
		log.println(s);
	}

	public void debug(String s) {
		if (disabled)
			return;
		if (debug.equals(log))
			s = "DEBUG: " + s;
		debug.print(s);
	}

	public void debugln(String s) {
		if (disabled)
			return;
		if (debug.equals(log))
			s = "DEBUG: " + s;
		debug.println(s);
	}

	public void disable() {
		disabled = true;
	}

	public void enable() {
		disabled = false;
	}

}
