package com.microfocus.bot;

import com.microfocus.bot.async.PollUserDataThread;
import com.microfocus.bot.dto.MyWorkFollowItem;
import com.microfocus.bot.dto.OctaneUser;
import com.microfocus.bot.dto.WorkItem;
import com.microfocus.bot.http.OctaneAuth;
import com.microfocus.bot.http.OctaneHttpClient;
import com.microfocus.bot.keyboard.KeyboardFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.abilitybots.api.toggle.CustomToggle;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.telegram.abilitybots.api.objects.Flag.REPLY;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;
import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

public class OctaneBot extends AbilityBot implements Constants {

    private static final Logger logger = LoggerFactory.getLogger(DefaultBotSession.class);

    private static final CustomToggle toggle = new CustomToggle();
    private final OctaneHttpClient octaneClient;

    private final Map<String, Thread> userPollingMap;

    protected OctaneBot(String botToken, String botUsername) {
        super(botToken, botUsername, toggle);
        octaneClient = OctaneHttpClient.INSTANCE;
        userPollingMap = new HashMap<>();
    }

    @Override
    public int creatorId() {
        return 360157588;
    }

    @SuppressWarnings("unused")
    public Ability onStart() {
        return Ability
                .builder()
                .name("start")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> {
                    silent.execute(new SendMessage()
                            .setText("Welcome to Octane Bot!")
                            .setChatId(ctx.chatId()));

                    silent.execute(new SendMessage()
                            .setText("Please Login to start working with your Octane account")
                            .setChatId(ctx.chatId())
                            .setReplyMarkup(KeyboardFactory.getLoginInLineButtons()));
                })
                .build();
    }

    @SuppressWarnings("unused")
    public Reply replyToInLineButtons() {
        Consumer<Update> action = update -> {
            String data = update.getCallbackQuery().getData();
            if (Constants.LOGIN_BUTTON.equals(data)) {
                if (isUserSingIn(update)) {
                    silent.send("Already sing-in", getChatId(update));
                }

                silent.forceReply(PLEASE_PROVIDE_LOGIN_REPLY, getChatId(update));
            } else if (data.startsWith(Constants.REPLY_COMMENT_BUTTON)) {
                if (!isUserSingIn(update)) {
                    silent.send("You are not logged", getChatId(update));
                    return;
                }
                parseAndStoreLastReplyCommentCallbackData(update);
                silent.forceReply(PLEASE_PROVIDE_REPLY_MESSAGE_REPLY, getChatId(update));
            } else if (data.startsWith(Constants.VIEW_ITEM_DETAILS_BUTTON)) {
                if (!isUserSingIn(update)) {
                    silent.send("You are not logged", getChatId(update));
                    return;
                }
                Pair<Long, String> workItemIdAndType = getWorkItemIdAndType(update);
                WorkItem workItemById = octaneClient.getWorkItemById(workItemIdAndType.getLeft());

                silent.send(BotMessageHelper.prepareFullWorkItemInfo(workItemById), getChatId(update));
            } else {
                throw new UnsupportedOperationException("not impl" + data);
            }

        };
        return Reply.of(action, Flag.CALLBACK_QUERY);
    }

    @SuppressWarnings("unused")
    public Reply replyToBigButtons() {
        Consumer<Update> action = update -> {
            logger.debug(update.toString());
            switch (update.getMessage().getText()) {
                case LOGOUT_BUTTON_BIG_BUTTON:
                    getUserDB(update).clear();
                    silent.execute(new SendMessage()
                            .setText("You are log out")
                            .setChatId(getChatId(update)));

                    stopPolling(update);

                    silent.execute(new SendMessage()
                            .setText("Please Login to start working with your Octane account")
                            .setChatId(getChatId(update))
                            .setReplyMarkup(KeyboardFactory.getLoginInLineButtons()));
                    break;
                case GET_MY_WORK_BIG_BUTTON:
                    if (!isUserSingIn(update)) {
                        silent.send("You are not logged", getChatId(update));
                        return;
                    }
                    Long userId = Long.valueOf(getUserDB(update).get(Constants.USER_ID_PROP));
                    List<MyWorkFollowItem> myWorkItems = octaneClient.getMyWork(createOctaneAuth(update),
                            userId);

                    myWorkItems.stream()
                            .map(MyWorkFollowItem::getWorkItem)
                            .forEach(myWorkItem -> {
                                silent.execute(new SendMessage()
                                        .setText(BotMessageHelper.prepareShotInfo(myWorkItem))
                                        .setChatId(getChatId(update))
                                        .setReplyMarkup(KeyboardFactory.getWorkItemInLineButtons(myWorkItem)));
                            });
                    break;
                case DISABLE_PUSH_BIG_BUTTON:
                    if (!isUserSingIn(update)) {
                        silent.send("You are not logged", getChatId(update));
                        return;
                    }
                    stopPolling(update);
                    silent.execute(new SendMessage()
                            .setText("Push notifications was disabled")
                            .setChatId(getChatId(update))
                            .setReplyMarkup(KeyboardFactory.getMainBigButtons(isPoolingEnabled(update))));
                    break;
                case ENABLE_PUSH_BIG_BUTTON:
                    if (!isUserSingIn(update)) {
                        silent.send("You are not logged", getChatId(update));
                        return;
                    }
                    startPolling(update);
                    silent.execute(new SendMessage()
                            .setText("Push notifications was enabled")
                            .setChatId(getChatId(update))
                            .setReplyMarkup(KeyboardFactory.getMainBigButtons(isPoolingEnabled(update))));
                    break;
                default:
                    throw new UnsupportedOperationException("not impl" + update.getMessage().getText());
            }
        };
        return Reply.of(action, upd -> Flag.TEXT.test(upd) && isBigButton(upd));
    }

    @SuppressWarnings("unused")
    public Reply replyToMessages() {
        Consumer<Update> action = update -> {
            if (!isReplyToBot(update)) {
                return;
            }
            switch (update.getMessage().getReplyToMessage().getText()) {
                case PLEASE_PROVIDE_LOGIN_REPLY:
                    getUserDB(update).put(USERNAME_PROP, update.getMessage().getText());
                    silent.forceReply(PLEASE_PROVIDE_PASSWORD_REPLY, getChatId(update));
                    break;
                case PLEASE_PROVIDE_PASSWORD_REPLY:
                    getUserDB(update).put(PASSWORD_PROP, update.getMessage().getText());
                    getUserDB(update).put(SING_IN_PROP, Boolean.TRUE.toString());

                    String octaneUserId = octaneClient.login(createOctaneAuth(update));
                    OctaneUser userById = octaneClient.getUserById(octaneUserId);
                    getUserDB(update).put(USER_ID_PROP, octaneUserId);
                    if (StringUtils.isNotBlank(octaneUserId)) {
                        silent.execute(new SendMessage().setText(userById.getFirstName() + " "
                                + userById.getLastName() + ", Welcome to Octane!\n" +
                                "You will be notified when any comment arrives")
                                .setChatId(getChatId(update))
                                .setReplyMarkup(KeyboardFactory.getMainBigButtons(isPoolingEnabled(update))));

                        startPolling(update);
                    } else {
                        getUserDB(update).clear();
                        silent.send("Login is failed, incorrect user name or password", getChatId(update));

                        silent.execute(new SendMessage()
                                .setText("Please Login to start working with your Octane account")
                                .setChatId(getChatId(update))
                                .setReplyMarkup(KeyboardFactory.getLoginInLineButtons()));
                    }
                    break;
                case PLEASE_PROVIDE_REPLY_MESSAGE_REPLY:
                    String replyText = update.getMessage().getText();
                    Pair<Long, String> itemData = readLastReplyCommentCallbackData(update);
                    octaneClient.postComment(createOctaneAuth(update), itemData, replyText);

/*                    silent.execute(new SendMessage()
                            .setText("Your respond is sent")
                            .setChatId(getChatId(update))
                            .setReplyMarkup(KeyboardFactory.getReplyCommentInLineButtons(itemData)));*/

                    silent.execute(new SendMessage()
                            .setText("Your respond is sent")
                            .setChatId(getChatId(update))
                            .setReplyMarkup(KeyboardFactory.getMainBigButtons(isPoolingEnabled(update))));

                    break;
                default:
                    throw new UnsupportedOperationException("not impl" + update.getMessage().getReplyToMessage().getText());
            }
        };
        return Reply.of(action, REPLY);
    }

    private boolean isPoolingEnabled(Update update) {
        return userPollingMap.get(getUserName(update)) != null;
    }

    private void startPolling(Update update) {
        if (!isPoolingEnabled(update)) {
            Thread pollThread = new PollUserDataThread(getUserDB(update), silent, getChatId(update), octaneClient);
            pollThread.start();
            userPollingMap.put(getUserName(update), pollThread);
        }
    }

    private void stopPolling(Update update) {
        Thread pollThread = userPollingMap.get(getUserName(update));
        if (pollThread != null && !pollThread.isInterrupted()) {
            pollThread.interrupt();
            userPollingMap.remove(getUserName(update));
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        super.onUpdateReceived(update);
    }

    private boolean isReplyToBot(Update update) {
        return update.getMessage().getReplyToMessage().getFrom().getUserName().equalsIgnoreCase(getBotUsername());
    }

    private OctaneAuth createOctaneAuth(Update update) {
        return new OctaneAuth(getUserDB(update));
    }

    private String getUserName(Update update) {
        if (update.getMessage() != null) {
            return update.getMessage().getFrom().getUserName();
        }
        return update.getCallbackQuery().getFrom().getUserName();
    }

    private Map<String, String> getUserDB(Update update) {
        String userName = getUserName(update);
        return db.getMap(userName);
    }

    private boolean isBigButton(Update upd) {
        return getBigButtons().contains(upd.getMessage().getText());
    }

    private void parseAndStoreLastReplyCommentCallbackData(Update update) {
        Pair<Long, String> pair = getWorkItemIdAndType(update);
        getUserDB(update).put(LAST_REPLY_COMMENT_ITEM_ID, pair.getLeft().toString());
        getUserDB(update).put(LAST_REPLY_COMMENT_ITEM_TYPE, pair.getRight());
    }

    private Pair<Long, String> getWorkItemIdAndType(Update update) {
        String data = update.getCallbackQuery().getData();

        Pattern p = Pattern.compile("\\{([^}]*)\\}");
        Matcher m = p.matcher(data);
        m.find();
        String[] split = m.group(1).split(":");
        return Pair.of(Long.valueOf(split[0]), split[1]);
    }

    private Pair<Long, String> readLastReplyCommentCallbackData(Update update) {
        return Pair.of(
                Long.valueOf(getUserDB(update).get(LAST_REPLY_COMMENT_ITEM_ID)),
                getUserDB(update).get(LAST_REPLY_COMMENT_ITEM_TYPE));
    }

    private boolean isUserSingIn(Update update) {
        return getUserDB(update).containsValue(SING_IN_PROP) && getUserDB(update).get(SING_IN_PROP).equalsIgnoreCase(Boolean.TRUE.toString());
    }

}