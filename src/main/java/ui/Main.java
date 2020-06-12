package ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import Backend.*;

public class Main {
    public static Map<String, InterfaceObj> interfaces = new HashMap<>();
    public static Map<String, ClassObj> classes = new HashMap<>();
    public static Map<String, ClassObj> abstractClasses = new HashMap<>(); // todo parser needs to modify this

    public static void main(String[] args) throws IOException, URISyntaxException {
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

//        interfaces.entrySet().forEach(entry->{
//            System.out.println(entry.getKey());
//            entry.getValue().print();
//        });
//
//        classes.entrySet().forEach(entry->{
//            System.out.println(entry.getKey());
//            if (entry.getValue() != null) {
//
//                entry.getValue().print();
//            }
//        });

        Exporter ex = new Exporter(interfaces,classes, abstractClasses);
        ex.writeToJson();
//        String os = System.getProperty("os.name").toLowerCase();
//        if(os.contains("win")) {
//            Runtime.getRuntime().exec("Frontend UI/winrun.bat");
//            Desktop.getDesktop().browse(new URI("http://127.0.0.1:8080/Frontend%20UI"));
//        }else { // todo the mac bit here needs work
//            String s = "/bin/bash http-server";
//            Runtime.getRuntime().exec(s);
//           Desktop.getDesktop().browse(new URI("http://127.0.0.1:8080/Frontend%20UI/"));
//        }
    }
}
