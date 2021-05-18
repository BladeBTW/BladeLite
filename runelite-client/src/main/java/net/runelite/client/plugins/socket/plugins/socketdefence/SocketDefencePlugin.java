/*     */ package net.runelite.client.plugins.socket.plugins.socketdefence;
/*     */ 
/*     */ import com.google.inject.Provides;
/*     */ import java.awt.Color;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import javax.inject.Inject;
/*     */ import net.runelite.api.Client;
/*     */ import net.runelite.api.Skill;
/*     */ import net.runelite.api.Varbits;
/*     */ import net.runelite.api.coords.WorldPoint;
/*     */ import net.runelite.api.events.AnimationChanged;
/*     */ import net.runelite.api.events.ChatMessage;
/*     */ import net.runelite.api.events.HitsplatApplied;
/*     */ import net.runelite.api.events.NpcDespawned;
/*     */ import net.runelite.api.events.VarbitChanged;
/*     */ import net.runelite.client.config.ConfigManager;
/*     */ import net.runelite.client.eventbus.EventBus;
/*     */ import net.runelite.client.eventbus.Subscribe;
/*     */ import net.runelite.client.events.ConfigChanged;
/*     */ import net.runelite.client.game.SkillIconManager;
/*     */ import net.runelite.client.plugins.Plugin;
/*     */ import net.runelite.client.plugins.PluginDescriptor;
/*     */ import net.runelite.client.plugins.socket.org.json.JSONObject;
/*     */ import net.runelite.client.plugins.socket.packet.SocketBroadcastPacket;
/*     */ import net.runelite.client.plugins.socket.packet.SocketReceivePacket;
/*     */ import net.runelite.client.ui.overlay.Overlay;
/*     */ import net.runelite.client.ui.overlay.OverlayManager;
/*     */ import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
/*     */ import net.runelite.client.util.ColorUtil;
/*     */ import net.runelite.client.util.Text;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @PluginDescriptor(name = "Socket - Defence", description = "Shows defence level for different bosses after specs", tags = {"socket", "pvm", "cox", "gwd", "corp", "tob"})
/*     */ public class SocketDefencePlugin
/*     */   extends Plugin
/*     */ {
/*     */   @Inject
/*     */   private Client client;
/*     */   @Inject
/*     */   private OverlayManager overlayManager;
/*     */   @Inject
/*     */   private EventBus eventBus;
/*     */   @Inject
/*     */   private SkillIconManager skillIconManager;
/*     */   @Inject
/*     */   private InfoBoxManager infoBoxManager;
/*     */   @Inject
/*     */   private SocketDefenceConfig config;
/*     */   @Inject
/*     */   private SocketDefenceOverlay overlay;
/*  59 */   public ArrayList<String> socketPlayerNames = new ArrayList<>();
/*  60 */   public String boss = "";
/*  61 */   public double bossDef = 0.0D;
/*  62 */   public String specWep = "";
/*  63 */   public DefenceInfoBox box = null;
/*  64 */   public BufferedImage img = null;
/*     */   public boolean isInCm = false;
/*  66 */   public ArrayList<String> bossList = new ArrayList<>(Arrays.asList(new String[] { "Corporeal Beast", "General Graardor", "K'ril Tsutsaroth", "Kalphite Queen", "The Maiden of Sugadinti", "Xarpus", "Great Olm (Left claw)", "Tekton", "Tekton (enraged)" }));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void startUp() throws Exception {
/*  73 */     reset();
/*  74 */     this.overlayManager.add((Overlay)this.overlay);
/*     */   }
/*     */   
/*     */   protected void shutDown() throws Exception {
/*  78 */     reset();
/*  79 */     this.overlayManager.remove((Overlay)this.overlay);
/*     */   }
/*     */   
/*     */   protected void reset() {
/*  83 */     this.infoBoxManager.removeInfoBox(this.box);
/*  84 */     this.socketPlayerNames.clear();
/*  85 */     this.boss = "";
/*  86 */     this.bossDef = -1.0D;
/*  87 */     this.specWep = "";
/*  88 */     this.box = null;
/*  89 */     this.img = null;
/*  90 */     this.isInCm = false;
/*     */   }
/*     */   
/*     */   @Provides
/*     */   SocketDefenceConfig getConfig(ConfigManager configManager) {
/*  95 */     return (SocketDefenceConfig)configManager.getConfig(SocketDefenceConfig.class);
/*     */   }
/*     */   
/*     */   @Subscribe
/*     */   public void onAnimationChanged(AnimationChanged event) {
/* 100 */     if (event.getActor() != null && this.client.getLocalPlayer() != null && event.getActor().getName() != null) {
/* 101 */       String actorName = event.getActor().getName();
/* 102 */       int animation = event.getActor().getAnimation();
/*     */       
/* 104 */       if (actorName.equals(this.client.getLocalPlayer().getName())) {
/* 105 */         if (animation == 1378 || animation == 7642 || event.getActor().getAnimation() == 7643 || animation == 2890) {
/* 106 */           if (this.bossList.contains(event.getActor().getInteracting().getName())) {
/* 107 */             this.boss = event.getActor().getInteracting().getName();
/*     */             
/* 109 */             if (event.getActor().getAnimation() == 1378) {
/* 110 */               this.specWep = "dwh";
/* 111 */             } else if (event.getActor().getAnimation() == 7642 || event.getActor().getAnimation() == 7643) {
/* 112 */               this.specWep = "bgs";
/* 113 */             } else if (event.getActor().getAnimation() == 2890) {
/* 114 */               this.specWep = "arclight";
/*     */             } 
/*     */           } 
/*     */         } else {
/* 118 */           this.specWep = "";
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   @Subscribe
/*     */   public void onHitsplatApplied(HitsplatApplied event) {
/* 126 */     if (!this.boss.equals("") && !this.specWep.equals("") && (
/* 127 */       event.getActor().getName().equals(this.boss) || (event.getActor().getName().contains("Tekton") && this.boss.contains("Tekton"))) && 
/* 128 */       event.getHitsplat().getAmount() >= 0 && event.getHitsplat().isMine()) {
/* 129 */       JSONObject data = new JSONObject();
/* 130 */       data.put("boss", this.boss);
/* 131 */       data.put("weapon", this.specWep);
/* 132 */       data.put("hit", event.getHitsplat().getAmount());
/* 133 */       JSONObject payload = new JSONObject();
/* 134 */       payload.put("socketdefence", data);
/* 135 */       this.eventBus.post(new SocketBroadcastPacket(payload));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Subscribe
/*     */   public void onNpcDespawned(NpcDespawned event) {
/* 143 */     if (event.getNpc().getName() != null && (
/* 144 */       event.getNpc().getName().equals(this.boss) || (event.getActor().getName().contains("Tekton") && this.boss.contains("Tekton"))) && event.getNpc().isDead()) {
/* 145 */       JSONObject data = new JSONObject();
/* 146 */       data.put("boss", this.boss);
/* 147 */       JSONObject payload = new JSONObject();
/* 148 */       payload.put("socketdefencebossdead", data);
/* 149 */       this.eventBus.post(new SocketBroadcastPacket(payload));
/* 150 */       this.boss = "";
/* 151 */       this.bossDef = -1.0D;
/* 152 */       this.specWep = "";
/* 153 */       this.infoBoxManager.removeInfoBox(this.box);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Subscribe
/*     */   public void onSocketReceivePacket(SocketReceivePacket event) {
/*     */     try {
/* 161 */       JSONObject payload = event.getPayload();
/* 162 */       if (payload.has("socketdefence")) {
/* 163 */         JSONObject data = payload.getJSONObject("socketdefence");
/* 164 */         String bossName = data.getString("boss");
/* 165 */         String weapon = data.getString("weapon");
/* 166 */         int hit = data.getInt("hit");
/*     */         
/* 168 */         if (((bossName.contains("Tekton") || bossName.contains("Great Olm")) && this.client.getVar(Varbits.IN_RAID) != 1) || ((bossName
/* 169 */           .contains("The Maiden of Sugadinti") || bossName.contains("Xarpus")) && this.client.getVar(Varbits.THEATRE_OF_BLOOD) != 2)) {
/*     */           return;
/*     */         }
/*     */         
/* 173 */         if (this.boss.equals("") || this.bossDef == -1.0D || !this.boss.equals(bossName)) {
/* 174 */           if (bossName.equals("Corporeal Beast")) {
/* 175 */             this.bossDef = 310.0D;
/* 176 */           } else if (bossName.equals("General Graardor")) {
/* 177 */             this.bossDef = 250.0D;
/* 178 */           } else if (bossName.equals("K'ril Tsutsaroth")) {
/* 179 */             this.bossDef = 270.0D;
/* 180 */           } else if (bossName.equals("Kalphite Queen")) {
/* 181 */             this.bossDef = 300.0D;
/* 182 */           } else if (bossName.equals("The Maiden of Sugadinti")) {
/* 183 */             this.bossDef = 200.0D;
/* 184 */           } else if (bossName.equals("Xarpus")) {
/* 185 */             this.bossDef = 250.0D;
/* 186 */           } else if (bossName.equals("Great Olm (Left claw)")) {
/* 187 */             this.bossDef = 175.0D * (1.0D + 0.01D * (this.client.getVarbitValue(5424) - 1));
/*     */             
/* 189 */             if (this.isInCm) {
/* 190 */               this.bossDef *= 1.5D;
/*     */             }
/* 192 */           } else if (bossName.contains("Tekton")) {
/* 193 */             this.bossDef = 205.0D * (1.0D + 0.01D * (this.client.getVarbitValue(5424) - 1));
/*     */             
/* 195 */             if (this.isInCm) {
/* 196 */               this.bossDef *= 1.2D;
/*     */             }
/*     */           } 
/* 199 */           this.boss = bossName;
/*     */         } 
/*     */         
/* 202 */         if ((this.bossDef != -1.0D && !this.boss.equals("")) || (!this.boss.equals(bossName) && !this.boss.contains("Tekton") && !bossName.contains("Tekton"))) {
/* 203 */           if (weapon.equals("dwh") && hit == 0) {
/* 204 */             if (this.client.getVar(Varbits.IN_RAID) == 1 && this.boss.contains("Tekton")) {
/* 205 */               this.bossDef -= this.bossDef * 0.05D;
/*     */             }
/* 207 */           } else if (weapon.equals("dwh") && hit > 0) {
/* 208 */             this.bossDef -= this.bossDef * 0.3D;
/* 209 */           } else if (weapon.equals("bgs")) {
/* 210 */             if (this.boss.equals("Corporeal Beast")) {
/* 211 */               this.bossDef -= (hit * 2);
/*     */             } else {
/* 213 */               this.bossDef -= hit;
/*     */             } 
/* 215 */           } else if (weapon.equals("arclight") && hit > 0) {
/* 216 */             this.bossDef -= this.bossDef * 0.05D;
/*     */           } 
/*     */           
/* 219 */           if (this.bossDef < -1.0D) {
/* 220 */             this.bossDef = 0.0D;
/*     */           }
/* 222 */           this.infoBoxManager.removeInfoBox(this.box);
/* 223 */           this.img = this.skillIconManager.getSkillImage(Skill.DEFENCE);
/* 224 */           this.box = new DefenceInfoBox(this.img, this, Math.round(this.bossDef), this.config);
/* 225 */           this.box.setTooltip(ColorUtil.wrapWithColorTag(this.boss, Color.WHITE));
/* 226 */           this.infoBoxManager.addInfoBox(this.box);
/*     */         } 
/* 228 */       } else if (payload.has("socketdefencebossdead")) {
/* 229 */         JSONObject data = payload.getJSONObject("socketdefencebossdead");
/* 230 */         String bossName = data.getString("boss");
/*     */         
/* 232 */         if (bossName.equals(this.boss) || (bossName.contains("Tekton") && this.boss.contains("Tekton"))) {
/* 233 */           this.boss = "";
/* 234 */           this.bossDef = -1.0D;
/* 235 */           this.specWep = "";
/* 236 */           this.infoBoxManager.removeInfoBox(this.box);
/*     */         } 
/* 238 */       } else if (this.config.cm()) {
/* 239 */         this.isInCm = true;
/*     */       } 
/* 241 */     } catch (Exception e) {
/* 242 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   @Subscribe
/*     */   private void onChatMessage(ChatMessage event) {
/* 248 */     String text = Text.standardize(event.getMessageNode().getValue());
/* 249 */     if (text.contains("member (") || text.contains("members (")) {
/* 250 */       text = text.substring(text.indexOf(":") + 1).trim();
/* 251 */       this.socketPlayerNames.clear();
/*     */       byte b;
/*     */       int i;
/*     */       String[] arrayOfString;
/* 255 */       for (i = (arrayOfString = text.split(",")).length, b = 0; b < i; ) {
/* 256 */         String str = arrayOfString[b];
/* 257 */         str = str.trim();
/* 258 */         if (!"".equals(str))
/* 259 */           this.socketPlayerNames.add(str.toLowerCase()); 
/* 260 */         b = (byte)(b + 1);
/*     */       } 
/*     */     } 
/* 263 */     if (text.contains("any active socket server connections were closed")) {
/* 264 */       this.socketPlayerNames.clear();
/*     */     }
/*     */   }
/*     */   
/*     */   @Subscribe
/*     */   private void onVarbitChanged(VarbitChanged event) {
/* 270 */     if (this.client.getVarbitValue(6385) != 0) {
/* 271 */       JSONObject data = new JSONObject();
/* 272 */       data.put("cm", this.boss.toLowerCase());
/* 273 */       JSONObject payload = new JSONObject();
/* 274 */       payload.put("socketdefencecm", data);
/* 275 */       this.eventBus.post(new SocketBroadcastPacket(payload));
/*     */     } 
/* 277 */     if (this.client.getVar(Varbits.IN_RAID) != 1 && (
/* 278 */       this.boss.toLowerCase().contains("tekton") || this.boss.toLowerCase().contains("great olm (left claw)"))) {
/* 279 */       reset();
/*     */     }
/*     */ 
/*     */     
/* 283 */     if (this.boss.toLowerCase().contains("the maiden of sugadinti") && getInstanceRegionId() != TobRegions.MAIDEN.getRegionId()) {
/* 284 */       reset();
/* 285 */     } else if (this.boss.toLowerCase().contains("xarpus") && getInstanceRegionId() != TobRegions.XARPUS.getRegionId()) {
/* 286 */       reset();
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getInstanceRegionId() {
/* 291 */     return WorldPoint.fromLocalInstance(this.client, this.client.getLocalPlayer().getLocalLocation()).getRegionID();
/*     */   }
/*     */   
/*     */   public enum TobRegions {
/* 295 */     MAIDEN(12613),
/* 296 */     BLOAT(13125),
/* 297 */     NYLOCAS(13122),
/* 298 */     SOTETSEG(13123),
/* 299 */     SOTETSEG_MAZE(13379),
/* 300 */     XARPUS(12612),
/* 301 */     VERZIK(12611);
/*     */     
/*     */     private final int regionId;
/*     */     
/*     */     TobRegions(int regionId) {
/* 306 */       this.regionId = regionId;
/*     */     }
/*     */     
/*     */     public int getRegionId() {
/* 310 */       return this.regionId;
/*     */     }
/*     */   }
/*     */   
/*     */   @Subscribe
/*     */   public void onConfigChanged(ConfigChanged e) {
/* 316 */     if (this.config.cm()) {
/* 317 */       this.isInCm = true;
/*     */     } else {
/* 319 */       this.isInCm = false;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Blade\Downloads\SocketDefence_1_1.jar!\net\runelite\client\plugins\socketdefence\SocketDefencePlugin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */