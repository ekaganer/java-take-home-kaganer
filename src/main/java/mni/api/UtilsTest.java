package mni.api;

import org.junit.Test;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class UtilsTest {

    @Test
    public void testFileContent(){
        try {
            byte [] array = Utils.getFileContent("test.dat");
            assertTrue(array != null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDBConnetion(){
        DBUtils.initializeDB();
        boolean init = false;
        if(DBUtils.getConnection() != null && DBUtils.getStatement() != null)
            init = true;
        assertTrue(init == true);
        DBUtils.closeDB();
    }

    @Test
    public void isMessageTableExistTest(){
        try {
            DBUtils.initializeDB();
            assertTrue(DBUtils.isMessageTableExist("MESSAGE") == true);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtils.closeDB();
        }
    }

    @Test
    public void prepareTableTest(){
        DBUtils.prepareTable("MESSAGE");
        try {
            DBUtils.initializeDB();
            assertTrue(DBUtils.isMessageTableExist("MESSAGE") == true);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtils.closeDB();
        }
    }

    @Test
    public void getDBdataAsArrayListTest(){
        ArrayList<WordPoint> list = DBUtils.getDBdataAsArrayList();
        assertTrue(list != null);
    }
}