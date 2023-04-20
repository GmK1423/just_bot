package org.example.bot.config.bot;

import lombok.extern.log4j.Log4j;
import org.example.bot.controllers.ProfileController;
import org.example.bot.database.models.Person;
import org.example.bot.database.repository.PersonRepository;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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

        if (update.getMessage().getChat().isUserChat() && update.hasMessage()) {
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
        update.getMessage().getChat().isGroupChat();
        this.update = update;
        this.chatId = update.getMessage().getChatId();
        if (update.getMessage().getChat().isUserChat()) {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                switch (messageText) {
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
            } else {

            }
        }
    }

    private void startCommandReceive() {
        userVerification();
        mainMenu();
//        if (update.hasMessage() && update.getMessage().hasText()) {
//            String messageText = update.getMessage().getText();
//            switch (messageText){
//                case "/profile":
//                    sendMessage("Your profile: \n\n" +
//                            "1. Name is " + getPersonData().getNickname() + "\n\n" +
//                            "2. Number of points is " + getPersonData().getNumberOfPoints() + "\n\n" +
//                            "3. Your rang is " + getPersonData().getRang() + "\n\n");
//                    break;
//                default:
//                    sendMessage("Error");
//            }
//
//        }

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

//    private void sendMessage(String textToSend) {
//        SendMessage message = new SendMessage();
//        message.setChatId(chatId);
//        message.setText(textToSend);
//        try {
//            execute(message);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }

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
