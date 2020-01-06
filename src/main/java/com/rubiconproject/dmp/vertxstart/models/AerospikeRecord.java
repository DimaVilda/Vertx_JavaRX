package com.rubiconproject.dmp.vertxstart.models;

import io.vertx.core.json.JsonObject;

import java.util.Map;

public class AerospikeRecord
{
    public final String key;
    public final Map<String, Object> bins;

    public AerospikeRecord(String key, Map<String, Object> bins)
    {
        this.key = key;
        this.bins = bins;
    }

    public static AerospikeRecord fromJson(JsonObject json)
    {
        return json.mapTo(AerospikeRecord.class);
    }

    public JsonObject toJson()
    {
        return JsonObject.mapFrom(this);
    }
}
