package nju.java.tools;

import nju.java.creatures.Creatures;
import nju.java.creatures.bad.Bad;
import nju.java.creatures.Good.Good;
import static nju.java.tools.ConstantValue.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/***
 * @author cbcwestwolf
 * <br>
 * This class is used to do IO operation for BackEnd class.
 * It is used in BackEnd class.
 *
 * @see nju.java.BackEnd
 */
public class FileOperation {

    private static int COUNT = 0;

    private static File defaultFile = new File("save"+File.separator+"default.fight");

    // 读写的文件
    private static File readFile = null;
    private static FileReader fileReader = null;
    private static BufferedReader bufferedReader = null;

    private static File writeFile = null;
    private static FileWriter fileWriter = null;
    private static BufferedWriter bufferedWriter = null;


    public static File getReadFile() {
        return readFile;
    }

    public static void setReadFile(File temp) {
        readFile = temp;

        try {
            fileReader = new FileReader(readFile);
            bufferedReader = new BufferedReader(fileReader);
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
    }

    public static synchronized String getNextString(){
        if( readFile == null )
            return null;

        String str = null;
        try {
            str = bufferedReader.readLine();


            if (str == null) {
                bufferedReader.close();
                fileReader.close();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return str;
    }

    public static synchronized void writeFile(ArrayList<Good> goodCreatures,
                                              ArrayList<Bad> badCreatures, ArrayList<Creatures> deadCreatures) throws FileNotFoundException {

        // 寻找一个可用的文件
        if (writeFile == null) {
            Date now = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            String str = "save" + File.separator + simpleDateFormat.format(now) + SUFFIX;
            // System.out.println(str);
            writeFile = new File(str);
        }


        // 把三个ArrayList中的对象都写进文件
        try {

            fileWriter = new FileWriter(writeFile, true);
            bufferedWriter = new BufferedWriter(fileWriter);

            ArrayList<String> str = new ArrayList<String>();

            if( ! goodCreatures.isEmpty())
                for (Good c : goodCreatures)
                    str.add(c.toString() + " " + c.getX() + " " + c.getY() + " " + 1);

            if( ! badCreatures.isEmpty() )
                for (Bad c : badCreatures)
                    str.add(c.toString() + " " + c.getX() + " " + c.getY() + " " + 1);

            if( ! deadCreatures.isEmpty() )
                for (Creatures c : deadCreatures)
                    str.add(c.toString() + " " + c.getX() + " " + c.getY() + " " + 0);


            for(String s : str){
                bufferedWriter.write(s);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
            bufferedWriter.close();
            fileWriter.close();

        } catch (FileNotFoundException e) {
            writeFile = defaultFile;
            try {
                fileWriter = new FileWriter(writeFile, true);
                bufferedWriter = new BufferedWriter(fileWriter);
            } catch (IOException ee) {
                ee.printStackTrace();
            }
            throw new FileNotFoundException("没有找到写入的文件");
        } catch (IOException e) {

        }
    }

}
