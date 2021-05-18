/*    */ package net.runelite.client.plugins.socket.plugins.socketdefence;
/*    */ 
/*    */ import net.runelite.client.config.Config;
/*    */ import net.runelite.client.config.ConfigGroup;
/*    */ import net.runelite.client.config.ConfigItem;
/*    */ import net.runelite.client.config.ConfigSection;
/*    */ import net.runelite.client.config.Range;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @ConfigGroup("socketdefence")
/*    */ public interface SocketDefenceConfig
/*    */   extends Config
/*    */ {
/*    */   @ConfigSection(name = "<html><font color=#00aeef>Corp", description = "Corp settings", position = 0, closedByDefault = true)
/*    */   public static final String corpSection = "corp";
/*    */   
/*    */   @ConfigItem(keyName = "cm", name = "Challenge Mode", description = "Toggle this to set the defence to Challenge Mode when doing Cox", position = 0)
/*    */   default boolean cm() {
/* 22 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Range(max = 50, min = 2)
/*    */   @ConfigItem(name = "Low Defence Threshold", keyName = "lowDef", description = "Sets when you want the defence to appear as yellow (low defence).", position = 1)
/*    */   default int lowDef() {
/* 33 */     return 10;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @ConfigItem(keyName = "corpChally", name = "Corp Chally Highlight", description = "Highlight corp when you should chally spec", position = 0, section = "corp")
/*    */   default CorpTileMode corpChally() {
/* 44 */     return CorpTileMode.OFF;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Range(min = 0, max = 255)
/*    */   @ConfigItem(keyName = "corpChallyOpacity", name = "Corp Chally Opactiy", description = "Toggles opacity of Corp Chally Highlight", position = 1, section = "corp")
/*    */   default int corpChallyOpacity() {
/* 56 */     return 20;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Range(min = 1, max = 5)
/*    */   @ConfigItem(keyName = "corpChallyThicc", name = "Corp Chally Width", description = "Toggles girth of Corp Chally Highlight", position = 2, section = "corp")
/*    */   default int corpChallyThicc() {
/* 68 */     return 2;
/*    */   }
/*    */ }


/* Location:              C:\Users\Blade\Downloads\SocketDefence_1_1.jar!\net\runelite\client\plugins\socketdefence\SocketDefenceConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */