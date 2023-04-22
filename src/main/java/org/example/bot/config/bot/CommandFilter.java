package org.example.bot.config.bot;

import lombok.extern.log4j.Log4j;
import org.example.bot.config.BotConfig;
import org.example.bot.controllers.ProfileController;
import org.example.bot.database.models.Person;
import org.example.bot.database.repository.PersonRepository;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;

@Log4j
@Component
public class CommandFilter {
    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    private Update update;
    private Long chatId;
    private final PersonRepository personRepository;

    private static final String HELP_TEXT = """
            Welcome to our game chat bot, created to maintain a cooperative spirit and organize a routine workflow.

            You can execute from the main menu on the left or by typing a command:

            Type /start to see a main menu but if you are not registered, the bot will offer to register

            """;

    public CommandFilter(MessageUtils messageUtils, PersonRepository personRepository) {
        this.messageUtils = messageUtils;
        this.personRepository = personRepository;
    }

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Received update is null");
            return;
        }

        if (update.hasMessage()) {
            distributeMessagesByType(update);
        } else {
            log.error("Unsupported message type is received: " + update);
        }
    }

    private void distributeMessagesByType(Update update) {
        var message = update.getMessage();
        if (message.hasText()) {
            processTextMessage(update);
        } else if (message.hasDocument()) {
            processDocMessage(update);
        } else if (message.hasPhoto()) {
            processPhotoMessage(update);
        } else {
            setUnsupportedMessageTypeView(update);
        }
    }

    private void setUnsupportedMessageTypeView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "Неподдерживаемый тип сообщения!");
        setView(sendMessage);
    }

    private void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    private void processPhotoMessage(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "Неподдерживаемый тип сообщения!");
        setView(sendMessage);
    }

    private void processDocMessage(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "Неподдерживаемый тип сообщения!");
        setView(sendMessage);
    }

    private void processTextMessage(Update update) {
        //commandsPrivate(update);
        log.debug(update.getMessage());
        if (update.getMessage().getChat().isUserChat()) {
            commandsPrivate(update);
        }
        if (update.getMessage().getChat().isGroupChat()) {
            commandsGroup(update);
        } else {
            log.error("Unsupported message type is received: " + update);
        }
    }

    private void commandsGroup(Update update) {
        String[] messageText = updateToArray(update.getMessage().getText());
        switch (messageText[0]) {
            case "/start" -> startCommandReceive();
            case "/events_executed" -> {
                if (!messageText[0].equals(messageText[1]) && !messageText[0].equals(messageText[2])) {
                    executed(messageText, update);
                }
                if (messageText[0].equals(messageText[1]) && messageText[0].equals(messageText[2])) {
                    executedinformation(update);
                }
            }
            default -> setView(messageUtils.generateSendMessageWithText(update, "Enter any command"));
        }
    }

    private void executedinformation(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "If you want to report an event, first type the command /events_executed, then a : the event description");
        setView(sendMessage);
    }

    private void executed(String[] messageText, Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                String.format("Who: %s \nMessage: %s \nId person: %s", update.getMessage().getFrom().getFirstName(), messageText[2], update.getMessage().getFrom().getId()));
        sendMessage.setChatId(BotConfig.moderid);
        setView(sendMessage);

    }

    private void commandsPrivate(Update update) {
        this.update = update;
        this.chatId = update.getMessage().getChatId();

        String[] messageText = updateToArray(update.getMessage().getText());
        switch (messageText[0]) {
            case "/start":
                startCommandReceive();
                break;
            case "/help":
                setView(messageUtils.generateSendMessageWithText(update, HELP_TEXT));
                break;
            case "/profile":
                getProfile();
                break;
            case "/users":
                setView(messageUtils.generateSendMessageWithText(update, getUsers()));
                break;

            default:
                if (!getPersonData().isAdmin()) {
                    setView(messageUtils.generateSendMessageWithText(update, "Enter any command"));
                }

        }

        if (getPersonData().isAdmin()) {
            switch (messageText[0]) {
                case "/events":
                    if (!messageText[0].equals(messageText[1]) && !messageText[0].equals(messageText[2])) {
                        createEvent(messageText);
                    }
                    if (messageText[0].equals(messageText[1]) && messageText[0].equals(messageText[2])) {
                        infirmationIvent(update);
                    }
                    break;
                case "/addcoin":
                    if (!messageText[0].equals(messageText[1]) && !messageText[0].equals(messageText[2])) {
                        addCoin(messageText);
                    }
                    if (messageText[0].equals(messageText[1]) && messageText[0].equals(messageText[2])) {
                        addCoinInformation(update);
                    }
                    break;
                case "/give_admin_status":
                    if (getPersonData().getId() == BotConfig.moderid) {
                        giveAdminState(messageText);
                        setView(messageUtils.generateSendMessageWithText(update, "Admin status given to user " + update.getMessage().getChat().getFirstName()));

                    }
                    break;
                case "/delete_user":
                    deleteUserFromBd(messageText);
                    break;
                case "/pickup_admin_status":
                    if (getPersonData().getId() == BotConfig.moderid) {
                        pickUpAdminState(messageText);
                        setView(messageUtils.generateSendMessageWithText(update, "User has been deleted"));
                    }
                    break;
            }
        }
    }

    private void addCoin(String[] messageText) {
        Person person;
        ProfileController profileController = new ProfileController(personRepository);
        person = profileController.getUserById(Long.parseLong(messageText[1]));
        int point = person.getNumberOfPoints();
        point += Integer.parseInt(messageText[2]);
        person.setNumberOfPoints(point);
        personRepository.save(person);
    }

    private void addCoinInformation(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "If you want add coin user, first type the command /addcoin, then a : idperson, then : coin");
        setView(sendMessage);
    }

    private void infirmationIvent(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "If you want to start an event, first type the command /events, then a :, the event description, and after the : how many coenes will get for completing the event");
        setView(sendMessage);
    }

    private void createEvent(String[] messageText) {
        List<Person> persons;
        ProfileController profileController = new ProfileController(personRepository);
        persons = profileController.getUsers();
        List<String> chatsid = new ArrayList<>();
        for (Person person : persons) {
            if (person.getId() < 0) {
                chatsid.add(String.valueOf(person.getId()));
            }
        }
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                String.format("Ivent: \nDescription: %s \nCoin: %s", messageText[1], messageText[2]));
        for (String chatid : chatsid) {
            sendMessage.setChatId(chatid);
            setView(sendMessage);
        }

    }

    private static String[] updateToArray(String text) {
        String substring = ":";
        String[] str = text.split(substring);
        for (int i = 0; i < str.length; i++) {
            str[i] = str[i].trim();
        }
        String[] str2 = new String[3];
        if (text.contains(substring) && str.length != 1 && str.length <= 3) {
            if (str.length == 3) {
                return str;
            } else {
                for (int i = 0; i < str.length; i++) {
                    str2[i] = str[i];
                }
                str2[2] = str[1];
                return str2;
            }
        } else {
            for (int i = 0; i < str2.length; i++) {
                str2[i] = str[0];
            }
            return str2;
        }
    }

    private void giveAdminState(String[] messageText) {
        long id;
        if (!messageText[1].isEmpty()) {
            id = Long.parseLong(messageText[1]);

            ProfileController profileController = new ProfileController(personRepository);
            if (personRepository.findById(chatId).isPresent()) {
                profileController.giveAdminStatus(id);
            }
        }
    }

    private void pickUpAdminState(String[] messageText) {
        long id;
        if (!messageText[1].isEmpty()) {
            id = Long.parseLong(messageText[1]);

            ProfileController profileController = new ProfileController(personRepository);
            if (personRepository.findById(chatId).isPresent()) {
                profileController.pickUpAdminStatus(id);
            }
        }
    }

    private void deleteUserFromBd(String[] messageText) {
        long id;
        if (!messageText[1].isEmpty()) {
            id = Long.parseLong(messageText[1]);

            ProfileController profileController = new ProfileController(personRepository);
            if (personRepository.findById(chatId).isPresent()) {
                profileController.deleteUserById(id);
            }
        }
    }

    private void startCommandReceive() {
        userVerification();
        mainMenu();

    }

    private void getProfile() {
        setView(messageUtils.generateSendMessageWithText(update, "Your profile: \n\n" +
                "1. Name is " + getPersonData().getNickname() + "\n\n" +
                "2. Number of points is " + getPersonData().getNumberOfPoints() + "\n\n" +
                "3. Your rang is " + getPersonData().getRang() + "\n\n"));
    }

    private String getUsers() {
        List<Person> persons;
        ProfileController profileController = new ProfileController(personRepository);
        persons = profileController.getUsers();
        String users = "";
        for (Person person : persons) {
            users += (person.getNickname() + "\n");
        }
        return users;
    }


    private void userVerification() {
        Person person = new Person();
        if (personRepository.findById(chatId).isEmpty()) {
            createPerson(person);
            setView(messageUtils.generateSendMessageWithText(update, "User has been registered"));
        }
    }

    private void mainMenu() {
        setView(messageUtils.generateSendMessageWithText(update, """
                Your profile:\s

                1. Profile (/profile)
                2. Users info (/users)
                """));
    }

    private Person getPersonData() {
        Person person;
        ProfileController profileController = new ProfileController(personRepository);
        person = profileController.getUserById(chatId);
        return person;
    }

    private void createPerson(Person person) {
        person.setId(chatId);
        person.setNickname(update.getMessage().getChat().getFirstName());
        person.setNumberOfPoints(0);
        person.setRang("Peasant");
        personRepository.save(person);
    }

}
