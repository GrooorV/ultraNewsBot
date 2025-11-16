package newsbot.command.resolver;

import newsbot.command.BotCommand;
import newsbot.engine.BotResponse;
import newsbot.shared.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CommandResolverTest {

    private static class StubHelpCommand implements BotCommand {

        @Override
        public String getName() {
            return "\\help";
        }

        @Override
        public BotResponse execute(UserId userId, String args) {
            return BotResponse.say("HELP_STUB_EXECUTED: args=" + args);
        }
    }

    private static class StubFreeTextCommand implements BotCommand {

        @Override
        public String getName() {
            return null;
        }

        @Override
        public BotResponse execute(UserId userId, String args) {
            return BotResponse.say("FREETEXT_STUB_EXECUTED: args=" + args);
        }
    }

    private CommandResolver resolver;
    private final UserId testUser = new UserId("test");

    @BeforeEach
    void setUp() {
        BotCommand helpCommand = new StubHelpCommand();
        BotCommand freeTextCommand = new StubFreeTextCommand();

        resolver = new CommandResolver(freeTextCommand);
        resolver.register(helpCommand);
    }

    @Test
    void resolvesExplicitCommand() {
        Optional<ResolvedCommand> result = resolver.resolve("\\help");

        assertTrue(result.isPresent());
        BotResponse response = result.get().getCommand().execute(testUser, result.get().getArgs());
        assertEquals("HELP_STUB_EXECUTED: args=", response.getMessage());
    }

    @Test
    void resolvesExplicitCommandWithArgs() {
        Optional<ResolvedCommand> result = resolver.resolve("\\help me please");

        assertTrue(result.isPresent());
        BotResponse response = result.get().getCommand().execute(testUser, result.get().getArgs());
        assertEquals("HELP_STUB_EXECUTED: args=me please", response.getMessage());
    }

    @Test
    void resolvesFreeTextCommand() {
        Optional<ResolvedCommand> result = resolver.resolve("спорт");

        assertTrue(result.isPresent());
        BotResponse response = result.get().getCommand().execute(testUser, result.get().getArgs());
        assertEquals("FREETEXT_STUB_EXECUTED: args=спорт", response.getMessage());
    }

    @Test
    void returnsEmptyForUnknownCommand() {
        Optional<ResolvedCommand> result = resolver.resolve("\\unknown");

        assertTrue(result.isEmpty());
    }

    @Test
    void returnsEmptyForPlainTextThatIsNotCategory() {
        Optional<ResolvedCommand> result = resolver.resolve("привет");

        assertTrue(result.isEmpty());
    }

    @Test
    void returnsEmptyForBlankInput() {
        Optional<ResolvedCommand> result = resolver.resolve("   ");
        assertTrue(result.isEmpty());
    }
}