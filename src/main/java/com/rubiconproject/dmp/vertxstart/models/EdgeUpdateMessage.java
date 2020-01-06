package com.rubiconproject.dmp.vertxstart.models;

import io.vertx.core.json.JsonObject;

import java.util.List;

public class EdgeUpdateMessage
{
    public final String userId;
    public final List<String> segments;

    public EdgeUpdateMessage(String userId, List<String> segments)
    {
        this.userId = userId;
        this.segments = segments;
    }

    public static EdgeUpdateMessage fromJson(JsonObject json)
    {
        return json.mapTo(EdgeUpdateMessage.class);
    }

    public JsonObject toJson()
    {
        return JsonObject.mapFrom(this);
    }
}
