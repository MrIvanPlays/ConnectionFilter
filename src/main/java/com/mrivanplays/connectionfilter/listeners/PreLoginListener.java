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
package com.mrivanplays.connectionfilter.listeners;

import com.mrivanplays.connectionfilter.ConnectionFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.scheduler.BukkitTask;

public class PreLoginListener implements Listener {

  private final List<UUID> connectedThisMinute = new ArrayList<>();
  private boolean limited = false;
  private BukkitTask task;
  private final ConnectionFilter plugin;

  public PreLoginListener(ConnectionFilter plugin) {
    this.plugin = plugin;
    plugin
        .getServer()
        .getScheduler()
        .runTaskTimerAsynchronously( // async to prevent lags
            plugin,
            () -> {
              if (!connectedThisMinute.isEmpty()) {
                connectedThisMinute.clear();
              }
            },
            0,
            20 * 60);
  }

  @EventHandler
  public void on(AsyncPlayerPreLoginEvent event) {
    connectedThisMinute.add(event.getUniqueId());
    if (connectedThisMinute.size() == plugin.getConfig().getInt("connections-to-toggle")) {
      for (UUID connected : connectedThisMinute) {
        Player player = plugin.getServer().getPlayer(connected);
        if (player != null) {
          // and now some stupidity of md_5
          // from 1.14 the threads are strict main and async and due to this event called
          // asynchronously and 1.14 not allowing player kicks to be made async we're
          // stick using bukkit scheduler to run something on the main thread.
          plugin
              .getServer()
              .getScheduler()
              .runTask(
                  plugin,
                  () ->
                      player.kickPlayer(
                          plugin.color(plugin.getConfig().getString("kick-message"))));
        }
      }
      event.disallow(
          Result.KICK_OTHER,
          plugin.color(plugin.getConfig().getString("current-connection-disallow-message")));
      limited = true;
    }
    if (limited) {
      if (task == null) {
        task =
            plugin
                .getServer()
                .getScheduler()
                .runTaskLater(
                    plugin,
                    () -> {
                      // these changes should be made on the thread the event's being called
                      // however that's not possible because...... java.....
                      limited = false;
                      task = null;
                    },
                    20 * plugin.getConfig().getInt("time-for-to-disallow"));
      }
      event.disallow(
          Result.KICK_OTHER,
          plugin.color(plugin.getConfig().getString("further-connections-disallow-message")));
    }
  }
}
