package Backend;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.generic.Type;
import ui.Main;

import javax.lang.model.type.ArrayType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassObj {
    String name;
    private List<Field> temp = new ArrayList<>();
    Map<ClassObj, Integer> fields = new HashMap<>();
    List<String> methods = new ArrayList<>();
    ;
    ClassObj superClass;
    List<InterfaceObj> interfaces = new ArrayList<>();
    List<ClassObj> staticCalls = new ArrayList<>(); // todo

    public ClassObj(String name, List<Field> fields, List<String> methods, String superclass, String[] interfaces) {
        this.name = name;
        this.temp = fields;
        this.methods = methods;
        if (superclass != null) {
            this.superClass = Main.classes.get(superclass);
        }
        for (String i : interfaces) {
            this.interfaces.add(Main.interfaces.get(i));
        }
    }

    public ClassObj(String name) {
        this.name = name;
    }

    public void insertField(ClassObj key) {
        if (fields.containsKey(key)) {
            int freq = fields.get(key);
            freq++;
            fields.put(key, freq);
        }
        fields.put(key, 1);
    }

    public void setFields() {
        Boolean found = false;
        for (Field f : temp) {
            if (f == null) {
                continue;
            }
            String t = f.getType().toString();
            if (Main.classes.containsKey(t)) {
                found = true;
                if (fields.containsKey(Main.classes.get(t))) {
                    int freq = fields.get(Main.classes.get(t));
                    freq++;
                    fields.put(Main.classes.get(t), freq);
                } else {
                    fields.put(Main.classes.get(t), 1);
                }
            } else if (Main.interfaces.containsKey(t)) {
                found = true;
                if (fields.containsKey(Main.interfaces.get(t))) {
                    int freq = fields.get(Main.interfaces.get(t));
                    freq++;
                    fields.put(Main.interfaces.get(t), freq);
                } else {
                    fields.put(Main.interfaces.get(t), 1);
                }
            } else if (Main.abstractClasses.containsKey(t)) {
                found = true;
                if (fields.containsKey(Main.abstractClasses.get(t))) {
                    int freq = fields.get(Main.abstractClasses.get(t));
                    freq++;
                    fields.put(Main.abstractClasses.get(t), freq);
                } else {
                    fields.put(Main.abstractClasses.get(t), 1);
                }
            }
            if (found || f.getGenericSignature() == null) {
                continue;
            }
            String s = f.getGenericSignature().replace('/', '.');
            if (s.contains("<") || f.getType().getClass().equals(ArrayType.class)) {
                for (String key : Main.classes.keySet()) {
                    if (s.contains(key)) {
                        this.insertField(Main.classes.get(key));
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    for (String key : Main.interfaces.keySet()) {
                        if (s.contains(key)) {
                            this.insertField(Main.interfaces.get(key));
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    for (String key : Main.abstractClasses.keySet()) {
                        if (s.contains(key)) {
                            this.insertField(Main.abstractClasses.get(key));
                            found = true;
                            break;
                        }
                    }
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
        for (ClassObj f : fields.keySet()) {
            fieldsOutput = fieldsOutput.concat(f.getName() + " - " + fields.get(f) + ", ");
        }
        System.out.println(indent + fieldsOutput);
        if (this.superClass != null) {
            System.out.println(indent + "Superclass: " + this.superClass.getName());
        }
        String interfaceOutput = "Interfaces: ";
        for (InterfaceObj i : interfaces) {
            interfaceOutput.concat(i.getName() + " , ");
        }
        System.out.println(indent + interfaceOutput);
    }
}
