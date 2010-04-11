package util.flood;

import java.util.Comparator;
import java.util.List;

public class SizeComparator implements Comparator<List<Edge>>  {

	@Override
	public int compare(List<Edge> edges0, List<Edge> edges1) {
		return edges1.size() - edges0.size();
	}
}
