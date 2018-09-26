package com.ftkj.cfg.base;

import com.ftkj.util.excel.ColData;
import com.ftkj.util.excel.RowData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 转换 excel 重复的列为 结构化数据.
 * <p>
 * 例如:转换
 * <p>
 * <pre>{@code
 *       ---------------------------------------------------------------------------------------------------------------
 *  列头 | colPreA1 | colPreB1 | colPreC1 | colPreD1 | colPreE1 | colPreA2 | colPreB2 | colPreC2 | colPreD2 | colPreE2 |
 *       ---------------------------------------------------------------------------------------------------------------
 *  列值 | a1 val   | b1 val   | c1 val   | d1 val   | e1 val   | a2 val   | b2 val   | c2 val   | d2 val   | e2 val   |
 *       ---------------------------------------------------------------------------------------------------------------
 * }</pre>
 * </p>
 * 为
 * <pre>{@code
 *   ListTuple
 *      list[ a1 val, a2 val]
 *      list[ b1 val, b2 val]
 *      list[ c1 val, c2 val]
 *      list[ d1 val, d2 val]
 *      list[ e1 val, e2 val]
 *    MapIDTuple[
 *      key: 1, IDTuple[1, a1 val, b1 val, c1 val, d1 val, e1 val]
 *      key: 2, IDTuple[2, a2 val, b2 val, c2 val, d2 val, e2 val]
 *    ]
 * }</pre>
 */
public class ParseListColumnUtil {
    public final static Function<String, Integer> toInt = str -> (str == null || str.trim().isEmpty()) ? 0 : Integer.parseInt(str);
    public final static Function<String, Float> toFloat = str -> (str == null || str.trim().isEmpty()) ? 0f : Float.parseFloat(str);
    public final static Function<String, String> toStr = Function.identity();

    /** 解析1列为一组的数据 */
    public static <K, E1> IDListTuple1<K, E1> parse(RowData row,
                                                    Function<String, K> kConverter,
                                                    ElementConverter<E1> e1Converter) {
        IDListTuple1<K, E1> idt = new IDListTuple1<>();
        Function<? super K, ? extends IDTuple1<K, E1>> getOrCreateIDT =
                key -> idt.getTuples().computeIfAbsent(key, IDTuple1::new);

        ParsePipeline<K> pipeline = new ParsePipeline<>(kConverter);
        pipeline.addLast(e1Converter, idt.listTuple1Consumer(), idTuple1Consumer(getOrCreateIDT));
        pipeline.findAllPrefix(row);
        return idt;
    }

    /** 解析2列为一组的数据 */
    public static <K, E1, E2> IDListTuple2<K, E1, E2> parse(RowData row,
                                                            Function<String, K> kConverter,
                                                            String prefix1, Function<String, E1> e1Converter,
                                                            String prefix2, Function<String, E2> e2Converter) {
        return parse(row, kConverter,
                new ElementConverter<>(prefix1, e1Converter),
                new ElementConverter<>(prefix2, e2Converter));
    }

    /** 解析2列为一组的数据 */
    public static <K, E1, E2> IDListTuple2<K, E1, E2> parse(RowData row,
                                                            Function<String, K> kConverter,
                                                            ElementConverter<E1> e1Converter,
                                                            ElementConverter<E2> e2Converter) {
        IDListTuple2<K, E1, E2> idt = new IDListTuple2<>();
        Function<? super K, ? extends IDTuple2<K, E1, E2>> getOrCreateIDT =
                key -> idt.getTuples().computeIfAbsent(key, IDTuple2::new);

        ParsePipeline<K> pipeline = new ParsePipeline<>(kConverter);
        pipeline.addLast(e1Converter, idt.listTuple1Consumer(), idTuple1Consumer(getOrCreateIDT));
        pipeline.addLast(e2Converter, idt.listTuple2Consumer(), idTuple2Consumer(getOrCreateIDT));
        pipeline.findAllPrefix(row);
        return idt;
    }

