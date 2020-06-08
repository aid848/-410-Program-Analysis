package Backend;

import java.util.List;

public class InterfaceObj extends ClassObj{

    public InterfaceObj(String name, List<String> fields, List<String> methods, String superclass, String[] interfaces) {
        super(name, fields, methods, null, interfaces);
    }
}
