### 2.1.6 Release (13.07.2025)
* Updated to MinigamesBox 1.4.3

### 2.1.5 Release (09.06.2025)
* Fixed startup issues on lower mc versions
* Added a onserverjoin message about wrong base arena setup if base cuboid is outside arena cuboid
* Added Death.Blindness in config.yml to change length of blindness effect after death
* Updated to minigamesbox 1.4.2

### 2.1.4 Release (03.05.2025)
* Fixed arena forcestop the game after a player leave the Fullgame and not enough players to continue are online
* Updated language.yml so by default base selection got prefix
* Removed unused permissions in permissions.yml
* Updated to minigamesbox 1.4.1

### 2.1.3 Release (13.03.2025)
* Updated to minigamesbox 1.4.0

### 2.1.2 Release (04.03.2025)
* Added new placeholder for scoreboard scoreboard_base_points to show only numbers at scoreboard
* Added new placeholder base_points to get points of the base a player is in (can be used with MODE_VALUE to get max value)
* Updated to minigamesbox 1.3.17

### 2.1.1 Release (18.02.2025)
* Fixed potions on different server versions
* Updated to minigamesbox 1.3.16

### 2.1.0 Release (24.07.2024)
* Fixed duplications of cursor items on death
* Updated to minigamesbox 1.3.14

### 2.0.10 Release (09.07.2024)
* Updated to minigamesbox 1.3.11
* Fixed arena after playing does not reset the arena options from config

### 2.0.9 Release (25.04.2024)
* Updated to minigamesbox 1.3.9

### 2.0.8 Release (24.04.2024)
* Updated to minigamesbox 1.3.8

### 2.0.7 Release (15.01.2024)
* Updated to minigamesbox 1.3.7

### 2.0.6 Release (13.01.2024)
* Changed default ingame time to 900 from 270, bow cooldown to 3 from 5;
* Added Natural-Regeneration and Block-Breakage-Item-Drop config option
* Renamed Food-Lose to Hunger-Lose in config
* Adjusted language; Removed "You can't move" beetween rounds
* Updated to minigamesbox 1.3.6

### 2.0.5 Release (03.01.2024)
* Added new command /tba locale <locale> to set your locale
* Fixed GameProfile API 1.20 change
* Fixed none kits related plugins issues

### 2.0.4 Release (31.12.2024)
* Moved kits.yml to own /kits files
* Updated to minigamesbox 1.3.4

