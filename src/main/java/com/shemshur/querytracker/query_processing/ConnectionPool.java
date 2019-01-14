package com.shemshur.querytracker.query_processing;

import org.apache.log4j.Logger;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectionPool {
    private static ConcurrentHashMap<String, Connection> connectionHash;
    public static ConnectionPool pool;
    private static Lock lock;
    private static HashSet<String> db_names;
    private static final Logger logger = Logger.getLogger(ConnectionPool.class);

    static {
        lock = new ReentrantLock();
        pool = new ConnectionPool();
        logger.info("Connection pool was created");
    }

    private ConnectionPool() {
        connectionHash = new ConcurrentHashMap<String, Connection>();
        db_names = new HashSet<String>();
        try {
            createNewConnection();
        } catch (SQLException e) {
            logger.error("Can not create db connection", e);
        }
    }

    private void createNewConnection() throws SQLException {
        DatasourceConfiguration datasourceConfiguration = new DatasourceConfiguration();
        DataSource dataSource1 = datasourceConfiguration.getDataSource1();
        DataSource dataSource2 = datasourceConfiguration.getDataSource2();
        db_names.add("test1");
        db_names.add("test2");
        Connection connection1 = dataSource1.getConnection();
        Connection connection2 = dataSource2.getConnection();
        connection1.setAutoCommit(false);
        connection2.setAutoCommit(false);
        connectionHash.put("test1", connection1);
        connectionHash.put("test2", connection2);
        logger.info("Connections are created");
    }

    public Connection getConnection(String db_name, int timeoutSec) throws NamingException, SQLException, TimeoutException, InterruptedException, ExecutionException {
        lock.lock();
        Connection connection = null;
        try {
            connection = connectionHash.get(db_name);
            if (connection==null) {
                if(!db_names.contains(db_name)) {
                    logger.info("Client request: There is no such DB" + db_name);
                    throw new NamingException("There is no such DB");
                }
                //trying to get a new connection
                Callable<Connection> connectionGetter = new ConnectionGetter<>(db_name);
                ExecutorService exec = Executors.newFixedThreadPool(1);
                Future<Connection> result = exec.submit(connectionGetter);
                try {
                    connection = result.get(timeoutSec, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    result.cancel(true);
                    String message = "TIMEOUT: Can not get connection more than ".concat(Integer.toString(timeoutSec)).concat(" seconds");
                    logger.info(message);
                    throw new TimeoutException(message);
                } catch (InterruptedException e) {
                    result.cancel(true);
                    String message = "TIMEOUT: Can not get connection more than ".concat(Integer.toString(timeoutSec)).concat(" seconds");
                    logger.info(message);
                    logger.info(message);
                    throw new InterruptedException("INTERRUPTED THREAD: Can mot get connection more than".concat(Integer.toString(timeoutSec)).concat(" seconds"));
                }
            }
            else {
               connectionHash.remove(db_name);
            }
        } finally {
            lock.unlock();
        }
        return connection;
    }

    public void closeConnection(String db_name, Connection connection) {
        lock.lock();
        try {
            connectionHash.put(db_name, connection);
        } finally {
            lock.unlock();
        }
    }

    private class ConnectionGetter<String> implements Callable<Connection> {
        private String db_name;
        public ConnectionGetter(String db_name) {
            this.db_name = db_name;
        }

        public Connection call()
        {
            Connection subConnection = null;
            try {
                do {
                    subConnection = connectionHash.get(this.db_name);
                    TimeUnit.MILLISECONDS.sleep(100);
                } while(subConnection == null);
            } catch (InterruptedException ignored) {
                logger.info("Interrupted waiting of connection, closing");
            }
            finally {
                return subConnection;
            }
        }
    }

}
