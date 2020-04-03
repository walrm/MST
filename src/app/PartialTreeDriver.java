package app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import structures.Arc;
import structures.Graph;
import structures.PartialTree;

public class PartialTreeDriver {

	public static void main(String[] args) throws IOException {
		Graph gra = new Graph("graph3.txt");
		PartialTreeList p = new PartialTreeList();
		p = PartialTreeList.initialize(gra);
		Iterator<PartialTree> iter = p.iterator();
		
		while (iter.hasNext()) {
			PartialTree pt = iter.next();
//			System.out.println(pt);
		}
		
		ArrayList<Arc> MST = PartialTreeList.execute(p);
		System.out.println(MST);
	}
}
