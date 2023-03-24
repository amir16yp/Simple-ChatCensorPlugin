package tuxpenguins.chatcensorplugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ChatCensorPlugin extends JavaPlugin implements Listener {

    private List<String> bannedWords;

    @Override
    public void onEnable() {
        loadBannedWords();
        getServer().getPluginManager().registerEvents(this, this);
    }

    private void loadBannedWords() {
        bannedWords = new ArrayList<>();
        String config = getConfig().getString("bannedWords");
        if (config.startsWith("http://") || config.startsWith("https://")) {
            // Load from URL
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(config).openStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    bannedWords.add(line.trim());
                }
            } catch (IOException e) {
                getLogger().warning("Failed to load banned words from URL: " + config);
                e.printStackTrace();
            }
        } else {
            // Load from file
            try {
                List<String> lines = Files.readAllLines(Paths.get(config), StandardCharsets.UTF_8);
                for (String line : lines) {
                    bannedWords.add(line.trim());
                }
            } catch (IOException e) {
                getLogger().warning("Failed to load banned words from file: " + config);
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        for (String bannedWord : bannedWords) {
            if (message.contains(bannedWord)) {
                String asterisks = "";
                for (int i = 0; i < bannedWord.length(); i++) {
                    asterisks += "*";
                }
                message = message.replaceAll(bannedWord, asterisks);
            }
        }
        event.setMessage(message);
    }
}
