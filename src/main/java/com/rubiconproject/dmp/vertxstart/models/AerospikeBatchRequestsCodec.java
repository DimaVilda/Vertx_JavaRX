package com.rubiconproject.dmp.vertxstart.models;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;

public class AerospikeBatchRequestsCodec implements MessageCodec<AerospikeBatchRequests, AerospikeBatchRequests>
{
  @Override
  public void encodeToWire(Buffer buffer, AerospikeBatchRequests aerospikeRequest)
  {
    aerospikeRequest.toJson().writeToBuffer(buffer);
  }

  @Override
  public AerospikeBatchRequests decodeFromWire(int pos, Buffer buffer)
  {
    return AerospikeBatchRequests.fromJson(new JsonObject(buffer));
  }

  @Override
  public AerospikeBatchRequests transform(AerospikeBatchRequests aerospikeRequest)
  {
    return aerospikeRequest;
  }

  @Override
  public String name()
  {
    return "AerospikeBatchRequests";
  }

  @Override
  public byte systemCodecID()
  {
    return -1;
  }
}
