package searchEngine;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import dao.daoProcess;

public class search extends HttpServlet{
	static daoProcess dao = new daoProcess();
    
    static List<String> addr = new ArrayList<String>();
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			request.setCharacterEncoding("utf-8");
            response.setCharacterEncoding("UTF-8");    
            response.setContentType("application/json; charset=utf-8");
            String text = request.getParameter("word");
            String key = URLDecoder.decode(text, "UTF-8");
            
            //查看参数
//            Map map=request.getParameterMap();
//            Set keSet=map.entrySet();
//            for(Iterator itr=keSet.iterator();itr.hasNext();){
//	            Map.Entry me=(Map.Entry)itr.next();
//	            Object ok=me.getKey();
//	            Object ov=me.getValue();
//	            String[] value=new String[1];
//	            if(ov instanceof String[]){
//	            	value=(String[])ov;
//	            }else{
//	            	value[0]=ov.toString();
//	            }
//	
//	            for(int k=0;k<value.length;k++){
//	            	System.out.println(ok+"="+value[k]);
//	            }
//            }
            
//            System.out.println("request: "+request.toString());
            System.out.println("key: "+key);
            
            daoProcess.getRow(key,addr);
            JSONObject json = new JSONObject();
            JSONArray array =new JSONArray();
            PrintWriter writer = response.getWriter();
            String line;
            
            for(Iterator<String> it = addr.iterator();it.hasNext();) {
            	line = it.next();
//    			System.out.println(line);
    			array.put(line);
    		}
            json.put("data", array);
            line = json.toString();
            writer.println(line);
            System.out.println("return:" + line);
            writer.flush();
            writer.close();
            
		} catch (Exception e) {
            e.printStackTrace();
        }
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Get");
	}
}
/*
 http://localhost:8080/searchEngine/s?word=123
 */
