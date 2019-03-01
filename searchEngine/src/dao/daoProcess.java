package dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

public class daoProcess {
	static Configuration conf = null;
	static HTableInterface table;
    static {
        conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "172.17.11.54,172.17.11.55,172.17.11.56");
        conf.set("hbase.zookeeper.property.clientPort","2181");
        conf.set("hbase.master", "172.17.11.54:16000");
    }
    
    public daoProcess() {
    	HTablePool tablePool = new HTablePool(conf, 10);
        table = tablePool.getTable("search");
    }
	
	public static void getRow(String row,List<String> addr) throws Exception {
        Get get = new Get(Bytes.toBytes(row));
        Result result = table.get(get);
        StringTokenizer stk = null;
        addr.clear();
        for (Cell rowKV : result.rawCells()) {
//        	Êä³ö½á¹û
//        	System.out.print("Row Name: " + new String(CellUtil.cloneRow(rowKV)) + " ");
//        	System.out.print("Timestamp: " + rowKV.getTimestamp() + " ");
//        	System.out.print("column Family: " + new String(CellUtil.cloneFamily(rowKV)) + " ");
//        	System.out.print("column Name:  " + new String(CellUtil.cloneQualifier(rowKV)) + " ");
//        	System.out.println("Value: " + new String(CellUtil.cloneValue(rowKV)) + " ");
        	stk = new StringTokenizer(new String(CellUtil.cloneValue(rowKV)),";");
        	while(stk.hasMoreTokens()) {
        		addr.add(stk.nextToken());
        	}
        }
	}
	
	public void close() {
		try {
			table.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
