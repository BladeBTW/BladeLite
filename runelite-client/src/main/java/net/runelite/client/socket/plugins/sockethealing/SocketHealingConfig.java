package net.runelite.client.plugins.socket.plugins.sockethealing;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("sockethealing")
public interface SocketHealingConfig extends Config
{
	@ConfigItem(
		position = 0,
		keyName = "displayHealth",
		name = "Display Health Overlays*",
		description = "This is solely used for disabling on clients that it is not desired to display the health on."
	)
	default boolean displayHealth()
	{
		return true;
	}

	@ConfigItem(
			position = 1,
			keyName = "whitelistPlayers",
			name = "Player Health Overlay Whitelist",
			description = "Type the name of the players you want to display the health overlay of. (ex: Zezima,ToysRUs)"
	)
	String whitelistPlayers();

	@ConfigItem(
			position = 2,
			keyName = "enableWhitelist",
			name = "Player Health Overlay Whitelist",
			description = "Toggles the whitelist restriction on which names to show the overlay for."
	)
	default boolean enableWhitelist()
	{
		return true;
	}

	@ConfigItem(
			position = 3,
			keyName = "blacklistPlayers",
			name = "Player Health Overlay Blacklist",
			description = "Type the name of the players you want to not display the health overlay of. (ex: Zezima,ToysRUs)"
	)
	String blacklistPlayers();

	@ConfigItem(
			position = 4,
			keyName = "enableBlacklist",
			name = "Player Health Overlay Blacklist",
			description = "Toggles the blacklist restriction on which names to show the overlay for."
	)
	default boolean enableBlacklist()
	{
		return true;
	}

	@Range(max = 20, min = 1)
	@ConfigItem(
			position = 6,
			keyName = "refreshRate",
			name = "Socket Refresh Rate",
			description = "This is how many ticks you would like in-between updating the information."
	)
	default int refreshRate()
	{
		return 5;
	}

	@Range(max = 30, min = -50)
	@ConfigItem(
			position = 7,
			keyName = "overlayOffset",
			name = "Player Overlay Height",
			description = "This adjusts the height of the health overlay."
	)
	default int overlayOffset()
	{
		return 0;
	}

	@Range(max = 20, min = 10)
	@ConfigItem(
			position = 8,
			keyName = "fontSize",
			name = "Font Size",
			description = "Shows total damage done to a boss"
	)
	default int fontSize()
	{
		return 12;
	}

	@Range(max = 100, min = 50)
	@ConfigItem(
			position = 9,
			keyName = "greenZone",
			name = "Green Zone - Floor",
			description = "Sets the bottom amount for the health to be displayed as Green. (Max 100, Min 50)"
	)
	default int greenZone()
	{
		return 75;
	}

	@Range(max = 75, min = 25)
	@ConfigItem(
			position = 10,
			keyName = "orangeZone",
			name = "Orange Zone - Floor",
			description = "Sets the bottom amount for the health to be displayed as Orange. (Max 75, Min 25)"
	)
	default int orangeZone()
	{
		return 50;
	}

	@ConfigItem(
			position = 11,
			keyName = "highlightPlayerHull",
			name = "Highlight Player Hull",
			description = "Choose to outline the player model of those names listed below."
	)
	default boolean highlightPlayerHull()
	{
		return false;
	}

	@ConfigItem(
			position = 12,
			keyName = "customHullColor",
			name = "Toggle Custom Player Hull",
			description = "Choose to either use the HP range colors OR a custom color for the hull."
	)
	default boolean customHullColor()
	{
		return false;
	}

	@Alpha
	@ConfigItem(
			position = 13,
			keyName = "highlightHullColor",
			name = "Custom Player Hull Color",
			description = "Select a color for the Player Hull Overlay to be."
	)
	default Color highlightHullColor()
	{
		return Color.CYAN;
	}

	@ConfigItem(
			position = 14,
			keyName = "highlightPlayerHullWhiteList",
			name = "Player Hull WhiteList",
			description = "Type the name of the players you don't want highlighted. (ex: Zezima,ToysRUs)"
	)
	String highlightPlayerHullFilterWhiteList();

	@ConfigItem(
			position = 15,
			keyName = "enableWhiteListPlayerHull",
			name = "Player Hull WhiteList",
			description = "Toggles the whitelist restriction on which names to show the player hull for."
	)
	default boolean enablePlayerHullWhiteList()
	{
		return false;
	}

	@ConfigItem(
			position = 16,
			keyName = "highlightPlayerHullBlackList",
			name = "Player Hull BlackList",
			description = "Type the name of the players you don't want highlighted. (ex: Zezima,ToysRUs)"
	)
	String highlightPlayerHullFilterBlackList();

	@ConfigItem(
			position = 17,
			keyName = "enableBlackListPlayerHull",
			name = "Player Hull Blacklist",
			description = "Toggles the blacklist restriction on which names to show the player hull for."
	)
	default boolean enablePlayerHullBlacklist()
	{
		return false;
	}
}