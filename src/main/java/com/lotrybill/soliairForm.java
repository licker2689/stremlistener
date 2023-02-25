package com.lotrybill;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import okhttp3.*;
import okio.ByteString;
import org.yaml.snakeyaml.Yaml;


import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;


import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;


import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;



public class soliairForm {

    private static String[] BROADCASTER_IDS;
    private static String[] STREAMER_IDS;
    private JPanel panel1;
    private JButton move;
    private JButton register;
    private JTextField name;
    private JComboBox<String> comboBox1;
    private JComboBox<String> comboBox2;
    public JPanel panel2;
    public JPanel panel3;
    public JPanel panel4;
    public JTextField textField1;
    private String link;
    private static TwitchClient tc;

    public soliairForm() {
        $$$setupUI$$$();

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
                RandomAccessFile rf = null;
                try {
                    rf = new RandomAccessFile(soliairForm.class.getClassLoader().getResource("setting.yml").getFile(), "rwd");
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    if (comboBox2.getSelectedItem().toString() == "afreeca") {
                        rf.writeChars(name.getText() + ":\n" +
                                "  platform: " +
                                comboBox2.getSelectedItem().toString() +
                                "\n" +
                                "  link: " +
                                textField1.getText() +
                                "\n" +
                                "  id: " +
                                textField1.getText().replace("https://bj.afreecatv.com/", "")
                                + "\n");
                    } else if (comboBox2.getSelectedItem().toString() == "twitch") {
                        rf.writeChars(name.getText() + ":\n" +
                                "  platform: " +
                                comboBox2.getSelectedItem().toString() +
                                "\n" +
                                "  link: " +
                                textField1.getText() +
                                "\n" +
                                "  id: " +
                                textField1.getText().replace("https://www.twitch.tv/", "")
                                + "\n");
                    }
                } catch (IOException exception) {
                    throw new RuntimeException(exception);
                }
            }
        });
    }

    public static String path = "https://yt3.ggpht.com/vepDzpQJAHTEizEEZ88T62hE-Ju14lIv_7oRbSDhz2cAl7aQ0aiwnSyFmATTWMUsWBm24Iy0Cfs=s68-c-k-c0x00ffffff-no-rj";

    public static void main(String args[]) throws IOException {
        try {
            // YAML 파일 로드
            InputStream inputStream = soliairForm.class.getClassLoader().getResource("setting.yml").openStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            // SnakeYAML 파서 생성
            Yaml yaml = new Yaml();

            // YAML을 Java 객체로 변환
            Map<String, Object> data = yaml.load(inputStream);

            String[] s = data.get("name").toString().replace("{", "").replace("}", "").split(", ");
            for (String s1 : s) {
                Map<String, Object> data1  = (Map) data.get(s1);
                if (data1.get("platform") == "twitch"){
                    tc.getClientHelper().enableStreamEventListener(data1.get("id").toString());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame("솔레어 방송 조회기");
        frame.setContentPane(new soliairForm().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setIconImage(ImageIO.read(new URL(path)));
        frame.setVisible(true);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("wss://realtime.afreecatv.com/app")
                .build();

        WebSocketListener listener = new WebSocketListener() {
            JComboBox<String> comboBox1 = new soliairForm().comboBox1;

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                for (String broadcasterId : BROADCASTER_IDS) {
                    String message = "{\"channel\":\"" + broadcasterId + "\",\"device\":\"pc\",\"type\":\"joinsession\"}";
                    webSocket.send(message);
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                // 방송 상태 변경 이벤트를 수신한 경우 로그에 해당 상태를 출력
                if (text.contains("\"type\":\"livechange\"")) {
                    int startIndex = text.indexOf("\"channel\":\"") + "\"channel\":\"".length();
                    int endIndex = text.indexOf("\"", startIndex);
                    String broadcasterId = text.substring(startIndex, endIndex);

                    startIndex = text.indexOf("\"live\":\"") + "\"live\":\"".length();
                    endIndex = text.indexOf("\"", startIndex);
                    String status = text.substring(startIndex, endIndex);

                    String logMessage = broadcasterId + " 방송 상태: " + (status.equals("true") ? "방송 중" : "방송 종료");
                    System.out.println(logMessage);
                    if (status.equals("true")) {
                        comboBox1.addItem(broadcasterId);
                    } else if (status.equals("false")) {
                        try {
                            comboBox1.removeItem(broadcasterId);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                // 바이트 스트림 메시지를 수신한 경우 이 부분에 로직 추가
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                // WebSocket이 종료될 때 이 부분에 로직 추가
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                // WebSocket 통신이 실패한 경우 이 부분에 로직 추가
            }
        };

        WebSocket webSocket = client.newWebSocket(request, listener);
        JComboBox<String> comboBox1 = new soliairForm().comboBox1;
        tc = TwitchClientBuilder.builder()
                .withDefaultEventHandler(SimpleEventHandler.class)
                .withEnableHelix(true)
                .withClientId("0sakrzch56upazgqannei8xvfl8b14")
                .withClientSecret("5ss9kojkeunzwmktdazrocf4plhv8g")
                .build();
        for (String broadcasterId : STREAMER_IDS) {
            System.out.println(broadcasterId);
            tc.getClientHelper().enableStreamEventListener(broadcasterId);
        }
        tc.getEventManager().setDefaultEventHandler(SimpleEventHandler.class);
        tc.getEventManager().getEventHandler(SimpleEventHandler.class).onEvent(ChannelGoLiveEvent.class, event -> {
                    comboBox1.addItem(event.getChannel().getName());
                }
        );
        tc.getEventManager().getEventHandler(SimpleEventHandler.class).onEvent(ChannelGoOfflineEvent.class, e -> {
            comboBox1.removeItem(e.getChannel().getName());
        });


    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel1.setBorder(BorderFactory.createTitledBorder(null, "솔레어 방송 조회기", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("NanumSquare ExtraBold", Font.BOLD, 12, panel1.getFont()), null));
        panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2);
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        comboBox1 = new JComboBox();
        comboBox1.setAutoscrolls(false);
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        comboBox1.setModel(defaultComboBoxModel1);
        panel4.add(comboBox1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        move = new JButton();
        move.setText("이동하기");
        panel4.add(move, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 5, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel3);
        panel3.setBorder(BorderFactory.createTitledBorder(null, "커스텀 등록", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final Spacer spacer2 = new Spacer();
        panel3.add(spacer2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        register = new JButton();
        register.setText("등록하기");
        panel3.add(register, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        name = new JTextField();
        name.setToolTipText("NAME");
        panel3.add(name, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        comboBox2 = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("twitch");
        defaultComboBoxModel2.addElement("afreeca");
        comboBox2.setModel(defaultComboBoxModel2);
        panel3.add(comboBox2, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textField1 = new JTextField();
        textField1.setText("");
        panel3.add(textField1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

}






