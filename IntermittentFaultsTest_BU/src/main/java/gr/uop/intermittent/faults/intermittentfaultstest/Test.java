/*
 * Copyleft 2015 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gr.uop.intermittent.faults.intermittentfaultstest;

import gr.uop.intermittent.faults.utils.Cache;
import gr.uop.intermittent.faults.utils.CacheApi;
import gr.uop.intermittent.faults.utils.CacheCollection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import org.jboss.metrics.javase.automatedmetricsjavaseapi.MetricsCacheApi;
import org.jboss.metrics.javase.automatedmetricsjavaseapi.MetricsPropertiesApi;
import org.jboss.metrics.jbossautomatedmetricslibrary.MetricsCache;
import org.jboss.metrics.jbossautomatedmetricslibrary.MetricsCacheCollection;
import org.jboss.metrics.jbossautomatedmetricsproperties.MetricProperties;

/**
 *
 * @author Panagiotis Sotiropoulos
 */
public class Test {

    private static String groupName = "intermittentFaultsGroup";
    private static String groupName2 = "myTestGroup";
    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        try {
            initializeMetricProperties();
            TestClass mTC = new TestClass();
            TestThreads mTreads =  new TestThreads("1",mTC);
            mTreads.start();
         
            TestThreads mTreads2 =  new TestThreads("2",mTC);
            mTreads2.start();
            
            TestThreads mTreads3 =  new TestThreads("3",mTC);
            mTreads3.start();
            
            while (mTreads.getT().isAlive() || mTreads2.getT().isAlive() || mTreads3.getT().isAlive());
            
            if (CacheCollection.getCacheCollection().getCacheInstance(groupName2)!=null)
                System.out.println(CacheApi.printCache(groupName2));
            
            if (MetricsCacheCollection.getMetricsCacheCollection().getMetricsCacheInstance(groupName)!=null)
                System.out.println(MetricsCacheApi.printMetricsCache(groupName));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public static MetricsCache test(String[] args) {
        try {
            initializeMetricProperties();
            TestClass mTC = new TestClass();
            TestThreads mTreads =  new TestThreads("1",mTC);
            mTreads.start();
         
            TestThreads mTreads2 =  new TestThreads("2",mTC);
            mTreads2.start();
            
            TestThreads mTreads3 =  new TestThreads("3",mTC);
            mTreads3.start();
            
            while (mTreads.getT().isAlive() || mTreads2.getT().isAlive() || mTreads3.getT().isAlive());
            
        //    if (MetricsCacheCollection.getMetricsCacheCollection().getMetricsCacheInstance(groupName2)!=null)
        //        System.out.println(MetricsCacheApi.printMetricsCache(groupName2));
            
         //   if (MetricsCacheCollection.getMetricsCacheCollection().getMetricsCacheInstance(groupName)!=null)
         //       System.out.println(MetricsCacheApi.printMetricsCache(groupName));

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return MetricsCacheCollection.getMetricsCacheCollection().getMetricsCacheInstance(groupName);
    }
    
    private static void initializeMetricProperties() {
        MetricProperties metricProperties = new MetricProperties();
        metricProperties.setCacheStore("true");
        MetricsPropertiesApi.storeProperties(groupName2, metricProperties);
        MetricProperties metricProperties2 = new MetricProperties();
        metricProperties2.setCacheStore("true");
        metricProperties2.setDatabaseStore("true");
        try {
            Connection  connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306", "root", "root");
            Statement stmt = connection.createStatement();
            createDbTable(stmt);
            HashMap<String,Statement> dbStmt = new HashMap<String,Statement>();
            dbStmt.put("statement_1", stmt);
            metricProperties2.setDatabaseStatement(dbStmt);
            HashMap<String,String> query1 = new HashMap<String,String>();
            query1.put("StoreDBMetric", "INSERT INTO mymetrics.metricvalues(METRIC_NAME,METRIC_VALUE,METRIC_INSTANCE,RECORD_TIME) VALUES('{1}', [1], '{instance}', '{time}');");
            metricProperties2.setUpdateDbQueries(query1);
        } catch(Exception e) {
            e.printStackTrace();
        }
        MetricsPropertiesApi.storeProperties(groupName, metricProperties2);
    }
    
    private static void createDbTable(Statement stmt) {
        try {
            String query = "DROP DATABASE mymetrics;";
            
            try {
            //    stmt.executeUpdate(query);      
            } catch (Exception e) {
            }
            
            query = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'mymetrics' AND table_name = 'metricvalues'";
            ResultSet rs = stmt.executeQuery(query);                  
            rs.next();
            boolean exists = rs.getInt("COUNT(*)") > 0;
            
            if (!exists) {
                String sql = "CREATE DATABASE MyMETRICS";
                stmt.executeUpdate(sql);
                System.out.println("Database created successfully...");

                sql = sql = "CREATE TABLE mymetrics.metricvalues(ID int NOT NULL AUTO_INCREMENT, METRIC_NAME varchar(255) NOT NULL," +
                      " METRIC_VALUE varchar(255) NOT NULL, METRIC_INSTANCE varchar(255), RECORD_TIME DATETIME, PRIMARY KEY(ID));"; 
                
                stmt.executeUpdate(sql);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
}