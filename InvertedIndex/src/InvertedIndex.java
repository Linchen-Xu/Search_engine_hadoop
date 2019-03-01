import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable; 
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat; 
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;

 
 
public class InvertedIndex
{
    public static class invMap extends Mapper<LongWritable,Text,Text,Text>
    {
        private Text keyInfo = new Text();
        private final Text one = new Text("1"); 
        public void map(LongWritable key, Text value,Context context) throws IOException, InterruptedException
        {
        	String line = new String(value.getBytes(), 0, value.getLength(), "UTF8");
//        	System.out.println("line:" + line);
        	StringTokenizer token = new StringTokenizer(line);
        	String keyv = token.nextToken();//缃戦〉閾炬帴
        	if(!token.hasMoreTokens()) {
        		return;
        	}
        	line = token.nextToken();
//        	System.out.println("next token:" + line);
            StringTokenizer stk = new StringTokenizer(line,"/");//鍒嗚瘝
//            System.out.println("key:" + keyv);
            while (stk.hasMoreElements()) 
            {
            	//keyinfo:鍒嗚瘝&&&閾炬帴
            	line = stk.nextToken();
//            	System.out.println("token:" + line);
                keyInfo.set(line + "&&&" + keyv);
//                System.out.println("keyinfo:" + keyInfo.toString());
                context.write(keyInfo, one);
            }
        }
    }
 
    public static class Combiner extends Reducer<Text,Text,Text,Text>
    {
    	
        private Text valueInfo = new Text();
        public void reduce(Text key, Iterable<Text> values,Context context) throws IOException, InterruptedException
        {
        	//杈撳叆锛�<鍒嗚瘝&&&閾炬帴锛�1>
            String line = key.toString();
            int splitIndex = line.indexOf("&&&");
            if(splitIndex == 0) {
            	return;
            }
            int sum = 0;
            for (Text value : values)
            {
                sum += Integer.valueOf(value.toString());
            }
            //valueInfo:閾炬帴|鏁板瓧
            valueInfo.set(line.substring(splitIndex+3) + "|"  + String.valueOf(sum));
            //key:鍒嗚瘝
//            System.out.println("  substr(0...):" + line.substring(0,splitIndex) + "\n  substr(index...):" + line.substring(splitIndex+3));
//            System.out.println("key:" + key.toString());
            key.set(line.substring(0,splitIndex));
            context.write(key, valueInfo);
        }
    }
    
    public static class invReduce extends TableReducer<Text,Text,ImmutableBytesWritable>
    {
        public void reduce(Text key, Iterable<Text> values,Context contex) throws IOException, InterruptedException
        {
        	//杈撳叆锛�<鍒嗚瘝锛岄摼鎺sum>
            //鐢熸垚閾炬帴鍒楄〃
        	//valueList:鍒嗚瘝锛岄摼鎺sum;....閾炬帴|sum;
//            String valueList = new String();
            //linkList:閾炬帴;...閾炬帴;
            String linkList = new String();
            String line = "";
            for (Text value : values)
            {
            	line = value.toString();
//            	valueList += line + ";";
            	line = StringUtils.split(line,'|')[0];
//            	System.out.println("line:" + line);
            	linkList += line + ";";
            }
            
//            System.out.println("key:" + key.toString());
 
            Put put = new Put(Bytes.toBytes(key.toString()));
		    //列族为word,列修饰符为key，列值为链接
		    put.add(Bytes.toBytes("word"), Bytes.toBytes("key"), Bytes.toBytes(linkList));
		    contex.write(new ImmutableBytesWritable(key.getBytes()), put);// 输出求和后的<key,value>
        }
    }
 
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException
    {
		System.setProperty("hadoop.home.dir","C:/hadoop-2.7.1"); 
	    
		String tablename = "search";
		Configuration conf = HBaseConfiguration.create();
	    conf.set("hbase.zookeeper.quorum", "your_ip_address");
        conf.set("hbase.zookeeper.property.clientPort","2181");
	    HBaseAdmin admin = new HBaseAdmin(conf);
		if(admin.tableExists(tablename)){
	        System.out.println("table exists!recreating.......");
	        admin.disableTable(tablename);
	        admin.deleteTable(tablename);
	    }
	    HTableDescriptor htd = new HTableDescriptor(tablename);
	    HColumnDescriptor tcd = new HColumnDescriptor("word");
	    htd.addFamily(tcd);//创建列族
	    admin.createTable(htd);//创建表

        Job job = new Job(conf);//鏂板缓job
        job.setJarByClass(InvertedIndex.class);//job绫�
        job.setMapperClass(invMap.class);//map璁剧疆
        job.setCombinerClass(Combiner.class);//combiner璁剧疆
        job.setJobName("InvertedIndex");

        TableMapReduceUtil.initTableReducerJob(tablename, invReduce.class, job);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
 
        job.setOutputValueClass(ImmutableBytesWritable.class);
		job.setInputFormatClass(TextInputFormat.class); 

		FileInputFormat.addInputPath(job, new Path("C:/Users/luyunyyyyy/Documents/xlc/saved_html"));
//		FileOutputFormat.setOutputPath(job, new Path("hdfs://172.17.11.54:9000/output")); 
        
        job.waitForCompletion(true);
    }
}

