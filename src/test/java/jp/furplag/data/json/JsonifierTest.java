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

package jp.furplag.data.json;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;

import jp.furplag.data.json.entity.Instance;
import jp.furplag.data.json.entity.Nothing;
import jp.furplag.reflect.SavageReflection;

public class JsonifierTest {

  @Before
  public void before() {
    System.setProperty("line.separator", "\n");
  }

  @Test
  public void test() throws JsonProcessingException, IOException {
    assertThat(Jsonifier.deserialize("0.0", LocalDateTime.class), is((LocalDateTime) null));
    assertThat(Jsonifier.deserialize("'12345-1231'", LocalDateTime.class), is((LocalDateTime) null));
    assertThat(Jsonifier.deserialize("'0000-00-00T00:00:00Z'", LocalDateTime.class), is((LocalDateTime) null));
  }

  @Test
  public void Jsonifier() throws Throwable {
    assertNotNull(new Jsonifier());
  }

  @Test
  public void serializeNull() throws Throwable {
    assertNull("null", Jsonifier.serialize(null));
  }

  @Test
  public void serializePrimitives() throws Throwable {
    assertThat("boolean", Jsonifier.serialize(true), is("true"));
    assertThat("boolean", Jsonifier.serialize(!true), is("false"));

    assertEquals("char", "\"諸\"", Jsonifier.serialize("諸行無常".charAt(0)));
  }

  @Test
  public void serializeIntegers() throws Throwable {
    assertThat("byte", Jsonifier.serialize(Byte.MIN_VALUE), is("-128"));
    assertThat("byte", Jsonifier.serialize((byte) 0), is("0"));
    assertThat("byte", Jsonifier.serialize(Byte.MAX_VALUE), is("127"));

    assertThat("short", Jsonifier.serialize(Short.MIN_VALUE), is("-32768"));
    assertThat("short", Jsonifier.serialize((short) 0), is("0"));
    assertThat("short", Jsonifier.serialize(Short.MAX_VALUE), is("32767"));

    assertThat("int", Jsonifier.serialize(Integer.MIN_VALUE), is("-2147483648"));
    assertThat("int", Jsonifier.serialize(0), is("0"));
    assertThat("int", Jsonifier.serialize(Integer.MAX_VALUE), is("2147483647"));

    assertThat("long", Jsonifier.serialize(Long.MIN_VALUE), is("-9223372036854775808"));
    assertThat("long", Jsonifier.serialize(0L), is("0"));
    assertThat("long", Jsonifier.serialize(Long.MAX_VALUE), is("9223372036854775807"));

    assertThat("Byte", Jsonifier.serialize(Byte.valueOf("123")), is("123"));
    assertThat("Short", Jsonifier.serialize(Short.valueOf("1234")), is("1234"));
    assertThat("Integer", Jsonifier.serialize(Integer.valueOf("123456")), is("123456"));
    assertThat("Long", Jsonifier.serialize(Long.valueOf("12345678901")), is("12345678901"));
    assertThat("BigInteger", Jsonifier.serialize(BigInteger.valueOf(Long.MAX_VALUE)), is("9223372036854775807"));
  }

  @Test
  public void serializeFractions() throws Throwable {
    assertThat("Float", "-0.123456", is(Jsonifier.serialize(-.123456f)));
    assertThat("Float", "0.0", is(Jsonifier.serialize(0f)));
    assertThat("Float", "0.0", is(Jsonifier.serialize(0.0f)));
    assertThat("Float", "0.0", is(Jsonifier.serialize(.0f)));
    assertThat("Float", "1.0E-6", is(Jsonifier.serialize(1E-6f)));
    assertThat("Float", "0.123456", is(Jsonifier.serialize(.123456f)));

    assertThat("Double", "-0.1234567890123", is(Jsonifier.serialize(-.1234567890123d)));
    assertThat("Double", "0.0", is(Jsonifier.serialize(0d)));
    assertThat("Double", "0.0", is(Jsonifier.serialize(0.0d)));
    assertThat("Double", "0.0", is(Jsonifier.serialize(.0d)));
    assertThat("Double", "1.0E-12", is(Jsonifier.serialize(1E-12d)));
    assertThat("Double", "0.1234567890123", is(Jsonifier.serialize(.1234567890123d)));

    assertThat("BigDecimal", "1.7976931348623157E308", is(Jsonifier.serialize(Double.valueOf(Double.MAX_VALUE))));
  }

