package net.runelite.client.plugins.socket.plugins.playerstatusextended;

import net.runelite.client.plugins.*;
import javax.inject.*;
import net.runelite.client.eventbus.*;
import net.runelite.client.plugins.socket.org.json.*;
import net.runelite.client.plugins.socket.packet.*;
import net.runelite.api.events.*;
import net.runelite.api.kit.*;
import net.runelite.api.*;
import org.slf4j.*;

@PluginDescriptor(name = "Socket - Player Status Extended", description = "Socket extension for displaying player status to members in your party.", tags = { "socket" }, enabledByDefault = true)
public class PlayerStatusExtendedPlugin extends Plugin
{
    private static final Logger log;
    @Inject
    private Client client;
    @Inject
    private EventBus eventBus;
    private boolean inTheatre;
    private boolean deferredCheck;
    private int deferredTick;

    public PlayerStatusExtendedPlugin() {
        this.inTheatre = false;
    }

    protected void startUp() throws Exception {
        if (this.enforceRegion()) {
            this.sendFlag(this.client.getLocalPlayer().getName() + " turned on extended player status");
        }
        this.deferredCheck = false;
        this.deferredTick = -1;
    }

    protected void shutDown() throws Exception {
        if (this.enforceRegion()) {
            this.sendFlag(this.client.getLocalPlayer().getName() + " turned off extended player status");
        }
    }

    @Subscribe
    public void onGameTick(final GameTick event) {
        if (!this.inTheatre) {
            if (this.enforceRegion()) {
                this.inTheatre = true;
                this.sendFlag(this.client.getLocalPlayer().getName() + " is using extended player status v1.03");
            }
        }
        else if (!this.enforceRegion()) {
            this.inTheatre = false;
        }
        if (this.deferredCheck && this.client.getTickCount() == this.deferredTick) {
            this.deferredTick = -1;
            this.deferredCheck = false;
            this.checkStats();
        }
    }

    private void sendFlag(final String msg) {
        final JSONArray data = new JSONArray();
        final JSONObject jsonmsg = new JSONObject();
        jsonmsg.put("msg", (Object)(" " + msg));
        data.put((Object)jsonmsg);
        final JSONObject send = new JSONObject();
        send.put("playerstatusextendedalt", (Object)data);
        this.eventBus.post((Object)new SocketBroadcastPacket(send));
    }

    private void sendFlag(final int lvl) {
        final JSONArray data = new JSONArray();
        final JSONObject jsonmsg = new JSONObject();
        jsonmsg.put("level1", lvl);
        jsonmsg.put("sender", (Object)this.client.getLocalPlayer().getName());
        data.put((Object)jsonmsg);
        final JSONObject send = new JSONObject();
        send.put("playerstatusextended", (Object)data);
        this.eventBus.post((Object)new SocketBroadcastPacket(send));
    }

    @Subscribe
    public void onSocketReceivePacket(final SocketReceivePacket event) {
        try {
            final JSONObject payload = event.getPayload();
            if (payload.has("playerstatusextended")) {
                final JSONArray data = payload.getJSONArray("playerstatusextended");
                final JSONObject jsonmsg = data.getJSONObject(0);
                final String sender = jsonmsg.getString("sender");
                final int lvl = jsonmsg.getInt("level1");
                final String msg = sender + " attacked at " + lvl + " strength.";
                this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", msg, (String)null);
            }
            else if (payload.has("playerstatusextendedalt")) {
                final JSONArray data = payload.getJSONArray("playerstatusextendedalt");
                final JSONObject jsonmsg = data.getJSONObject(0);
                final String msg2 = jsonmsg.getString("msg");
                this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", msg2, (String)null);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean enforceRegion() {
        return this.inRegion(12613, 13125, 13123, 12612, 12611);
    }

    private void checkStats() {
        if (this.client.getBoostedSkillLevel(Skill.STRENGTH) != 118) {
            this.sendFlag(this.client.getBoostedSkillLevel(Skill.STRENGTH));
        }
    }

    private void deferStatCheck() {
        this.deferredCheck = true;
        this.deferredTick = this.client.getTickCount();
    }

    @Subscribe
    public void onAnimationChanged(final AnimationChanged event) {
        if (event.getActor() instanceof Player && this.enforceRegion()) {
            final Player p = (Player)event.getActor();
            if (p.equals(this.client.getLocalPlayer())) {
                final int animation = event.getActor().getAnimation();
                if (animation == 8056 || animation == 1378 || animation == 7642) {
                    this.deferStatCheck();
                }
            }
        }
        if (event.getActor() instanceof Player && this.enforceRegion()) {
            int weapon = -1;
            final Player p2 = (Player)event.getActor();
            try {
                weapon = p2.getPlayerComposition().getEquipmentId(KitType.WEAPON);
            }
            catch (NullPointerException e) {
                return;
            }
            if (p2.equals(this.client.getLocalPlayer())) {
                if (event.getActor().getAnimation() == 1378) {
                    if (!this.client.isPrayerActive(Prayer.PIETY)) {
                        this.sendFlag(this.client.getLocalPlayer().getName() + " DWH specced without piety.");
                    }
                }
                else if (event.getActor().getAnimation() == 8056) {
                    if (!this.client.isPrayerActive(Prayer.PIETY)) {
                        this.sendFlag(this.client.getLocalPlayer().getName() + " scythed without piety.");
                    }
                    if (this.client.getVar(VarPlayer.ATTACK_STYLE) != 1) {
                        this.sendFlag(this.client.getLocalPlayer().getName() + " scythed on crush");
                    }
                }
                else if (event.getActor().getAnimation() == 7642) {
                    if (!this.client.isPrayerActive(Prayer.PIETY)) {
                        this.sendFlag(this.client.getLocalPlayer().getName() + " BGS specced without piety.");
                    }
                }
                else if (event.getActor().getAnimation() == 426 && weapon == 20997 && !this.client.isPrayerActive(Prayer.RIGOUR)) {
                    this.sendFlag(this.client.getLocalPlayer().getName() + " bowed without rigour.");
                }
            }
        }
    }

    private boolean inRegion(final int... regions) {
        if (this.client.getMapRegions() != null) {
            for (final int i : this.client.getMapRegions()) {
                for (final int j : regions) {
                    if (i == j) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    static {
        log = LoggerFactory.getLogger((Class)PlayerStatusExtendedPlugin.class);
    }
}
