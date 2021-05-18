package net.runelite.client.plugins.socket.plugins.socketDPS;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Player;
import net.runelite.client.plugins.socket.plugins.socketDPS.table.SocketTableAlignment;
import net.runelite.client.plugins.socket.plugins.socketDPS.table.SocketTableComponent;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.QuantityFormatter;
import net.runelite.client.ws.PartyService;

class SocketDpsOverlay extends OverlayPanel {
    private static final DecimalFormat DPS_FORMAT = new DecimalFormat("#0.0");

    private static final int PANEL_WIDTH_OFFSET = 10;

    @Inject
    SocketDpsOverlay(SocketDpsCounterPlugin socketDpsCounterPlugin, SocketDpsConfig socketDpsConfig, PartyService partyService, Client client) {
        super(socketDpsCounterPlugin);
        this.socketDpsCounterPlugin = socketDpsCounterPlugin;
        this.socketDpsConfig = socketDpsConfig;
        this.client = client;
        getMenuEntries().add(RESET_ENTRY);
    }

    public Dimension render(Graphics2D graphics) {
        if (this.socketDpsConfig.displayOverlay()) {
            Map<String, Integer> dpsMembers = this.socketDpsCounterPlugin.getMembers();
            this.panelComponent.getChildren().clear();
            int tot = 0;
            if (dpsMembers.isEmpty())
                return null;
            Player player = this.client.getLocalPlayer();
            if (dpsMembers.containsKey("total")) {
                tot = ((Integer)dpsMembers.get("total")).intValue();
                dpsMembers.remove("total");
            }
            SocketTableComponent tableComponent = new SocketTableComponent();
            tableComponent.setColumnAlignments(new SocketTableAlignment[] { SocketTableAlignment.LEFT, SocketTableAlignment.RIGHT });
            int maxWidth = 129;
            dpsMembers.forEach((k, v) -> {
                String right = QuantityFormatter.formatNumber(v.intValue());
                if (k.equalsIgnoreCase(this.client.getLocalPlayer().getName()) && this.socketDpsConfig.highlightSelf()) {
                    tableComponent.addRow(new String[] { ColorUtil.prependColorTag(k, Color.green), ColorUtil.prependColorTag(right, Color.green) });
                } else if (this.socketDpsConfig.highlightOtherPlayer() && this.socketDpsCounterPlugin.getHighlights().contains(k.toLowerCase())) {
                    tableComponent.addRow(new String[] { ColorUtil.prependColorTag(k, this.socketDpsConfig.getHighlightColor()), ColorUtil.prependColorTag(right, this.socketDpsConfig.getHighlightColor()) });
                } else {
                    tableComponent.addRow(new String[] { ColorUtil.prependColorTag(k, Color.white), ColorUtil.prependColorTag(right, Color.white) });
                }
            });
            this.panelComponent.setPreferredSize(new Dimension(maxWidth + 10, 0));
            dpsMembers.put("total", Integer.valueOf(tot));
            if (player.getName() != null && dpsMembers.containsKey(player.getName()) && tot > ((Integer)dpsMembers.get(player.getName())).intValue() && this.socketDpsConfig.showTotal())
                tableComponent.addRow(new String[] { ColorUtil.prependColorTag("total", Color.red), ColorUtil.prependColorTag(((Integer)dpsMembers.get("total")).toString(), Color.red) });
            if (!tableComponent.isEmpty()) {
                this.panelComponent.getChildren().add(tableComponent);
            }
            if (this.socketDpsConfig.backgroundStyle() == SocketDpsConfig.backgroundMode.HIDE) {

                this.panelComponent.setBackgroundColor(null);
                return super.render(graphics);
            }
            if (this.socketDpsConfig.backgroundStyle() == SocketDpsConfig.backgroundMode.STANDARD) {

                this.panelComponent.setBackgroundColor(ComponentConstants.STANDARD_BACKGROUND_COLOR);
                return this.panelComponent.render(graphics);
            }
            if (this.socketDpsConfig.backgroundStyle() == SocketDpsConfig.backgroundMode.CUSTOM) {

                this.panelComponent.setBackgroundColor(new Color(this.socketDpsConfig.backgroundColor().getRed(), this.socketDpsConfig.backgroundColor().getGreen(), this.socketDpsConfig.backgroundColor().getBlue(), this.socketDpsConfig.backgroundColor().getAlpha()));
                return this.panelComponent.render(graphics);
            }
        }
        return null;
    }

    static final OverlayMenuEntry RESET_ENTRY = new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY, "Reset", "DPS counter");
    private final SocketDpsCounterPlugin socketDpsCounterPlugin;
    private final SocketDpsConfig socketDpsConfig;
    private final Client client;
}