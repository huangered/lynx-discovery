package com.yih.lynx.server.codec;

import com.google.gson.Gson;
import com.yih.lynx.core.SvcDesc;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class SvcDescCodec implements MessageCodec<SvcDesc, SvcDesc> {

    Gson gson = new Gson();

    @Override
    public void encodeToWire(Buffer buffer, SvcDesc svcDesc) {
        String str = gson.toJson(svcDesc);
        int len = str.length();
        buffer.appendInt(len);
        buffer.appendString(gson.toJson(svcDesc));
    }

    @Override
    public SvcDesc decodeFromWire(int pos, Buffer buffer) {
        int len = buffer.getInt(pos);
        String str = buffer.getString(pos + 4, pos + 4 + len);
        return gson.fromJson(str, SvcDesc.class);
    }

    @Override
    public SvcDesc transform(SvcDesc svcDesc) {
        return svcDesc;
    }

    @Override
    public String name() {
        return "SvcDescCodec";
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
