package mni.api;

import java.sql.*;
import java.util.ArrayList;

public class DBUtils {
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:~/test";
    private static final String USER = "ek";
    private static final String PASS = "";
    private static Connection connection;
    private static Statement statement;

    private DBUtils(){}

    public static void initializeDB(){
        try{
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL,USER,PASS);
            statement = connection.createStatement();
            System.out.println("Database initialized!");
        }catch(SQLException | ClassNotFoundException ex){
            System.out.println(ex.getMessage());
        }
    }

    public static void updateStatement(String query){
        try {
            statement.addBatch(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void executeStatement(){
        try {
            System.out.println("Executing Statement.");
            int []result = statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<WordPoint> getDBdataAsArrayList(){
        initializeDB();
        ArrayList<WordPoint> list = new ArrayList<>();
        try {
            ResultSet resultSet = statement.executeQuery("select * from MESSAGE");
            while(resultSet.next()){
                WordPoint wordPoint = new WordPoint();
                wordPoint.setName(resultSet.getString(1));
                wordPoint.setX(resultSet.getInt(2));
                wordPoint.setY(resultSet.getInt(3));
                list.add(wordPoint);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeDB();
        }
        return list;
    }

    public static void deleteCreateTable(String sql, String message){
        try {
            statement = connection.createStatement();
            statement.execute(sql);
            System.out.println(message);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeDB(){
        try {
            statement.close();
            connection.close();
            System.out.println("Database closed!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isMessageTableExist(String tableName) throws SQLException {
        DatabaseMetaData dbMetaData = connection.getMetaData();
        ResultSet rsTables = dbMetaData.getTables(null, null, null, new String[] {"TABLE"});
        while(rsTables.next()){
            if(rsTables.getString("TABLE_NAME").equals("MESSAGE")){
                return true;
            }
        }
        return false;
    }

    public static void prepareTable(String tableName){
        initializeDB();
        try {
            if(isMessageTableExist(tableName)){
                deleteTable(tableName);
            }
            createTable(tableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeDB();
        }
    }

    public static void createTable(String tableName){
        String sql = "CREATE TABLE " + tableName + " " +
                "(word VARCHAR(255), " +
                " xCoord INTEGER, " +
                " yCoord INTEGER)";
        deleteCreateTable(sql, "Table \"" + tableName + "\" created!");
    }

    public static void deleteTable(String tableName){
        deleteCreateTable("drop table \"" + tableName + "\"", "Table \"" + tableName + "\" deleted!");
    }

    public static Connection getConnection(){return connection; }
    public static Statement getStatement() {return statement; }
}
