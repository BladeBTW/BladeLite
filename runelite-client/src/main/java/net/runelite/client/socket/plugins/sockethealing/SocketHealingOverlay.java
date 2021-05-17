package net.runelite.client.plugins.socket.plugins.sockethealing;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.graph.Graph;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

public class SocketHealingOverlay extends OverlayPanel
{
	private final Client client;
	private final SocketHealingPlugin plugin;
	private final SocketHealingConfig config;

	@Inject
	private SocketHealingOverlay(Client client, SocketHealingPlugin plugin, SocketHealingConfig config)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.setPosition(OverlayPosition.DYNAMIC);
		this.setPriority(OverlayPriority.HIGH);
		this.setLayer(OverlayLayer.ALWAYS_ON_TOP);
	}

	public Dimension render(Graphics2D graphics)
	{
		if (!plugin.getPartyMembers().isEmpty())
		{
			if (config.displayHealth())
			{
				graphics.setFont(new Font(FontManager.getRunescapeFont().toString(), Font.BOLD, config.fontSize()));

				final ArrayListMultimap<WorldPoint, Player> locations = ArrayListMultimap.create();
				Iterator clientPlayers = this.client.getPlayers().iterator();

				while (clientPlayers.hasNext())
				{
					Player player = (Player) clientPlayers.next();
					if (plugin.getPartyMembers().containsKey(player.getName()))
					{
						locations.put(player.getWorldLocation(), player);
					}

				}

				for (WorldPoint wp : locations.keySet())
				{
					int offset = 0;

					for (Player p : locations.get(wp))
					{
						if (config.enableBlacklist() && config.blacklistPlayers().contains(p.getName()))
						{
							continue;
						}

						else if (config.enableWhitelist() && !config.whitelistPlayers().contains(p.getName()))
						{
							continue;
						}

						if (wp.toString().equalsIgnoreCase(p.getWorldLocation().toString()))
						{
							SocketHealingPlayer socketPlayer = plugin.getPartyMembers().get(p.getName());
							int health = socketPlayer.getHealth();
							Color color = Color.WHITE;
							if (health >= config.greenZone())
							{
								color = Color.GREEN;
							}
							if (health < config.greenZone() && health > config.orangeZone())
							{
								color = Color.ORANGE;
							}
							else if (health < config.orangeZone())
							{
								color = Color.RED;
							}

							String healthDisplay;
							healthDisplay = String.valueOf(health);

							drawHealthOverlay(graphics, p.getCanvasTextLocation(graphics, "", p.getLogicalHeight() + (config.overlayOffset() * 10)), healthDisplay, color, offset);
							offset += graphics.getFontMetrics().getHeight();

							if (config.highlightPlayerHull())
							{
								if (config.enablePlayerHullWhiteList()) {
									if (!Strings.isNullOrEmpty(config.highlightPlayerHullFilterWhiteList())) {
										String[] names = config.highlightPlayerHullFilterWhiteList().toLowerCase().split(",");

										for (int i = 0; i < names.length; i++) {
											if (names[i].equalsIgnoreCase(p.getName())) {
												Shape playerClickbox = p.getConvexHull();
												if (playerClickbox != null) {
													OverlayUtil.renderPolygon(graphics, playerClickbox, (config.customHullColor() ? config.highlightHullColor() : color));
												}
											}
										}

									}
								}
								if (config.enablePlayerHullBlacklist()) {
									if (!Strings.isNullOrEmpty(config.highlightPlayerHullFilterBlackList())) {
										String[] names = config.highlightPlayerHullFilterBlackList().toLowerCase().split(",");

										for (int i = 0; i < names.length; i++) {
											if (!names[i].equalsIgnoreCase(p.getName())) {
												Shape playerClickbox = p.getConvexHull();
												if (playerClickbox != null) {
													OverlayUtil.renderPolygon(graphics, playerClickbox, (config.customHullColor() ? config.highlightHullColor() : color));
												}
											}
										}

									}
								}
									else {
										Shape playerClickbox = p.getConvexHull();
										if (playerClickbox != null) {
											OverlayUtil.renderPolygon(graphics, playerClickbox, (config.customHullColor() ? config.highlightHullColor() : color));
										}
									}

								}
							}
						}
					}
				}
			}

		return null;
	}

	private void drawHealthOverlay(Graphics2D graphics, Point textLocation, String health, Color color, int offset)
	{
		if (textLocation != null)
		{
			OverlayUtil.renderTextLocation(graphics, new Point(textLocation.getX() - 15, textLocation.getY() - offset + 25), health, color);
		}
	}
}