### 2.0.3 Release (04.12.2023)
* Fixed block breakage on bridge
* Fixed blindness after choosing base if arena border isn't inside spawn
* (Experimental) Added new kits.yml where you can edit all kits file based (patreon: in-game gui based) [changes will happen; don't invest much time]
* Added bow cooldown per kit
* Updated to minigamesbox 1.3.3

### 2.0.2 Release (21.08.2023)
Fixed rewards enabling
Fixed bossbar disabling
Fixed locale registration if service unavailable (e.g. server blocks outgoing traffic, cloudflare blacklisted countries)

### 2.0.1 Release (08.08.2023)
* Fixed teleport after death
* Fixed block regeneration for lower 1.13

### 2.0.0 Release (07.08.2023)
* Added up to 1.20 compatibility
* Added full cage support
* Added bridge block breakage [https://app.feedbacky.net/i/3734]
* Added base colours as placeholder [https://app.feedbacky.net/i/2867]
* Changed die screen to just teleportation [https://app.feedbacky.net/i/3721]
* Changed supported languages [https://translate.plugily.xyz]
* Changed plugin base is based on MinigamesCore [https://github.com/Plugily-Projects/MiniGamesBox]
* Changed native java building to java17, java 8 downloadable on our discord [https://discord.plugily.xyz]
* Fixed player can move in cage
* Fixed all known bugs

### 1.1.5 Release (26.08.2021 - 2022) [released as dev version]
* Fixed build check outside of arena border was checking for player and not for block location
* Fixed maxplayers command not checking if arena was already in-game
* Fixed bases without players can be used to get scores

### 1.1.4 Release (19.07.2021 - 01.08.2021)
* Reuse XMaterial for specialitems
* Fixed all Time-Manager values on config.yml do not work
* Fixed DEAD_TAG message not found
* Fixed death chat format not found
* Fixed maxplayers <teamsize> join

### 1.1.3 Release (09.07.2021)
* Fixed golden apple insta heal does not work on lower 1.15 servers

### 1.1.2 Release (18.05.2021 - 09.07.2021)
* 1.17 compatible
* !CLASS NAME CHANGE OF COMMONSBOX!
* Added forcestart item
* Fixed golden apple does not insta heal
* Fixed respawn bug with some other plugins that respawns you on other places
* Fixed netherportals will teleport to nether
* Changed kit slot locations for blocks
>>> b1
* Added title for scored point
* Added possibility to edit bases ingame
* Fixed NPE on disable - Do not attempt to teleport players twice
* Fixed Players are not always teleported to lobby location after falling into void
* Fixed NPE (Cannot invoke) if no cage on base
* Fixed fireticks are not removed after jumped into lava portal
* Fixed arena does not end if there are only players in one base left
* Fixed parties can not join to leader because it throws NPE
* Fixed party (parties v3.1.0) not working anymore
* Fixed portal hologram is not on mid if its manually added
* Fixed when plugins scoreboards not re-appeared after game end
* Removed temp added scoreboard rewards
* Fixed join maxplayers did not match the arena with highest player amount
* Changed empty message is no longer sent

### 1.1.1 Release (23.04.2021 - 10.05.2021)
* Added /tb join maxplayers which tries to get the arena with the highest amount of players
* Added placeholder arena_players_online
* Added possibility to define baseSize on join commands
* Added party players will now be on the same base team as the leader as long as there is enough size on the base
* Added new RewardType scoreboard_add
* Added new placeholder base_color
* Changed You can now move if the base got a cage while round reset
* Changed randomjoin mechanics -> Searches for starting -> random join -> Searches for waiting -> random join

### 1.1.0 Release (21.03.2021 - 13.04.2021)
* Fixed per arena join perm does not work
* Fixed NPE on ArrowEvents and other version improvements
* Fixed when arena selector GUI does not open

### 1.0.9 Release (19.03.2021)
* Added configurable arena selector items (per state)
* Fixed particle issues on some versions

### 1.0.8 Release (17.03.2021)
* Added customizable arena state placeholder

### 1.0.7 Release (18.01.2021 - 15.03.2021)
* Added Legacy Version support
* Added configurable maxLifeTime value for MySQL databases  
* Added TeleportArgument (/tba tp)
* Added new RewardType scoreboard_remove
* Added option to disable hunger completely  
* Fixed tb items are not removed if inventory manager is enabled and the server is stopping
* Fixed players not getting teleported to end location after arena leave
* Fixed cage fall damage
* Fixed 2 stats messages were not added
* Fixed POINTS mode wins/loses stats
* Fixed NPE on fall damage before a player is on any base
* Fixed block dropping on breaking
* Fixed material could not fetch colors on some types of color
* Fixed winner check on Heart mode games with empty bases
* Fixed reload command that does now proper unregister arenas
* Fixed baseselector does not remove players if they leave before the game starts  
* Fixed blocks reset on server stop
* Changed own portal jump to kill players who stuck on own portal
* Changed player movement on round resets now allows head rotation and jumping
* Changed round end message ("%base_players% (%base_scored%)")

### 1.0.6 Release (13.01.2021 - 18.01.2021)
* ArenaRegistry will set isDone to false if arena cuboid is not big enough
* Fixed joining through a sign while hotbar slot for leave-item is active
* Fixed Cage doesn't disappear on first round
* Added error message if cage floor contains Air
* Added customizable item name in arena selector gui (by ajgeiss0702)
* Added preconfigured locales for Czech, Dutch, French, Hungarian, Italian,
  Polish, Portuguese, Russian, Spanish [Thanks to PoEditor contributors]
* Changed SetupInventory to open base menu while editing base
* Fix wrong teleport location on arena leave

### 1.0.5 Release (05.01.2021 - 12.01.2021)
* Fixed Teleportation to the end is now no longer possible
* Fixed NoSuchMethod isAir check
* Added default language.yml for locales integration later

### 1.0.4 Release (04.01.2021)
* Fixed NPE on disable

### 1.0.3 Release (02.01.2021)
* Added working leveling system [MySQL needs manual removal!]
* Changed player death to 5 blocks out of arena instead of void

### 1.0.2 Release (01.01.2021)
* Fixed Setup on 1.11 & 1.12

### 1.0.1 Release (31.12.2020)
* Added UpdateChecker

### 1.0.0 Release (31.12.2020)
* Full release of TheBridge


