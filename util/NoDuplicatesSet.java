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
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class NoDuplicatesSet implements Set<Point>,Serializable {

	private static final long serialVersionUID = 1L;
	
	ArrayList<Point> data = new ArrayList<Point>();

	@Override
	public boolean add(Point e) {
		if (contains(e))
			return false;
		data.add(e);
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends Point> c) {
		for (Point p : c) {
			add(p);
		}
		return false;
	}

	@Override
	public void clear() {
		data = new ArrayList<Point>();
	}

	@Override
	public boolean contains(Object o) {
		if (o instanceof Point) {
			Point op = (Point) o;
			for (Point p : data) {
				if (p.equals(op))
					return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return data.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return data.isEmpty();
	}

	@Override
	public Iterator<Point> iterator() {
		return data.iterator();
	}

	@Override
	public boolean remove(Object o) {
		return data.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return data.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return data.retainAll(c);
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public Object[] toArray() {
		return data.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return data.toArray(a);
	}

}
