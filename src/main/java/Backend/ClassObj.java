package Backend;

import ui.Main;

import java.util.ArrayList;
import java.util.List;

public class ClassObj {
    String name;
    List<String> temp;
    List<ClassObj> fields = new ArrayList<>();
    List<String> methods;
    ClassObj superClass;
    List<InterfaceObj> interfaces = new ArrayList<>();

    public ClassObj(String name, List<String> fields, List<String> methods, String superclass, String[] interfaces) {
        this.name = name;
        this.temp = fields;
        this.methods = methods;
        if (superclass != null) {
            this.superClass = Main.classes.get(superclass);
        }
        for (String i: interfaces) {
            this.interfaces.add(Main.interfaces.get(i));
        }
    }

    public void setFields() {
        if (temp == null) {
            return;
        }
        for (String t: temp) {
            if (Main.classes.containsKey(t) && Main.classes.get(t) != null) {
                fields.add(Main.classes.get(t));
            } else if (Main.interfaces.containsKey(t) && Main.interfaces.get(t) != null) {
                fields.add(Main.interfaces.get(t));
            }
        }
    }

    public List<String> getMethods() {
        return methods;
    }

    public List<InterfaceObj> getInterfaces() {
        return interfaces;
    }

    public String getName() {
        return name;
    }

    public void print() {
        System.out.println("Class name: " + name);
        if (methods != null) {
            System.out.println("Methods: " + methods);
        }
        System.out.println("Fields: ");
        for (ClassObj f: fields) {
            System.out.println(f.getName());
        }
        System.out.println("Interfaces: " + interfaces);
    }
}
