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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import util.Point;
import util.flood.FloodHexagonalGrid;
import behaviours.flood.UpdateFloodGridBehav;

/**
 * {@link Set} of {@link Point}s optimized for handling the modified tiles on
 * {@link FloodHexagonalGrid} by {@link UpdateFloodGridBehav}. It depends on the
 * parameters of the {@link FloodHexagonalGrid} that use this
 * {@link ModifiedTilesSet}.
 * 
 * @author Alejandro Blanco, Manuel Gomar
 * 
 */
public class ModifiedTilesSet implements Set<Point>, Serializable {

	private static final long serialVersionUID = 1L;

	private int columns;
	private int offCol;
	private int offRow;
	private int initialCapacity;
	private ArrayList<Point> data;

	/**
	 * {@link ModifiedTilesSet} constructor
	 * 
	 * @param columns
	 *            of the grid
	 * @param rows
	 *            of the grid
	 * @param offCol
	 *            offset of columns of the grid
	 * @param offRow
	 *            offset of rows of the grid
	 */
	public ModifiedTilesSet(int columns, int rows, int offCol, int offRow) {
		this.columns = columns;
		this.offCol = offCol;
		this.offRow = offRow;
		initialCapacity = columns * rows;
		newData();
	}

	/**
	 * Drops the data and cleans the set
	 */
	private void newData() {
		data = new ArrayList<Point>(initialCapacity);
		for (int i = 0; i < initialCapacity; i++) {
			data.add(null);
		}
	}

	/**
	 * Return the index on data of the specified {@link Point}
	 * 
	 * @param p
	 * @return
	 */
	private int idx(Point p) {
		return ((p.getRow() + 1 - offRow) * columns)
				+ (p.getCol() + 1 - offCol);
	}

	/**
	 * Returns a {@link HashSet}<{@link Point}> without null elements
	 * 
	 * @return a {@link HashSet}<{@link Point}> without null elements
	 */
	public HashSet<Point> withoutNulls() {
		HashSet<Point> result = new HashSet<Point>();
		for (Point p : data) {
			if (p != null)
				result.add(p);
		}
		return result;
	}

	@Override
	public boolean add(Point e) {
		if (contains(e))
			return false;
		data.set(idx(e), e);
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends Point> c) {
		boolean result = false;
		for (Point p : c) {
			result = result || add(p);
		}
		return result;
	}

	@Override
	public void clear() {
		newData();
	}

	@Override
	public boolean contains(Object o) {
		if (o instanceof Point) {
			Point op = (Point) o;
			return data.get(idx(op)) != null;
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
