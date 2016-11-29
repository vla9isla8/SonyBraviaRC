package net.ddns.vla9isla8.ircc.service;

import net.ddns.vla9isla8.ircc.entity.Command;

import java.util.List;

/**
 * Created by Vladk on 26.11.2016.
 */
public interface IRCCService {
    List<Command> getCommandsList();
    void executeCommand(Command command,boolean printOutput);
    void disconnect();
}
