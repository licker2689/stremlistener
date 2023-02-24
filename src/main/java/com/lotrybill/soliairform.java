package com.lotrybill;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.apache.commons.configuration.EnvironmentConfiguration;


import javax.swing.*;


import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;


import java.util.Properties;


public class soliairform {
    private static String[] BROADCASTER_IDS;

    private static String[] STREAMER_IDS;
    private JPanel panel1;

    private JButton move;
    private JTextField url;
    private JButton register;
    private JTextField name;
    private JComboBox<String> comboBox1;
    private JComboBox comboBox2;

    private String link;
    private static TwitchClient tc;

    private static final String FILE_PATH = "config.properties";

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
        Properties prop = new Properties();
        InputStream input = null;
        OutputStream output = null;
        JFrame frame = new JFrame("App");
        frame.setContentPane(new soliairform().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        Properties pr = new Properties(); // config testing
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("wss://realtime.afreecatv.com/app")
                .build();
        try {

            input = new FileInputStream(FILE_PATH);

            // 프로퍼티 파일 로드
            prop.load(input);

            // 프로퍼티 값 읽기

            // 프로퍼티 값 설정

            output = new FileOutputStream(FILE_PATH);

            // 프로퍼티 파일 저장
            prop.store(output, null);

        } catch (IOException ex) {
            // 프로퍼티 파일이 없는 경우 기본값으로 파일 생성
            try {
                output = new FileOutputStream(FILE_PATH);

                prop.store(output, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        WebSocketListener listener = new WebSocketListener() {
            JComboBox comboBox1 = new soliairform().comboBox1;
            @Override
            public void onOpen(WebSocket webSocket, okhttp3.Response response) {
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
                    if (status.equals("true")){
                        comboBox1.addItem(broadcasterId);
                    } else if (status.equals("false")) {
                        try {
                            comboBox1.removeItem(broadcasterId);
                        } catch (NullPointerException e){
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
            public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
                // WebSocket 통신이 실패한 경우 이 부분에 로직 추가
            }
        };

        WebSocket webSocket = client.newWebSocket(request, listener);
        JComboBox comboBox1 = new soliairform().comboBox1;
        tc = TwitchClientBuilder.builder()
                .withDefaultEventHandler(SimpleEventHandler.class)
                .withEnableHelix(true)
                .withClientId("0sakrzch56upazgqannei8xvfl8b14")
                .withClientSecret("5ss9kojkeunzwmktdazrocf4plhv8g")
                .build();
        for (String broadcasterId : STREAMER_IDS) {
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
}






