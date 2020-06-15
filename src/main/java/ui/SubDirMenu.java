package ui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.*;
import java.util.List;

public class SubDirMenu extends JDialog
        implements ActionListener,
        PropertyChangeListener {
    Set<String> selected;
    public SubDirMenu(File[] subDirs) {
        setModal(true);
        selected = new HashSet<>();
        setPreferredSize(new Dimension(500,500));
        setTitle("Select packages to visualize");
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout((int)Math.ceil(Math.sqrt(subDirs.length)) +1, (int)Math.ceil(Math.sqrt(subDirs.length))));
        JRadioButton[] buttons = createButtons(subDirs);
        for(JRadioButton j: buttons){
            add(j);
        }
        JButton close = new JButton("Close");
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        add(close);

    }

    private JRadioButton[] createButtons(File[] subDirs) {
        JRadioButton[] out = new JRadioButton[subDirs.length];
        int idx = 0;
        for(File f: subDirs){
            String s = f.getPath();
            JRadioButton j = new JRadioButton(s);
            j.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
//                    System.out.println(e.getActionCommand());
                    selected.add(e.getActionCommand());
                }
            });
            out[idx] = j;
            idx++;
        }
        return out;
    }

    public List<String> getSelected() {
        return Arrays.asList((selected.toArray(new String[]{})));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }


}




