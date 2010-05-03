package test;

import osm.OsmEdge;
import osm.OsmNode;
import util.jcoord.LatLng;
import util.jcoord.LatLngBox;

public class LatLngBoxTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LatLngBox box = new LatLngBox(new LatLng(3.0, -3.0), new LatLng(-3.0,
				3.0), 100000);

		OsmNode a = new OsmNode(new LatLng(2, -2));
		OsmNode b = new OsmNode(new LatLng(2, 2));
		OsmNode c = new OsmNode(new LatLng(-2, -2));
		OsmNode d = new OsmNode(new LatLng(-2, 2));
		OsmNode e = new OsmNode(new LatLng(5, -4));
		OsmNode f = new OsmNode(new LatLng(4, -5));
		OsmNode g = new OsmNode(new LatLng(5, 4));
		OsmNode h = new OsmNode(new LatLng(4, 5));
		OsmNode i = new OsmNode(new LatLng(-4, 5));
		OsmNode j = new OsmNode(new LatLng(-5, 4));
		OsmNode k = new OsmNode(new LatLng(-5, -4));
		OsmNode l = new OsmNode(new LatLng(-4, -5));
		OsmNode m = new OsmNode(new LatLng(0, -5));
		OsmNode n = new OsmNode(new LatLng(0, 5));
		OsmNode o = new OsmNode(new LatLng(5, 0));
		OsmNode p = new OsmNode(new LatLng(-5, 0));

		OsmEdge ab = new OsmEdge(a, b);
		OsmEdge ba = new OsmEdge(b, a);
		OsmEdge bd = new OsmEdge(b, d);
		OsmEdge db = new OsmEdge(d, b);
		OsmEdge dc = new OsmEdge(d, c);
		OsmEdge cd = new OsmEdge(c, d);
		OsmEdge ac = new OsmEdge(a, c);
		OsmEdge ca = new OsmEdge(c, a);
//		OsmEdge ae = new OsmEdge(a, e);
		OsmEdge ea = new OsmEdge(e, a);
		OsmEdge af = new OsmEdge(a, f);
//		OsmEdge fa = new OsmEdge(f, a);
		OsmEdge ao = new OsmEdge(a, o);
		OsmEdge bg = new OsmEdge(b, g);
//		OsmEdge bh = new OsmEdge(b, h);
		OsmEdge bn = new OsmEdge(b, n);
		OsmEdge cl = new OsmEdge(c, l);
//		OsmEdge ck = new OsmEdge(c, k);
//		OsmEdge dj = new OsmEdge(d, j);
		OsmEdge di = new OsmEdge(d, i);
		OsmEdge dp = new OsmEdge(d, p);
		OsmEdge dn = new OsmEdge(d, n);
//		OsmEdge ce = new OsmEdge(c, e);
//		OsmEdge cf = new OsmEdge(c, f);

		System.err.println("*********Probando Next");
		 System.err.println(ab + " next:\n\t " + ab.next(a, box));
		 System.err.println(ba + " next:\n\t " + ba.next(b, box));
		 System.err.println(bd + " next:\n\t " + bd.next(b, box));
		 System.err.println(db + " next:\n\t " + db.next(d, box));
		 System.err.println(dc + " next:\n\t " + dc.next(d, box));
		 System.err.println(cd + " next:\n\t " + cd.next(c, box));
		 System.err.println(ca + " next:\n\t " + ca.next(c, box));
		 System.err.println(ac + " next:\n\t " + ac.next(a, box));

		// Probando Lineas
		// System.err.println("linea "+ae+", " + ae.getLine(box));
		// System.err.println("linea "+af+", " + af.getLine(box));
		// System.err.println("linea "+bg+", " + bg.getLine(box));
		// System.err.println("linea "+bh+", " + bh.getLine(box));
		// System.err.println("linea "+di+", " + di.getLine(box));
		// System.err.println("linea "+dj+", " + dj.getLine(box));
		// System.err.println("linea "+ce+", " + ce.getLine(box));
		// System.err.println("linea "+cf+", " + cf.getLine(box));

		// Probando posiciones absolutas
