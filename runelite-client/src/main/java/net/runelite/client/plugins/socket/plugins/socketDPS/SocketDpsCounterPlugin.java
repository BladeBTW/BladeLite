package net.runelite.client.plugins.socket.plugins.socketDPS;

import com.google.inject.Provides;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.inject.Inject;

import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Hitsplat;
import net.runelite.api.NPC;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.NpcDespawned;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.OverlayMenuClicked;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.socket.SocketPlugin;
import net.runelite.client.plugins.socket.org.json.JSONObject;
import net.runelite.client.plugins.socket.packet.SocketBroadcastPacket;
import net.runelite.client.plugins.socket.packet.SocketReceivePacket;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name = "Socket - DPS", description = "Counts damage by a party", enabledByDefault = false)
@PluginDependency(SocketPlugin.class)
public class SocketDpsCounterPlugin extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(SocketDpsCounterPlugin.class);

    private static final Set<Integer> BOSSES = new HashSet<>();

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private SocketDpsOverlay socketDpsOverlay;

    @Inject
    private SocketDpsConfig socketDpsConfig;

    @Inject
    private EventBus eventBus;

    @Inject
    private PluginManager pluginManager;

    @Inject
    private SocketPlugin socketPlugin;

    @Inject
    private ClientThread clientThread;

    private Map<String, Integer> members = new ConcurrentHashMap<>();

    private List<String> highlights = new ArrayList<>();

    private List<String> danger = new ArrayList<>();

    public List<String> getDanger() {
        return this.danger;
    }

    @Provides
    SocketDpsConfig provideConfig(ConfigManager configManager) {
        return (SocketDpsConfig) configManager.getConfig(SocketDpsConfig.class);
    }

    protected void startUp() {
        this.members.clear();
        this.overlayManager.add((Overlay) this.socketDpsOverlay);
        this.clientThread.invoke(this::rebuildAllPlayers);
        BOSSES.addAll(Arrays.asList(new Integer[]{Integer.valueOf(5886), Integer.valueOf(5887), Integer.valueOf(5888), Integer.valueOf(5889), Integer.valueOf(5890), Integer.valueOf(5891), Integer.valueOf(5908), Integer.valueOf(6503), Integer.valueOf(6609), Integer.valueOf(5862),
                Integer.valueOf(5863), Integer.valueOf(5866), Integer.valueOf(2054), Integer.valueOf(6505), Integer.valueOf(319), Integer.valueOf(2215), Integer.valueOf(6494), Integer.valueOf(5779), Integer.valueOf(6499), Integer.valueOf(128),
                Integer.valueOf(963), Integer.valueOf(965), Integer.valueOf(4303), Integer.valueOf(4304), Integer.valueOf(6500), Integer.valueOf(6501), Integer.valueOf(239), Integer.valueOf(2642), Integer.valueOf(650), Integer.valueOf(3129),
                Integer.valueOf(6495), Integer.valueOf(8713), Integer.valueOf(6504), Integer.valueOf(6610), Integer.valueOf(6611), Integer.valueOf(6612), Integer.valueOf(3106), Integer.valueOf(3108), Integer.valueOf(8360), Integer.valueOf(8361),
                Integer.valueOf(8362), Integer.valueOf(8363), Integer.valueOf(8364), Integer.valueOf(8365), Integer.valueOf(8359), Integer.valueOf(8354), Integer.valueOf(8355), Integer.valueOf(8356), Integer.valueOf(8357), Integer.valueOf(8387),
                Integer.valueOf(8388), Integer.valueOf(8340), Integer.valueOf(8341), Integer.valueOf(8370), Integer.valueOf(8372), Integer.valueOf(8374), Integer.valueOf(7540), Integer.valueOf(7541), Integer.valueOf(7542), Integer.valueOf(7543),
                Integer.valueOf(7544), Integer.valueOf(7545), Integer.valueOf(7530), Integer.valueOf(7531), Integer.valueOf(7532), Integer.valueOf(7533), Integer.valueOf(7525), Integer.valueOf(7526), Integer.valueOf(7527), Integer.valueOf(7528),
                Integer.valueOf(7529), Integer.valueOf(7551), Integer.valueOf(7552), Integer.valueOf(7553), Integer.valueOf(7554), Integer.valueOf(7555), Integer.valueOf(7559), Integer.valueOf(7560), Integer.valueOf(7561), Integer.valueOf(7562),
                Integer.valueOf(7563), Integer.valueOf(7566), Integer.valueOf(7567), Integer.valueOf(7569), Integer.valueOf(7570), Integer.valueOf(7571), Integer.valueOf(7572), Integer.valueOf(7573), Integer.valueOf(7574), Integer.valueOf(7584),
                Integer.valueOf(7585), Integer.valueOf(7604), Integer.valueOf(7605), Integer.valueOf(7606), Integer.valueOf(9425), Integer.valueOf(9426), Integer.valueOf(9427), Integer.valueOf(9428), Integer.valueOf(9429), Integer.valueOf(9430),
                Integer.valueOf(9431), Integer.valueOf(9432), Integer.valueOf(9433), Integer.valueOf(3162), Integer.valueOf(2205), Integer.valueOf(2265), Integer.valueOf(2266), Integer.valueOf(2267)}));
    }

    protected void shutDown() {
        this.overlayManager.remove((Overlay) this.socketDpsOverlay);
        this.members.clear();
    }

    @Subscribe
    void onConfigChanged(ConfigChanged configChanged) {
        if (configChanged.getGroup().equals("socketdpscounter"))
            this.clientThread.invoke(this::rebuildAllPlayers);
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGIN_SCREEN || event.getGameState() == GameState.HOPPING)
            this.members.clear();
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied hitsplatApplied) {
        Actor target = hitsplatApplied.getActor();
        Hitsplat hitsplat = hitsplatApplied.getHitsplat();
        if (hitsplat.isMine() && target != this.client.getLocalPlayer() && target instanceof NPC) {

            NPC npc = (NPC) target;
            int interactingId = npc.getId();
            if ((!this.socketDpsConfig.onlyBossDps() || BOSSES.contains(Integer.valueOf(interactingId))) && hitsplat
                    .getAmount() > 0) {
                int hit = hitsplat.getAmount();
                String pName = this.client.getLocalPlayer().getName();
                this.members.put(pName, Integer.valueOf(((Integer) this.members.getOrDefault(pName, Integer.valueOf(0))).intValue() + hit));
                this.members.put("total", Integer.valueOf(((Integer) this.members.getOrDefault("total", Integer.valueOf(0))).intValue() + hit));
                JSONObject data = new JSONObject();
                data.put("player", pName);
                data.put("target", interactingId);
                data.put("hit", hit);
                data.put("world", this.client.getWorld());
                JSONObject payload = new JSONObject();
                payload.put("dps-counter", data);
                this.eventBus.post(new SocketBroadcastPacket(payload));
                this.members = sortByValue(this.members);
            }
        }
    }

    @Subscribe
    public void onOverlayMenuClicked(OverlayMenuClicked event) {
        if (event.getEntry() == SocketDpsOverlay.RESET_ENTRY)
            this.members.clear();
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned) {
        NPC npc = npcDespawned.getNpc();
        if (npc.isDead() && BOSSES.contains(Integer.valueOf(npc.getId()))) {
            log.debug("Boss has died!");
            if (this.socketDpsConfig.autoclear())
                this.members.clear();
            if (this.socketDpsConfig.clearAnyBossKill()) {
                JSONObject data = new JSONObject();
                data.put("boss", npc.getId());
                data.put("world", this.client.getWorld());
                JSONObject payload = new JSONObject();
                payload.put("dps-clear", data);
                this.eventBus.post(new SocketBroadcastPacket(payload));
            }
        }
    }

    @Subscribe
    public void onSocketReceivePacket(SocketReceivePacket event) {
        try {
            if (this.client.getGameState() != GameState.LOGGED_IN)
                return;
            JSONObject payload = event.getPayload();
            if (!payload.has("dps-counter")) {
                if (payload.has("dps-clear"))
                    if (this.socketDpsConfig.onlySameWorld()) {
                        JSONObject jSONObject = payload.getJSONObject("dps-clear");
                        if (jSONObject.getInt("world") == this.client.getWorld())
                            this.members.clear();
                    } else {
                        this.members.clear();
                    }
                return;
            }
            String pName = this.client.getLocalPlayer().getName();
            JSONObject data = payload.getJSONObject("dps-counter");
            if (this.socketDpsConfig.onlySameWorld() && this.client.getWorld() != data.getInt("world"))
                return;
            if (data.getString("player").equals(pName))
                return;
            this.clientThread.invoke(() -> {
                String attacker = data.getString("player");
                int targetId = data.getInt("target");
                updateDpsMember(attacker, targetId, data.getInt("hit"));
            });
        } catch (Exception var5) {
            var5.printStackTrace();
        }
    }

    private void updateDpsMember(String attacker, int targetId, int hit) {
        if (BOSSES.contains(Integer.valueOf(targetId))) {
            this.members.put(attacker, Integer.valueOf(((Integer) this.members.getOrDefault(attacker, Integer.valueOf(0))).intValue() + hit));
            this.members.put("total", Integer.valueOf(((Integer) this.members.getOrDefault("total", Integer.valueOf(0))).intValue() + hit));
            this.members = sortByValue(this.members);
            updateDanger();
        }
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return (Map<K, V>) map.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, java.util.LinkedHashMap::new));
    }

    public List<String> getHighlights() {
        String configplayers = this.socketDpsConfig.getPlayerToHighlight().toLowerCase();
        return configplayers.isEmpty() ? Collections.<String>emptyList() : SocketText.fromCSV(configplayers);
    }

    void rebuildAllPlayers() {
        this.highlights = getHighlights();
    }

    void updateDanger() {
        this.danger.clear();
        for (String mem1 : this.members.keySet()) {
            if (this.highlights.contains(mem1))
                for (String mem2 : this.members.keySet()) {
                    if (!mem2.equalsIgnoreCase(mem1) && (
                            (Integer) this.members.get(mem2)).intValue() - ((Integer) this.members.get(mem1)).intValue() <= 50)
                        this.danger.add(mem2);
                }
        }
    }

    public Map<String, Integer> getMembers() {
        return this.members;
    }
}