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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class NoDuplicatePointsSet implements Set<Point> {

	private int initialCapacity = -1;
	private ArrayList<Point> data;

	public NoDuplicatePointsSet() {
		data = new ArrayList<Point>();
	}

	public NoDuplicatePointsSet(int initialCapacity) {
		this.initialCapacity = initialCapacity;
		data = new ArrayList<Point>(initialCapacity);
	}

	@Override
	public boolean add(Point e) {
		boolean result = contains(e);
		if (!result)
			data.add(e);
		return result;
	}

	@Override
	public boolean addAll(Collection<? extends Point> c) {
		boolean result = false;
		for (Point pt : c) {
			result = result || add(pt);
		}
		return result;
	}

	@Override
	public void clear() {
		if (initialCapacity > 0)
			data = new ArrayList<Point>(initialCapacity);
		else
			data = new ArrayList<Point>();
	}

	@Override
	public boolean contains(Object o) {
		Point e;
		if (o instanceof Point)
			e = (Point) o;
		else
			return false;
		boolean result = false;
		for (Point pt : data) {
			if (pt.equals(e)) {
				result = true;
				break;
			}
		}
		return result;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		boolean result = true;
		Iterator<?> it = c.iterator();
		while (result && it.hasNext()) {
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
