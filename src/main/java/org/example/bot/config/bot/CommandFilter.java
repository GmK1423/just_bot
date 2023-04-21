package org.example.bot.config.bot;

import lombok.extern.log4j.Log4j;
import org.example.bot.config.BotConfig;
import org.example.bot.controllers.ProfileController;
import org.example.bot.database.models.Person;
import org.example.bot.database.repository.PersonRepository;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Log4j
@Component
public class CommandFilter {
    private TelegramBot telegramBot;
    private MessageUtils messageUtils;
    private Update update;
    private Long chatId;
    private final PersonRepository personRepository;

    private static final String HELP_TEXT = "Welcome to our game chat bot, created to maintain a cooperative spirit and organize a routine workflow.\n\n" +
            "You can execute from the main menu on the left or by typing a command:\n\n" +
            "Type /start to see a main menu but if you are not registered, the bot will offer to register\n\n";

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
        commands(update);
        log.debug(update.getMessage());
    }

    private void commands(Update update) {
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
                setView(messageUtils.generateSendMessageWithText(update, "Enter any command"));

        }

        if (getPersonData().isAdmin()) {
            switch (messageText[0]) {
                case "/events":
                    if (messageText[0] != messageText[1] && messageText[0] != messageText[2]) {
                        createEvent(messageText);
                    }
                    if (messageText[0] == messageText[2]) {
                        setView(messageUtils.generateSendMessageWithText(update, "Wrong information try again"));
                    }
                    break;
                case "/give_admin_status":
                    if (getPersonData().getId() == BotConfig.moderid) {
                        giveAdminState(messageText);
                        setView(messageUtils.generateSendMessageWithText(update, "Admin status given to user " + update.getMessage().getChat().getFirstName()));

                    }
                    break;
            }
        }
    }

    private void createEvent(String[] messageText) {
        List<Person> persons;
        ProfileController profileController = new ProfileController(personRepository);
        persons = profileController.getUsers();
        List<String> chatsid = new ArrayList<>();
        for (Person person : persons) {
            if (Integer.parseInt(String.valueOf(person.getId())) < 0) {
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
            setView(messageUtils.generateSendMessageWithText(update, "User has been registered\n\n"));
//            person = getPersonData();
//            sendMessage("Your profile: \n\n" +
//                    "1. Name is " + person.getNickname() + "\n\n" +
//                    "2. Number of points is " + person.getNumberOfPoints() + "\n\n" +
//                    "3. Your rang is " + person.getRang() + "\n\n");
        }
//        else {
//            person = getPersonData();
//            sendMessage("Your profile: \n\n" +
//                    "1. Name is " + person.getNickname() + "\n\n" +
//                    "2. Number of points is " + person.getNumberOfPoints() + "\n\n" +
//                    "3. Your rang is " + person.getRang() + "\n\n");
//        }
    }

    private void mainMenu() {
        setView(messageUtils.generateSendMessageWithText(update, "Your profile: \n\n" +
                "1. Profile (/profile)\n" +
                "2. Users info (/users)\n"));
    }

    private Person getPersonData() {
        Person person = new Person();
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
