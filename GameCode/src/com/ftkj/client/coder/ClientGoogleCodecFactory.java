package com.ftkj.client.coder;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * @author tim.huang
 * 2016年12月28日
 * 客户端Codec工厂
 */
public class ClientGoogleCodecFactory implements ProtocolCodecFactory {
	
	private final ProtocolEncoder encoder;
    private final ProtocolDecoder decoder;
	public ClientGoogleCodecFactory() {
		 encoder = new ClientGoogleEncoder();
         decoder = new ClientGoogleDecoder();
	}

	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return decoder;
	}

	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return encoder;
	}


}
