package nju.java.tools;

import nju.java.creatures.Creatures;
import nju.java.creatures.bad.Bad;
import nju.java.creatures.Good.Good;
import static nju.java.tools.ConstantValue.*;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/***
 * @author cbcwestwolf
 * <br>
 * FileOperation类用于进行IO操作 <br>
 * FileOperation类由BackEnd类调用
 *
 * @see nju.java.BackEnd
 */
public class FileOperation {

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

    /**
     * 从打开的文件中得到一行字符
     * @return 打开文件中的字符串。如果文件读取到末尾，返回一个null值
     */
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

    /**
     * 向文件中写入交战记录
     * @param goodCreatures 正方列表
     * @param badCreatures 反方列表
     * @param deadCreatures 牺牲的角色列表
     * @throws FileNotFoundException 写入文件时找不到文件，会写入默认文件，并再交由调用者处理
     */
    public static synchronized void writeFile(ArrayList<Good> goodCreatures,
                                              ArrayList<Bad> badCreatures, ArrayList<Creatures> deadCreatures) throws IOException {

        // 寻找一个可用的文件
        if (writeFile == null) {
            Date now = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

            //String str = "save" + File.separator + simpleDateFormat.format(now) + SUFFIX;

//            URL url = FileOperation.class.getClassLoader().getResource(simpleDateFormat.format(now) + SUFFIX);
//            if( url.getFile() != null ) {
//
//                writeFile = new File(url.getFile());
//                writeFile.createNewFile();
//            }
//            else{
//                System.out.println("出错了");
//            }

            File directory = new File("save");
            if( ! directory.exists() ){
                directory.mkdir();
            }

            String str = "save" + File.separator + simpleDateFormat.format(now) + SUFFIX;
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

        }
        catch (IOException e) {
            e.printStackTrace();
            throw new  IOException();
        }
    }

}
