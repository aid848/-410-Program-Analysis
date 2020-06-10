package Backend;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.io.*;
import java.util.*;

// Exporter class from backend data structure to json for front end
public class Exporter {

    public List<Tuple> interfaces;
    public List<Tuple> classes;
    private Hashtable<String, Integer> ids;
    private int count;
    private Map<Integer, Boolean> allowed;
    // TODO check sources and target fields for existance in the written nodes

    public Exporter(Map<String, InterfaceObj> interfaces, Map<String, ClassObj> classes) {

        this.interfaces = new ArrayList<>();
        this.classes = new ArrayList<>();
        allowed = new HashMap<>();
        ids = new Hashtable<>();
        count = 1;

        for (Map.Entry<String, InterfaceObj> entry: interfaces.entrySet()) {
            try {
                this.interfaces.add(new Tuple(entry.getKey(), entry.getValue()));
            } catch (Exception b) {
                b.printStackTrace();
            }

        }
        for(Map.Entry<String, ClassObj> entry: classes.entrySet()){
            try {
                this.classes.add(new Tuple(entry.getKey(), entry.getValue()));
//                idx++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void writeToJson() {
//        JFrame outside = new JFrame();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select save location");
        int good = fileChooser.showSaveDialog(null);
        if (good == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String directory = file.getAbsolutePath();
            System.out.println(directory + ".json");
//            outside.dispose();
            try {
                write(directory);
            }catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        } else {
            System.out.println("Save error");
        }
    }

    private void write(String directory) throws IOException {
        JSONObject outer = new JSONObject();
        JSONArray nodes = new JSONArray();
        JSONArray links = new JSONArray();
        writeNodes(nodes);
        writeLinks(links);


        outer.put("nodes", nodes);
        outer.put("links",links);
        FileWriter f = new FileWriter(new File(directory+".json"));
        outer.writeJSONString(f);
        f.close();
    }

    private void writeLinks(JSONArray links) {
        writeLinksHelper(links, interfaces);
        writeLinksHelper(links, classes);
    }

    private void writeLinksHelper(JSONArray links, List<Tuple> t) {
        for(Tuple tup: t) {
            try {
                if (tup.value.temp != null) {
                    for (String s : tup.value.temp) {
                        writeLink(links, tup.id, ids.get(s), "field");
                    }
                }
                if (tup.value.interfaces != null) {
                    for (InterfaceObj in : tup.value.interfaces) {
                        if (in != null) {
                            writeLink(links, tup.id, ids.get(in.name), "interface");
                        }
                    }
                }
                if (tup.value.superClass != null) {
                    // todo what about recursive inheritance?
                    writeLink(links, tup.id, ids.get(tup.value.superClass.name), "super class");
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void writeLink(JSONArray links, int src, int dest, String type) {

        if(!allowed.containsKey(src) || !allowed.containsKey(dest)) {
            return;
        }
        JSONObject ob = new JSONObject();
        ob.put("source", src);
        ob.put("target", dest);
        ob.put("type", type);
        links.add(ob);
    }

    private void writeNodes(JSONArray nodes) {
        for(Tuple t: interfaces) {
            try {
                JSONObject ob = new JSONObject();
                ob.put("name", t.value.name);
                ob.put("label", "interface");
                ob.put("id", t.id);
                nodes.add(ob);
                allowed.put(t.id, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for(Tuple t: classes) {
            try {
                JSONObject ob = new JSONObject();
                ob.put("name", t.value.name);
                ob.put("label", "class");
                ob.put("id", t.id);
                nodes.add(ob);
                allowed.put(t.id, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int hashClass(String in) {
        if (!ids.contains(in)) {
            ids.put(in, count);
            count++;
        }
        return ids.get(in);
    }

    private class Tuple {
        public String key;
        public ClassObj value;
        public int id;
        public Tuple(String key, ClassObj value){
            this.key = key;
            this.value = value;
            this.id = hashClass(key);
        }
    }

}