    /** 解析3列为一组的数据 */
    public static <K, E1, E2, E3> IDListTuple3<K, E1, E2, E3> parse(RowData row,
                                                                    Function<String, K> kConverter,
                                                                    String prefix1, Function<String, E1> e1Converter,
                                                                    String prefix2, Function<String, E2> e2Converter,
                                                                    String prefix3, Function<String, E3> e3Converter) {
        return parse(row, kConverter,
                new ElementConverter<>(prefix1, e1Converter),
                new ElementConverter<>(prefix2, e2Converter),
                new ElementConverter<>(prefix3, e3Converter));
    }

    /** 解析3列为一组的数据 */
    public static <K, E1, E2, E3> IDListTuple3<K, E1, E2, E3> parse(RowData row,
                                                                    Function<String, K> kConverter,
                                                                    ElementConverter<E1> e1Converter,
                                                                    ElementConverter<E2> e2Converter,
                                                                    ElementConverter<E3> e3Converter) {
        IDListTuple3<K, E1, E2, E3> idt = new IDListTuple3<>();
        Function<? super K, ? extends IDTuple3<K, E1, E2, E3>> getOrCreateIDT =
                key -> idt.getTuples().computeIfAbsent(key, IDTuple3::new);

        ParsePipeline<K> pipeline = new ParsePipeline<>(kConverter);
        pipeline.addLast(e1Converter, idt.listTuple1Consumer(), idTuple1Consumer(getOrCreateIDT));
        pipeline.addLast(e2Converter, idt.listTuple2Consumer(), idTuple2Consumer(getOrCreateIDT));
        pipeline.addLast(e3Converter, idt.listTuple3Consumer(), idTuple3Consumer(getOrCreateIDT));
        pipeline.findAllPrefix(row);
        return idt;
    }

    /** 解析4列为一组的数据 */
    public static <K, E1, E2, E3, E4> IDListTuple4<K, E1, E2, E3, E4> parse(RowData row,
                                                                            Function<String, K> kConverter,
                                                                            String prefix1, Function<String, E1> e1Converter,
                                                                            String prefix2, Function<String, E2> e2Converter,
                                                                            String prefix3, Function<String, E3> e3Converter,
                                                                            String prefix4, Function<String, E4> e4Converter) {
        return parse(row, kConverter,
                new ElementConverter<>(prefix1, e1Converter),
                new ElementConverter<>(prefix2, e2Converter),
                new ElementConverter<>(prefix3, e3Converter),
                new ElementConverter<>(prefix4, e4Converter));
    }

    /** 解析4列为一组的数据 */
    public static <K, E1, E2, E3, E4> IDListTuple4<K, E1, E2, E3, E4> parse(RowData row,
                                                                            Function<String, K> kConverter,
                                                                            ElementConverter<E1> e1Converter,
                                                                            ElementConverter<E2> e2Converter,
                                                                            ElementConverter<E3> e3Converter,
                                                                            ElementConverter<E4> e4Converter) {
        IDListTuple4<K, E1, E2, E3, E4> idt = new IDListTuple4<>();
        Function<? super K, ? extends IDTuple4<K, E1, E2, E3, E4>> getOrCreateIDT =
                key -> idt.getTuples().computeIfAbsent(key, IDTuple4::new);

        ParsePipeline<K> pipeline = new ParsePipeline<>(kConverter);
        pipeline.addLast(e1Converter, idt.listTuple1Consumer(), idTuple1Consumer(getOrCreateIDT));
        pipeline.addLast(e2Converter, idt.listTuple2Consumer(), idTuple2Consumer(getOrCreateIDT));
        pipeline.addLast(e3Converter, idt.listTuple3Consumer(), idTuple3Consumer(getOrCreateIDT));
        pipeline.addLast(e4Converter, idt.listTuple4Consumer(), idTuple4Consumer(getOrCreateIDT));
        pipeline.findAllPrefix(row);
        return idt;
    }

