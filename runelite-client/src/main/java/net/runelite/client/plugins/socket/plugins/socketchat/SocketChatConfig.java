//package net.runelite.client.plugins.socket.plugins.socketchat;
//
//import net.runelite.client.config.Config;
//import net.runelite.client.config.ConfigGroup;
//import net.runelite.client.config.ConfigItem;
//
//import java.awt.*;
//
//@ConfigGroup("Socket SocketChat Config")
//public interface SocketChatConfig extends Config {
//
//    @ConfigItem(
//            position = 0,
//            keyName = "userNameColour",
//            name = "Name colour",
//            description = "Colour of the username text"
//    )
//    default Color getNameColor() {
//        return Color.WHITE;
//    }
//
//    @ConfigItem(
//            position = 2,
//            keyName = "messageColour",
//            name = "Message colour",
//            description = "Colour of the message text"
//    )
//    default Color getMsgColor() {
//        return Color.WHITE;
//    }
//    @ConfigItem(
//            position = 3,
//            keyName = "dateTimeColour",
//            name = "DateTime colour",
//            description = "Colour of the DateTime text"
//    )
//    default Color getDateTimeColor() {
//        return Color.WHITE;
//    }
//    @ConfigItem(
//            position = 4,
//            keyName = "chatIcon",
//            name = "Chat Icon",
//            description = "Select an Icon to appear before your name in socket chat"
//    )
//    default images getIcon()
//    {
//        return images.IMG;
//    }
//
//    @ConfigItem(
//            position = 5,
//            keyName = "showTimeStamp",
//            name = "Show Time",
//            description = "Show time of the message"
//    )
//    default boolean getTimeStamp() {
//        return false;
//    }
//
//    @ConfigItem(
//            position = 6,
//            keyName = "showDateStamp",
//            name = "Show Date",
//            description = "Show date of the message"
//    )
//    default boolean getDateStamp() {
//        return false;
//    }
//
//    @ConfigItem(
//            position = 7,
//            keyName = "freedomUnits",
//            name = "American Andys",
//            description = "Display the date incorrectly for Americans"
//    )
//    default boolean getFreedomUnits()
//    {
//        return false;
//    }
//}
