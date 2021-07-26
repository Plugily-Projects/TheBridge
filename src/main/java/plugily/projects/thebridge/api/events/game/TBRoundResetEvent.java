package plugily.projects.thebridge.api.events.game;

import org.bukkit.event.HandlerList;
import plugily.projects.thebridge.api.events.TheBridgeEvent;
import plugily.projects.thebridge.arena.Arena;

public class TBRoundResetEvent extends TheBridgeEvent {

  private static final HandlerList handlers = new HandlerList();
  private final int actualRound;

  public TBRoundResetEvent(Arena arena, int round){
    super(arena);
    this.actualRound = round;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public int getNewRound(){
    return actualRound;
  }
}