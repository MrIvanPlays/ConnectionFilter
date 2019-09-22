/*
    Copyright (c) 2019 Ivan Pekov
    Copyright (c) 2019 Contributors

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
*/
package com.mrivanplays.connectionfilter;

import com.mrivanplays.connectionfilter.commands.CommandPluginReload;
import com.mrivanplays.connectionfilter.listeners.PreLoginListener;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class ConnectionFilter extends JavaPlugin {

  @Override
  public void onEnable() {
    saveDefaultConfig();
    new MetricsLite(this);
    getServer().getPluginManager().registerEvents(new PreLoginListener(this), this);
    CommandPluginReload command = new CommandPluginReload(this);
    getCommand("connectionfilter").setExecutor(command);
    getCommand("connectionfilter").setTabCompleter(command);
    getLogger().info("Plugin enabled!");
  }

  @Override
  public void onDisable() {
    getLogger().info("Plugin disabled!");
  }

  public String color(String text) {
    return ChatColor.translateAlternateColorCodes('&', text);
  }
}
