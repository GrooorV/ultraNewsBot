package newsbot.engine;

import newsbot.command.resolver.CommandResolver;
import newsbot.command.resolver.ResolvedCommand;
import newsbot.repository.SessionRepository;
import newsbot.shared.UserId;

import java.util.Optional;


public class DialogueService {
    private final DialogueEngine engine;
    private final SessionRepository sessions;
    private final CommandResolver commandResolver;


    public  DialogueService(DialogueEngine engine, SessionRepository sessions, CommandResolver commandResolver) {
        this.engine = engine;
        this.sessions = sessions;
        this.commandResolver = commandResolver;
    }

    public BotResponse handle(String rawUserId, String rawInput) {
        UserId userId = new UserId(rawUserId);
        UserSession session = sessions.getOrCreate(userId);

        Optional<ResolvedCommand> resolved = commandResolver.resolve(rawInput);

        if (resolved.isPresent()) {
            ResolvedCommand cmd = resolved.get();
            return cmd.getCommand().execute(userId, cmd.getArgs());
        }

        if (!session.isStarted()) {
            session.markStarted();
            sessions.save(userId, session);
            return BotResponse.say("Начнём! " + engine.onboardText());
        }

        return BotResponse.say(engine.onboardText());
    }
}