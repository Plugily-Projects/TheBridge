package plugily.projects.thebridge.api.events.game;

import org.bukkit.event.HandlerList;
import plugily.projects.thebridge.api.events.TheBridgeEvent;
import plugily.projects.thebridge.arena.Arena;

/**
 * @author MatiRosen
 * Called when the round start again, after the time on the cage, and on the first round.
 */
public class TBRoundStartEvent extends TheBridgeEvent {

  private static final HandlerList handlers = new HandlerList();

  public TBRoundStartEvent(Arena arena){
    super(arena);
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

}