//		System.err.println(LatLngBox.Above_Left);
//		System.err.println("\t" + box.absoluteBoxPosition(e));
//		System.err.println("\t" + box.absoluteBoxPosition(f));
//
//		System.err.println(LatLngBox.Above);
//		System.err.println("\t" + box.absoluteBoxPosition(o));
//		System.err
//				.println("\t" + box.absoluteBoxPosition(new LatLng(5.0, 3.0)));
//		System.err.println("\t"
//				+ box.absoluteBoxPosition(new LatLng(5.0, -3.0)));
//
//		System.err.println(LatLngBox.Above_Rigth);
//		System.err.println("\t" + box.absoluteBoxPosition(g));
//		System.err.println("\t" + box.absoluteBoxPosition(h));
//
//		System.err.println(LatLngBox.Rigth);
//		System.err.println("\t" + box.absoluteBoxPosition(n));
//		System.err
//				.println("\t" + box.absoluteBoxPosition(new LatLng(3.0, 4.0)));
//		System.err.println("\t"
//				+ box.absoluteBoxPosition(new LatLng(-3.0, 4.0)));
//
//		System.err.println(LatLngBox.Below_Rigth);
//		System.err.println("\t" + box.absoluteBoxPosition(i));
//		System.err.println("\t" + box.absoluteBoxPosition(j));
//
//		System.err.println(LatLngBox.Below);
//		System.err.println("\t" + box.absoluteBoxPosition(p));
//		System.err.println("\t"
//				+ box.absoluteBoxPosition(new LatLng(-4.0, 3.0)));
//		System.err.println("\t"
//				+ box.absoluteBoxPosition(new LatLng(-4.0, -3.0)));
//
//		System.err.println(LatLngBox.Below_Left);
//		System.err.println("\t" + box.absoluteBoxPosition(k));
//		System.err.println("\t" + box.absoluteBoxPosition(l));
//
//		System.err.println(LatLngBox.Left);
//		System.err.println("\t" + box.absoluteBoxPosition(m));
//		System.err.println("\t"
//				+ box.absoluteBoxPosition(new LatLng(3.0, -4.0)));
//		System.err.println("\t"
//				+ box.absoluteBoxPosition(new LatLng(-3.0, -4.0)));
		//
//		 System.err.println(LatLngBox.In);
//		 System.err.println("\t" + box.absoluteBoxPosition(box.getNw()));
//		 System.err.println("\t" + box.absoluteBoxPosition(box.getSe()));
//		 System.err.println("\t" + box.absoluteBoxPosition(a));
//		 System.err.println("\t" + box.absoluteBoxPosition(b));
//		 System.err.println("\t" + box.absoluteBoxPosition(c));
//		 System.err.println("\t" + box.absoluteBoxPosition(d));

		System.err.println("***********Probando puntos de corte");
		 System.err.println(LatLngBox.ABOVE);
		 OsmEdge ob = new OsmEdge(o, b);
		 System.err.println(ao + " corta\n\t " + ao.getCutNode(box));
		 System.err.println(ob + " corta\n\t " + ob.getCutNode(box));
				
		 System.err.println(LatLngBox.RIGHT);
		 OsmEdge nd = new OsmEdge(n, d);
		 System.err.println(bn + " corta\n\t " + bn.getCutNode(box));
		 System.err.println(nd + " corta\n\t " + nd.getCutNode(box));
		 System.err.println(dn + " corta\n\t " + dn.getCutNode(box));
		
		 System.err.println(LatLngBox.BELOW);
		 OsmEdge pc = new OsmEdge(p, c);
		 System.err.println(dp + " corta\n\t " + dp.getCutNode(box));
		 System.err.println(pc + " corta\n\t " + pc.getCutNode(box));
				
		 System.err.println(LatLngBox.LEFT);
		 OsmEdge cm = new OsmEdge(c, m);
		 OsmEdge ma = new OsmEdge(m, a);
		 System.err.println(cm + " corta\n\t " + cm.getCutNode(box));
		 System.err.println(ma + " corta\n\t " + ma.getCutNode(box));
		
				
		 System.err.println(LatLngBox.ABOVE_LEFT);
		 System.err.println(af + " corta\n\t " + af.getCutNode(box));
		 System.err.println(ea + " corta\n\t " + ea.getCutNode(box));
				
		 System.err.println(LatLngBox.ABOVE_RIGHT);
		 OsmEdge hb = new OsmEdge(h, b);
		 System.err.println(bg + " corta\n\t " + bg.getCutNode(box));
		 System.err.println(hb + " corta\n\t " + hb.getCutNode(box));
				
		
		 System.err.println(LatLngBox.BELOW_LEFT);
		 OsmEdge kc = new OsmEdge(k, c);
		 System.err.println(cl + " corta\n\t " + cl.getCutNode(box));
		 System.err.println(kc + " corta\n\t " + kc.getCutNode(box));
				
		 System.err.println(LatLngBox.BELOW_RIGHT);
		 OsmEdge jd = new OsmEdge(j, d);
		 System.err.println(di + " corta\n\t " + di.getCutNode(box));
		 System.err.println(jd + " corta\n\t " + jd.getCutNode(box));

		// Probando interseccion de box
//		 LatLngBox box1 = new LatLngBox(new LatLng(4.0, -4.0), new LatLng(-4.0,
//		 4.0), 100000);
//		 LatLngBox box2 = new LatLngBox(new LatLng(2.0, -2.0), new LatLng(0.0,
//		 0.0), 100000);
//		 LatLngBox box3 = new LatLngBox(new LatLng(5.0, 2.0), new LatLng(0.0,
//		 4.0), 100000);
//		 System.err.println(box + " intersec " +box1);
//		 System.err.println("\t"+box.intersection(box1));
//		 System.err.println(box1 + " intersec " +box);
//		 System.err.println("\t"+box1.intersection(box));
//		 System.err.println(box + " intersec " +box2);
//		 System.err.println("\t"+box.intersection(box2));
//		 System.err.println(box + " intersec " +box3);
//		 System.err.println("\t"+box.intersection(box3));

		// Posiciones absolutas LatLng