  @Test
  public void serializeWrappers() throws Throwable {
    assertThat("Boolean", Jsonifier.serialize(Boolean.TRUE), is("true"));
    assertThat("Boolean", Jsonifier.serialize(Boolean.FALSE), is("false"));

    assertEquals("Character", "\"諸\"", Jsonifier.serialize(Character.valueOf("諸行無常".charAt(0))));
  }

  @Test
  public void serializeStrings() throws Throwable {
    assertThat("empty", Jsonifier.serialize(""), is("\"\""));
    assertThat("String", Jsonifier.serialize("南無阿弥陀仏"), is("\"南無阿弥陀仏\""));
    assertThat("Text", Jsonifier.serialize(String.join("\n", "南無阿弥陀仏".split(""))), is("\"南\\n無\\n阿\\n弥\\n陀\\n仏\""));
  }

  static enum OneTwo {
    One, Two;
  };

  @Test
  public void serialize() throws Throwable {
    assertThat("Enum", Jsonifier.serialize(OneTwo.One), is("\"One\""));

    assertThat("Array:empty", Jsonifier.serialize(new Object[] {}), is("[]"));
    assertThat("Array:empty", Jsonifier.serialize(new Object[][] {}), is("[]"));
    assertThat("Array", Jsonifier.serialize(new Object[] {1, 2.3f, .3456d, "789", false}), is("[1,2.3,0.3456,\"789\",false]"));
    assertThat("Array", Jsonifier.serialize(new Object[][] {{1, 2, 3, 4}, {.5, .6, .7, .8}, {"9", "yep", "Nope"}, {false, true}, OneTwo.values()}), is("[[1,2,3,4],[0.5,0.6,0.7,0.8],[\"9\",\"yep\",\"Nope\"],[false,true],[\"One\",\"Two\"]]"));
    assertThat("Set", Jsonifier.serialize(Arrays.stream("南無阿弥陀仏".split("")).collect(Collectors.toCollection(LinkedHashSet::new))), is("[\"南\",\"無\",\"阿\",\"弥\",\"陀\",\"仏\"]"));
    assertThat("List", Jsonifier.serialize(Arrays.asList("南無阿弥陀仏".split(""))), is("[\"南\",\"無\",\"阿\",\"弥\",\"陀\",\"仏\"]"));
    assertThat("Map", Jsonifier.serialize(Arrays.stream("南無阿弥陀仏".split("")).map(s -> "南無阿弥陀仏".indexOf(s)).collect(Collectors.toMap(k -> Integer.valueOf(k), v -> "南無阿弥陀仏".substring(v, v + 1), (v1, v2) -> v2))), is("{\"0\":\"南\",\"1\":\"無\",\"2\":\"阿\",\"3\":\"弥\",\"4\":\"陀\",\"5\":\"仏\"}"));
    assertThat("Map", Jsonifier.serialize(Arrays.stream("南無阿弥陀仏".split("")).map(s -> "南無阿弥陀仏".indexOf(s)).collect(Collectors.toMap(k -> Integer.valueOf(k), v -> "南無阿弥陀仏".substring(v, v + 1), (v1, v2) -> v2)).entrySet().stream().collect(Collectors.toMap(k -> k.getKey(), v -> v, (v1, v2) -> v2))),
        is("{\"0\":{\"0\":\"南\"},\"1\":{\"1\":\"無\"},\"2\":{\"2\":\"阿\"},\"3\":{\"3\":\"弥\"},\"4\":{\"4\":\"陀\"},\"5\":{\"5\":\"仏\"}}"));

    Instance that = new Instance();
    that.versionNo = 1;
    that.deleted = false;
    that.created = LocalDateTime.of(2017, 1, 1, 1, 23, 45).plus(678, ChronoUnit.MILLIS);
    that.modified = LocalDateTime.of(2017, 1, 23, 1, 23, 45).plus(678, ChronoUnit.MILLIS);
    assertThat("that", Jsonifier.serialize(that), is("{\"versionNo\":1,\"deleted\":false,\"created\":\"2017-01-01T01:23:45.678\",\"modified\":\"2017-01-23T01:23:45.678\"}"));
  }

