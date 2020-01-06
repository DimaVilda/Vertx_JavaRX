package com.rubiconproject.dmp.vertxstart.models;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;

public class EdgeUpdateMessageCodec implements MessageCodec<EdgeUpdateMessage, EdgeUpdateMessage>
{
    @Override
    public void encodeToWire(Buffer buffer, EdgeUpdateMessage edgeUpdateMessage)
    {
        edgeUpdateMessage.toJson().writeToBuffer(buffer);
    }

    @Override
    public EdgeUpdateMessage decodeFromWire(int pos, Buffer buffer)
    {
        return EdgeUpdateMessage.fromJson(new JsonObject(buffer));
    }

    @Override
    public EdgeUpdateMessage transform(EdgeUpdateMessage edgeUpdateMessage)
    {
        return edgeUpdateMessage;
    }

    @Override
    public String name()
    {
        return "EdgeUpdateMessage";
    }

    @Override
    public byte systemCodecID()
    {
        return -1;
    }
}
