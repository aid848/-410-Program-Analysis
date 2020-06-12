package Backend;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.generic.Type;

import java.util.List;

public class InterfaceObj extends ClassObj{

    public InterfaceObj(String name, List<Field> fields, List<String> methods, String superclass, String[] interfaces) {
        super(name, fields, methods, null, interfaces);
    }

    public InterfaceObj(String name) {
        super(name);
    }
}
