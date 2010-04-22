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

package util.java;

import java.io.Serializable;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import util.Point;

public class NoDuplicatePointsSet implements Set<Point>, Serializable {

	private static final long serialVersionUID = 1L;

	private Hashtable<String, Point> data;
	private int iniCap = -1;

	public NoDuplicatePointsSet() {
		data = new Hashtable<String, Point>();
	}

	public NoDuplicatePointsSet(int initialCapacity) {
		iniCap = initialCapacity;
		data = new Hashtable<String, Point>(initialCapacity);
	}

	@Override
	public boolean add(Point e) {
		if (e == null)
			throw new IllegalArgumentException(
					"This set doesn't admit null elements.");

		if (!data.containsKey(e.hashPos())) {
			data.put(e.hashPos(), e);
			return true;
		}

		return false;
	}

	@Override
	public boolean addAll(Collection<? extends Point> c) {
		boolean result = false;
		for (Point point : c) {
			result = result || add(point);
		}
		return result;
	}

	@Override
	public void clear() {
		if (iniCap > 0)
			data = new Hashtable<String, Point>(iniCap);
		else
			data = new Hashtable<String, Point>();
	}

	@Override
	public boolean contains(Object o) {
		if (o instanceof Point) {
			Point p = (Point) o;
			return data.containsKey(p.hashPos());
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		boolean result = true;
		Iterator<?> it = c.iterator();
		while (it.hasNext() && result) {
			result = result && contains(it.next());
		}
		return result;
	}

	@Override
	public boolean isEmpty() {
		return data.isEmpty();
	}

	@Override
	public Iterator<Point> iterator() {
		return data.values().iterator();
	}

	@Override
	public boolean remove(Object o) {
		if (o instanceof Point) {
			Point p = (Point) o;
			Point old = data.remove(p.hashPos());
			return old != null;
		}
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean result = false;
		for (Object obj : c) {
			result = result || remove(obj);
		}
		return result;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean result = false;
		for (Point p : data.values()) {
			if (c.contains(p))
				result = result || remove(p);
		}
		return result;
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public Object[] toArray() {
		return data.values().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return data.values().toArray(a);
	}

}
