package com.rubiconproject.dmp.vertxstart.models;

import io.vertx.core.json.JsonObject;

public interface IAerospikeRequest {
    public JsonObject toJson();
}