    /** 解析5列为一组的数据 */
    public static <K, E1, E2, E3, E4, E5> IDListTuple5<K, E1, E2, E3, E4, E5> parse(RowData row,
                                                                                    Function<String, K> kConverter,
                                                                                    String prefix1, Function<String, E1> e1Converter,
                                                                                    String prefix2, Function<String, E2> e2Converter,
                                                                                    String prefix3, Function<String, E3> e3Converter,
                                                                                    String prefix4, Function<String, E4> e4Converter,
                                                                                    String prefix5, Function<String, E5> e5Converter) {
        return parse(row, kConverter,
                new ElementConverter<>(prefix1, e1Converter),
                new ElementConverter<>(prefix2, e2Converter),
                new ElementConverter<>(prefix3, e3Converter),
                new ElementConverter<>(prefix4, e4Converter),
                new ElementConverter<>(prefix5, e5Converter));
    }

    /** 解析6列为一组的数据 */
    public static <K, E1, E2, E3, E4, E5, E6> IDListTuple6<K, E1, E2, E3, E4, E5, E6> parse(RowData row,
                                                                                            Function<String, K> kConverter,
                                                                                            String prefix1, Function<String, E1> e1Converter,
                                                                                            String prefix2, Function<String, E2> e2Converter,
                                                                                            String prefix3, Function<String, E3> e3Converter,
                                                                                            String prefix4, Function<String, E4> e4Converter,
                                                                                            String prefix5, Function<String, E5> e5Converter,
                                                                                            String prefix6, Function<String, E6> e6Converter) {
        return parse(row, kConverter,
                new ElementConverter<>(prefix1, e1Converter),
                new ElementConverter<>(prefix2, e2Converter),
                new ElementConverter<>(prefix3, e3Converter),
                new ElementConverter<>(prefix4, e4Converter),
                new ElementConverter<>(prefix5, e5Converter),
                new ElementConverter<>(prefix6, e6Converter));
    }

    /** 解析5列为一组的数据 */
    public static <K, E1, E2, E3, E4, E5> IDListTuple5<K, E1, E2, E3, E4, E5> parse(RowData row,
                                                                                    Function<String, K> kConverter,
                                                                                    ElementConverter<E1> e1Converter,
                                                                                    ElementConverter<E2> e2Converter,
                                                                                    ElementConverter<E3> e3Converter,
                                                                                    ElementConverter<E4> e4Converter,
                                                                                    ElementConverter<E5> e5Converter) {
        IDListTuple5<K, E1, E2, E3, E4, E5> idt = new IDListTuple5<>();
        Function<? super K, ? extends IDTuple5<K, E1, E2, E3, E4, E5>> getOrCreateIDT =
                key -> idt.getTuples().computeIfAbsent(key, IDTuple5::new);

        ParsePipeline<K> pipeline = new ParsePipeline<>(kConverter);
        pipeline.addLast(e1Converter, idt.listTuple1Consumer(), idTuple1Consumer(getOrCreateIDT));
        pipeline.addLast(e2Converter, idt.listTuple2Consumer(), idTuple2Consumer(getOrCreateIDT));
        pipeline.addLast(e3Converter, idt.listTuple3Consumer(), idTuple3Consumer(getOrCreateIDT));
        pipeline.addLast(e4Converter, idt.listTuple4Consumer(), idTuple4Consumer(getOrCreateIDT));
        pipeline.addLast(e5Converter, idt.listTuple5Consumer(), idTuple5Consumer(getOrCreateIDT));
        pipeline.findAllPrefix(row);
        return idt;
    }

    /** 解析6列为一组的数据 */
    public static <K, E1, E2, E3, E4, E5, E6> IDListTuple6<K, E1, E2, E3, E4, E5, E6> parse(RowData row,
                                                                                            Function<String, K> kConverter,
                                                                                            ElementConverter<E1> e1Converter,
                                                                                            ElementConverter<E2> e2Converter,
                                                                                            ElementConverter<E3> e3Converter,
                                                                                            ElementConverter<E4> e4Converter,
                                                                                            ElementConverter<E5> e5Converter,
                                                                                            ElementConverter<E6> e6Converter) {
        IDListTuple6<K, E1, E2, E3, E4, E5, E6> idt = new IDListTuple6<>();
        Function<? super K, ? extends IDTuple6<K, E1, E2, E3, E4, E5, E6>> getOrCreateIDT =
                key -> idt.getTuples().computeIfAbsent(key, IDTuple6::new);

        ParsePipeline<K> pipeline = new ParsePipeline<>(kConverter);
        pipeline.addLast(e1Converter, idt.listTuple1Consumer(), idTuple1Consumer(getOrCreateIDT));
        pipeline.addLast(e2Converter, idt.listTuple2Consumer(), idTuple2Consumer(getOrCreateIDT));
        pipeline.addLast(e3Converter, idt.listTuple3Consumer(), idTuple3Consumer(getOrCreateIDT));
        pipeline.addLast(e4Converter, idt.listTuple4Consumer(), idTuple4Consumer(getOrCreateIDT));
        pipeline.addLast(e5Converter, idt.listTuple5Consumer(), idTuple5Consumer(getOrCreateIDT));
        pipeline.addLast(e6Converter, idt.listTuple6Consumer(), idTuple6Consumer(getOrCreateIDT));
        pipeline.findAllPrefix(row);
        return idt;
    }

