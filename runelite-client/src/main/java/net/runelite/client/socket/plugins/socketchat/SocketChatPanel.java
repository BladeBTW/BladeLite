//package net.runelite.client.plugins.socket.plugins.socketchat;
//
//import net.runelite.api.Client;
//import net.runelite.api.Friend;
//import net.runelite.api.GameState;
//import net.runelite.api.Ignore;
//import net.runelite.client.RuneLiteProperties;
//import net.runelite.client.ui.ClientToolbar;
//import net.runelite.client.ui.ColorScheme;
//import net.runelite.client.ui.PluginPanel;
//import javax.inject.Inject;
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Date;
//
//public class SocketChatPanel extends PluginPanel {
//
//    @Inject
//    private Client client;
//    @Inject
//    private SocketChatPlugin socketChatPlugin;
//    @Inject
//    private SocketChatConfig socketChatConfig;
//
//    @Inject
//    private ClientToolbar clientToolbar;
//
//    @com.google.inject.Inject
//    private RuneLiteProperties runeLiteProperties;
//
//    private JButton chatButton = new JButton("Export Todays Chat");
//
//
//    private JLabel message = new JLabel("<html><center><h3>Chat Exporter</h3>This button will export the chat logs from current session of socket chat.<br></center></html>");
//    void init(){
//
//        getParent().setLayout(new FlowLayout());
//        getParent().add(this, BorderLayout.CENTER);
//
//        setLayout(new BorderLayout());
//        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//        setBackground(ColorScheme.DARK_GRAY_COLOR);
//
//        JPanel parentContainer = new JPanel();
//        JPanel buttons = new JPanel();
//        parentContainer.setLayout(new BorderLayout());
//        buttons.setLayout(new BorderLayout());
//        buttons.setBackground(ColorScheme.DARKER_GRAY_COLOR);
//        parentContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
//
//        JPanel friendFrame = new JPanel();
//        JPanel ignoreFrame = new JPanel();
//
//        chatButton.addActionListener((ActionEvent e) ->
//                {
//                    if ((client.getGameState() == GameState.LOGGED_IN))
//                    {
//                        final String fileLocation = System.getProperty("user.home") + "\\Desktop\\";
//                        try{
//                            FileWriter fw;
//                            String filePath = "";
//                            Date date = new Date();
//                            if(socketChatConfig.getFreedomUnits()){
//                                filePath = socketChatPlugin.formatterrrr.format(date) + "_ChatLogs.txt";
//
//                            } else {
//                                filePath = socketChatPlugin.formatterrrrr.format(date) + "_ChatLogs.txt";
//
//                            }
//                            fw = new FileWriter(fileLocation + filePath);
//                            for(String msg : socketChatPlugin.getChatMsgs()){
//                                fw.write(msg + "\r\n");
//                            }
//                            fw.close();
//                        } catch(IOException exception){
//                            exception.printStackTrace();
//
//                        }
//
//
//                    }
//                }
//        );
//        friendFrame.add(chatButton);
//        parentContainer.add(friendFrame, BorderLayout.NORTH);
//        parentContainer.add(ignoreFrame, BorderLayout.SOUTH);
//        add(message, BorderLayout.PAGE_START);
//        add(parentContainer, BorderLayout.CENTER);
//    }
//
//
//}
