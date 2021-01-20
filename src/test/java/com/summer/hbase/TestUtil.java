package com.summer.hbase;

import com.summer.hbase.dao.DDoSDao;
import org.junit.Test;

import java.io.*;

public class TestUtil {

    @Test
    public void TestScan() throws IOException {
        DDoSDao.scanTable("screen:ddos");
    }

    @Test
    public void TestWrite(){
        method1();
    }

    @Test
    public void writeData(){
        try {
//            StringBuffer sb= new StringBuffer("");
//
//            FileReader reader = new FileReader("ddosdata.txt");
//
//            BufferedReader br = new BufferedReader(reader);
//
//            String str = null;
//
//            while((str = br.readLine()) != null) {
//
//                sb.append(str+"/n");
//
//                System.out.println(str);
//            }
//
//            br.close();
//
//            reader.close();

            // write string to file

            FileWriter writer = new FileWriter("test2.txt");

            BufferedWriter bw = new BufferedWriter(writer);

            bw.newLine();
            bw.write("sb/n".toString());

            bw.close();

            writer.close();

        }catch(FileNotFoundException e) {

            e.printStackTrace();

        }catch(IOException e) {

            e.printStackTrace();

        }
    }

    public void method1() {
        FileWriter fw = null;
        try {
//如果文件存在，则追加内容；如果文件不存在，则创建文件
            File f=new File("ddosdata.txt");
            fw = new FileWriter(f, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw);
        pw.println("追加内容");
        pw.flush();
        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

