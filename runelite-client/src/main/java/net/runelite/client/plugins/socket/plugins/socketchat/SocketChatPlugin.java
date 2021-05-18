//package net.runelite.client.plugins.socket.plugins.socketchat;
//
//import com.google.inject.Provides;
//import lombok.Getter;
//import net.runelite.api.*;
//import net.runelite.api.coords.WorldPoint;
//import net.runelite.api.events.*;
//import net.runelite.api.widgets.Widget;
//import net.runelite.api.widgets.WidgetInfo;
//import net.runelite.client.callback.ClientThread;
//import net.runelite.client.chat.*;
//import net.runelite.client.config.ConfigManager;
//import net.runelite.client.eventbus.EventBus;
//import net.runelite.client.eventbus.Subscribe;
//import net.runelite.client.input.KeyListener;
//import net.runelite.client.input.KeyManager;
//import net.runelite.client.plugins.Plugin;
//import net.runelite.client.plugins.PluginDescriptor;
//import net.runelite.client.plugins.friendlistexporter.FriendListExportPanel;
//import net.runelite.client.plugins.keyremapping.KeyRemappingPlugin;
//import net.runelite.client.plugins.socket.SocketPlugin;
//import net.runelite.client.plugins.socket.org.json.JSONArray;
//import net.runelite.client.plugins.socket.org.json.JSONObject;
//import net.runelite.client.plugins.socket.packet.SocketBroadcastPacket;
//import net.runelite.client.plugins.socket.packet.SocketReceivePacket;
//import net.runelite.client.ui.ClientToolbar;
//import net.runelite.client.ui.NavigationButton;
//import net.runelite.client.ui.overlay.OverlayManager;
//import javax.imageio.ImageIO;
//import javax.inject.Inject;
//import java.awt.event.KeyEvent;
//import java.awt.image.BufferedImage;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.Executors;
//
//
//@PluginDescriptor(
//        name = "[S] Socket - Socket Chat",
//        description = "Private chat within your socket",
//        tags = {"socket", "server", "discord", "connection", "broadcast", "chat", "private", "can i say nigger"},
//        enabledByDefault = false
//)
//public class SocketChatPlugin extends Plugin implements KeyListener {
//
//    @Inject
//    private Client client;
//
//    @Inject
//    private OverlayManager overlayManager;
//
//    @Inject
//    private ClientThread clientThread;
//
//    @Inject
//    private EventBus eventBus;
//
//    @Inject
//    private KeyManager keyManager;
//
//    @Inject
//    private SocketChatConfig config;
//
//    @Inject
//    private ClientToolbar clientToolbar;
//
//    private NavigationButton navButton;
//
//
//    private KeyRemappingPlugin keyRemappingPlugin;
//    @Inject
//    private ChatMessageManager chatMessageManager;
//    @Provides
//    SocketChatConfig getConfig(ConfigManager configManager) {
//        return configManager.getConfig(SocketChatConfig.class);
//    }
//
//    private Map<String, String> iconOptions = new HashMap<>();
//    private String msg;
//    private Widget x = null;
//    private boolean typing;
//    boolean chatLocked;
//    @Getter
//    private ArrayList<String> chatMsgs = new ArrayList<>();
//
//    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM");
//    SimpleDateFormat formatterr = new SimpleDateFormat("HH:mm");
//    SimpleDateFormat formatterrr = new SimpleDateFormat("MM/dd"); //freedom units
//    SimpleDateFormat formatterrrr = new SimpleDateFormat("MM-dd-YY"); //freedom units
//    SimpleDateFormat formatterrrrr = new SimpleDateFormat("dd-MM-YY"); //freedom units
//
//
//    private void populateMap(){
//        iconOptions.put("", "Blank");
//        iconOptions.put("<img=0>", "P-Mod Crown");
//        iconOptions.put("<img=1>", "J-Mod Crown");
//        iconOptions.put("<img=2>", "Ironman");
//        iconOptions.put("<img=3>", "Ultimate Ironman");
//        iconOptions.put("<img=4>", "BH Red");
//        iconOptions.put("<img=5>", "BH Blue");
//        iconOptions.put("<img=6>", "BH Green");
//        iconOptions.put("<img=7>", "BH Grey");
//        iconOptions.put("<img=8>", "BH Orange");
//        iconOptions.put("<img=9>", "Skull");
//        iconOptions.put("<img=10>", "Hardccore Ironman");
//        iconOptions.put("<img=11>", "Stop Sign");
//        iconOptions.put("<img=12>", "Weird symbol");
//        iconOptions.put("<img=13>", "Weird symbol2");
//        iconOptions.put("<img=14>", "Silver Dot");
//        iconOptions.put("<img=15>", "Information");
//        iconOptions.put("<img=16>", "Tree");
//        iconOptions.put("<img=17>", "Pickaxe");
//        iconOptions.put("<img=18>", "Fish");
//        iconOptions.put("<img=19>", "Speech Bubble");
//        iconOptions.put("<img=20>", "Black Trophy");
//        iconOptions.put("<img=21>", "Gravestone");
//        iconOptions.put("<img=22>", "Shitty Black Square");
//    }
//
//    @Override
//    protected void startUp() throws Exception {
//        chatLocked = false;
//        typing = false;
//        populateMap();
//        this.keyManager.registerKeyListener(this);
//        msg = null;
//        x = null;
//        chatMsgs.clear();
//        final SocketChatPanel panel = injector.getInstance(SocketChatPanel.class);
//        panel.init();
//
//        final BufferedImage icon;
//        synchronized (ImageIO.class)
//        {
//            icon = ImageIO.read(SocketPlugin.class.getResourceAsStream("Notepad-icon.png"));
//        }
//
//        navButton = NavigationButton.builder()
//                .tooltip("Export ChatLogs")
//                .icon(icon)
//                .priority(10)
//                .panel(panel)
//                .build();
//
//        clientToolbar.addNavigation(navButton);
//    }
//
//    @Override
//    protected void shutDown() throws Exception {
//        chatLocked = false;
//        typing = false;
//        this.keyManager.unregisterKeyListener(this);
//        msg = null;
//        x = null;
//        chatMsgs.clear();
//        clientToolbar.removeNavigation(navButton);
//    }
//
//    @Subscribe
//    public void onSocketReceivePacket(SocketReceivePacket event) {
//
//        Date date = new Date();
//
//        try {
//            JSONObject payload = event.getPayload();
//            if (!payload.has("socketchat") && !payload.has("socketKeyremap")){
//                return;
//            }
//
//            else if(payload.has("socketchat")){
//                String sender = "";
//                String msg = "";
//                JSONArray data = payload.getJSONArray("socketchat");
//                for (int i = 0; i < data.length(); i++) {
//                    JSONObject jsonwp = data.getJSONObject(i);
//                    msg = jsonwp.getString("msg");
//                    sender = jsonwp.getString("sender");
//                }
//
//                String dateTime = "";
//
//                if(config.getDateStamp()){
//                    if(config.getFreedomUnits()){
//                        dateTime += formatterrr.format(date);
//                    } else {
//                        dateTime += formatter.format(date);
//                    }
//                }
//                if(config.getTimeStamp()){
//                    if(dateTime != ""){
//                        dateTime += " | " + formatterr.format(date);
//                    } else {
//                        dateTime += formatterr.format(date);
//                    }
//
//                }
//                String dateTimeString = "[" + dateTime + "] ";
//                if (!dateTime.equals("")) {
//                    chatMessageManager.queue(QueuedMessage.builder()
//                            .type(ChatMessageType.TRADE)
//                            .runeLiteFormattedMessage(new ChatMessageBuilder()
//                                    .append(config.getDateTimeColor(), dateTimeString)
//                                    .append(config.getNameColor(), sender)
//                                    .append(config.getMsgColor(), msg)
//                                    .build())
//                            .build());
//                    chatMsgs.add(dateTimeString + sender + msg);
//                } else {
//                    chatMessageManager.queue(QueuedMessage.builder()
//                            .type(ChatMessageType.TRADE)
//                            .runeLiteFormattedMessage(new ChatMessageBuilder()
//                                    .append(config.getNameColor(), sender)
//                                    .append(config.getMsgColor(), msg)
//                                    .build())
//                            .build());
//                    chatMsgs.add(sender + msg);
//                }
//            } else if(payload.has("socketKeyremap")){
//                String sender = "";
//                String enabled = "no";
//                JSONArray data = payload.getJSONArray("socketKeyremap");
//                for (int i = 0; i < data.length(); i++) {
//                    JSONObject jsonwp = data.getJSONObject(i);
//                    enabled = jsonwp.getString("enabled");
//                    sender = jsonwp.getString("sender");
//                }
//                if(sender.equals(client.getLocalPlayer().getName())){
//                    if(enabled.equals("yes")){
//                        typing = true;
//                    } else if(enabled.equals("no")) {
//                        typing = false;
//                    }
//
//                }
//            }
//
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @Override
//    public void keyTyped(KeyEvent e) {
//
//
//    }
//
//    @Subscribe
//    public void onClientTick(ClientTick event){
//
//        Widget y = client.getWidget(WidgetInfo.CHATBOX_INPUT);
//        if(y != null && !y.isHidden() && !y.isSelfHidden()){
//            x = y;
//        } else {
//            x = null;
//        }
//    }
//
//    @Override
//    public void keyPressed(KeyEvent e) {
//
//
//
//        if(e.getKeyCode() == 10){
//            Widget w = client.getWidget(WidgetInfo.CHATBOX_INPUT);
//            if(w != null){
//                String s = w.getText();
//
//                String strippedStart = "";
//                if(typing){
//                    chatLocked = false;
//                    /**
//                    if(s.charAt(0) == '<'){ //fucking ironman
//                        strippedStart = s.substring("<img=2>".length() + client.getLocalPlayer().getName().length() + ": <col=9090ff>".length());
//                    } else {
//                        strippedStart = s.substring(client.getLocalPlayer().getName().length() + ": <col=9090ff>".length());
//                    }
//                    String reversed = new StringBuilder(strippedStart).reverse().toString();
//                    String strippedEnd = reversed.substring("</col>*".length());
//                    msg = new StringBuilder(strippedEnd).reverse().toString();**/
//                }
//                else {
//                    if(s.contains("Press Enter to Chat")){
//                        chatLocked = true;
//                    } else {
//                        chatLocked = false;
//                        /**
//                        if(s.charAt(0) == '<'){ //fucking ironman
//                            strippedStart = s.substring("<img=2>".length() + client.getLocalPlayer().getName().length() + ": <col=9090ff>".length());
//                        } else {
//                            strippedStart = s.substring(client.getLocalPlayer().getName().length() + ": <col=9090ff>".length());
//                        }
//                        String reversed = new StringBuilder(strippedStart).reverse().toString();
//                        String strippedEnd = "";
//                        if(reversed.length() == 7){
//                            strippedEnd = reversed.substring("</col>*".length());
//                        } else {
//                            strippedEnd = reversed.substring(">loc/<*>ff0909=loc<>loc/<".length());
//                        }
//
//
//                        msg = new StringBuilder(strippedEnd).reverse().toString();**/
//                    }
//
//                }
//                msg = client.getVar(VarClientStr.CHATBOX_TYPED_TEXT);
//
//
//            }
//            if(!chatLocked){
//                if(x != null){
//                    if(msg != null && msg.length() > 0){
//                        if(msg.charAt(0) == '-'){
//                            String s = "";
//                            for(Map.Entry<String, String> entry : iconOptions.entrySet()){
//                                if(entry.getValue().equals(config.getIcon().getMenuName())){
//                                    s = entry.getKey();
//                                }
//                            }
//                                if(client.getLocalPlayer().getName().toLowerCase().equals("text")){
//                                    sendPacket(s + "Captain Igbo", msg.substring(1));
//                                }else if (client.getLocalPlayer().getName().toLowerCase().equals("class6") || client.getLocalPlayer().getName().toLowerCase().equals("oblv ross")){
//                                    sendPacket(s + "Ross", msg.substring(1));
//                                }else if (client.getLocalPlayer().getName().toLowerCase().equals("oblv nonce") || client.getLocalPlayer().getName().toLowerCase().equals("pr3") || client.getLocalPlayer().getName().toLowerCase().equals("oblv mcneill") || client.getLocalPlayer().getName().toLowerCase().equals("mcneill")){
//                                    sendPacket(s + "Liam", msg.substring(1));
//                                } else if (client.getLocalPlayer().getName().toLowerCase().equals("subwooferman") || client.getLocalPlayer().getName().toLowerCase().equals("zdiia")){
//                                    sendPacket(s + "Naka", msg.substring(1));
//                                }else if (client.getLocalPlayer().getName().toLowerCase().equals("oblv kep") || client.getLocalPlayer().getName().toLowerCase().equals("keplunk") || client.getLocalPlayer().getName().toLowerCase().equals("heal simp") || client.getLocalPlayer().getName().toLowerCase().equals("dps simp")){
//                                    sendPacket(s + "Kep", msg.substring(1));
//                                }else if (client.getLocalPlayer().getName().toLowerCase().equals("oblv thot") || client.getLocalPlayer().getName().toLowerCase().equals("sweet dee")){
//                                    sendPacket(s + "Big D", msg.substring(1));
//                                }else if (client.getLocalPlayer().getName().toLowerCase().equals("yonex") || client.getLocalPlayer().getName().toLowerCase().equals("oblv zulu")){
//                                    sendPacket(s + "Coach", msg.substring(1));
//                                }else if (client.getLocalPlayer().getName().toLowerCase().equals("silent")) {
//                                    sendPacket(s + "Damber", msg.substring(1));
//                                }else if (client.getLocalPlayer().getName().toLowerCase().equals("spoon")){
//                                    sendPacket(s + "Spoon", msg.substring(1));
//                                }else if (client.getLocalPlayer().getName().toLowerCase().equals("dudash") || client.getLocalPlayer().getName().toLowerCase().equals("oblv dudash")){
//                                    sendPacket(s + "Dudash", msg.substring(1));
//                                }else if (client.getLocalPlayer().getName().toLowerCase().equals("hooti")){
//                                    sendPacket(s + "Ditters Thick Brown Shaft", msg.substring(1));
//                                }
//                                else {
//                                    sendPacket(s + client.getLocalPlayer().getName(), msg.substring(1));
//                                }
//                                client.setVar(VarClientStr.CHATBOX_TYPED_TEXT, "");
//
//                                if(typing){
//                                    chatLocked = true;
//                                    clientThread.invoke(this::lockChat);
//                                } else {
//                                    e.consume();
//                                }
//
//
//                        }
//
//                    }
//                }
//            }
//
//        }
//    }
//    void lockChat()
//    {
//        Widget chatboxInput = client.getWidget(WidgetInfo.CHATBOX_INPUT);
//        if (chatboxInput != null)
//        {
//            setChatboxWidgetInput(chatboxInput, "Press Enter to Chat...");
//        }
//    }
//    private void setChatboxWidgetInput(Widget widget, String input)
//    {
//        String text = widget.getText();
//        int idx = text.indexOf(':');
//        if (idx != -1)
//        {
//            String newText = text.substring(0, idx) + ": " + input;
//            widget.setText(newText);
//        }
//    }
//
//    @Override
//    public void keyReleased(KeyEvent e) {
//        //System.out.println(e.getKeyChar());
//        //e.consume();
//    }
//
//
//    private void sendPacket(String sender,String msg){
//        JSONArray data = new JSONArray();
//
//
//        JSONObject jsonwp = new JSONObject();
//        jsonwp.put("sender", sender + ": ");
//        jsonwp.put("msg", msg);
//
//
//
//        data.put(jsonwp);
//
//
//        JSONObject payload = new JSONObject();
//        payload.put("socketchat", data);
//
//        eventBus.post(new SocketBroadcastPacket(payload));
//
//    }
//
//}