    private static <K, E1> BiConsumer<K, E1>
    idTuple1Consumer(Function<? super K, ? extends IDTuple1<K, E1>> getOrCreateIDT) {
        return (k, e) -> getOrCreateIDT.apply(k).setE1(e);
    }

    private static <K, E1, E2> BiConsumer<K, E2>
    idTuple2Consumer(Function<? super K, ? extends IDTuple2<K, E1, E2>> getOrCreateIDT) {
        return (k, e) -> getOrCreateIDT.apply(k).setE2(e);
    }

    private static <K, E1, E2, E3> BiConsumer<K, E3>
    idTuple3Consumer(Function<? super K, ? extends IDTuple3<K, E1, E2, E3>> getOrCreateIDT) {
        return (k, e) -> getOrCreateIDT.apply(k).setE3(e);
    }

    private static <K, E1, E2, E3, E4> BiConsumer<K, E4>
    idTuple4Consumer(Function<? super K, ? extends IDTuple4<K, E1, E2, E3, E4>> getOrCreateIDT) {
        return (k, e) -> getOrCreateIDT.apply(k).setE4(e);
    }

    private static <K, E1, E2, E3, E4, E5> BiConsumer<K, E5>
    idTuple5Consumer(Function<? super K, ? extends IDTuple5<K, E1, E2, E3, E4, E5>> getOrCreateIDT) {
        return (k, e) -> getOrCreateIDT.apply(k).setE5(e);
    }

    private static <K, E1, E2, E3, E4, E5, E6> BiConsumer<K, E6>
    idTuple6Consumer(Function<? super K, ? extends IDTuple6<K, E1, E2, E3, E4, E5, E6>> getOrCreateIDT) {
        return (k, e) -> getOrCreateIDT.apply(k).setE6(e);
    }

    public final static class ParsePipeline<K> {
        ConverterContext head;
        ConverterContext tail;

        final Function<String, K> kConverter;
        Map<String, ElementConverterContext<K, ?>> converters = new LinkedHashMap<>();

        public ParsePipeline(Function<String, K> kConverter) {
            this.kConverter = kConverter;

            head = new HeadContext();
            tail = new TailContext();

            head.next = tail;
            tail.prev = head;
        }

        protected final <E> K parseKey(ElementConverter<E> converter, String colName) {
            return kConverter.apply(getSuffix(colName, converter.prefix));
        }

        final <E> void addConverter(ElementConverterContext<K, E> eConverter) {
            converters.put(eConverter.converter.prefix, eConverter);
        }

        final void findAllPrefix(RowData row) {
            checkPrefix(new ArrayList<>(converters.keySet()));
            for (Map.Entry<String, ColData> e : row.getRows().entrySet()) {
                String colName = e.getKey();
                ColData cd = e.getValue();
                String v = cd.getValueStr();
                head.convert(colName, v);
            }
        }

        <E> void addLast(ElementConverter<E> converter,
                         Consumer<E> listTupleElement,
                         BiConsumer<K, E> idTupleElement) {
            ConverterContext newCtx = new ElementConverterContext<>(this, converter, listTupleElement, idTupleElement);
            ConverterContext prev = tail.prev;
            newCtx.prev = prev;
            newCtx.next = tail;
            prev.next = newCtx;
            tail.prev = newCtx;
        }
    }