//		 System.err.println(LatLng.Rigth);
//		 System.err.println("\t"+a+", "+b+": "+a.absolutePosition(b));
//		 System.err.println("\t"+c+", "+d+": "+c.absolutePosition(d));
//		 System.err.println("\t"+m+", "+n+": "+m.absolutePosition(n));
//				
//		 System.err.println(LatLng.Below_Rigth);
//		 System.err.println("\t"+a+", "+j+": "+a.absolutePosition(j));
//		 System.err.println("\t"+e+", "+j+": "+e.absolutePosition(j));
//		 System.err.println("\t"+d+", "+j+": "+d.absolutePosition(j));
//				
//		 System.err.println(LatLng.Above_Left);
//		 System.err.println("\t"+j+", "+f+": "+j.absolutePosition(f));
//		 System.err.println("\t"+p+", "+l+": "+p.absolutePosition(l));
//		 System.err.println("\t"+c+", "+f+": "+c.absolutePosition(f));

		// Probando next
//		 System.err.println("Sig "+ab+": "+ab.next(a, box));
//		 System.err.println("Sig "+ba+": "+ba.next(b, box));
//		 System.err.println("Sig "+af+": "+af.next(a, box));
//		 System.err.println("Sig "+ae+": "+ae.next(a, box));
//		 OsmEdge jf = new OsmEdge(j, f);
//		 OsmEdge fj = new OsmEdge(f, j);
//		 System.err.println("Sig "+jf+": "+jf.next(j, box));
//		 System.err.println("Sig "+fj+": "+fj.next(f, box));

		// Probando lines
//		OsmEdge ei = new OsmEdge(e, i);
//		OsmEdge jf = new OsmEdge(j, f);
//		System.err.println("linea " + ab);
//		System.err.println("\t" + ab.getLine(box));
//		System.err.println("linea " + ei);
//		System.err.println("\t" + ei.getLine(box));
//		System.err.println("linea " + jf);
//		System.err.println("\t" + jf.getLine(box));
//		
//		OsmEdge lh = new OsmEdge(l, h);
//		OsmEdge gk = new OsmEdge(g, k);
//		System.err.println("linea " + lh);
//		System.err.println("\t" + lh.getLine(box));
//		System.err.println("linea " + gk);
//		System.err.println("\t" + gk.getLine(box));
//		
//		OsmEdge mn = new OsmEdge(m, n);
//		OsmEdge nm = new OsmEdge(n, m);
//		System.err.println("linea " + mn);
//		System.err.println("\t" + mn.getLine(box));
//		System.err.println("linea " + nm);
//		System.err.println("\t" + nm.getLine(box));
//
//		OsmEdge op = new OsmEdge(o, p);
//		OsmEdge po = new OsmEdge(p, o);
//		System.err.println("linea " + mn);
//		System.err.println("\t" + mn.getLine(box));
//		System.err.println("linea " + nm);
//		System.err.println("\t" + nm.getLine(box));
//		
//		OsmEdge fl = new OsmEdge(f, l);
//		OsmEdge ez = new OsmEdge(e, new LatLng(3.0, -4.0));
//		System.err.println("linea " + fl);
//		System.err.println(fl.next(f, box));
////		System.err.println("\t" + fl.getLine(box));
//		System.err.println("linea " + ez);
////		System.err.println("\t" + ez.getLine(box));
//		System.err.println(ez.next(e, box));
//		OsmEdge lf = new OsmEdge(l, f);
//		System.err.println("linea " + lf);
//		System.err.println(lf.next(l, box));
//		
//		LatLng NW = new LatLng(29.939898, -90.064604);
//		LatLng SE = new LatLng(29.934196, -90.051663);
//
//
//		HexagonalGrid grid = new HexagonalGrid(NW, SE, 0, 0, 30);
//		OsmEdge peta = new OsmEdge(new LatLng(29.939845, -90.0532), new LatLng(29.940258, -90.053235));
//		System.err.println(peta);
//		LatLng ss = peta.next(new LatLng(29.939845, -90.0532), grid.getBox());
//		System.err.println(ss);
//		System.err.println(peta.next(ss, grid.getBox()));
		
		
		// PRubas AddtoBox
//		System.err.println(box);
//		box.addToBox(e);
//		System.err.println(box);
//		
//		LatLngBox bbb = new LatLngBox();
//		System.err.println(bbb);
//		bbb.addToBox(e);
//		System.err.println(bbb);
//		bbb.addToBox(f);
//		System.err.println(bbb);
//		bbb.addToBox(j);
//		System.err.println(bbb);		
	}
}
