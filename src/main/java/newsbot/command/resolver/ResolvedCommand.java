package newsbot.command.resolver;

import  newsbot.command.BotCommand;

public class ResolvedCommand {
    private final BotCommand command;
    private final String args;

    public ResolvedCommand(BotCommand command, String args) {
        this.command = command;
        this.args = args;
    }

    public BotCommand getCommand() {
        return command;
    }

    public String getArgs() {
        return args;
    }
}
