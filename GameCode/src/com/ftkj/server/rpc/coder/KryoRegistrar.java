package com.ftkj.server.rpc.coder;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.google.protobuf.GeneratedMessage;
import de.javakaffee.kryoserializers.ArraysAsListSerializer;
import de.javakaffee.kryoserializers.GregorianCalendarSerializer;
import de.javakaffee.kryoserializers.JdkProxySerializer;
import de.javakaffee.kryoserializers.SynchronizedCollectionsSerializer;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;
import de.javakaffee.kryoserializers.guava.ArrayListMultimapSerializer;
import de.javakaffee.kryoserializers.guava.HashMultimapSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableListSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableMapSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableMultimapSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableSetSerializer;
import de.javakaffee.kryoserializers.guava.LinkedHashMultimapSerializer;
import de.javakaffee.kryoserializers.guava.LinkedListMultimapSerializer;
import de.javakaffee.kryoserializers.guava.ReverseListSerializer;
import de.javakaffee.kryoserializers.guava.TreeMultimapSerializer;
import de.javakaffee.kryoserializers.guava.UnmodifiableNavigableSetSerializer;
import de.javakaffee.kryoserializers.jodatime.JodaDateTimeSerializer;
import de.javakaffee.kryoserializers.jodatime.JodaLocalDateSerializer;
import de.javakaffee.kryoserializers.jodatime.JodaLocalDateTimeSerializer;
import de.javakaffee.kryoserializers.protobuf.ProtobufSerializer;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.util.Arrays;
import java.util.GregorianCalendar;

import static com.esotericsoftware.kryo.Kryo.NULL;

/**
 * @author luch
 */
public class KryoRegistrar {
    public static void apply(Kryo kryo) {
        kryo.register(Arrays.asList("").getClass(), new ArraysAsListSerializer());
        //        kryo.register(Collections.EMPTY_LIST.getClass(), new CollectionsEmptyListSerializer());
        //        kryo.register(Collections.EMPTY_MAP.getClass(), new CollectionsEmptyMapSerializer());
        //        kryo.register(Collections.EMPTY_SET.getClass(), new CollectionsEmptySetSerializer());
        //        kryo.register(Collections.singletonList("").getClass(), new CollectionsSingletonListSerializer());
        //        kryo.register(Collections.singleton("").getClass(), new CollectionsSingletonSetSerializer());
        //        kryo.register(Collections.singletonMap("", "").getClass(), new CollectionsSingletonMapSerializer());
        kryo.register(GregorianCalendar.class, new GregorianCalendarSerializer());
        kryo.register(InvocationHandler.class, new JdkProxySerializer());
        UnmodifiableCollectionsSerializer.registerSerializers(kryo);
        SynchronizedCollectionsSerializer.registerSerializers(kryo);

        // custom serializers for non-jdk libs

        // register CGLibProxySerializer, works in combination with the appropriate action in handleUnregisteredClass (see below)
        //        kryo.register(CGLibProxySerializer.CGLibProxyMarker.class, new CGLibProxySerializer(kryo));
        // dexx
        //        ListSerializer.registerSerializers(kryo);
        //        MapSerializer.registerSerializers(kryo);
        //        SetSerializer.registerSerializers(kryo);

        // joda DateTime, LocalDate, LocalDateTime and LocalTime
        kryo.register(DateTime.class, new JodaDateTimeSerializer());
        kryo.register(LocalDate.class, new JodaLocalDateSerializer());
        kryo.register(LocalDateTime.class, new JodaLocalDateTimeSerializer());
        //        kryo.register(LocalDateTime.class, new JodaLocalTimeSerializer());
        // protobuf
        kryo.register(GeneratedMessage.class, new ProtobufSerializer()); // or override Kryo.getDefaultSerializer as shown below
        // wicket
        //        kryo.register(MiniMap.class, new MiniMapSerializer());
        // guava ImmutableList, ImmutableSet, ImmutableMap, ImmutableMultimap, ReverseList, UnmodifiableNavigableSet
        ImmutableListSerializer.registerSerializers(kryo);
        ImmutableSetSerializer.registerSerializers(kryo);
        ImmutableMapSerializer.registerSerializers(kryo);
        ImmutableMultimapSerializer.registerSerializers(kryo);
        ReverseListSerializer.registerSerializers(kryo);
        UnmodifiableNavigableSetSerializer.registerSerializers(kryo);
        // guava ArrayListMultimap, HashMultimap, LinkedHashMultimap, LinkedListMultimap, TreeMultimap
        ArrayListMultimapSerializer.registerSerializers(kryo);
        HashMultimapSerializer.registerSerializers(kryo);
        LinkedHashMultimapSerializer.registerSerializers(kryo);
        LinkedListMultimapSerializer.registerSerializers(kryo);
        TreeMultimapSerializer.registerSerializers(kryo);

        kryo.register(Serializable.class, new X3JavaSerializer());
    }

    /**
     * This Serializer uses standard Java Serialization
     */
    public static final class X3JavaSerializer extends Serializer<Serializable> {
        private static final Logger log = LoggerFactory.getLogger(JavaSerializer.class);

        {
            setAcceptsNull(true);
        }

        @Override
        public void write(Kryo kryo, Output output, Serializable object) {
            if (object == null) {
                output.writeVarInt(NULL, true);
                log.info("Serializer clazz {} len {}");
                return;
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                ObjectOutputStream out = new ObjectOutputStream(bos);
                out.writeObject(object);
                out.flush();
                byte[] bytes = bos.toByteArray();
                log.info("Serializer clazz {} len {}", object.getClass(), bytes.length);
                out.close();
                output.writeVarInt(bytes.length + 1, true);
                output.writeBytes(bytes);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

        }

        @Override
        public Serializable read(Kryo kryo, Input input, Class<Serializable> type) {
            int length = input.readVarInt(true);
            if (length == NULL) {
                return null;
            }
            byte[] bytes = input.readBytes(length - 1);
            try {
                ByteArrayInputStream is = new ByteArrayInputStream(bytes);
                ObjectInputStream oi = new ObjectInputStream(is);
                Object obj = oi.readObject();
                oi.close();
                return (Serializable) obj;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            return null;
        }

    }

}
