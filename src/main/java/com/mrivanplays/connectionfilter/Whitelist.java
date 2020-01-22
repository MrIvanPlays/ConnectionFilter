package com.mrivanplays.connectionfilter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Whitelist {

  private File file;
  private FileConfiguration configuration;

  public Whitelist(File dataFolder) {
    this.file = new File(dataFolder + File.separator, "whitelist.yml");
    createFile();
    configuration = YamlConfiguration.loadConfiguration(file);
  }

  private void createFile() {
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
      try (Writer writer = new FileWriter(file)) {
        writer.write("whitelist:");
        writer.append('\n').write(" - 'MrIvanPlays'");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public List<String> getWhitelist() {
    return configuration.getStringList("whitelist");
  }

  public void reload() {
    if (!file.exists()) {
      createFile();
    }
    configuration = YamlConfiguration.loadConfiguration(file);
  }
}
