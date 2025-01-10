/*
 * This file is part of BlueMap, licensed under the MIT License (MIT).
 *
 * Copyright (c) Blue (Lukas Rieger) <https://bluecolored.de>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.bluecolored.bluemap.bukkit;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import de.bluecolored.bluemap.common.plugin.Plugin;
import de.bluecolored.bluemap.common.plugin.commands.Commands;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collection;

public class BukkitCommands implements Listener {

    private final CommandDispatcher<CommandSender> dispatcher;

    public BukkitCommands(final Plugin plugin) {
        this.dispatcher = new CommandDispatcher<>();

        // register commands
        new Commands<>(plugin, dispatcher, bukkitSender -> new BukkitCommandSource(plugin, bukkitSender));
    }

    public Collection<Command> getRootCommands(){
        Collection<Command> rootCommands = new ArrayList<>();

        for (CommandNode<CommandSender> node : this.dispatcher.getRoot().getChildren()) {
            rootCommands.add(new CommandProxy(node.getName()));
        }

        return rootCommands;
    }

    private class CommandProxy extends Command {

        protected CommandProxy(String name) {
            super(name);
        }

        @Override
        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            String command = commandLabel;
            if (args.length > 0) {
                command += " " + String.join(" ", args);
            }

            try {
                return dispatcher.execute(command, sender) > 0;
            } catch (CommandSyntaxException ex) {
                sender.sendMessage(ChatColor.RED + ex.getRawMessage().getString());

                String context = ex.getContext();
                if (context != null) sender.sendMessage(ChatColor.GRAY + context);

                return false;
            }
        }

    }

}
