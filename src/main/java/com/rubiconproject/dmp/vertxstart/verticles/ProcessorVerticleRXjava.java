package com.rubiconproject.dmp.vertxstart.verticles;

import com.rubiconproject.dmp.vertxstart.models.AerospikeRecord;
import com.rubiconproject.dmp.vertxstart.models.AerospikeRequest;
import com.rubiconproject.dmp.vertxstart.models.EdgeUpdateMessage;
import com.rubiconproject.dmp.vertxstart.models.UserMessage;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ProcessorVerticleRXjava extends AbstractVerticle {
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

  public void stop(){
    disposable.dispose();
  }

  public Flowable<ArrayList<AerospikeRecord>> getData(UserMessage message)  {
    userId = message.userId;
    List<String> idList = new ArrayList<>(message.alternateIds);
    idList.add(userId);
    idList.add("527ab6ea-b247-49af-suka-79d1e8382452");
    List<Flowable<Message<Object>>> observablesToMerge = new ArrayList<>();
    Flowable<Object> flow;

    for (String id : idList) {
       vertx.eventBus().rxSend(AerospikeVerticle.address, new AerospikeRequest("optout", null, id))
              .subscribe(reply -> {
                  checkUserOptOut ((AerospikeRecord) reply.body());
               });
      if (!isOptOut) {
        observablesToMerge.add(vertx.eventBus().rxSend(AerospikeVerticle.address, new AerospikeRequest("segment", null, id)) .toFlowable());
      }
    }
    flow = Flowable.merge(observablesToMerge);
    return flow.map(item -> ((Message<AerospikeRecord>)item).body()).collectInto(new ArrayList<AerospikeRecord>(), ArrayList::add).toFlowable();
  }

  private void checkUserOptOut(AerospikeRecord aerospikeRecord) {
    isOptOut = (Boolean) aerospikeRecord.bins.get("optout");
  }

  private void sendData(ArrayList<AerospikeRecord> aerospikeRecordArrayList) {
    if (!aerospikeRecordArrayList.isEmpty()) {
      segmentsMap = aerospikeRecordArrayList.stream()
            //  .collect(Collectors.toMap(aerospikeRecord -> aerospikeRecord.key,aerospikeRecord -> (List<String>)aerospikeRecord.bins.get("segments")));
              .collect(Collectors.toMap(aerospikeRecord -> aerospikeRecord.key,aerospikeRecord -> (List<String>)aerospikeRecord.bins.get("segments")));
      List<String> segmentslist = segmentsMap.keySet()
             .stream()
             .map(o -> segmentsMap.get(o))
             .flatMap(Collection::stream)
             .collect(Collectors.toList());
      vertx.eventBus().send(EdgeUpdateVerticle.address, new EdgeUpdateMessage(userId, segmentslist));
    } else {
      vertx.eventBus().send(EdgeUpdateVerticle.address, new EdgeUpdateMessage(userId,  Arrays.asList("No data, optOut is true")));
    }
  }
}
