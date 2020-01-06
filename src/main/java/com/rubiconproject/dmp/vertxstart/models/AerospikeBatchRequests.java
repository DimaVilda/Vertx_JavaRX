package com.rubiconproject.dmp.vertxstart.models;

import io.vertx.core.json.JsonObject;

import java.util.List;

public class AerospikeBatchRequests extends Request {

    public final List<String> keysList;
    public final String primarykey;

    public AerospikeBatchRequests(String namespace, String set, List<String> keysList, String primarykey) {
        super(namespace, set);
        this.keysList = keysList;
        this.primarykey = primarykey;
    }
    public static AerospikeBatchRequests fromJson(JsonObject json)
    {
        return json.mapTo(AerospikeBatchRequests.class);
    }
}
