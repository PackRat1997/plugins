package ryan.ryanschatplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Random;

import static java.lang.Math.random;

public final class Ryanschatplugin extends JavaPlugin implements CommandExecutor {

    public static boolean game_active = false;
    public static String answer = "";
    public static String version = "1.3.17 BETA";
    public static String question = "";
    public static Ryanschatplugin plugin;
    public void loadConfiguration() {
        //See "Creating you're defaults"
        this.getConfig().options().copyDefaults(true); // NOTE: You do not have to use "plugin." if the class extends the java plugin
        //Save the config whenever you manipulate it
        this.saveDefaultConfig();
    }
    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new chat_events(), this);
        loadConfiguration();
        plugin = this;

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    public static int gamemode = 1;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String prefix = "";
        if(label.equalsIgnoreCase("chatgames")) {

            if (sender.hasPermission("chatgames.admin")|| sender.getName().equalsIgnoreCase("CrafterRyan04")) {
                if (args.length == 0) {
                    sender.sendMessage(ChatColor.RED + "Use /chatgames start [math/scramble/both] or /chatgames stop");
                }
                else if (args[0].equalsIgnoreCase("start")) {
                    if (game_active) {
                        sender.sendMessage(ChatColor.RED + "Game is already active!");
                    } else {
                        if (args.length == 1) {
                            gamemode = 1;
                        } else {
                            if (args[1].equalsIgnoreCase("all")) {
                                gamemode = 1;
                            } else if (args[1].equalsIgnoreCase("math")) {
                                gamemode = 2;
                            } else if (args[1].equalsIgnoreCase("scramble")) {
                                gamemode = 3;
                            }
                                else if (args[1].equalsIgnoreCase("trivia")) {
                                    gamemode = 4;
                            }
                        }

                        game_startup(sender, command, label, args);

                    }
                } else if (args[0].equalsIgnoreCase("stop")) {
                    if (game_active) {
                        game_active = false;
                        sender.sendMessage(ChatColor.RED + "Game stopped");
                        Bukkit.broadcastMessage("" + ChatColor.RED + sender.getName() + " has ended game");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Cannot stop game if no game is active");
                    }
                } else if (args[0].equalsIgnoreCase("skip")) {
                    if (gamemode == 4) {
                        Bukkit.broadcastMessage("" + ChatColor.GREEN + sender.getName() + " has skipped a question!");
                        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                            @Override
                            public void run() {
                                start_trivia_game();
                            }
                        }, (3*20L));
                    }
                    else if(gamemode == 3) {
                        Bukkit.broadcastMessage("" + ChatColor.GREEN + sender.getName() + " has skipped a question!");
                        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                            @Override
                            public void run() {
                                start_scramble_game();
                            }
                        }, (3*20L));
                    }
                    else if (gamemode == 2) {
                        Bukkit.broadcastMessage("" + ChatColor.GREEN + sender.getName() + " has skipped a question!");
                        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                            @Override
                            public void run() {
                                start_math_game();
                            }
                        }, (3*20L));
                    }
                    else if (gamemode == 1) {
                        Bukkit.broadcastMessage("" + ChatColor.GREEN + sender.getName() + " has skipped a question!");

                        int toggleb = (int) Math.rint(Math.random()*3+1);
                        if(toggleb == 1) {
                            Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                                @Override
                                public void run() {
                                    start_math_game();
                                }
                            }, (3*20L));
                        }
                        else if (toggleb == 2){
                            Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                                @Override
                                public void run() {
                                    start_scramble_game();
                                }
                            }, (3*20L));
                        }
                        else {
                            Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                                @Override
                                public void run() {
                                    start_trivia_game();
                                }
                            }, (3*20L));
                        }
                    }

                }
                else if(args[0].equalsIgnoreCase("version")) {
                    sender.sendMessage("" +"This server is running ChatGames version " + version);
                }
                else if(args[0].equalsIgnoreCase("help")) {
                    sender.sendMessage(ChatColor.YELLOW + "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
                    sender.sendMessage(ChatColor.BLUE + "\n[ChatGames Help]");
                    sender.sendMessage(ChatColor.YELLOW + "\nStart a game with /chatgames start [math/scramble/trivia/all]");
                    sender.sendMessage(ChatColor.YELLOW + "Skip question with /chatgames skip");
                    sender.sendMessage(ChatColor.YELLOW + "End game with /chatgames stop");
                    sender.sendMessage(ChatColor.YELLOW + "Reload config with /chatgames reload");
                    sender.sendMessage(ChatColor.YELLOW + "Get the answer to the question with /chatgames answer");
                    sender.sendMessage(ChatColor.YELLOW + "Repeat question with /chatgames repeat");
                    sender.sendMessage(ChatColor.YELLOW + "\n-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

                }
                else if (args[0].equalsIgnoreCase("reload")) {
                    plugin.reloadConfig();
                    sender.sendMessage("" + ChatColor.YELLOW + "Reloaded.");
                }
                else if (args[0].equalsIgnoreCase("answer")) {
                    sender.sendMessage("" + ChatColor.YELLOW + "The answer is: " + ChatColor.ITALIC + answer);
                }
                else if (args[0].equalsIgnoreCase("repeat")) {
                    repeat_question((Player) sender);
                }
                else
                 {
                    sender.sendMessage(ChatColor.RED + "Use /chatgames start [math/scramble/trivia/all] or /chatgames stop");
                }
            }
        else {
            if (args[0].equalsIgnoreCase("repeat")) {
                repeat_question((Player) sender);
            }
            else if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(ChatColor.YELLOW + "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
                sender.sendMessage(ChatColor.BLUE + "\n[ChatGames Help] - "+ ChatColor.RED+"(non admin)"+ChatColor.BLUE +"");
                sender.sendMessage(ChatColor.YELLOW + "\nRepeat question with /chatgames repeat");
                sender.sendMessage(ChatColor.GREEN + "\nChatGames was made by: CrafterRyan04");
                sender.sendMessage(ChatColor.YELLOW + "\n-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

            }
            else {
                sender.sendMessage(ChatColor.RED + "Sorry, you don't have permission!");
            }
        } }
            return true;

    }
    private void repeat_question(Player sender) {
        String prefix = "";
        if(game_active) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.YELLOW + "The question was: " + ChatColor.ITALIC + question);
        }
        else {
            sender.sendMessage(ChatColor.RED + "There is no active game");
        }

    }
    private void game_startup(CommandSender sender, Command command, String label, String[] args) {
        game_active = true;
        String prefix = "";

        sender.sendMessage(ChatColor.GREEN + "Game Started");
        if(gamemode == 4) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.GREEN + sender.getName() + " has started Trivia!");
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                @Override
                public void run() {
                    start_trivia_game();
                }
            }, (3*20L));
        }
        if(gamemode == 3) {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.GREEN + sender.getName() + " has started Scramble!");
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                @Override
                public void run() {
                    start_scramble_game();
                }
            }, (3*20L));
        }
        else if (gamemode == 2) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.GREEN + sender.getName() + " has started Math Games");
                Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                    @Override
                    public void run() {
                        start_math_game();
                    }
                }, (3*20L));
        }
        else if (gamemode == 1) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.GREEN + sender.getName() + " has started all the games!");

            int toggleb = (int) Math.rint(Math.random()*3+1);
            if(toggleb == 1) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                    @Override
                    public void run() {
                        start_math_game();
                    }
                }, (3*20L));
            }
            else {
                Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                    @Override
                    public void run() {
                        start_scramble_game();
                    }
                }, (3*20L));
            }
        }
    }


    public static void start_math_game() {
        String prefix = "";
        FileConfiguration config = plugin.getConfig();
        int minimum = config.getInt("minimum-number");
        int maximum = config.getInt("maximum-number");
        int toggle = config.getInt("addition-subtraction-both");
        boolean allowNegatives = config.getBoolean("allow-negatives");

        int firstNumber = (int) Math.rint(Math.random()*(maximum-minimum)+minimum);
        int secondNumber = (int) Math.rint(Math.random()*(maximum-minimum)+minimum);
        int addsub = (int) Math.rint(Math.random()+1);

        if (firstNumber < secondNumber && toggle != 1 && !allowNegatives) {
            int FirstNumber = firstNumber;
            int SecondNumber = secondNumber;
            firstNumber = SecondNumber;
            secondNumber = FirstNumber;
        }

        if(toggle == 1) {
            answer = Integer.toString(firstNumber + secondNumber);
            question = "What's "+firstNumber+" + "+secondNumber + "?";
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.GREEN + "What's "+firstNumber +" + " + secondNumber + "?");
            //Bukkit.broadcastMessage(ChatColor.BLUE + "[ChatGames] "+ ChatColor.GREEN + "\"/chatgames repeat\" to repeat the question");
        }
        else if (toggle == 2) {
            answer = Integer.toString(firstNumber - secondNumber);
            question = "What's "+firstNumber+" - "+secondNumber + "?";
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.GREEN + " What's"+firstNumber +" - " + secondNumber + "?");
            //Bukkit.broadcastMessage(ChatColor.BLUE + "[ChatGames] "+ ChatColor.GREEN + "\"/chatgames repeat\" to repeat the question");


        }
        else if (toggle == 3) {


            if(addsub == 1) {
                answer = Integer.toString(firstNumber + secondNumber);
                question = "What's "+firstNumber+" + "+secondNumber + "?";
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.GREEN + "What's "+firstNumber +" + " + secondNumber + "?");
                //Bukkit.broadcastMessage(ChatColor.BLUE + "[ChatGames] "+ ChatColor.GREEN + "\"/chatgames repeat\" to repeat the question");
            }
            else {
                answer = Integer.toString(firstNumber - secondNumber);
                question = "What's "+firstNumber+" - "+secondNumber + "?";
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.GREEN + "What's "+firstNumber +" - " + secondNumber + "?");
                //Bukkit.broadcastMessage(ChatColor.BLUE + "[ChatGames] "+ ChatColor.GREEN + "\"/chatgames repeat\" to repeat the question");

            }
        }


    }
    public static void start_scramble_game() {
        String prefix = "";
        FileConfiguration config = plugin.getConfig();
        List words = config.getList("words-to-scramble");
        Random r = new Random();
        String randomWord = (String) words.get(r.nextInt(words.size()));

        String scrambledWord = scramble(r, randomWord);

        answer = randomWord;
        question = "What's this unscrambled word: "+scrambledWord + "?";
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.GREEN + "Unscramble this word: " + scrambledWord);
        //Bukkit.broadcastMessage(ChatColor.BLUE + "[ChatGames] "+ ChatColor.GREEN + "\"/chatgames repeat\" to repeat the question");

    }
    public static void start_trivia_game() {
        String prefix = "";


        FileConfiguration config = plugin.getConfig();
        List trivia = config.getList("trivia-questions");
        List answers = config.getList("trivia-answers");
        Random r = new Random();
        int c = (int) r.nextInt(trivia.size());
        String randomQuestion = (String) trivia.get(c);
        answer = (String) answers.get(c);
        question = randomQuestion;
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.GREEN +randomQuestion);

    }
    public static String scramble(Random random, String inputString )
    {
        // Convert your string into a simple char array:
        char a[] = inputString.toCharArray();

        // Scramble the letters using the standard Fisher-Yates shuffle,
        for( int i=0 ; i<a.length ; i++ )
        {
            int j = random.nextInt(a.length);
            // Swap letters
            char temp = a[i]; a[i] = a[j];  a[j] = temp;
        }

        return new String(a);
    }
}


