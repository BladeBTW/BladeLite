/*    */ package net.runelite.client.plugins.socket.plugins.socketdefence;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.image.BufferedImage;
/*    */ import javax.inject.Inject;
/*    */ import net.runelite.client.plugins.Plugin;
/*    */ import net.runelite.client.ui.overlay.infobox.InfoBox;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DefenceInfoBox
/*    */   extends InfoBox
/*    */ {
/*    */   @Inject
/*    */   private final SocketDefenceConfig config;
/*    */   private long count;
/*    */   
/*    */   public String toString() {
/* 39 */     return "DefenceInfoBox(config=" + this.config + ", count=" + getCount() + ")";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public long getCount() {
/* 45 */     return this.count; } public void setCount(long count) {
/* 46 */     this.count = count;
/*    */   }
/*    */ 
/*    */   
/*    */   public DefenceInfoBox(BufferedImage image, Plugin plugin, long count, SocketDefenceConfig config) {
/* 51 */     super(image, plugin);
/* 52 */     this.count = count;
/* 53 */     this.config = config;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getText() {
/* 59 */     return Long.toString(getCount());
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Color getTextColor() {
/* 65 */     if (this.count == 0L)
/* 66 */       return Color.GREEN; 
/* 67 */     if (this.count >= 1L && this.count <= this.config.lowDef()) {
/* 68 */       return Color.YELLOW;
/*    */     }
/* 70 */     return Color.WHITE;
/*    */   }
/*    */ }


/* Location:              C:\Users\Blade\Downloads\SocketDefence_1_1.jar!\net\runelite\client\plugins\socketdefence\DefenceInfoBox.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */