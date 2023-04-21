package org.example.bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("application.properties")
public class BotConfig {
    @Value("${bot.name}")
    String botName;
    @Value("${bot.key}")
    String token;

    public static final long moderid = 619465925;

    public String getBotName() {
        return botName;
    }

    public String getToken() {
        return token;
    }

    public long getModerid() {
        return moderid;
    }
}
