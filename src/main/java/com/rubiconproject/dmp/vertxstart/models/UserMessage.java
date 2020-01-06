package com.rubiconproject.dmp.vertxstart.models;

import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

public class UserMessage
{
    public final int id;
    public final String userId;
    public final List<String> alternateIds;

    public UserMessage(int id, String userId, List<String> alternateIds)
    {
        this.id = id;
        this.userId = userId;
        this.alternateIds = alternateIds;
    }

    public UserMessage(JsonObject json)
    {
        id = json.getInteger("id");
        userId = json.getString("userId");
        alternateIds =
                json.getJsonArray("alternateIds").stream().map(obj -> (String)obj).collect(Collectors.toList());
    }

    public JsonObject toJson()
    {
        return new JsonObject().put("id", id).put("userId", userId).put("alternateIds", alternateIds);
    }
}