  @Test
  public void deserialize() throws Throwable {
    assertNull("null", Jsonifier.deserialize("[1, 2]", (Class<?>) null));
    assertNull("null", Jsonifier.deserialize("[1, 2]", (JavaType) null));
    assertNull("null", Jsonifier.deserialize("[1, 2]", (TypeReference<?>) null));
    assertNull("null", Jsonifier.deserialize(null, Instance.class));
    assertNull("null", Jsonifier.deserialize(null, TypeFactory.defaultInstance().constructType(Instance.class)));
    assertNull("null", Jsonifier.deserialize(null, new TypeReference<Instance>() {}));
    assertNull("null", Jsonifier.deserialize("", Instance.class));
    assertNull("null", Jsonifier.deserialize("", TypeFactory.defaultInstance().constructType(Instance.class)));
    assertNull("null", Jsonifier.deserialize("", new TypeReference<Instance>() {}));
    assertThat("nonField", Jsonifier.deserialize("{}", Nothing.class), is(new Nothing()));
    assertThat("unspecified", Jsonifier.deserialize("{name:'john'}", Nothing.class), is(new Nothing()));

    Instance that = new Instance();
    that.versionNo = 1;
    that.deleted = false;
    that.created = LocalDateTime.of(2017, 1, 1, 1, 23, 45).plus(678, ChronoUnit.MILLIS);
    that.modified = LocalDateTime.of(2017, 1, 23, 1, 23, 45).plus(678, ChronoUnit.MILLIS);
    assertThat("that", Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23T01:23:45.678'}", Instance.class), is(that));
    that.modified = LocalDateTime.of(2017, 1, 23, 0, 0, 0);
    assertThat("that", Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23'}", Instance.class), is(that));
    assertThat("that", Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017/01/23'}", Instance.class), is(that));
    assertThat("that", Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '20170123'}", Instance.class), is(that));
    assertThat("that", Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017.01.23'}", Instance.class), is(that));
    that.modified = LocalDateTime.of(2017, 1, 1, 0, 0, 0);
    assertThat("that", Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017.1.1'}", Instance.class), is(that));
    that.modified = LocalDateTime.of(2017, 1, 23, 0, 0, 0);
    assertThat("that", Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017年1月23日'}", Instance.class), is(that));
    assertThat("that", Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017年1月23日'}", Instance.class), is(that));
    that.modified = null;
    assertThat("that", Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '元禄元年.1.1'}", Instance.class), is(that));
    that.modified = LocalDateTime.of(201 + (71 / 12), 71 % 12, 23, 0, 0);
    assertThat("that", Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017123'}", Instance.class), is(that));
    that.modified = LocalDateTime.of(2017, 1, 23, 0, 0, 0);
    assertThat("that:JavaType", Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23'}", TypeFactory.defaultInstance().constructType(Instance.class)), is(that));
    assertThat("that:JavaType", Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017/01/23'}", TypeFactory.defaultInstance().constructType(Instance.class)), is(that));
    assertThat("that:JavaType", Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '20170123'}", TypeFactory.defaultInstance().constructType(Instance.class)), is(that));
    assertThat("that:JavaType", Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017.01.23'}", TypeFactory.defaultInstance().constructType(Instance.class)), is(that));
    assertThat("that:JavaType", Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '20170123'}", TypeFactory.defaultInstance().constructType(Instance.class)), is(that));

    Map<String, Object> those = new HashMap<>();
    those.put("versionNo", 1);
    those.put("deleted", false);
    those.put("created", LocalDateTime.of(2017, 1, 1, 1, 23, 45).plus(678, ChronoUnit.MILLIS));
    those.put("modified", LocalDateTime.of(2017, 1, 23, 1, 23, 45).plus(678, ChronoUnit.MILLIS));
    assertThat("that:TypeReference", Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23T01:23:45.678'}", new TypeReference<Map<String, Object>>() {}).keySet().stream().sorted().collect(Collectors.joining()), is(those.keySet().stream().sorted().collect(Collectors.joining())));
    assertThat("that:TypeReference", Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23T01:23:45.678'}", new TypeReference<Map<String, Object>>() {}).values().stream().map(Objects::toString).sorted().collect(Collectors.joining()), is(those.values().stream().map(Objects::toString).sorted().collect(Collectors.joining())));
  }

  @SuppressWarnings("unused")
  @Test
  public void lazy() throws Throwable {
    assertNull("null", Jsonifier.deserializeLazy("[1, 2]", (Class<?>) null));
    assertNull("null", Jsonifier.deserializeLazy("[1, 2]", (JavaType) null));
    assertNull("null", Jsonifier.deserializeLazy("[1, 2]", (TypeReference<?>) null));
    assertNull("null", Jsonifier.deserializeLazy(null, Instance.class));
    assertNull("null", Jsonifier.deserializeLazy(null, TypeFactory.defaultInstance().constructType(Instance.class)));
    assertNull("null", Jsonifier.deserializeLazy(null, new TypeReference<Instance>() {}));
    assertNull("null", Jsonifier.deserializeLazy("", Instance.class));
    assertNull("null", Jsonifier.deserializeLazy("", TypeFactory.defaultInstance().constructType(Instance.class)));
    assertNull("null", Jsonifier.deserializeLazy("", new TypeReference<Instance>() {}));
    assertThat("nonField", Jsonifier.deserializeLazy("{}", Nothing.class), is(new Nothing()));
    assertThat("unspecified", Jsonifier.deserializeLazy("{name:'john'}", Nothing.class), is(new Nothing()));

    Instance that = new Instance();
    that.versionNo = 1;
    that.deleted = false;
    that.created = LocalDateTime.of(2017, 1, 1, 1, 23, 45).plus(678, ChronoUnit.MILLIS);
    that.modified = LocalDateTime.of(2017, 1, 23, 1, 23, 45).plus(678, ChronoUnit.MILLIS);
    assertThat("that", Jsonifier.deserializeLazy("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23T01:23:45.678'}", Instance.class), is(that));
    that.modified = LocalDateTime.of(2017, 1, 23, 0, 0, 0);
    assertThat("that", Jsonifier.deserializeLazy("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23'}", Instance.class), is(that));
    assertThat("that", Jsonifier.deserializeLazy("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017/01/23'}", Instance.class), is(that));
    assertThat("that", Jsonifier.deserializeLazy("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '20170123'}", Instance.class), is(that));
    assertThat("that", Jsonifier.deserializeLazy("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017.01.23'}", Instance.class), is(that));
    that.modified = LocalDateTime.of(2017, 1, 1, 0, 0, 0);
    assertThat("that", Jsonifier.deserializeLazy("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017.1.1'}", Instance.class), is(that));
    that.modified = LocalDateTime.of(2017, 1, 23, 0, 0, 0);
    assertThat("that", Jsonifier.deserializeLazy("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017年1月23日'}", Instance.class), is(that));
    assertThat("that", Jsonifier.deserializeLazy("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017年1月23日'}", Instance.class), is(that));
    that.modified = null;
    assertThat("that", Jsonifier.deserializeLazy("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '元禄元年.1.1'}", Instance.class), is(that));
    that.modified = LocalDateTime.of(201 + (71 / 12), 71 % 12, 23, 0, 0);
    assertThat("that", Jsonifier.deserializeLazy("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017123'}", Instance.class), is(that));
    that.modified = LocalDateTime.of(2017, 1, 23, 0, 0, 0);
    assertThat("that:JavaType", Jsonifier.deserializeLazy("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23'}", TypeFactory.defaultInstance().constructType(Instance.class)), is(that));
    assertThat("that:JavaType", Jsonifier.deserializeLazy("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017/01/23'}", TypeFactory.defaultInstance().constructType(Instance.class)), is(that));
    assertThat("that:JavaType", Jsonifier.deserializeLazy("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '20170123'}", TypeFactory.defaultInstance().constructType(Instance.class)), is(that));
    assertThat("that:JavaType", Jsonifier.deserializeLazy("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017.01.23'}", TypeFactory.defaultInstance().constructType(Instance.class)), is(that));
    assertThat("that:JavaType", Jsonifier.deserializeLazy("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '20170123'}", TypeFactory.defaultInstance().constructType(Instance.class)), is(that));

    Map<String, Object> those = new HashMap<>();
    those.put("versionNo", 1);
    those.put("deleted", false);
    those.put("created", LocalDateTime.of(2017, 1, 1, 1, 23, 45).plus(678, ChronoUnit.MILLIS));
    those.put("modified", LocalDateTime.of(2017, 1, 23, 1, 23, 45).plus(678, ChronoUnit.MILLIS));
    assertThat("that:TypeReference", Jsonifier.deserializeLazy("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23T01:23:45.678'}", new TypeReference<Map<String, Object>>() {}).keySet().stream().sorted().collect(Collectors.joining()), is(those.keySet().stream().sorted().collect(Collectors.joining())));
    assertThat("that:TypeReference", Jsonifier.deserializeLazy("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23T01:23:45.678'}", new TypeReference<Map<String, Object>>() {}).values().stream().map(Objects::toString).sorted().collect(Collectors.joining()), is(those.values().stream().map(Objects::toString).sorted().collect(Collectors.joining())));

    final class Another extends Instance {
      @SuppressWarnings("unused")
      public boolean created;
    }
    try {
      Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23T01:23:45.678'}", Another.class);
      fail("raise JsonMappingException .");
    } catch (JsonMappingException e) {
      assertThat(e instanceof JsonMappingException, is(true));
    }
    try {
      Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23T01:23:45.678'}", TypeFactory.defaultInstance().constructType(Another.class));
      fail("raise JsonMappingException .");
    } catch (JsonMappingException e) {
      assertThat(e instanceof JsonMappingException, is(true));
    }
    try {
      Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23T01:23:45.678'}", new TypeReference<Set<Integer>>() {});
      fail("raise JsonMappingException .");
    } catch (JsonMappingException e) {
      assertThat(e instanceof JsonMappingException, is(true));
    }
    assertThat("thatToAnother", Jsonifier.deserializeLazy("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23T01:23:45.678'}", Another.class), is((Another) null));
    assertThat("thatToAnother", Jsonifier.deserializeLazy("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23T01:23:45.678'}", TypeFactory.defaultInstance().constructType(Another.class)), is((Another) null));
    assertThat("thatToAnother", Jsonifier.deserializeLazy("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23T01:23:45.678'}", new TypeReference<Set<Integer>>() {}), is((Another) null));

    ObjectMapper mapper = ((ObjectMapper) SavageReflection.get(Jsonifier.class, "mapper"));
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, true);
    SavageReflection.set(Jsonifier.class, "mapper", mapper);
    try {
      Jsonifier.serialize(new Nothing());
      fail("raise JsonMappingException .");
    } catch (JsonMappingException e) {
      assertThat(e instanceof JsonMappingException, is(true));
    }
    assertThat("lazySerialization", Jsonifier.serializeLazy(new Nothing()), is((Nothing) null));
    assertThat("lazySerialization", Jsonifier.serializeLazy(new Instance()), is(Jsonifier.serialize(new Instance())));

    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    SavageReflection.set(Jsonifier.class, "mapper", mapper);
  }
}
