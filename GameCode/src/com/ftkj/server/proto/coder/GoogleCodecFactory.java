package com.ftkj.server.proto.coder;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class GoogleCodecFactory implements ProtocolCodecFactory {
	
	private final ProtocolEncoder encoder;
    private final ProtocolDecoder decoder;
	public GoogleCodecFactory() {
		 encoder = new GoogleEncoder();
         decoder = new GoogleDecoder();
	}

	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return decoder;
	}

	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return encoder;
	}


}
