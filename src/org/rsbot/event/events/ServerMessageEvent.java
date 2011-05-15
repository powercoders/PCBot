/*    */ package org.rsbot.event.events;
/*    */ 
/*    */ import java.util.EventListener;
/*    */ import org.rsbot.event.listeners.ServerMessageListener;
/*    */ 
/*    */ @Deprecated
/*    */ public class ServerMessageEvent extends RSEvent
/*    */ {
/*    */   private static final long serialVersionUID = -2786472026976811201L;
/*    */   private final String message;
/*    */ 
/*    */   public ServerMessageEvent(String message)
/*    */   {
/* 19 */     this.message = message;
/*    */   }
/*    */ 
/*    */   public void dispatch(EventListener el)
/*    */   {
/* 24 */     ((ServerMessageListener)el).serverMessageRecieved(this);
/*    */   }
/*    */ 
/*    */   public long getMask()
/*    */   {
/* 29 */     return 1536L;
/*    */   }
/*    */ 
/*    */   public String getMessage() {
/* 33 */     return this.message;
/*    */   }
/*    */ }

/* Location:           C:\Users\Helcast\Desktop\
 * Qualified Name:     org.rsbot.event.events.ServerMessageEvent
 * JD-Core Version:    0.6.0
 */