package org.example.bot.config.bot;

import org.example.bot.config.BotConfig;
import org.example.bot.controllers.ProfileController;
import org.example.bot.database.models.Person;
import org.example.bot.database.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig config;
    @Autowired
    private final PersonRepository personRepository;
    private Update update;
    private Long chatId;
    private static final String HELP_TEXT = "Welcome to our game chat bot, created to maintain a cooperative spirit and organize a routine workflow.\n\n" +
            "You can execute from the main menu on the left or by typing a command:\n\n" +
            "Type /start to see a main menu but if you are not registered, the bot will offer to register\n\n";

    @Autowired
    public TelegramBot(BotConfig config, PersonRepository personRepository) {
        this.config = config;
        this.personRepository = personRepository;

        List<BotCommand> listOfCommand = new ArrayList<>();
        listOfCommand.add(new BotCommand("/start", "Start"));
        listOfCommand.add(new BotCommand("/help", "Info about bot"));
        listOfCommand.add(new BotCommand("/profile", "Get profile info"));
        listOfCommand.add(new BotCommand("/users", "Get users info"));
        try {
            execute(new SetMyCommands(listOfCommand, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
        }
    }


    @Override
    public String getBotUsername() {
        return config.getBotName();
    }


    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        this.update = update;
        this.chatId = update.getMessage().getChatId();
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            switch (messageText) {
                case "/start":
                    startCommandReceive();
                    break;
                case "/help":
                    sendMessage(HELP_TEXT);
                    break;
                default:
                    sendMessage("Enter command");

            }
        }
    }

//    private String filter() {
//        String message;
//        if (update.hasMessage() && update.getMessage().hasText()) {
//            message = update.getMessage().getText();
//
//            return message;
//        }
//        return null;
//    }

    private void startCommandReceive() {
        userVerification();
        mainMenu();
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            switch (messageText) {
                case "/profile":
                    sendMessage("Your profile: \n\n" +
                            "1. Name is " + getPersonData().getNickname() + "\n\n" +
                            "2. Number of points is " + getPersonData().getNumberOfPoints() + "\n\n" +
                            "3. Your rang is " + getPersonData().getRang() + "\n\n");
            }
        }

    }

    private void sendMessage(String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void userVerification() {
        Person person = new Person();
        if (personRepository.findById(chatId).isEmpty()) {
            createPerson(person);
            sendMessage("User has been registered\n\n");
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
        sendMessage("Your profile: \n\n" +
                "1. Profile (/profile)\n" +
                "2. Users info (/users)\n");
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

//    public void sendText(Long who, String what) {
//        SendMessage sm = SendMessage.builder()
//                .chatId(who.toString()) //Who are we sending a message to
//                .text(what).build();    //Message content
//        try {
//            execute(sm);                        //Actually sending the message
//        } catch (TelegramApiException e) {
//            throw new RuntimeException(e);      //Any error will be printed here
//        }
//
//    }
}
