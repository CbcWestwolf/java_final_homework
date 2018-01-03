package nju.java.tools;

import nju.java.creatures.Creatures;
import nju.java.creatures.bad.Bad;
import nju.java.creatures.good.Good;
import static nju.java.tools.ConstantValue.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by cbcwestwolf on 2018/1/3.
 */
public class FileOperation {

    // 读写的文件
    private static File readFile = null;
    private static File writeFile = null;
    private static File defaultFile = new File("save"+File.separator+"default.fight");
    private FileReader fileReader;
    private BufferedReader bufferedReader ;

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public void setBufferedReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    public FileReader getFileReader() {
        return fileReader;
    }

    public void setFileReader(FileReader fileReader) {
        this.fileReader = fileReader;
    }

    public File getReadFile() {
        return readFile;
    }

    public void setReadFile(File readFile) {
        this.readFile = readFile;
    }

    public static synchronized void writeFile(ArrayList<Good> goodCreatures,
                                        ArrayList<Bad> badCreatures, ArrayList<Creatures> deadCreatures) throws FileNotFoundException
    {
        // 寻找一个可用的文件
        if (writeFile == null){
            Date now = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            String str = "save"+ File.separator+simpleDateFormat.format(now)+SUFFIX;
            // System.out.println(str);
            writeFile = new File(str);
        }

        // 把三个ArrayList中的对象都写进文件
        try {
            FileWriter fileWriter = new FileWriter(writeFile,true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            ArrayList<Creatures> temp = new ArrayList<Creatures>();
            if( ! goodCreatures.isEmpty() )
                for( Creatures g : goodCreatures )
                    temp.add(g);
            if( ! badCreatures.isEmpty() )
                for( Creatures b : badCreatures )
                    temp.add(b);

            for( Creatures c : temp){
                bufferedWriter.write(c.toString()+" "+c.getX()+" "+c.getY()+" "+1 );
                bufferedWriter.newLine();
                //System.out.println(c.toString()+" "+c.getX()+" "+c.getY()+" "+true);
            }

            temp.clear();

            if( ! deadCreatures.isEmpty() )
                for( Creatures d : deadCreatures )
                    temp.add(d);
            for( Creatures c : temp){
                bufferedWriter.write(c.toString()+" "+c.getX()+" "+c.getY()+" "+ 0 );
                bufferedWriter.newLine();
                //System.out.println(c.toString()+" "+c.getX()+" "+c.getY()+" "+false);
            }
            bufferedWriter.close();
            fileWriter.close();
        }
        catch (FileNotFoundException e){
            writeFile = defaultFile;
            throw new FileNotFoundException("没有找到写入的文件");
        }
        catch (IOException e){

        }

    }
}
