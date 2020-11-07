package plugily.projects.thebridge.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Plajer
 * <p>
 * Created at 31.10.2020
 */
public class Utils {

  public static String matchColorRegex(String s) {
    String regex = "&?#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})";
    Matcher matcher = Pattern.compile(regex).matcher(s);
    while (matcher.find()) {
      String group = matcher.group(0);
      String group2 = matcher.group(1);

      try {
        s = s.replace(group, net.md_5.bungee.api.ChatColor.of("#" + group2) + "");
      } catch (Exception e) {
        Debugger.debug("Bad hex color match: " + group);
      }
    }

    return s;
  }

}
