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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Log4j
@Component
public class CommandFilter {
    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    private Update update;
    private Long chatId;
    private final PersonRepository personRepository;
    private final ProfileController profileController;
    private Ranks ranks;

    private static final String HELP_START = """
            Welcome to our game chat bot, created to maintain a cooperative spirit and organize a routine workflow.

            If you want to see all the commands write /help

            Type /start to see a main menu but if you are not registered, the bot will offer to register

            """;
    private static final String HELP_COMMAND_PRIVATE = """
            The bot has the following commands.
            
            /start - Getting started with the bot.
            /help - Get information about existing commands.
            /profile - Get information about yourself.
            /users - View all existing users.
            
            """;
    private static final String HELP_COMMAND_ADMIN = """
            The bot has the following commands.
            
            /start - Getting started with the bot.
            /help - Get information about existing commands.
            /profile - Get information about yourself.
            /users - View all existing users.
            /events - Start Event.
            /addcoin - Add to user coin.
            /giveAdminStatus - Give administrator rights to the user.
            /deleteUser - Remove a user from the database.
            /pickupAdminStatus - Take away the administrator rights
            
            """;

    public CommandFilter(MessageUtils messageUtils, PersonRepository personRepository, ProfileController profileController, Ranks ranks) {
        this.messageUtils = messageUtils;
        this.personRepository = personRepository;
        this.profileController = profileController;
        this.ranks = ranks;
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
                "Unsupported message type!");
        setView(sendMessage);
    }

    private void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    private void processPhotoMessage(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "Unsupported message type!");
        setView(sendMessage);
    }

    private void processDocMessage(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "Unsupported message type!");
        setView(sendMessage);
    }

    private void processTextMessage(Update update) {
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
                    executedInformation(update);
                }
            }
            default -> setView(messageUtils.generateSendMessageWithText(update, "Enter any command"));
        }
    }

    private void executedInformation(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "If you want to report an event, first type the command /events_executed, then a : the event description");
        setView(sendMessage);
    }

    private void executed(String[] messageText, Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                String.format("Who: %s \nMessage: %s \nId person: %s", update.getMessage().getFrom().getFirstName(), messageText[2], update.getMessage().getFrom().getId()));
        List<Person> persons = profileController.getUsers();
        for (Person person : persons) {
            if(person.isAdmin()){
                sendMessage.setChatId(person.getId());
                setView(sendMessage);
            }
        }
    }

    private void commandsPrivate(Update update) {
        this.update = update;
        this.chatId = update.getMessage().getChatId();

        String[] messageText = updateToArray(update.getMessage().getText());
        switch (messageText[0]) {
            case "/start":
                setView(messageUtils.generateSendMessageWithText(update, HELP_START));
                startCommandReceive();
                break;
            case "/help":
                setView(messageUtils.generateSendMessageWithText(update, HELP_COMMAND_PRIVATE));
                break;
            case "/profile":
                getProfile();
                break;
            case "/users":
                setView(messageUtils.generateSendMessageWithText(update, getUsers()));
                break;
            case "/player_rating":
                setView(messageUtils.generateSendMessageWithText(update, getPlayerRating()));
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
                case "/giveAdminStatus":
                    if (getPersonData().getId() == BotConfig.moderid) {
                        setAdminStatus(messageText);
                        setView(messageUtils.generateSendMessageWithText(update, "Admin status given to user " + update.getMessage().getChat().getFirstName()));

                    }
                    break;
                case "/deleteUser":
                    deleteUserFromBd(messageText);
                    break;
                case "/pickupAdminStatus":
                    if (getPersonData().getId() == BotConfig.moderid) {
                        pickUpAdminState(messageText);
                        setView(messageUtils.generateSendMessageWithText(update, "User has been deleted"));
                    }
                    break;

                case "/helpAdmin":
                    setView(messageUtils.generateSendMessageWithText(update, HELP_COMMAND_ADMIN));
                    startCommandReceive();

                case "/resetBot":
                    if (getPersonData().getId() == BotConfig.moderid) {
                        resetBot();
                    }
                    break;
            }
        }
    }

    private void addCoin(String[] messageText) {
        Person person = profileController.getUserById(Long.parseLong(messageText[1]));
        int point = person.getNumberOfPoints();
        point += Integer.parseInt(messageText[2]);
        person.setNumberOfPoints(point);
        person.setRang(ranks.getPersonStatus(point));
        personRepository.save(person);
    }

    private void addCoinInformation(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "If you want add coin user, first type the command /addcoin, then a : idperson, then : coin");
        setView(sendMessage);
    }

    private void infirmationIvent(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "If you want to start an event, first type the command /events, then a :, the event description, and after the : how many coenes will get for completing the event" +
                        "\n This command sends alerts to all the chats in which the bot is a member. ");
        setView(sendMessage);
    }

    private void resetBot(){
        List<Person> persons = profileController.getUsers();
        for(Person person:persons){
            person.setNumberOfPoints(0);
            person.setRang("");
            personRepository.save(person);
        }

    }

    private void createEvent(String[] messageText) {
        List<Person> persons = profileController.getUsers();
        List<String> chatsId = new ArrayList<>();
        for (Person person : persons) {
            if (person.getId() < 0) {
                chatsId.add(String.valueOf(person.getId()));
            }
        }
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                String.format("Ivent: \nDescription: %s \nCoin: %s", messageText[1], messageText[2]));
        for (String chatid : chatsId) {
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

    private String getPlayerRating() {
        int counter = 1;
        List<Person> persons = profileController.getUsers();
        chekUser(persons);
        Collections.sort(persons, new Comparator<Person>() {
            @Override
            public int compare(Person o1, Person o2) {
                if (o1.getNumberOfPoints() != o2.getNumberOfPoints()) {
                    return o2.getNumberOfPoints() - o1.getNumberOfPoints();
                }
                return o2.getNumberOfPoints() - o1.getNumberOfPoints();
            }
        });
        StringBuilder users = new StringBuilder();
        for (Person person : persons) {
            users.append(counter++).append(". ").append(person.getNickname()).append(" ").
                    append(person.getRang()).append(" ").
                    append(person.getNumberOfPoints()).append("\n");
        }
        return users.toString();
    }

    private List<Person> chekUser(List<Person> persons) {
        for (int i = 0; i < persons.size(); i++) {
            if (persons.get(i).getId() < 0) {
                persons.remove(persons.get(i));
            }
        }
        return persons;
    }

    private void setAdminStatus(String[] messageText) {
        if (!messageText[1].isEmpty()) {
            long id = Long.parseLong(messageText[1]);
            if (personRepository.findById(chatId).isPresent()) {
                profileController.giveAdminStatus(id);
            }
        }
    }

    private void pickUpAdminState(String[] messageText) {
        if (!messageText[1].isEmpty()) {
            long id = Long.parseLong(messageText[1]);
            if (personRepository.findById(chatId).isPresent()) {
                profileController.pickUpAdminStatus(id);
            }
        }
    }

    private void deleteUserFromBd(String[] messageText) {
        if (!messageText[1].isEmpty()) {
            long id = Long.parseLong(messageText[1]);
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
        List<Person> persons = profileController.getUsers();
        chekUser(persons);
        StringBuilder users = new StringBuilder();
        for (Person person : persons) {
            users.append(person.getNickname()).append("\n");
        }
        return users.toString();
    }


    private void userVerification() {
        if (personRepository.findById(chatId).isEmpty()) {
            createPerson();
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
        return profileController.getUserById(chatId);
    }

    private void createPerson() {
        Person person = new Person();
        person.setId(chatId);
        person.setNickname(update.getMessage().getChat().getFirstName());
        person.setNumberOfPoints(0);
        person.setRang("Peasant");
        personRepository.save(person);
    }

}
