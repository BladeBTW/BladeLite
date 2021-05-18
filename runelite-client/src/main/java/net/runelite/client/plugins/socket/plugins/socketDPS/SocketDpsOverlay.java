package net.runelite.client.plugins.socket.plugins.socketDPS;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import static net.runelite.api.MenuAction.RUNELITE_OVERLAY;
import net.runelite.api.Player;
import net.runelite.client.plugins.socket.plugins.socketDPS.table.SocketTableAlignment;
import net.runelite.client.plugins.socket.plugins.socketDPS.table.SocketTableComponent;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.QuantityFormatter;
import net.runelite.client.ws.PartyService;

class SocketDpsOverlay extends OverlayPanel {
    private static final DecimalFormat DPS_FORMAT = new DecimalFormat("#0.0");
    private static final int PANEL_WIDTH_OFFSET = 10;
    static final OverlayMenuEntry RESET_ENTRY;
    private final SocketDpsCounterPlugin socketDpsCounterPlugin;
    private final SocketDpsConfig socketDpsConfig;
    private final Client client;

    @Inject
    SocketDpsOverlay(SocketDpsCounterPlugin socketDpsCounterPlugin, SocketDpsConfig socketDpsConfig, PartyService partyService, Client client) {
        super(socketDpsCounterPlugin);
        this.socketDpsCounterPlugin = socketDpsCounterPlugin;
        this.socketDpsConfig = socketDpsConfig;
        this.client = client;
        this.getMenuEntries().add(RESET_ENTRY);
    }

    public Dimension render(Graphics2D graphics) {
        if (socketDpsConfig.displayOverlay())
        {
            Map<String, Integer> dpsMembers = this.socketDpsCounterPlugin.getMembers();
            this.panelComponent.getChildren().clear();
            int tot = 0;
            if (dpsMembers.isEmpty()) {
                return null;
            } else {
                Player player = this.client.getLocalPlayer();
                if (dpsMembers.containsKey("total")) {
                    tot = (Integer)dpsMembers.get("total");
                    dpsMembers.remove("total");
                }

                SocketTableComponent tableComponent = new SocketTableComponent();
                tableComponent.setColumnAlignments(new SocketTableAlignment[]{SocketTableAlignment.LEFT, SocketTableAlignment.RIGHT});
                int maxWidth = 129;
                dpsMembers.forEach((k, v) -> {
                    String right = QuantityFormatter.formatNumber((long)v);
                    if (k.equalsIgnoreCase(client.getLocalPlayer().getName()) && socketDpsConfig.highlightSelf()) {
                        tableComponent.addRow(new String[]{ColorUtil.prependColorTag(k, Color.green), ColorUtil.prependColorTag(right, Color.green)});
                    } else if (this.socketDpsConfig.highlightOtherPlayer() && this.socketDpsCounterPlugin.getHighlights().contains(k.toLowerCase()))
                    {
                        tableComponent.addRow(new String[]{ColorUtil.prependColorTag(k, this.socketDpsConfig.getHighlightColor()), ColorUtil.prependColorTag(right, this.socketDpsConfig.getHighlightColor())});
                    } else {
                        tableComponent.addRow(new String[]{ColorUtil.prependColorTag(k, Color.white), ColorUtil.prependColorTag(right, Color.white)});
                    }
                });
                this.panelComponent.setPreferredSize(new Dimension(maxWidth + 10, 0));
                dpsMembers.put("total", tot);
                if (player.getName() != null && dpsMembers.containsKey(player.getName()) && tot > (Integer)dpsMembers.get(player.getName()) && socketDpsConfig.showTotal()) {
                    tableComponent.addRow(new String[]{ColorUtil.prependColorTag("total", Color.red), ColorUtil.prependColorTag(dpsMembers.get("total").toString(), Color.red)});
                }

                if (!tableComponent.isEmpty()) {
                    this.panelComponent.getChildren().add(tableComponent);
                }

                return this.panelComponent.render(graphics);
            }
        }
        return null;
    }

    static {
        RESET_ENTRY = new OverlayMenuEntry(RUNELITE_OVERLAY, "Reset", "DPS counter");
    }
}