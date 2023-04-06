package org.example;

import org.example.bot.Telebot;
import org.example.config.SpringConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Telebot());
        } catch (Exception e) {
            e.printStackTrace();
        }

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                SpringConfig.class
        );






        context.close();
    }
}