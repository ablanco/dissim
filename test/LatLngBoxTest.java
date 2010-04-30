package test;

public class LatLngBoxTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		LatLngBox box = new LatLngBox(new LatLng(3.0, -3.0), new LatLng(-3.0,
//				3.0), 100000);

//		LatLng a = new LatLng(2, -2);
//		LatLng b = new LatLng(2, 2);
//		LatLng c = new LatLng(-2, -2);
//		LatLng d = new LatLng(-2, 2);
//		LatLng e = new LatLng(5, -4);
//		LatLng f = new LatLng(4, -5);
//		LatLng g = new LatLng(5, 4);
//		LatLng h = new LatLng(4, 5);
//		LatLng i = new LatLng(-4, 5);
//		LatLng j = new LatLng(-5, 4);
//		LatLng k = new LatLng(-5, -4);
//		LatLng l = new LatLng(-4, -5);
//		LatLng m = new LatLng(0, -5);
//		LatLng n = new LatLng(0, 5);
//		LatLng o = new LatLng(5, 0);
//		LatLng p = new LatLng(-5, 0);

//		OsmEdge ab = new OsmEdge(a, b);
//		OsmEdge ba = new OsmEdge(b, a);
//		OsmEdge bd = new OsmEdge(b, d);
//		OsmEdge db = new OsmEdge(d, b);
//		OsmEdge dc = new OsmEdge(d, c);
//		OsmEdge cd = new OsmEdge(c, d);
//		OsmEdge ac = new OsmEdge(a, c);
//		OsmEdge ca = new OsmEdge(c, a);
//		OsmEdge ae = new OsmEdge(a, e);
//		OsmEdge ea = new OsmEdge(e, a);
//		OsmEdge af = new OsmEdge(a, f);
//		OsmEdge fa = new OsmEdge(f, a);
//		OsmEdge ao = new OsmEdge(a, o);
//		OsmEdge bg = new OsmEdge(b, g);
//		OsmEdge bh = new OsmEdge(b, h);
//		OsmEdge bn = new OsmEdge(b, n);
//		OsmEdge cl = new OsmEdge(c, l);
//		OsmEdge ck = new OsmEdge(c, k);
//		OsmEdge dj = new OsmEdge(d, j);
//		OsmEdge di = new OsmEdge(d, i);
//		OsmEdge dp = new OsmEdge(d, p);
//		OsmEdge dn = new OsmEdge(d, n);
//		OsmEdge ce = new OsmEdge(c, e);
//		OsmEdge cf = new OsmEdge(c, f);

		// Probando Next
		// System.err.println(ab);
		// System.err.println(ba);
		// System.err.println(ab + " next: " + ab.next(a, box));
		// System.err.println(ba + " next: " + ba.next(b, box));
		// System.err.println(bd + " next: " + bd.next(b, box));
		// System.err.println(db + " next: " + db.next(d, box));
		// System.err.println(dc + " next: " + dc.next(d, box));
		// System.err.println(cd + " next: " + cd.next(c, box));
		// System.err.println(ca + " next: " + ca.next(c, box));
		// System.err.println(ac + " next: " + ac.next(a, box));

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

		// Probando puntos de corte
//		 System.err.println(LatLngBox.Above);
//		 OsmEdge ob = new OsmEdge(o, b);
//		 System.err.println("\t"+ao + " corta " + ao.cutOff(box));
//		 System.err.println("\t"+ob + " corta " + ob.cutOff(box));
//				
//		 System.err.println(LatLngBox.Rigth);
//		 OsmEdge nd = new OsmEdge(n, d);
//		 System.err.println("\t"+bn + " corta " + bn.cutOff(box));
//		 System.err.println("\t"+nd + " corta " + nd.cutOff(box));
//		 System.err.println("\t"+dn + " corta " + dn.cutOff(box));
//		
//		 System.err.println(LatLngBox.Below);
//		 OsmEdge pc = new OsmEdge(p, c);
//		 System.err.println("\t"+dp + " corta " + dp.cutOff(box));
//		 System.err.println("\t"+pc + " corta " + pc.cutOff(box));
//				
//		 System.err.println(LatLngBox.Left);
//		 OsmEdge cm = new OsmEdge(c, m);
//		 OsmEdge ma = new OsmEdge(m, a);
//		 System.err.println("\t"+cm + " corta " + cm.cutOff(box));
//		 System.err.println("\t"+ma + " corta " + ma.cutOff(box));
//		
//				
//		 System.err.println(LatLngBox.Above_Left);
//		 System.err.println("\t"+af + " corta " + af.cutOff(box));
//		 System.err.println("\t"+ea + " corta " + ea.cutOff(box));
//				
//		 System.err.println(LatLngBox.Above_Rigth);
//		 OsmEdge hb = new OsmEdge(h, b);
//		 System.err.println("\t"+bg + " corta " + bg.cutOff(box));
//		 System.err.println("\t"+hb + " corta " + hb.cutOff(box));
//				
//		
//		 System.err.println(LatLngBox.Below_Left);
//		 OsmEdge kc = new OsmEdge(k, c);
//		 System.err.println("\t"+cl + " corta " + cl.cutOff(box));
//		 System.err.println("\t"+kc + " corta " + kc.cutOff(box));
//				
//		 System.err.println(LatLngBox.Below_Rigth);
//		 OsmEdge jd = new OsmEdge(j, d);
//		 System.err.println("\t"+di + " corta " + di.cutOff(box));
//		 System.err.println("\t"+jd + " corta " + jd.cutOff(box));

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
