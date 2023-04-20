package org.example.bot.config.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import org.example.bot.config.BotConfig;
import org.example.bot.controllers.ProfileController;
import org.example.bot.database.models.Person;
import org.example.bot.database.repository.PersonRepository;
import org.example.bot.game.Game;
import org.example.bot.game.Ranks.Ranks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Log4j
public class TelegramBot extends TelegramLongPollingBot {

    private CommandFilter commandFilter;
    private final BotConfig config;
    private final PersonRepository personRepository;
    //    private final Ranks rang;
    private Update update;
    private Long chatId;
    private static final String HELP_TEXT = "Welcome to our game chat bot, created to maintain a cooperative spirit and organize a routine workflow.\n\n" +
            "You can execute from the main menu on the left or by typing a command:\n\n" +
            "Type /start to see a main menu but if you are not registered, the bot will offer to register\n\n";

    @Autowired
    public TelegramBot(BotConfig config, PersonRepository personRepository, CommandFilter commandFilter) {
        this.config = config;
        this.personRepository = personRepository;
        this.commandFilter = commandFilter;

//        this.rang = rang;

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
        commandFilter.processUpdate(update);
    }

    @PostConstruct
    public void init() {
        commandFilter.registerBot(this);
    }

    public void sendAnswerMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }

}
