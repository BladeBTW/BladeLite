/*     */ package net.runelite.client.plugins.socket.plugins.socketdefence;
/*     */ 
/*     */ import com.google.common.collect.ImmutableSet;
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Polygon;
/*     */ import java.awt.Shape;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import javax.inject.Inject;
/*     */ import javax.inject.Singleton;
/*     */ import net.runelite.api.Actor;
/*     */ import net.runelite.api.Client;
/*     */ import net.runelite.api.NPC;
/*     */ import net.runelite.api.NPCComposition;
/*     */ import net.runelite.api.Perspective;
/*     */ import net.runelite.api.coords.LocalPoint;
/*     */ import net.runelite.client.graphics.ModelOutlineRenderer;
/*     */ import net.runelite.client.ui.overlay.OverlayLayer;
/*     */ import net.runelite.client.ui.overlay.OverlayPanel;
/*     */ import net.runelite.client.ui.overlay.OverlayPosition;
/*     */ import net.runelite.client.ui.overlay.OverlayPriority;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Singleton
/*     */ public class SocketDefenceOverlay
/*     */   extends OverlayPanel
/*     */ {
            private static final Set<Integer> GAP;
/*  46 */    static {
    GAP = (Set)ImmutableSet.of((Object)34, (Object)33, (Object)26, (Object)25, (Object)18, (Object)17, (Object[])new Integer[] { 10, 9, 2, 1 });
}
/*     */   
/*     */   private final Client client;
/*     */   private final SocketDefencePlugin plugin;
/*     */   private final SocketDefenceConfig config;
/*     */   private ModelOutlineRenderer modelOutlineRenderer;
/*     */   
/*     */   @Inject
/*     */   private SocketDefenceOverlay(Client client, SocketDefencePlugin plugin, SocketDefenceConfig config, ModelOutlineRenderer modelOutlineRenderer) {
/*  55 */     this.client = client;
/*  56 */     this.plugin = plugin;
/*  57 */     this.config = config;
/*  58 */     this.modelOutlineRenderer = modelOutlineRenderer;
/*  59 */     setPosition(OverlayPosition.DYNAMIC);
/*  60 */     setPriority(OverlayPriority.HIGH);
/*  61 */     setLayer(OverlayLayer.ABOVE_SCENE);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Dimension render(Graphics2D graphics) {
/*  67 */     if (this.config.corpChally() != CorpTileMode.OFF)
/*  68 */       for (NPC npc : this.client.getNpcs()) {
/*  69 */         if (((String)Objects.<String>requireNonNull(npc.getName())).toLowerCase().equals("corporeal beast")) {
/*  70 */           Color color = Color.RED;
/*     */           
/*  72 */           if (this.plugin.bossDef >= 0.0D && this.plugin.bossDef <= 10.0D) {
/*  73 */             color = Color.GREEN;
/*     */           }
/*  75 */           if (this.config.corpChally() == CorpTileMode.AREA) {
/*  76 */             renderAreaOverlay(graphics, npc, color);
/*     */             continue;
/*     */           } 
/*  79 */           if (this.config.corpChally() == CorpTileMode.TILE) {
/*  80 */             NPCComposition npcComp = npc.getComposition();
/*  81 */             int size = npcComp.getSize();
/*  82 */             LocalPoint lp = npc.getLocalLocation();
/*  83 */             Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, lp, size);
/*  84 */             renderPoly(graphics, color, tilePoly);
/*     */             continue;
/*     */           } 
/*  87 */           if (this.config.corpChally() == CorpTileMode.HULL) {
/*  88 */             Shape objectClickbox = npc.getConvexHull();
/*  89 */             if (objectClickbox != null) {
/*  90 */               graphics.setStroke(new BasicStroke(this.config.corpChallyThicc()));
/*  91 */               graphics.setColor(color);
/*  92 */               graphics.draw(objectClickbox);
/*     */             } 
/*     */             continue;
/*     */           } 
/*  96 */           if (this.config.corpChally() == CorpTileMode.TRUE_LOCATIONS) {
/*  97 */             int size = 1;
/*  98 */             NPCComposition composition = npc.getTransformedComposition();
/*  99 */             if (composition != null)
/* 100 */               size = composition.getSize(); 
/* 101 */             LocalPoint lp = LocalPoint.fromWorld(this.client, npc.getWorldLocation());
/* 102 */             if (lp != null) {
/* 103 */               lp = new LocalPoint(lp.getX() + size * 128 / 2 - 64, lp.getY() + size * 128 / 2 - 64);
/* 104 */               Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, lp, size);
/* 105 */               renderPoly(graphics, color, tilePoly);
/*     */             } 
/*     */             continue;
/*     */           } 
/* 109 */           if (this.config.corpChally() == CorpTileMode.OUTLINE) {
/* 110 */             this.modelOutlineRenderer.drawOutline((Actor)npc, 2, color);
/*     */           }
/*     */         } 
/*     */       }  
/* 114 */     return null;
/*     */   }
/*     */   
/*     */   private void renderAreaOverlay(Graphics2D graphics, NPC actor, Color color) {
/* 118 */     Shape objectClickbox = actor.getConvexHull();
/* 119 */     if (objectClickbox != null) {
/* 120 */       graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
/* 121 */       graphics.fill(actor.getConvexHull());
/*     */     } 
/*     */   }
/*     */   
/*     */   private void renderPoly(Graphics2D graphics, Color color, Shape polygon) {
/* 126 */     if (polygon != null) {
/* 127 */       graphics.setColor(color);
/* 128 */       graphics.setStroke(new BasicStroke(this.config.corpChallyThicc()));
/* 129 */       graphics.draw(polygon);
/* 130 */       graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), this.config.corpChallyOpacity()));
/* 131 */       graphics.fill(polygon);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Blade\Downloads\SocketDefence_1_1.jar!\net\runelite\client\plugins\socketdefence\SocketDefenceOverlay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */