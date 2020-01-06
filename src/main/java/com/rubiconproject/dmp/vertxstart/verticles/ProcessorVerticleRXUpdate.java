package com.rubiconproject.dmp.vertxstart.verticles;

import com.rubiconproject.dmp.vertxstart.models.*;
import io.reactivex.Flowable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ProcessorVerticleRXUpdate extends AbstractVerticle {

    private static Logger logger = LoggerFactory.getLogger(ProcessorVerticle.class);
    private boolean isOptOut = false;
    private Map<String, List<String>> segmentsMap = new HashMap<>();
    Disposable disposable = null;
    private String userId = null;

    @Override
    public void start() throws Exception {
        disposable = vertx.eventBus().consumer(UserMessageProducerVerticle.producerAddress)
                .toFlowable()
                .doOnNext(message -> message.reply(true))
                .map(message -> new UserMessage((JsonObject) message.body()))
                .flatMap(this::getData)
                .subscribe(this::sendData); //return Disposable object!
    }

    public void stop() {
        disposable.dispose();
    }

    private Flowable<AerospikeRecord> getData(UserMessage message) {
        userId = message.userId;
        List<String> idList = new ArrayList<>(message.alternateIds);
        idList.add(userId);
        for (String id : idList) {
            vertx.eventBus().rxSend(AerospikeVerticle.address, new AerospikeRequest("optout", null, id))
                    .subscribe(reply -> {
                        checkUserOptOut((AerospikeRecord) reply.body());
                    });
            if (isOptOut) {
                idList.remove(id);
            }
        }
        Flowable<Message<Object>> segments = vertx.eventBus()
                .rxSend(AerospikeVerticle.address, new AerospikeBatchRequests("segment", null, idList, userId))
                .retryWhen(throwableFlowable -> throwableFlowable.take(3).delay(1, TimeUnit.SECONDS))
                .toFlowable();
        return segments.map(item -> (AerospikeRecord) item.body());
    }

    private void checkUserOptOut(AerospikeRecord aerospikeRecord) {
        isOptOut = (Boolean) aerospikeRecord.bins.get("optout");
    }

    private void sendData(AerospikeRecord aerospikeRecord) {
        List<String> segmentsList =  aerospikeRecord.bins
                .entrySet()
                .stream()
                .map(o -> ((HashMap<String,List<String>>) o.getValue()))
                .map(o -> o.values())
                .flatMap(Collection::stream)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        if (!segmentsList.isEmpty()) {
            vertx.eventBus().send(EdgeUpdateVerticle.address, new EdgeUpdateMessage(userId, segmentsList));
        } else {
            vertx.eventBus().send(EdgeUpdateVerticle.address, new EdgeUpdateMessage(userId, Arrays.asList("No data, optOut is true")));
        }

    }
}
