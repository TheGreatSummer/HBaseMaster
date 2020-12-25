package com.summer.hbase.utils;


import com.summer.hbase.constants.Constants;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;

public class HBaseUtil {

    public static void crateNameSpace(String namespace) throws IOException {

        Connection connection = ConnectionFactory.createConnection(Constants.CONFIGURATION);

        Admin admin = connection.getAdmin();

        NamespaceDescriptor namespaceDescriptor = NamespaceDescriptor.create(namespace).build();

        admin.createNamespace(namespaceDescriptor);

        admin.close();

        connection.close();;

    }

    public static boolean isTableExist(String tablename) throws IOException {

        Connection connection = ConnectionFactory.createConnection(Constants.CONFIGURATION);

        Admin admin = connection.getAdmin();

        boolean b = admin.tableExists(TableName.valueOf(tablename));

        admin.close();
        connection.close();

        return b;
    }

    public static void createTable(String tablename,int version,String... cfs) throws IOException {

        if(cfs.length <= 0){
            System.out.println("列族信息不完整！");
            return;
        }
        if(isTableExist(tablename)){
            System.out.println(tablename+"表已存在");
            return ;

        }

        Connection connection = ConnectionFactory.createConnection(Constants.CONFIGURATION);

        Admin admin = connection.getAdmin();

        HTableDescriptor hTableDescriptor = new HTableDescriptor(TableName.valueOf(tablename));

        for(String cf:cfs){
            HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(cf);
            hColumnDescriptor.setMaxVersions(version);
            hTableDescriptor.addFamily(hColumnDescriptor);
        }

        admin.createTable(hTableDescriptor);

        admin.close();

        connection.close();


    }

    public static void dropTable(String tableName) throws IOException {
        Connection connection = ConnectionFactory.createConnection(Constants.CONFIGURATION);
        Admin admin = connection.getAdmin();
        if(isTableExist(tableName)){
            admin.disableTable(TableName.valueOf(tableName));
            admin.deleteTable(TableName.valueOf(tableName));
            System.out.println("删除成功");
            return ;
        }else{
            System.out.println("该表不存在");
            return ;
        }
    }
}
