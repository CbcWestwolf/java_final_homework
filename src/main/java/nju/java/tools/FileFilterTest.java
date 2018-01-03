package nju.java.tools;

import javax.swing.*;
import java.io.File;

/***
 * @author cbcwestwolf
 * <br>
 * FileFilerTest类用于筛选能打开的文件类型
 *
 * @see nju.java.Ground
 */
public class FileFilterTest extends javax.swing.filechooser.FileFilter{
    public boolean accept(java.io.File f) {
        if (f.isDirectory())return true;
        return f.getName().endsWith(".fight");  //设置为选择以.class为后缀的文件
    }
    public String getDescription(){
        return ".fight";
    }
}
