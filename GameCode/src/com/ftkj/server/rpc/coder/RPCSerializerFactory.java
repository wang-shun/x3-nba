package com.ftkj.server.rpc.coder;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.esotericsoftware.kryo.util.MapReferenceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class RPCSerializerFactory {
    private static final Logger log = LoggerFactory.getLogger(RPCSerializerFactory.class);

    public interface X3Serializer {
        byte[] toBinary(Object obj);

        <T> T fromBinary(byte[] bytes, Class<T> clz);
    }

    public static final class KryoSerializer implements X3Serializer {
        private static final KryoFactory factory = () -> {
            Kryo kryo = new Kryo(new X3KryoClassResolver(), new MapReferenceResolver());
            KryoRegistrar.apply(kryo);
            // configure kryo instance, customize settings
            return kryo;
        };
        // Build pool with SoftReferences enabled (optional)
        private static final KryoPool pool = new KryoPool.Builder(factory)
                .softReferences().build();

        public KryoSerializer() {
        }

        public byte[] toBinary(Object obj) {
            return pool.run(kryo -> {
                try (Output output = new Output(new ByteArrayOutputStream())) {
                    kryo.writeClassAndObject(output, obj);
                    return output.toBytes();
                }
            });
        }

        public <T> T fromBinary(byte[] bytes, Class<T> clz) {
            return pool.run(kryo -> {
                try (Input input = new Input(bytes)) {
                    return clz.cast(kryo.readClassAndObject(input));
                }
            });
        }
    }

    public static class X3JavaSerializer implements X3Serializer {
        @Override
        public byte[] toBinary(Object obj) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                ObjectOutputStream out = new ObjectOutputStream(bos);
                out.writeObject(obj);
                out.flush();
                byte[] bytes = bos.toByteArray();
                if (log.isDebugEnabled()) {
                    log.debug("Serializer clazz {} len {}", obj.getClass().getName(), bytes.length);
                }
                out.close();
                return bytes;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            return new byte[0];
        }

        @Override
        public <T> T fromBinary(byte[] bytes, Class<T> clz) {
            try {
                ByteArrayInputStream is = new ByteArrayInputStream(bytes);
                ObjectInputStream oi = new ObjectInputStream(is);
                Object obj = oi.readObject();
                oi.close();
                return clz.cast(obj);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            return null;
        }
    }

}
