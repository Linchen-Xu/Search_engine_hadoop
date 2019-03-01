package dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class daoTest {
	public static void main(String[] args) {
		daoProcess dao = new daoProcess();
		List<String> addr = new ArrayList<String>();
		try {
			daoProcess.getRow("°Ù¶È",addr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(Iterator<String> it = addr.iterator();it.hasNext();) {
			System.out.println(it.next());
		}
	}
}
