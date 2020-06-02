package com.microfocus.bot;

import com.microfocus.bot.keyboard.KeyboardFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.abilitybots.api.toggle.CustomToggle;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.telegram.abilitybots.api.objects.Flag.MESSAGE;
import static org.telegram.abilitybots.api.objects.Flag.REPLY;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;
import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

public class OctaneBot extends AbilityBot {
    private static final Logger logger = LoggerFactory.getLogger(DefaultBotSession.class);

    private static final CustomToggle toggle = new CustomToggle();

    protected OctaneBot(String botToken, String botUsername) {
        super(botToken, botUsername, toggle);
    }

    @Override
    public int creatorId() {
        return 360157588;
    }

    public Ability sayStart() {
        return Ability
                .builder()
                .name("start")
                .info("bla bla bla")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> {
                    silent.execute(new SendMessage()
                            .setText("Welcome In Octane Bot")
                            .setChatId(ctx.chatId()));

                    silent.execute(new SendMessage()
                            .setText("please login")
                            .setChatId(ctx.chatId())
                            .setReplyMarkup(KeyboardFactory.getInitReplyKeyboard()));
                })
                .build();
    }

    public Ability playWithMe() {
        String playMessage = "Play with me!";

        return Ability.builder()
                .name("play")
                .info("Do you want to play with me?")
                .privacy(PUBLIC)
                .locality(ALL)
                .input(0)
                .action(ctx -> silent.forceReply(playMessage, ctx.chatId()))
                // The signature of a reply is -> (Consumer<Update> action, Predicate<Update>... conditions)
                // So, we  first declare the action that takes an update (NOT A MESSAGECONTEXT) like the action above
                // The reason of that is that a reply can be so versatile depending on the message, context becomes an inefficient wrapping
                .reply(upd -> {
                            // Prints to console
                            System.out.println("I'm in a reply!");
                            // Sends message
                            silent.send("It's been nice playing with you!", upd.getMessage().getChatId());
                        },
                        // Now we start declaring conditions, MESSAGE is a member of the enum Flag class
                        // That class contains out-of-the-box predicates for your replies!
                        // MESSAGE means that the update must have a message
                        // This is imported statically, Flag.MESSAGE
                        MESSAGE,
                        // REPLY means that the update must be a reply, Flag.REPLY
                        REPLY,
                        // A new predicate user-defined
                        // The reply must be to the bot
                        isReplyToBot(),
                        // If we process similar logic in other abilities, then we have to make this reply specific to this message
                        // The reply is to the playMessage
                        isReplyToMessage(playMessage)
                )
                // You can add more replies by calling .reply(...)
                .build();
    }

    private Predicate<Update> isReplyToMessage(String message) {
        return upd -> {
            Message reply = upd.getMessage().getReplyToMessage();
            return reply.hasText() && reply.getText().equalsIgnoreCase(message);
        };
    }

    private Predicate<Update> isReplyToBot() {
        return upd -> upd.getMessage().getReplyToMessage().getFrom().getUserName().equalsIgnoreCase(getBotUsername());
    }

    public Reply replyToButtons() {
        Consumer<Update> action = update -> {
            switch (update.getCallbackQuery().getData()) {
                case Constants.LOGIN_BUTTON:
                    silent.forceReply("Please provide login", getChatId(update));
                    break;
                case Constants.LOGOUT_BUTTON:
                default:
                    throw new UnsupportedOperationException("not impl");
            }

        };

        return Reply.of(action, Flag.CALLBACK_QUERY);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage() != null && update.getMessage().getReplyToMessage() != null) {
            if (update.getMessage().getReplyToMessage().getText().equalsIgnoreCase("Please provide login")) {
                db.getMap(Constants.CHAT_STATE).put("login", update.getMessage().getText());
                silent.forceReply("Please provide password", getChatId(update));
            }

            if (update.getMessage().getReplyToMessage().getText().equalsIgnoreCase("Please provide password")) {
                db.getMap(Constants.CHAT_STATE).put("password", update.getMessage().getText());

                silent.send("Try to login", getChatId(update));

                //TODO login

                boolean badData = true;
                if (badData) {
                    db.getMap(Constants.CHAT_STATE).clear();
                    silent.send("bad data, pls provide again", getChatId(update));

                    silent.execute(new SendMessage()
                            .setText("please login")
                            .setChatId(getChatId(update))
                            .setReplyMarkup(KeyboardFactory.getInitReplyKeyboard()));
                }
            }
        }

        super.onUpdateReceived(update);
    }
}