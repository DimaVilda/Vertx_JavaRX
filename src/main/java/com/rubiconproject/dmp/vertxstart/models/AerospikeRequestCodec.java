package com.rubiconproject.dmp.vertxstart.models;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;

public class AerospikeRequestCodec implements MessageCodec<AerospikeRequest, AerospikeRequest>
{
    @Override
    public void encodeToWire(Buffer buffer, AerospikeRequest aerospikeRequest)
    {
        aerospikeRequest.toJson().writeToBuffer(buffer);
    }

    @Override
    public AerospikeRequest decodeFromWire(int pos, Buffer buffer)
    {
        return AerospikeRequest.fromJson(new JsonObject(buffer));
    }

    @Override
    public AerospikeRequest transform(AerospikeRequest aerospikeRequest)
    {
        return aerospikeRequest;
    }

    @Override
    public String name()
    {
        return "AerospikeRequestCodec";
    }

    @Override
    public byte systemCodecID()
    {
        return -1;
    }
}
