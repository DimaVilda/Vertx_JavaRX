package com.rubiconproject.dmp.vertxstart.models;

import io.vertx.core.json.JsonObject;

public  class Request implements IAerospikeRequest{
    public final String namespace;
    public final String set;

    public Request(String namespace, String set) {
        this.namespace = namespace;
        this.set = set;
    }

    @Override
    public JsonObject toJson() {
        return JsonObject.mapFrom(this);
    }
}
