package com.rubiconproject.dmp.vertxstart.models;

import io.vertx.core.json.JsonObject;

public class AerospikeRequest extends Request
{

    public final String key;

    public AerospikeRequest(String namespace, String set, String key) {
        super(namespace, set);
        this.key = key;
    }

    public static AerospikeRequest fromJson(JsonObject json)
    {
        return json.mapTo(AerospikeRequest.class);
    }

/*    public JsonObject toJson()
    {
        return JsonObject.mapFrom(this);
    }*/
}
