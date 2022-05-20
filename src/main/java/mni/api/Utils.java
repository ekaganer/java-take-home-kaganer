package mni.api;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    private static int messageLength, checkedSum;

    private Utils(){}

    public static byte[] getFileContent(String fileName) throws IOException {
        File file = new File(fileName);

        return Files.readAllBytes(file.toPath());
    }

    public static void parseFile(byte [] fileContent) throws IOException {
        int i = 0;
        int x;
        int y;
        DBUtils.initializeDB();
        File csvFile = new File("checksum_errors.csv");
        DataOutputStream output = new DataOutputStream(new FileOutputStream(csvFile));

        while(i < fileContent.length){//Processing the file. Good messages saved to DB bad ro csv file.
            messageLength = getMessageLength(i, fileContent);
            checkedSum = getCheckSum(i , i + messageLength - 1, fileContent);
            if(checkedSum == (fileContent[i + messageLength - 1]  & 0xFF)){
                x = getCoordinate(i + messageLength - 9, fileContent);
                y = getCoordinate(i + messageLength - 5, fileContent);
                DBUtils.updateStatement("insert into MESSAGE(word, xCoord, yCoord) values('" +
                        new String(Arrays.copyOfRange(fileContent, i + 2, i + messageLength - 9)) +
                        "', " + x + ", " + y + ")");
            }else{
                output.writeChars(i + ", " + new String(Arrays.copyOfRange(fileContent, i + 2,
                        i + messageLength - 9)) + ",");
                output.writeChars(checkedSum + ",");
                output.writeChars((fileContent[i + messageLength - 1]  & 0xFF) + ",");
                output.writeChars("\n");
            }
            i += messageLength;
        }
        DBUtils.executeStatement();
        DBUtils.closeDB();
        output.close();
    }

    public static void processFile(InputStream input) throws IOException {
        ArrayList<Character> list = new ArrayList<>();
        DBUtils.initializeDB();
        File csvFile = new File("checksum_errors.csv");
        DataOutputStream output = new DataOutputStream(new FileOutputStream(csvFile));
        while (input.available() > 0) {
            parseNewMessage(list, input);
            if(checkedSum == (list.get(list.size() - 1)  & 0xFF)){
                updateStatement(list);
            }else{
                saveIncorrectMessages(list, output);
            }
            list.clear();
        }
        DBUtils.executeStatement();
        DBUtils.closeDB();
        output.close();
    }

    private static void parseNewMessage(ArrayList<Character> list, InputStream input) throws IOException {
        for(int i = 0; i < 2; ++i)
            list.add((char)input.read());
        messageLength = getMessageLength(list.subList(0, 2));
        for(int i = 0; i < messageLength - 2; ++i) {
            list.add((char) input.read());
        }
        checkedSum = getCheckSum(list);
    }

    private static void saveIncorrectMessages(ArrayList<Character> list, DataOutputStream output){
        try {
            output.writeChars(getString(list.subList(2, messageLength - 9)) + ",");
            output.writeChars(checkedSum + ",");
            output.writeChars((list.get(list.size() - 1)  & 0xFF) + ",");
            output.writeChars("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateStatement(ArrayList<Character> list){
        int x = getCoordinate(list.subList(list.size() - 9, list.size() - 5));
        int y = getCoordinate(list.subList(list.size() - 5, list.size() - 1));
        DBUtils.updateStatement("insert into MESSAGE(word, xCoord, yCoord) values('" +
                getString(list.subList(2, messageLength - 9)) +
                "', " + x + ", " + y + ")");
    }

    public static int getMessageLength(int i, byte[] fileContent){
        return ((fileContent[i] & 0xff) << 8) | (fileContent[i + 1] & 0xff);
    }

    public static int getMessageLength(List<Character> list){
        return ((list.get(0) & 0xff) << 8) | (list.get(1) & 0xff);
    }

    public static int getCoordinate(int i, byte[] fileContent){
        return ((fileContent[i] & 0xFF) << 24) | ((fileContent[i + 1] & 0xFF) << 16)
                | ((fileContent[i + 2] & 0xFF) << 8) | (fileContent[i + 3] & 0xFF);
    }

    public static int getCoordinate(List<Character> list){
        return ((list.get(0) & 0xFF) << 24) | ((list.get(0 + 1) & 0xFF) << 16)
                | ((list.get(0 + 2) & 0xFF) << 8) | (list.get(0 + 3) & 0xFF);
    }

    public static int getCheckSum(int initIndex, int messageLength, byte [] fileContent){
        int sum = 0;
        for(int i = initIndex; i < messageLength; ++i){
            sum += fileContent[i] & 0xFF;
        }
        return sum % 255;
    }

    public static int getCheckSum(List<Character> list){
        int sum = 0;
        for(int i = 0; i < list.size() - 1; ++i){
            sum += list.get(i) & 0xFF;
        }
        return sum % 255;
    }

    public static void setDistanceValue(int xRef, int yRef, ArrayList<WordPoint> list){
        for(WordPoint wp : list){
            wp.setDistance(Math.sqrt((xRef - wp.getX()) * (xRef - wp.getX()) + (yRef - wp.getY()) * (yRef - wp.getY())));
        }
    }

    private static String getString(List<Character> list){
        return list.stream().map(String::valueOf).collect(Collectors.joining());
    }
}
