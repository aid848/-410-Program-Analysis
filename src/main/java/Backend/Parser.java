package Backend;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import ui.Main;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Parser {
    private final String[] primitives = {"boolean", "byte", "char", "short", "int", "long", "float", "double"};

    public void parseDir(String dirPath) {
        // modified from https://stackoverflow.com/questions/4917326/how-to-iterate-over-the-files-of-a-certain-directory-in-java
        File dir = new File(dirPath);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                if (child.isDirectory()) {
                    parseDir(child.getAbsolutePath());
                }
                if ((child.getName().substring(child.getName().lastIndexOf(".")+1)).equals("class")) {
                    try {
                        // sourced from https://www.programcreek.com/java-api-examples/index.php?api=org.apache.bcel.classfile.ClassParser
                        byte[] b = new byte[(int) new File(child.getAbsolutePath()).length()];
                        InputStream in = new FileInputStream(child.getAbsolutePath());
                        new DataInputStream(in).readFully(b);
                        in.close();

                        ClassParser cp = new ClassParser(new ByteArrayInputStream(b), child.getName());
                        JavaClass c = cp.parse();
                        parseClass(c);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        } else if(dir.isFile()){
            if (dir.getName().endsWith(".jar")) {
                try {
                    new File("temp").mkdir();
                    new ZipFile(dir).extractAll("temp");
                } catch (ZipException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Unable to extract directory");
                }
                File[] subDirs = new File("temp").listFiles();
                // todo call ui
                String[] selected = {"temp\\interfaces", "temp\\entities"};
                for (String sub: selected){
                    parseDir(sub);
                }
                try {
                    Files.walk(Paths.get(dir.getPath()))
                            .map(Path::toFile)
                            .sorted((o1, o2) -> -o1.compareTo(o2))
                            .forEach(File::delete);
                }catch (Exception e){
                    throw new RuntimeException("failed to delete temp directory");
                }
            }else {
                throw new RuntimeException("only jar files supported");
            }
        }else {
            throw new RuntimeException("Invalid Directory");
        }
    }

    private void parseClass(JavaClass c) {
        ClassObj classObj = null;
        InterfaceObj interfaceObj = null;
        List<String> methods = new ArrayList<>();
        List<Field> fields = new ArrayList<>();
        for (Method method: c.getMethods()) {
            methods.add(method.getName());
        }
        for (String i: c.getInterfaceNames()) {
            if (!Main.interfaces.containsKey(i)) {
                Main.interfaces.put(i, new InterfaceObj(i));
            }
        }
        for (Field f: c.getFields()) {
            fields.add(f);
        }
        if (c.isInterface()) {
            interfaceObj = new InterfaceObj(c.getClassName(), fields, methods, null, c.getInterfaceNames());
            Main.interfaces.put(interfaceObj.getName(), interfaceObj);
        } else {
            if (!c.getSuperclassName().equals("java.lang.Object") && !Main.classes.containsKey(c.getSuperclassName())) {
                Main.classes.put(c.getSuperclassName(), new ClassObj(c.getSuperclassName()));
            }
            if (c.isAbstract()) {
                classObj = new ClassObj(c.getClassName(), fields, methods, c.getSuperclassName(), c.getInterfaceNames());
                if (Main.classes.containsKey(c.getClassName())) {
                    Main.classes.put(c.getClassName(), classObj);
                    Main.abstractClasses.put(classObj.getName(), Main.classes.get(c.getClassName()));
                    Main.classes.remove(c.getClassName());
                } else {
                    Main.abstractClasses.put(classObj.getName(), classObj);
                }
            } else {
                classObj = new ClassObj(c.getClassName(), fields, methods, c.getSuperclassName(), c.getInterfaceNames());
                Main.classes.put(classObj.getName(), classObj);
            }
        }
    }

    public void parseFields() {
        Main.classes.entrySet().forEach(entry->{
            if (entry.getValue() != null) {
                entry.getValue().setFields();
            }
        });

        Main.interfaces.entrySet().forEach(entry->{
            if (entry.getValue() != null) {
                entry.getValue().setFields();
            }
        });

        Main.abstractClasses.entrySet().forEach(entry->{
            if (entry.getValue() != null) {
                entry.getValue().setFields();
            }
        });

    }

}


