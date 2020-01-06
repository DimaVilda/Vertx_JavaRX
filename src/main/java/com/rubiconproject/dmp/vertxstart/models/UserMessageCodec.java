package com.rubiconproject.dmp.vertxstart.models;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;

public class UserMessageCodec implements MessageCodec<UserMessage, UserMessage>
{
    public void encodeToWire(Buffer buffer, UserMessage userMessage)
    {
        userMessage.toJson().writeToBuffer(buffer);
    }

    public UserMessage decodeFromWire(int pos, Buffer buffer)
    {
        return new UserMessage(new JsonObject(buffer));
    }

    public UserMessage transform(UserMessage userMessage)
    {
        return userMessage;
    }

    public String name()
    {
        return "UserMessageCodec";
    }

    public byte systemCodecID()
    {
        return -1;
    }
}
