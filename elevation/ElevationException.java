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

package elevation;

public class ElevationException extends Exception {

	private static final long serialVersionUID = 1L;

	private String url = null;
	private String data = null;

	public ElevationException(String msg) {
		super(msg);
	}

	public ElevationException(String msg, String url, String data) {
		super(msg);
		this.url = url;
		this.data = data;
	}

	public ElevationException() {
		super();
	}

	public ElevationException(String url, String data) {
		super();
		this.url = url;
		this.data = data;
	}

	public String getUrl() {
		return url;
	}

	public String getData() {
		return data;
	}

}
