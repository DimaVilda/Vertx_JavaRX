package com.rubiconproject.dmp.vertxstart.verticles;

import com.rubiconproject.dmp.vertxstart.models.AerospikeRecord;
import com.rubiconproject.dmp.vertxstart.models.AerospikeRequest;
import com.rubiconproject.dmp.vertxstart.models.EdgeUpdateMessage;
import com.rubiconproject.dmp.vertxstart.models.UserMessage;
import io.vertx.core.AsyncResult;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ProcessorVerticle extends AbstractVerticle {
    private static Logger logger = LoggerFactory.getLogger(ProcessorVerticle.class);
    private boolean isOptOut = false;
    private Map<String,List<String>> segmentsMap = new HashMap<>();

    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer(UserMessageProducerVerticle.producerAddress, this::handleUserMessage);
    }

    private void handleUserMessage(Message message) {
        UserMessage userMessage = new UserMessage((JsonObject) message.body());
        String userId = userMessage.userId;
        List<String> idList = new ArrayList<>(userMessage.alternateIds);
        idList.add(userId);
            for (String altID : idList) {
                readSpikeValue("optout", null, altID);
                if (!isOptOut) {
                    readSpikeValue("segment", null, altID);// combine
                }
            }
        sendToEdgeUpdateVerticle(userId);
        message.reply(true);
    }

    private void readSpikeValue(String namespace, String set, String key) {
        vertx.eventBus().send(AerospikeVerticle.address, new AerospikeRequest(namespace, set, key),
                result -> {
                    if (result.succeeded()) {
                        AerospikeRecord aerospikeRecord = (AerospikeRecord) result.result().body();
                        if (namespace.equals("optout")) {
                            checkUserOptOut(aerospikeRecord);
                            return;
                        }
                        combineSegmentsAltId(aerospikeRecord);
                    } else {
                        logger.error("Failed to read from AE ", result.cause());
                    }
                });
    }

    private void checkUserOptOut(AerospikeRecord aerospikeRecord) {
        isOptOut = (Boolean) aerospikeRecord.bins.get("optout");
    }

    private void combineSegmentsAltId(AerospikeRecord aerospikeRecord) {
        segmentsMap = aerospikeRecord.bins.entrySet()
                .stream()
                .collect(Collectors.toMap(val -> val.getKey(), val -> (List<String>)val.getValue()));
    }

    private void sendToEdgeUpdateVerticle(String userId) {
        if (!segmentsMap.isEmpty()) {
            List<String> segmentslist  = segmentsMap.values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
            vertx.eventBus().send(EdgeUpdateVerticle.address, new EdgeUpdateMessage(userId, segmentslist));
        } else {
            vertx.eventBus().send(EdgeUpdateVerticle.address, new EdgeUpdateMessage(userId,  Arrays.asList("No data, optOut is true")));
        }

    }
}
