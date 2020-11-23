package plugily.projects.thebridge.utils.conversation;

import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.plugin.java.JavaPlugin;
import plugily.projects.thebridge.Main;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 25.05.2019
 */
public class SimpleConversationBuilder {

  private static final Main plugin = JavaPlugin.getPlugin(Main.class);
  private final ConversationFactory conversationFactory;

  public SimpleConversationBuilder() {
    conversationFactory = new ConversationFactory(plugin)
      .withModality(true)
      .withLocalEcho(false)
      .withEscapeSequence("cancel")
      .withTimeout(30)
      .addConversationAbandonedListener(listener -> {
        if (listener.gracefulExit()) {
          return;
        }
        listener.getContext().getForWhom().sendRawMessage(plugin.getChatManager().colorRawMessage("&7Operation cancelled!"));
      })
      .thatExcludesNonPlayersWithMessage(ChatColor.RED + "Only by players!");
  }

  public SimpleConversationBuilder withPrompt(Prompt prompt) {
    conversationFactory.withFirstPrompt(prompt);
    return this;
  }

  public void buildFor(Conversable conversable) {
    conversationFactory.buildConversation(conversable).begin();
  }

}
