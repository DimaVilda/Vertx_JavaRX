package com.rubiconproject.dmp.vertxstart.verticles;

import com.rubiconproject.dmp.vertxstart.models.*;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AerospikeVerticle extends AbstractVerticle
{
    private static Logger logger = LoggerFactory.getLogger(AerospikeVerticle.class);

    public static final String address = "aerospike";

    private static final List<String> storedSegments =
            Stream.iterate(0, i -> i + 1)
                    .map(i -> UUID.randomUUID().toString())
                    .limit(1000)
                    .collect(Collectors.toList());

    @Override
    public void start() throws Exception
    {
        vertx.eventBus().consumer(address, this::handle);
    }

    private void handle( Message message) {
        //produceRandomError(message);
        Request request = ((Request) message.body());
        switch (request.namespace)
        {
            case "segment":
            {
                if (request instanceof AerospikeRequest) {
                    handleSegmentRequest((AerospikeRequest)message.body(), message);
                } else if (request instanceof AerospikeBatchRequests) {
                    handleBatchSegmentRequest((AerospikeBatchRequests)message.body(), message);
                }
                break;
            }
            case "optout":
            {
                if (request instanceof AerospikeRequest) {
                    handleOptOutRequest((AerospikeRequest)message.body(), message);
                } else if (request instanceof AerospikeBatchRequests) {
                    handleBatchOptOutRequest((AerospikeBatchRequests)message.body(), message);
                }
                break;
            }
            default:
            {
                message.fail(0, "Unknown namespace " + request.namespace);
            }
        }
    }

    private void produceRandomError(Message message) {
       // Random random = new Random();
         if (new Random().nextBoolean()) {
             message.fail(-1,"Eto fiasko, bratan");
         }
    }

    private void handleBatchSegmentRequest(AerospikeBatchRequests request, Message message) {
        Map<String, Object> bins = new HashMap<>();
        for (String key : request.keysList) {
             bins.put(key, produceSegments(key));
        }
        message.reply(new AerospikeRecord("batchSegments", Collections.singletonMap("segments", bins)));
    }

    private void handleBatchOptOutRequest(AerospikeBatchRequests request, Message message) {
        Map<String, Object> bins = new HashMap<>();
        for (String key : request.keysList) {
            boolean isOptOut = key.hashCode() % 2 == 0;
            bins.put(key, isOptOut);
        }
        message.reply(new AerospikeRecord("batchOptout", Collections.singletonMap("optout", bins)));
    }

    private void handleSegmentRequest(AerospikeRequest request, Message message)
    {
        Map<String, Object> bins = Collections.singletonMap("segments", produceSegments(request.key));
        message.reply(new AerospikeRecord(request.key, bins));
    }

    private void handleOptOutRequest(AerospikeRequest request, Message message)
    {
        boolean isOptOut = request.key.hashCode() % 2 == 0;

        Map<String, Object> bins = Collections.singletonMap("optout", isOptOut);

        message.reply(new AerospikeRecord(request.key, bins));
    }

    private List<String> produceSegments(String key) {
        int n = Math.abs(key.hashCode() % 10) + 1;

        List<String> segments = new ArrayList<>();

        for (int i = 0; i < n; i++)
        {
            //choose a random segment in stored segments
            int index = Math.abs((key + n + new Random().nextInt(99)).hashCode() % storedSegments.size());
            segments.add(storedSegments.get(index));
        }
        return segments;
    }
}
