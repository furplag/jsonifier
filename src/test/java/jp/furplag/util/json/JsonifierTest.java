/**
 * Copyright (C) 2017+ furplag (https://github.com/furplag)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.furplag.util.json;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

public class JsonifierTest {

  @EqualsAndHashCode
  static final class Nothing {}

  @Data
  static class TheEntity {

    private final Long serialNo;

    private final String codeName;
  }

  @Data
  @EqualsAndHashCode(callSuper = true)
  public static class TheEntityExtended extends TheEntity {

    final LocalDate created;

    TheEntityExtended(@JsonProperty("serialNo") Long serialNo, @JsonProperty("codeName") String codeName, @JsonProperty("created") LocalDate created) {
      super(serialNo, codeName);
      this.created = created;
    }
  }

  @Data
  @EqualsAndHashCode(callSuper = true)
  static class TheEntitySerializable extends TheEntityExtended implements Serializable {
    TheEntitySerializable(@JsonProperty("serialNo") Long serialNo, @JsonProperty("codeName") String codeName, @JsonProperty("created") LocalDate created) {
      super(serialNo, codeName, created);
    }
  }

  @Data
  static final class Moron {
    public String suchAs;

    @JsonProperty
    public int meh() {
      return suchAs.length();
    }

    public Moron suchAs(String suchAs) {
      this.suchAs = suchAs;

      return this;
    }
  }

  @Test
  public void testSerializePrimitives() {
    try {
      assertEquals(boolean.class.toGenericString(), "false", Jsonifier.serialize(false));
      assertEquals(byte.class.toGenericString(), "-128", Jsonifier.serialize((byte) 128));
      assertEquals(short.class.toGenericString(), "128", Jsonifier.serialize((short) 128));
      assertEquals(int.class.toGenericString(), "1", Jsonifier.serialize(1));
      assertEquals(long.class.toGenericString(), "123456789", Jsonifier.serialize(123456789L));
      assertEquals(float.class.toGenericString(), "1.2345679", Jsonifier.serialize(1.23456789f));
      assertEquals(double.class.toGenericString(), "0.123456789", Jsonifier.serialize(.123456789d));
      assertEquals(char.class.toGenericString(), "\"諸\"", Jsonifier.serialize("諸行無常".charAt(0)));
    } catch (Throwable e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
      fail(e.getLocalizedMessage());
    }
  }

  @Test
  public void testSerializeWrappers() {
    try {
      assertEquals(Boolean.class.toGenericString(), "true", Jsonifier.serialize(Boolean.valueOf(true)));
      assertEquals(Byte.class.toGenericString(), "-128", Jsonifier.serialize(Byte.valueOf((byte) 128)));
      assertEquals(Short.class.toGenericString(), "128", Jsonifier.serialize(Short.valueOf((short) 128)));
      assertEquals(Integer.class.toGenericString(), "1", Jsonifier.serialize(Integer.valueOf(1)));
      assertEquals(Long.class.toGenericString(), "123456789", Jsonifier.serialize(Long.valueOf(123456789L)));
      assertEquals(Float.class.toGenericString(), "1.2345679", Jsonifier.serialize(Float.valueOf(1.23456789f)));
      assertEquals(Double.class.toGenericString(), "0.123456789", Jsonifier.serialize(Double.valueOf(.123456789d)));
      assertEquals(Character.class.toGenericString(), "\"諸\"", Jsonifier.serialize(new Character("諸行無常".charAt(0))));
    } catch (Throwable t) {
      t.printStackTrace();
      fail(t.getLocalizedMessage());
    }
  }

  @Test
  public void testSerialize() {
    try {
      assertNull(Jsonifier.serialize(null));
      assertEquals("String", "\"十万億土\"", Jsonifier.serialize("十万億土"));

      assertEquals("Array", "[ \"南\", \"無\", \"阿\", \"弥\", \"陀\", \"仏\" ]", Jsonifier.serialize("南無阿弥陀仏".split("")));

      assertEquals("List", "[ \"南\", \"無\", \"阿\", \"弥\", \"陀\", \"仏\" ]", Jsonifier.serialize(Arrays.asList("南無阿弥陀仏".split(""))));
      Map<String, String> orokuji = Arrays.stream("南無阿弥陀仏".split("")).collect(Collectors.toMap(k -> k, v -> "南無阿弥陀仏".replaceAll("^.*" + v, ""), (v1, v2) -> v1));
      List<Entry<String, String>> orokuji_x = orokuji.entrySet().stream().sorted(new Comparator<Entry<String, String>>() {
        @Override public int compare(Entry<String, String> o1, Entry<String, String> o2) {return o1.getKey().compareTo(o2.getKey());}
      }).collect(Collectors.toList());
      assertEquals("Map", "[{\"仏\":\"\"},{\"南\":\"無阿弥陀仏\"},{\"弥\":\"陀仏\"},{\"無\":\"阿弥陀仏\"},{\"阿\":\"弥陀仏\"},{\"陀\":\"仏\"}]", Jsonifier.serialize(orokuji_x).replaceAll("[\\s\\r\\n]", ""));

      assertEquals("Empty", "{ }", Jsonifier.serialize(new Nothing()));
      assertEquals("Object", "{\r\n  \"serialNo\" : 1,\r\n  \"codeName\" : \"Lorem\"\r\n}", Jsonifier.serialize(new TheEntity(1L, "Lorem")));
      assertEquals("Extended", "{\r\n  \"serialNo\" : 2,\r\n  \"codeName\" : \"ipsum\",\r\n  \"created\" : \"1996-01-23\"\r\n}", Jsonifier.serialize(new TheEntityExtended(2L, "ipsum", LocalDate.of(1996, 1, 23))));
      assertEquals("Serializable", "{\r\n  \"serialNo\" : 3,\r\n  \"codeName\" : \"doler\",\r\n  \"created\" : \"1996-01-23\"\r\n}", Jsonifier.serialize(new TheEntitySerializable(3L, "doler", LocalDate.of(1996, 1, 23))));
    } catch (Throwable t) {
      t.printStackTrace();
      fail(t.getLocalizedMessage());
    }
  }

  @Test
  public void testSerializeLazy() {
    try {
      assertEquals(boolean.class.toGenericString(), "false", Jsonifier.serializeLazy(false));
      assertEquals(byte.class.toGenericString(), "-128", Jsonifier.serializeLazy((byte) 128));
      assertEquals(short.class.toGenericString(), "128", Jsonifier.serializeLazy((short) 128));
      assertEquals(int.class.toGenericString(), "1", Jsonifier.serializeLazy(1));
      assertEquals(long.class.toGenericString(), "123456789", Jsonifier.serializeLazy(123456789L));
      assertEquals(float.class.toGenericString(), "1.2345679", Jsonifier.serializeLazy(1.23456789f));
      assertEquals(double.class.toGenericString(), "0.123456789", Jsonifier.serializeLazy(.123456789d));
      assertEquals(char.class.toGenericString(), "\"諸\"", Jsonifier.serializeLazy("諸行無常".charAt(0)));

      assertEquals(Boolean.class.toGenericString(), "true", Jsonifier.serializeLazy(Boolean.valueOf(true)));
      assertEquals(Byte.class.toGenericString(), "-128", Jsonifier.serializeLazy(Byte.valueOf((byte) 128)));
      assertEquals(Short.class.toGenericString(), "128", Jsonifier.serializeLazy(Short.valueOf((short) 128)));
      assertEquals(Integer.class.toGenericString(), "1", Jsonifier.serializeLazy(Integer.valueOf(1)));
      assertEquals(Long.class.toGenericString(), "123456789", Jsonifier.serializeLazy(Long.valueOf(123456789L)));
      assertEquals(Float.class.toGenericString(), "1.2345679", Jsonifier.serializeLazy(Float.valueOf(1.23456789f)));
      assertEquals(Double.class.toGenericString(), "0.123456789", Jsonifier.serializeLazy(Double.valueOf(.123456789d)));
      assertEquals(Character.class.toGenericString(), "\"諸\"", Jsonifier.serializeLazy(new Character("諸行無常".charAt(0))));

      assertNull(Jsonifier.serializeLazy(null));
      assertEquals("String", "\"十万億土\"", Jsonifier.serializeLazy("十万億土"));

      assertEquals("Array", "[ \"南\", \"無\", \"阿\", \"弥\", \"陀\", \"仏\" ]", Jsonifier.serializeLazy("南無阿弥陀仏".split("")));

      assertEquals("List", "[ \"南\", \"無\", \"阿\", \"弥\", \"陀\", \"仏\" ]", Jsonifier.serializeLazy(Arrays.asList("南無阿弥陀仏".split(""))));
      Map<String, String> orokuji = Arrays.stream("南無阿弥陀仏".split("")).collect(Collectors.toMap(k -> k, v -> "南無阿弥陀仏".replaceAll("^.*" + v, ""), (v1, v2) -> v1));
      List<Entry<String, String>> orokuji_x = orokuji.entrySet().stream().sorted(new Comparator<Entry<String, String>>() {
        @Override public int compare(Entry<String, String> o1, Entry<String, String> o2) {return o1.getKey().compareTo(o2.getKey());}
      }).collect(Collectors.toList());
      assertEquals("Map", "[{\"仏\":\"\"},{\"南\":\"無阿弥陀仏\"},{\"弥\":\"陀仏\"},{\"無\":\"阿弥陀仏\"},{\"阿\":\"弥陀仏\"},{\"陀\":\"仏\"}]", Jsonifier.serializeLazy(orokuji_x).replaceAll("[\\s\\r\\n]", ""));

      assertEquals("Empty", "{ }", Jsonifier.serializeLazy(new Nothing()));
      assertEquals("Object", "{\r\n  \"serialNo\" : 1,\r\n  \"codeName\" : \"Lorem\"\r\n}", Jsonifier.serializeLazy(new TheEntity(1L, "Lorem")));
      assertEquals("Extended", "{\r\n  \"serialNo\" : 2,\r\n  \"codeName\" : \"ipsum\",\r\n  \"created\" : \"1996-01-23\"\r\n}", Jsonifier.serializeLazy(new TheEntityExtended(2L, "ipsum", LocalDate.of(1996, 1, 23))));
      assertEquals("Serializable", "{\r\n  \"serialNo\" : 3,\r\n  \"codeName\" : \"doler\",\r\n  \"created\" : \"1996-01-23\"\r\n}", Jsonifier.serializeLazy(new TheEntitySerializable(3L, "doler", LocalDate.of(1996, 1, 23))));

      assertNull(Jsonifier.serializeLazy(new Moron()));
      assertEquals("{\"suchAs\":\"silly\",\"meh\":5}", Jsonifier.serializeLazy(new Moron().suchAs("silly")).replaceAll("[\\s\\r\\n]", ""));
    } catch (Throwable t) {
      t.printStackTrace();
      fail(t.getLocalizedMessage());
    }
  }

  @Test
  public void testDeserializeWrappers() {
    try {
      assertEquals(Boolean.class.toGenericString(), Boolean.TRUE, Jsonifier.deserialize("true", Boolean.class));
      assertEquals(Byte.class.toGenericString(), Byte.valueOf("127"), Jsonifier.deserialize("127", Byte.class));
      assertEquals(Short.class.toGenericString(), Short.valueOf((short) 128), Jsonifier.deserialize("128", Short.class));
      assertEquals(Integer.class.toGenericString(), Integer.valueOf(1), Jsonifier.deserialize("1", Integer.class));
      assertEquals(Long.class.toGenericString(), Long.valueOf(123456789L), Jsonifier.deserialize("123456789", Long.class));
      assertEquals(Float.class.toGenericString(), Float.valueOf(1.2345679f), Jsonifier.deserialize("1.23456789", Float.class));
      assertEquals(Double.class.toGenericString(), Double.valueOf(.123456789), Jsonifier.deserialize("0.123456789", Double.class));
      assertEquals(Character.class.toGenericString(), new Character('諸'), Jsonifier.deserialize("\"諸\"", Character.class));

    } catch (Throwable t) {
      t.printStackTrace();
      fail(t.getLocalizedMessage());
    }
  }

  @Test
  public void testDeserialize() {
    try {
      assertNull(Jsonifier.deserialize(null, String.class));
      assertEquals("String", "十万億土", Jsonifier.deserialize("\"十万億土\"", String.class));

      assertArrayEquals("Array", "南無阿弥陀仏".split(""), Jsonifier.deserialize("[ \"南\", \"無\", \"阿\", \"弥\", \"陀\", \"仏\" ]", String[].class));
      assertEquals("List", Arrays.asList("南無阿弥陀仏".split("")), Jsonifier.deserialize("[ \"南\", \"無\", \"阿\", \"弥\", \"陀\", \"仏\" ]", List.class));
      assertThat(Arrays.stream("南無阿弥陀仏".split("")).collect(Collectors.toMap(k -> k, v -> "南無阿弥陀仏".replaceAll("^.*" + v, ""), (v1, v2) -> v1)), is(Jsonifier.deserialize("{\"仏\":\"\",\"南\":\"無阿弥陀仏\",\"弥\":\"陀仏\",\"無\":\"阿弥陀仏\",\"阿\":\"弥陀仏\",\"陀\":\"仏\"}", Map.class)));

      assertEquals("Empty", new Nothing(), Jsonifier.deserialize("{}", Nothing.class));
      assertEquals("Object", new TheEntity(1L, "Lorem"), Jsonifier.deserialize("{\"serialNo\": 1, \"codeName\": \"Lorem\"}", TheEntity.class));
      assertEquals("Extended", new TheEntityExtended(2L, "ipsum", LocalDate.of(1996, 1, 23)), Jsonifier.deserialize("{\"serialNo\": 2, \"codeName\": \"ipsum\", \"created\": \"1996-01-23\"}", TheEntityExtended.class));
      assertEquals("Serializable", new TheEntitySerializable(3L, "doler", LocalDate.of(1996, 1, 23)), Jsonifier.deserialize("{\"serialNo\": 3, \"codeName\": \"doler\", \"created\": \"1996-01-23\"}", TheEntitySerializable.class));
      assertEquals(new Moron().suchAs("silly"), Jsonifier.deserialize("{\"suchAs\":\"silly\"}", Moron.class));
    } catch (Throwable t) {
      t.printStackTrace();
      fail(t.getLocalizedMessage());
    }
  }

  @Test
  public void testDeserializeLazy() {
    assertEquals(Boolean.class.toGenericString(), Boolean.TRUE, Jsonifier.deserializeLazy("true", Boolean.class));
    assertEquals(Byte.class.toGenericString(), Byte.valueOf("127"), Jsonifier.deserializeLazy("127", Byte.class));
    assertEquals(Short.class.toGenericString(), Short.valueOf((short) 128), Jsonifier.deserializeLazy("128", Short.class));
    assertEquals(Integer.class.toGenericString(), Integer.valueOf(1), Jsonifier.deserializeLazy("1", Integer.class));
    assertEquals(Long.class.toGenericString(), Long.valueOf(123456789L), Jsonifier.deserializeLazy("123456789", Long.class));
    assertEquals(Float.class.toGenericString(), Float.valueOf(1.2345679f), Jsonifier.deserializeLazy("1.23456789", Float.class));
    assertEquals(Double.class.toGenericString(), Double.valueOf(.123456789), Jsonifier.deserializeLazy("0.123456789", Double.class));
    assertEquals(Character.class.toGenericString(), new Character('諸'), Jsonifier.deserializeLazy("\"諸\"", Character.class));
    assertNull(Jsonifier.deserializeLazy(null, String.class));
    assertEquals("String", "十万億土", Jsonifier.deserializeLazy("\"十万億土\"", String.class));

    assertArrayEquals("Array", "南無阿弥陀仏".split(""), Jsonifier.deserializeLazy("[ \"南\", \"無\", \"阿\", \"弥\", \"陀\", \"仏\" ]", String[].class));
    assertEquals("List", Arrays.asList("南無阿弥陀仏".split("")), Jsonifier.deserializeLazy("[ \"南\", \"無\", \"阿\", \"弥\", \"陀\", \"仏\" ]", List.class));
    assertThat(Arrays.stream("南無阿弥陀仏".split("")).collect(Collectors.toMap(k -> k, v -> "南無阿弥陀仏".replaceAll("^.*" + v, ""), (v1, v2) -> v1)), is(Jsonifier.deserializeLazy("{\"仏\":\"\",\"南\":\"無阿弥陀仏\",\"弥\":\"陀仏\",\"無\":\"阿弥陀仏\",\"阿\":\"弥陀仏\",\"陀\":\"仏\"}", Map.class)));

    assertEquals("Empty", new Nothing(), Jsonifier.deserializeLazy("{}", Nothing.class));
    assertEquals("Object", new TheEntity(1L, "Lorem"), Jsonifier.deserializeLazy("{\"serialNo\": 1, \"codeName\": \"Lorem\"}", TheEntity.class));
    assertEquals("Extended", new TheEntityExtended(2L, "ipsum", LocalDate.of(1996, 1, 23)), Jsonifier.deserializeLazy("{\"serialNo\": 2, \"codeName\": \"ipsum\", \"created\": \"1996-01-23\"}", TheEntityExtended.class));
    assertEquals("Serializable", new TheEntitySerializable(3L, "doler", LocalDate.of(1996, 1, 23)), Jsonifier.deserializeLazy("{\"serialNo\": 3, \"codeName\": \"doler\", \"created\": \"1996-01-23\"}", TheEntitySerializable.class));
    assertEquals(new Moron().suchAs("silly"), Jsonifier.deserializeLazy("{\"suchAs\":\"silly\"}", Moron.class));
  }
}
