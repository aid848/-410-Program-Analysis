package ui;

import javax.swing.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import Backend.*;

public class Main {
    public static Map<String, InterfaceObj> interfaces = new HashMap<>();
    public static Map<String, ClassObj> classes = new HashMap<>();

    public static void main(String[] args) {
        Parser p = new Parser();

        // modified from https://mkyong.com/swing/java-swing-jfilechooser-example/
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            p.parseDir(file.getAbsolutePath());
        } else {
            System.out.println("no file chosen");
        }

       p.parseFields();

        interfaces.entrySet().forEach(entry->{
            entry.getValue().print();
        });

        classes.entrySet().forEach(entry->{
            entry.getValue().print();
        });

    }



}
