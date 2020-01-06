package com.rubiconproject.dmp.vertxstart.verticles;

import com.rubiconproject.dmp.vertxstart.models.EdgeUpdateMessage;
import io.vertx.reactivex.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EdgeUpdateVerticle extends AbstractVerticle
{
    private static Logger logger = LoggerFactory.getLogger(UserMessageProducerVerticle.class);

    public static final String address = "edge-update";//FIXME may be use private ?

    @Override
    public void start() throws Exception
    {
        vertx.eventBus().consumer(address, message -> {
            EdgeUpdateMessage edgeUpdateMessage = (EdgeUpdateMessage)message.body();
            logger.debug("Sent update message {}", edgeUpdateMessage.toJson());
            message.reply(true);
        });
    }
}