    public static final class ElementConverter<E> {
        /** 列名前缀 */
        private final String prefix;
        /** 列值转换器. f[列值:str, 新的值:E] */
        private final Function<String, E> eConverter;

        public ElementConverter(String prefix, Function<String, E> eConverter) {
            this.prefix = prefix;
            this.eConverter = eConverter;
        }

    }

    static class ConverterContext {
        private ConverterContext prev;
        private ConverterContext next;

        void convert(String colName, String v) {
        }

        ConverterContext getNext() {
            return next;
        }
    }

    static class HeadContext extends ConverterContext {
        @Override
        void convert(String colName, String v) {
            getNext().convert(colName, v);
        }
    }

    static class TailContext extends ConverterContext {

    }

    static final class ElementConverterContext<K, E> extends ConverterContext {
        private final ParsePipeline<K> parser;
        private final ElementConverter<E> converter;
        private final Consumer<E> listTupleElement;
        private final BiConsumer<K, E> idTupleElement;

        ElementConverterContext(ParsePipeline<K> parser,
                                ElementConverter<E> converter,
                                Consumer<E> listTupleElement,
                                BiConsumer<K, E> idTupleElement) {
            this.parser = parser;
            this.converter = converter;
            this.listTupleElement = listTupleElement;
            this.idTupleElement = idTupleElement;
        }

        @Override
		void convert(String colName, String v) {
            if (colName.startsWith(converter.prefix)) {
                E e = converter.eConverter.apply(v);
                K k = parser.parseKey(converter, colName);
                listTupleElement.accept(e);
                idTupleElement.accept(k, e);
            } else if (getNext() != null) {
                getNext().convert(colName, v);
            }
        }

    }

    public static class IDTuple1<K, E1> {
        private K k;
        private E1 e1;

        public IDTuple1(K key) {
            this.k = key;
        }

        public void setE1(E1 e1) {
            this.e1 = e1;
        }

        public K getK() {
            return k;
        }

        public E1 getE1() {
            return e1;
        }
    }

    public static class IDTuple2<K, E1, E2> extends IDTuple1<K, E1> {
        private E2 e2;

        public IDTuple2(K key) {
            super(key);
        }

        public void setE2(E2 e2) {
            this.e2 = e2;
        }

        public E2 getE2() {
            return e2;
        }
    }

    public static class IDTuple3<K, E1, E2, E3> extends IDTuple2<K, E1, E2> {
        private E3 e3;

        public IDTuple3(K key) {
            super(key);
        }

        public void setE3(E3 e3) {
            this.e3 = e3;
        }

        public E3 getE3() {
            return e3;
        }
    }

    public static class IDTuple4<K, E1, E2, E3, E4> extends IDTuple3<K, E1, E2, E3> {
        private E4 e4;

        public IDTuple4(K key) {
            super(key);
        }

        public E4 getE4() {
            return e4;
        }

        public void setE4(E4 e4) {
            this.e4 = e4;
        }
    }

    public static class IDTuple5<K, E1, E2, E3, E4, E5> extends IDTuple4<K, E1, E2, E3, E4> {
        private E5 e5;

        public IDTuple5(K key) {
            super(key);
        }

        public E5 getE5() {
            return e5;
        }

        public void setE5(E5 e5) {
            this.e5 = e5;
        }
    }

    public static class IDTuple6<K, E1, E2, E3, E4, E5, E6> extends IDTuple5<K, E1, E2, E3, E4, E5> {
        private E6 e6;

        public IDTuple6(K key) {
            super(key);
        }

        public void setE6(E6 e6) {
            this.e6 = e6;
        }

        public E6 getE6() {
            return e6;
        }
    }

    public static class ListTuple1<E1> {
        private final List<E1> l1 = new ArrayList<>();

        public List<E1> getL1() {
            return l1;
        }

        final Consumer<E1> listTuple1Consumer() {
            return e -> getL1().add(e);
        }
    }

    public static class ListTuple2<E1, E2> extends ListTuple1<E1> {
        private final List<E2> l2 = new ArrayList<>();

