package plugily.projects.thebridge.arena.managers;

import plugily.projects.minigamesbox.classic.arena.managers.PluginMapRestorerManager;
import plugily.projects.thebridge.arena.Arena;

public class MapRestorerManager extends PluginMapRestorerManager {

  public final Arena arena;

  public MapRestorerManager(Arena arena) {
    super(arena);
    this.arena = arena;
  }

  @Override
  public void fullyRestoreArena() {
    arena.cleanUpArena();
    super.fullyRestoreArena();
  }
}
