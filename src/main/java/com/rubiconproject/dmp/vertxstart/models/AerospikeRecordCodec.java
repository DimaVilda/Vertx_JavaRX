package com.rubiconproject.dmp.vertxstart.models;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;

public class AerospikeRecordCodec implements MessageCodec<AerospikeRecord, AerospikeRecord>
{
    @Override
    public void encodeToWire(Buffer buffer, AerospikeRecord aerospikeRecord)
    {
        aerospikeRecord.toJson().writeToBuffer(buffer);
    }

    @Override
    public AerospikeRecord decodeFromWire(int pos, Buffer buffer)
    {
        return AerospikeRecord.fromJson(new JsonObject(buffer));
    }

    @Override
    public AerospikeRecord transform(AerospikeRecord aerospikeRecord)
    {
        return aerospikeRecord;
    }

    @Override
    public String name()
    {
        return "AerospikeRecordCodec";
    }

    @Override
    public byte systemCodecID()
    {
        return -1;
    }
}