        public List<E2> getL2() {
            return l2;
        }

        final Consumer<E2> listTuple2Consumer() {
            return e -> getL2().add(e);
        }
    }

    public static class ListTuple3<E1, E2, E3> extends ListTuple2<E1, E2> {
        private final List<E3> l3 = new ArrayList<>();

        public List<E3> getL3() {
            return l3;
        }

        final Consumer<E3> listTuple3Consumer() {
            return e -> getL3().add(e);
        }
    }

    public static class ListTuple4<E1, E2, E3, E4> extends ListTuple3<E1, E2, E3> {
        private final List<E4> l4 = new ArrayList<>();

        public List<E4> getL4() {
            return l4;
        }

        final Consumer<E4> listTuple4Consumer() {
            return e -> getL4().add(e);
        }
    }

    public static class ListTuple5<E1, E2, E3, E4, E5> extends ListTuple4<E1, E2, E3, E4> {
        private final List<E5> l5 = new ArrayList<>();

        public List<E5> getL5() {
            return l5;
        }

        final Consumer<E5> listTuple5Consumer() {
            return e -> getL5().add(e);
        }
    }

    public static class ListTuple6<E1, E2, E3, E4, E5, E6> extends ListTuple5<E1, E2, E3, E4, E5> {
        private final List<E6> l6 = new ArrayList<>();

        public List<E6> getL6() {
            return l6;
        }

        final Consumer<E6> listTuple6Consumer() {
            return e -> getL6().add(e);
        }
    }

    public static class IDListTuple1<K, E1> extends ListTuple1<E1> {
        private final Map<K, IDTuple1<K, E1>> tuples = new LinkedHashMap<>();

        public Map<K, IDTuple1<K, E1>> getTuples() {
            return tuples;
        }
    }

    public static class IDListTuple2<K, E1, E2> extends ListTuple2<E1, E2> {
        private final Map<K, IDTuple2<K, E1, E2>> tuples = new LinkedHashMap<>();

        public Map<K, IDTuple2<K, E1, E2>> getTuples() {
            return tuples;
        }
    }

    public static class IDListTuple3<K, E1, E2, E3> extends ListTuple3<E1, E2, E3> {
        private final Map<K, IDTuple3<K, E1, E2, E3>> tuples = new LinkedHashMap<>();

        public Map<K, IDTuple3<K, E1, E2, E3>> getTuples() {
            return tuples;
        }
    }

    public static class IDListTuple4<K, E1, E2, E3, E4> extends ListTuple4<E1, E2, E3, E4> {
        private final Map<K, IDTuple4<K, E1, E2, E3, E4>> tuples = new LinkedHashMap<>();

        public Map<K, IDTuple4<K, E1, E2, E3, E4>> getTuples() {
            return tuples;
        }
    }

    public static class IDListTuple5<K, E1, E2, E3, E4, E5> extends ListTuple5<E1, E2, E3, E4, E5> {
        private final Map<K, IDTuple5<K, E1, E2, E3, E4, E5>> tuples = new LinkedHashMap<>();

        public Map<K, IDTuple5<K, E1, E2, E3, E4, E5>> getTuples() {
            return tuples;
        }
    }

    public static class IDListTuple6<K, E1, E2, E3, E4, E5, E6> extends ListTuple6<E1, E2, E3, E4, E5, E6> {
        private final Map<K, IDTuple6<K, E1, E2, E3, E4, E5, E6>> tuples = new LinkedHashMap<>();

        public Map<K, IDTuple6<K, E1, E2, E3, E4, E5, E6>> getTuples() {
            return tuples;
        }
    }

    static void checkPrefix(List<String> prefixArr) {
        for (int i = 0; i < prefixArr.size(); i++) {
            for (int j = 0; j < prefixArr.size(); j++) {
                if (i == j) {
                    continue;
                }
                if (prefixArr.get(j).contains(prefixArr.get(i))) {
                    throw new IllegalArgumentException("prefix " + prefixArr.get(j) + " contains prefix " + prefixArr.get(i));
                }
            }
        }
    }

    private static String getSuffix(String colName, String prefix) {
        return colName.substring(prefix.length());
    }

}
