package newsbot.command.resolver;

import newsbot.command.BotCommand;
import newsbot.engine.DialogueEngine;
import newsbot.news.NewsCategory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class CommandResolver {


    private final Map<String, BotCommand> explicitCommands = new HashMap<>();
    private final BotCommand freeTextHandler;

    public CommandResolver(BotCommand freeTextHandler) {
        this.freeTextHandler = Objects.requireNonNull(freeTextHandler);
    }

    public void register(String name, BotCommand command) {
        explicitCommands.put(name.toLowerCase(), command);
    }

    private boolean isLikeCategoryInput(String input) {
        if (input == null || input.isBlank() || input.startsWith("\\")) {
            return false;
        }

        String[] parts = input.split("[,;\\s]+");

        for (String part : parts) {
            if (NewsCategory.parse(part).isPresent()) {
                return true;
            }
        }

        return false;
    }

    public Optional<ResolvedCommand> resolve(String rawInput) {
        if (rawInput == null || rawInput.isBlank()) {
            return Optional.empty();
        }

        if (rawInput.startsWith("\\")) {
            String[] parts = rawInput.split("\\s+", 2);
            String commandName = parts[0].toLowerCase();
            BotCommand command = explicitCommands.get(commandName);

            if (command != null) {
                String args = (parts.length > 1) ? parts[1] : "";
                return Optional.of(new ResolvedCommand(command, args));
            }
            return Optional.empty();
        }

        if (isLikeCategoryInput(rawInput)) {
            return Optional.of(new ResolvedCommand(freeTextHandler, rawInput));
        }

        return Optional.empty();
    }
}