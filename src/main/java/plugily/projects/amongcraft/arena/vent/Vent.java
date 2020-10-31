package plugily.projects.amongcraft.arena.vent;

import plugily.projects.amongcraft.arena.Arena;

import org.jetbrains.annotations.Nullable;

/**
 * @author Plajer
 * <p>
 * Created at 31.10.2020
 */
public class Vent {

  private Arena arena;
  @Nullable private Vent rightVent;
  @Nullable private Vent leftVent;

  public Vent(Arena arena, @Nullable Vent rightVent, @Nullable Vent leftVent) {
    this.arena = arena;
    this.rightVent = rightVent;
    this.leftVent = leftVent;
  }

  public Arena getArena() {
    return arena;
  }

  @Nullable
  public Vent getRightVent() {
    return rightVent;
  }

  @Nullable
  public Vent getLeftVent() {
    return leftVent;
  }
}
