package Backend;

import ui.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassObj {
    String name;
    private List<String> temp = new ArrayList<>();
    Map<ClassObj, Integer> fields = new HashMap<>();
    List<String> methods = new ArrayList<>();;
    ClassObj superClass;
    List<InterfaceObj> interfaces = new ArrayList<>();
    List<ClassObj> staticCalls = new ArrayList<>(); // todo

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

    public ClassObj(String name) {
        this.name = name;
    }

    public void setFieldFrequency() {
        for (String t: temp) {

        }
    }

    public void setFields() {
        for (String t: temp) {
            if (Main.classes.containsKey(t)) {
                if (fields.containsKey(Main.classes.get(t))) {
                    int freq = fields.get(Main.classes.get(t));
                    freq++;
                    fields.put(Main.classes.get(t), freq);
                } else {
                    fields.put(Main.classes.get(t), 1);
                }
            } else if (Main.interfaces.containsKey(t)) {
                if (fields.containsKey(Main.interfaces.get(t))) {
                    int freq = fields.get(Main.interfaces.get(t));
                    freq++;
                    fields.put(Main.interfaces.get(t), freq);
                } else {
                    fields.put(Main.classes.get(t), 1);
                }
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
        String indent = "    ";
        System.out.println("Class name: " + name);
        if (methods != null) {
            System.out.println(indent + "Methods: " + methods);
        }
        String fieldsOutput = "Fields: ";
        for (ClassObj f: fields.keySet()) {
            fieldsOutput = fieldsOutput.concat(f.getName() + " - " + fields.get(f) + ", ");
        }
        System.out.println(indent + fieldsOutput);
        if (this.superClass != null) {
            System.out.println(indent + "Superclass: " + this.superClass.getName());
        }
        String interfaceOutput = "Interfaces: ";
        for (InterfaceObj i: interfaces) {
            interfaceOutput.concat(i.getName() + " , ");
        }
        System.out.println(indent + interfaceOutput);
    }
}
