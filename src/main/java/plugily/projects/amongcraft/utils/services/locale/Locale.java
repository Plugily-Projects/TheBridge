package plugily.projects.amongcraft.utils.services.locale;

import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 31.10.2020
 */
public class Locale {

  private final String name;
  private final String originalName;
  private final String prefix;
  private final String author;
  private final List<String> aliases;

  public Locale(String name, String originalName, String prefix, String author, List<String> aliases) {
    this.prefix = prefix;
    this.name = name;
    this.originalName = originalName;
    this.author = author;
    this.aliases = aliases;
  }

  /**
   * Gets name of locale, ex. English or German
   *
   * @return name of locale
   */
  public String getName() {
    return name;
  }

  /**
   * Gets original name of locale ex. for German it will return Deutsch, Polish returns Polski etc.
   *
   * @return name of locale in its language
   */
  public String getOriginalName() {
    return originalName;
  }

  /**
   * @return authors of locale
   */
  public String getAuthor() {
    return author;
  }

  /**
   * Language code ex. en_GB, de_DE, pl_PL etc.
   *
   * @return language code of locale
   */
  public String getPrefix() {
    return prefix;
  }

  /**
   * Valid aliases of locale ex. for German - deutsch, de, german; Polish - polski, pl, polish etc.
   *
   * @return aliases for locale
   */
  public List<String> getAliases() {
    return aliases;
  }

}
