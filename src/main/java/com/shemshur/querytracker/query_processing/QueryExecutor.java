package com.shemshur.querytracker.query_processing;

import org.apache.log4j.Logger;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class QueryExecutor {
   private String query;
   private String db_name;
   private int executionTimes;
   private int timeout;
   public ArrayList<Long> resultTimes;
   private static final Logger logger = Logger.getLogger(QueryExecutor.class);


   public QueryExecutor(String query, String db_name, Integer executionTimes, Integer timeout){
       this.query = query;
       this.db_name = db_name;
       this.executionTimes = (executionTimes!=null ? executionTimes : 1);
       this.timeout = (timeout!=null ? timeout : 100);
       this.resultTimes = new ArrayList<Long>();
   }

   public ArrayList<Long> check() throws SQLException, InterruptedException, ExecutionException, TimeoutException, NamingException {
       for(int i=0;i<executionTimes;i++) {
           ConnectionPool pool = ConnectionPool.pool;
           Connection connection = null;
           connection = pool.getConnection(db_name,timeout);
           if (connection==null) {
               logger.info("Couldn't get connection");
           }
           else {
               Statement stmt = null;
               try {
                   stmt = connection.createStatement();
                   long start = System.currentTimeMillis();
                   stmt.executeQuery(query);
                   long finish = System.currentTimeMillis();
                   long timeConsumedMillis = finish - start;
                   resultTimes.add(timeConsumedMillis);
                   connection.commit();
               } catch (SQLException e) {
                   logger.info("There is an error in client query statement");
                   throw new SQLException("There is an error in your query statement");
               } finally {
                   connection.rollback();
                   pool.closeConnection(db_name, connection);
               }
           }
       }
       return resultTimes;
   }
}
