package com.ftkj.server.rpc.coder;

import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.util.DefaultClassResolver;
import com.ftkj.server.rpc.coder.KryoRegistrar.X3JavaSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class X3KryoClassResolver extends DefaultClassResolver {
    private static final Logger log = LoggerFactory.getLogger(X3KryoClassResolver.class);

    public Registration registerImplicit(Class type) {
        if (Serializable.class.isAssignableFrom(type)) {
            Registration registration = getRegistration(Serializable.class);
            if (registration == null) {
                registration = kryo.register(Serializable.class, new X3JavaSerializer());
            }
            log.info("convert class {} to Serializable. use JavaSerializer registration {}", type.getName(), registration);
            return registration;
        }
        return register(new Registration(type, kryo.getDefaultSerializer(type), NAME));
    }
}
