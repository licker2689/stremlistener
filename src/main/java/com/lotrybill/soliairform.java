package com.lotrybill;

import lombok.Value;
import org.apache.commons.configuration.EnvironmentConfiguration;
import org.springframework.core.env.Environment;

import javax.annotation.processing.RoundEnvironment;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;


public class soliairform {
    private JPanel panel1;

    private JButton move;
    private JTextField url;
    private JButton register;
    private JTextField name;
    private JComboBox comboBox1;
    private JComboBox comboBox2;

    private String link;

    static EnvironmentConfiguration erc = new EnvironmentConfiguration();

    public soliairform() {
        move.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (link != null) {
                    try {
                        Desktop.getDesktop().browse(new URI(link));
                    } catch (IOException er) {
                        er.printStackTrace();
                    } catch (URISyntaxException er) {
                        er.printStackTrace();
                    }
                } else {
                    JOptionPane aa = new JOptionPane();
                    aa.showMessageDialog(null, "원하는 멤버를 선택하고 다시 눌러주세요, 리스트에 없는 경우 해당 멤버가 방송을 켜지 않은 것입니다.");

                }
            }
        });
        comboBox1.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                Object item = comboBox1.getSelectedItem();
            }
        });
        register.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane aa = new JOptionPane();
                aa.showMessageDialog(null, "구현중인 기능입니다.");

            }
        });
    }

    public static void main(String args[]) throws IOException {
        JFrame frame = new JFrame("App");
        frame.setContentPane(new soliairform().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        Properties pr = new Properties();
        pr.setProperty("아이스크림", "집");
        pr.store(
    }

}



