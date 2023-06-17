package waterpunch.lectern.plugin.main;

import org.bukkit.plugin.java.JavaPlugin;
import waterpunch.lectern.plugin.event.Event;

public class Core extends JavaPlugin {

  @Override
  public void onEnable() {
    new Event(this);
    System.out.println("lectern_system Start");
  }

  @Override
  public void onDisable() {
    System.out.println("lectern_system Stop");
  }
}
