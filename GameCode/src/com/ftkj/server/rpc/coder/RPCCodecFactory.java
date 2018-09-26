package com.ftkj.server.rpc.coder;

import com.ftkj.server.rpc.coder.RPCSerializerFactory.X3JavaSerializer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class RPCCodecFactory implements ProtocolCodecFactory {
    private final ProtocolEncoder encoder;
    private final ProtocolDecoder decoder;

    public RPCCodecFactory() {
        X3JavaSerializer codec = new X3JavaSerializer();
        encoder = new RPCCodecEncoder(codec);
        decoder = new RPCCodecDecoder(codec);
    }

    public ProtocolDecoder getDecoder(IoSession session) {
        return decoder;
    }

    public ProtocolEncoder getEncoder(IoSession session) {
        return encoder;
    }

}
