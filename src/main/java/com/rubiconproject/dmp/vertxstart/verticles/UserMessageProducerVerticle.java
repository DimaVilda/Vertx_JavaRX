package com.rubiconproject.dmp.vertxstart.verticles;

import com.rubiconproject.dmp.vertxstart.models.UserMessage;
import io.vertx.reactivex.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class UserMessageProducerVerticle extends AbstractVerticle
{
    private static Logger logger = LoggerFactory.getLogger(UserMessageProducerVerticle.class);

    public static final String producerAddress = "message.producer";

    private static final int SEND_MESSAGE_PERIOD_MS = 1000;
    private static final int MAX_SENT_IDS = 100;

    private int nextId = 0;

    private List<String> sentIds = new ArrayList<>();

    private Random random = new Random();

    @Override
    public void start() throws Exception
    {
        //produces random messages every 1 second

        logger.debug("Started");

        vertx.setPeriodic(SEND_MESSAGE_PERIOD_MS, timerId -> sendMessage());
    }

    private void sendMessage()
    {
        int id = ++nextId;
        String userId = UUID.randomUUID().toString();

        List<String> alternateIds = new ArrayList<>();
        int n = random.nextInt(10);
        for (int i = 0; i < Math.min(n, sentIds.size()); i++) //always zero !
        {
            String alternateId = sentIds.get(random.nextInt(sentIds.size()));
            alternateIds.add(alternateId);
        }

        sentIds.add(userId);

        if (sentIds.size() > MAX_SENT_IDS)
        {
            //remove a random sent id
            sentIds.remove(random.nextInt(sentIds.size()));
        }

        UserMessage userMessage = new UserMessage(id, userId, alternateIds);

        logger.debug("Sending message {}", userMessage.toJson());

        long startMs = System.currentTimeMillis();
        vertx.eventBus().send(producerAddress, userMessage.toJson(), result -> {
            if (result.succeeded())
            {
                logger.debug("Finished processing userMessage {} after {}ms", userMessage.id, System.currentTimeMillis() - startMs);
            }
            else
            {
                logger.error("Failed to process userMessage", result.cause());
            }
        });
    }
}
