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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.IOException;
import java.lang.reflect.Constructor;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import jp.furplag.data.json.entity.Instance;
import jp.furplag.data.json.entity.Nothing;
import jp.furplag.data.json.entity.Unseen;
import jp.furplag.sandbox.reflect.SavageReflection;
import jp.furplag.sandbox.trebuchet.Trebuchet;

public class JsonifierTest {

  private String lineSeparator = System.getProperty("line.separator");

  @BeforeEach
  public void before() {
    System.setProperty("line.separator", "\n");
  }

  @AfterEach
  public void after() {
    System.setProperty("line.separator", lineSeparator);
  }

  @Test
  public void test() throws JsonProcessingException, IOException {
    assertNull(Jsonifier.deserializeStrictly("0.0", LocalDateTime.class));
    assertNull(Jsonifier.deserializeStrictly("'12345-1231'", LocalDateTime.class));
  }

  @Test
  public void Jsonifier() throws Throwable {
    assertNotNull(new Jsonifier() {});

    Constructor<?> c = Jsonifier.Shell.class.getDeclaredConstructor();
    c.setAccessible(true);
    assertTrue(c.newInstance() instanceof Jsonifier.Shell);
  }

  @Test
  public void serializeNull() throws Throwable {
    assertEquals((String) null, Jsonifier.serializeStrictly(null));
  }

  @Test
  public void serializePrimitives() throws Throwable {
    assertEquals("true", Jsonifier.serializeStrictly(true));
    assertEquals("false", Jsonifier.serializeStrictly(!true));

    assertEquals("\"諸\"", Jsonifier.serializeStrictly("諸行無常".charAt(0)));
  }

  @Test
  public void serializeIntegers() throws Throwable {
    assertEquals("-128", Jsonifier.serializeStrictly(Byte.MIN_VALUE));
    assertEquals("0", Jsonifier.serializeStrictly((byte) 0));
    assertEquals("127", Jsonifier.serializeStrictly(Byte.MAX_VALUE));

    assertEquals("-32768", Jsonifier.serializeStrictly(Short.MIN_VALUE));
    assertEquals("0", Jsonifier.serializeStrictly((short) 0));
    assertEquals("32767", Jsonifier.serializeStrictly(Short.MAX_VALUE));

    assertEquals("-2147483648", Jsonifier.serializeStrictly(Integer.MIN_VALUE));
    assertEquals("0", Jsonifier.serializeStrictly(0));
    assertEquals("2147483647", Jsonifier.serializeStrictly(Integer.MAX_VALUE));

    assertEquals("-9223372036854775808", Jsonifier.serializeStrictly(Long.MIN_VALUE));
    assertEquals("0", Jsonifier.serializeStrictly(0L));
    assertEquals("9223372036854775807", Jsonifier.serializeStrictly(Long.MAX_VALUE));

    assertEquals("123", Jsonifier.serializeStrictly(Byte.valueOf("123")));
    assertEquals("1234", Jsonifier.serializeStrictly(Short.valueOf("1234")));
    assertEquals("123456", Jsonifier.serializeStrictly(Integer.valueOf("123456")));
    assertEquals("12345678901", Jsonifier.serializeStrictly(Long.valueOf("12345678901")));
    assertEquals("9223372036854775807", Jsonifier.serializeStrictly(BigInteger.valueOf(Long.MAX_VALUE)));
  }

  @Test
  public void serializeFractions() throws Throwable {
    assertEquals(Jsonifier.serializeStrictly(-.123456f), "-0.123456");
    assertEquals(Jsonifier.serializeStrictly(0f), "0.0");
    assertEquals(Jsonifier.serializeStrictly(0.0f), "0.0");
    assertEquals(Jsonifier.serializeStrictly(.0f), "0.0");
    assertEquals(Jsonifier.serializeStrictly(1E-6f), "1.0E-6");
    assertEquals(Jsonifier.serializeStrictly(.123456f), "0.123456");

    assertEquals(Jsonifier.serializeStrictly(-.1234567890123d), "-0.1234567890123");
    assertEquals(Jsonifier.serializeStrictly(0d), "0.0");
    assertEquals(Jsonifier.serializeStrictly(0.0d), "0.0");
    assertEquals(Jsonifier.serializeStrictly(.0d), "0.0");
    assertEquals(Jsonifier.serializeStrictly(1E-12d), "1.0E-12");
    assertEquals(Jsonifier.serializeStrictly(.1234567890123d), "0.1234567890123");

    assertEquals(Jsonifier.serializeStrictly(Double.valueOf(Double.MAX_VALUE)), "1.7976931348623157E308");
  }

  @Test
  public void serializeWrappers() throws Throwable {
    assertEquals("true", Jsonifier.serializeStrictly(Boolean.TRUE));
    assertEquals("false", Jsonifier.serializeStrictly(Boolean.FALSE));

    assertEquals("\"諸\"", Jsonifier.serializeStrictly(Character.valueOf("諸行無常".charAt(0))));
  }

  @Test
  public void serializeStrings() throws Throwable {
    assertEquals("\"\"", Jsonifier.serializeStrictly(""));
    assertEquals("\"南無阿弥陀仏\"", Jsonifier.serializeStrictly("南無阿弥陀仏"));
    assertEquals("[\"南\",\"無\",\"阿\",\"弥\",\"陀\",\"仏\"]", Jsonifier.serializeStrictly("南無阿弥陀仏".split("")));
  }

  static enum OneTwo {
    One, Two;
  };

  @Test
  public void serialize() throws Throwable {
    assertEquals("\"One\"", Jsonifier.serializeStrictly(OneTwo.One));

    assertEquals("[]", Jsonifier.serializeStrictly(new Object[] {}));
    assertEquals("[]", Jsonifier.serializeStrictly(new Object[][] {}));
    assertEquals("[1,2.3,0.3456,\"789\",false]", Jsonifier.serializeStrictly(new Object[] {1, 2.3f, .3456d, "789", false}));
    assertEquals("[[1,2,3,4],[0.5,0.6,0.7,0.8],[\"9\",\"yep\",\"Nope\"],[false,true],[\"One\",\"Two\"]]", Jsonifier.serializeStrictly(new Object[][] {{1, 2, 3, 4}, {.5, .6, .7, .8}, {"9", "yep", "Nope"}, {false, true}, OneTwo.values()}));
    assertEquals("[\"南\",\"無\",\"阿\",\"弥\",\"陀\",\"仏\"]", Jsonifier.serializeStrictly(Arrays.stream("南無阿弥陀仏".split("")).collect(Collectors.toCollection(LinkedHashSet::new))));
    assertEquals("[\"南\",\"無\",\"阿\",\"弥\",\"陀\",\"仏\"]", Jsonifier.serializeStrictly(Arrays.asList("南無阿弥陀仏".split(""))));
    assertEquals("{\"0\":\"南\",\"1\":\"無\",\"2\":\"阿\",\"3\":\"弥\",\"4\":\"陀\",\"5\":\"仏\"}", Jsonifier.serializeStrictly(Arrays.stream("南無阿弥陀仏".split("")).map(s -> "南無阿弥陀仏".indexOf(s)).collect(Collectors.toMap(k -> Integer.valueOf(k), v -> "南無阿弥陀仏".substring(v, v + 1), (v1, v2) -> v2))));
    assertEquals("{\"0\":{\"0\":\"南\"},\"1\":{\"1\":\"無\"},\"2\":{\"2\":\"阿\"},\"3\":{\"3\":\"弥\"},\"4\":{\"4\":\"陀\"},\"5\":{\"5\":\"仏\"}}", Jsonifier.serializeStrictly(Arrays.stream("南無阿弥陀仏".split("")).map(s -> "南無阿弥陀仏".indexOf(s)).collect(Collectors.toMap(k -> Integer.valueOf(k), v -> "南無阿弥陀仏".substring(v, v + 1), (v1, v2) -> v2)).entrySet().stream().collect(Collectors.toMap(k -> k.getKey(), v -> v, (v1, v2) -> v2))));

    Instance that = new Instance();
    that.versionNo = 1;
    that.deleted = false;
    that.created = LocalDateTime.of(2017, 1, 1, 1, 23, 45).plus(678, ChronoUnit.MILLIS);
    that.modified = LocalDateTime.of(2017, 1, 23, 1, 23, 45).plus(678, ChronoUnit.MILLIS);
    assertEquals("{\"versionNo\":1,\"deleted\":false,\"created\":\"2017-01-01T01:23:45.678\",\"modified\":\"2017-01-23T01:23:45.678\"}", Jsonifier.serializeStrictly(that));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void deserialize() throws Throwable {
    assertEquals(new Nothing(), Jsonifier.deserializeStrictly("{}", Nothing.class));
    assertEquals(new Nothing(), Jsonifier.deserializeStrictly("{name:'john'}", Nothing.class));

    Instance that = new Instance();
    that.versionNo = 1;
    that.deleted = false;
    that.created = LocalDateTime.of(2017, 1, 1, 1, 23, 45).plus(678, ChronoUnit.MILLIS);
    that.modified = LocalDateTime.of(2017, 1, 23, 1, 23, 45).plus(678, ChronoUnit.MILLIS);
    assertEquals(that, Jsonifier.deserializeStrictly("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23T01:23:45.678'}", Instance.class));
    that.modified = LocalDateTime.of(2017, 1, 23, 0, 0, 0);
    assertEquals(that, Jsonifier.deserializeStrictly("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23'}", Instance.class));
    assertEquals(that, Jsonifier.deserializeStrictly("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017/01/23'}", Instance.class));
    assertEquals(that, Jsonifier.deserializeStrictly("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '20170123'}", Instance.class));
    assertEquals(that, Jsonifier.deserializeStrictly("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017.01.23'}", Instance.class));
    that.modified = LocalDateTime.of(2017, 1, 1, 0, 0, 0);
    assertEquals(that, Jsonifier.deserializeStrictly("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017.1.1'}", Instance.class));
    that.modified = LocalDateTime.of(2017, 1, 23, 0, 0, 0);
    assertEquals(that, Jsonifier.deserializeStrictly("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017年1月23日'}", Instance.class));
    assertEquals(that, Jsonifier.deserializeStrictly("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017年1月23日'}", Instance.class));
    that.modified = null;
    assertEquals(that, Jsonifier.deserializeStrictly("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '元禄元年.1.1'}", Instance.class));
    that.modified = LocalDateTime.of(201 + (71 / 12), 71 % 12, 23, 0, 0);
    assertEquals(that, Jsonifier.deserializeStrictly("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017123'}", Instance.class));
    that.modified = LocalDateTime.of(2017, 1, 23, 0, 0, 0);
    assertEquals(that, Jsonifier.deserializeStrictly("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23'}", TypeFactory.defaultInstance().constructType(Instance.class)));
    assertEquals(that, Jsonifier.deserializeStrictly("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017/01/23'}", TypeFactory.defaultInstance().constructType(Instance.class)));
    assertEquals(that, Jsonifier.deserializeStrictly("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '20170123'}", TypeFactory.defaultInstance().constructType(Instance.class)));
    assertEquals(that, Jsonifier.deserializeStrictly("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017.01.23'}", TypeFactory.defaultInstance().constructType(Instance.class)));
    assertEquals(that, Jsonifier.deserializeStrictly("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '20170123'}", TypeFactory.defaultInstance().constructType(Instance.class)));

    Map<String, Object> those = new HashMap<>();
    those.put("versionNo", 1);
    those.put("deleted", false);
    those.put("created", LocalDateTime.of(2017, 1, 1, 1, 23, 45).plus(678, ChronoUnit.MILLIS));
    those.put("modified", LocalDateTime.of(2017, 1, 23, 1, 23, 45).plus(678, ChronoUnit.MILLIS));
    assertEquals(those.keySet().stream().sorted().collect(Collectors.joining()), ((Map<String, Object>) (Jsonifier.deserializeStrictly("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23T01:23:45.678'}", new TypeReference<Map<String, Object>>() {}))).keySet().stream().sorted().collect(Collectors.joining()));
    assertEquals(those.values().stream().map(Objects::toString).sorted().collect(Collectors.joining()), ((Map<String, Object>) (Jsonifier.deserializeStrictly("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23T01:23:45.678'}", new TypeReference<Map<String, Object>>() {}))).values().stream().map(Objects::toString).sorted().collect(Collectors.joining()));
  }

  @SuppressWarnings({ "unused" })
  @Test
  public void lazy() throws Throwable {
    assertNull(Jsonifier.deserialize("[1, 2]", (Class<?>) null));
    assertNull(Jsonifier.deserialize("[1, 2]", (JavaType) null));
    assertNull(Jsonifier.deserialize("[1, 2]", (TypeReference<?>) null));
    assertNull(Jsonifier.deserialize(null, Instance.class));
    assertNull(Jsonifier.deserialize(null, TypeFactory.defaultInstance().constructType(Instance.class)));
    assertNull(Jsonifier.deserialize(null, new TypeReference<Instance>() {}));
    assertNull(Jsonifier.deserialize("", Instance.class));
    assertNull(Jsonifier.deserialize("", TypeFactory.defaultInstance().constructType(Instance.class)));
    assertNull(Jsonifier.deserialize("", new TypeReference<Instance>() {}));
    assertEquals(new Nothing(), Jsonifier.deserializeStrictly("{name:'john'}", Nothing.class));
    assertEquals(new Nothing(), Jsonifier.deserialize("{name:'john'}", Nothing.class));

    Instance that = new Instance();
    that.versionNo = 1;
    that.deleted = false;
    that.created = LocalDateTime.of(2017, 1, 1, 1, 23, 45).plus(678, ChronoUnit.MILLIS);
    that.modified = LocalDateTime.of(2017, 1, 23, 1, 23, 45).plus(678, ChronoUnit.MILLIS);
    assertEquals(that, Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23T01:23:45.678'}", Instance.class));
    that.modified = LocalDateTime.of(2017, 1, 23, 0, 0, 0);
    assertEquals(that, Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23'}", Instance.class));
    assertEquals(that, Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017/01/23'}", Instance.class));
    assertEquals(that, Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '20170123'}", Instance.class));
    assertEquals(that, Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017.01.23'}", Instance.class));
    that.modified = LocalDateTime.of(2017, 1, 1, 0, 0, 0);
    assertEquals(that, Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017.1.1'}", Instance.class));
    that.modified = LocalDateTime.of(2017, 1, 23, 0, 0, 0);
    assertEquals(that, Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017年1月23日'}", Instance.class));
    assertEquals(that, Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017年1月23日'}", Instance.class));
    that.modified = null;
    assertEquals(that, Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '元禄元年.1.1'}", Instance.class));
    that.modified = LocalDateTime.of(201 + (71 / 12), 71 % 12, 23, 0, 0);
    assertEquals(that, Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017123'}", Instance.class));
    that.modified = LocalDateTime.of(2017, 1, 23, 0, 0, 0);
    assertEquals(that, Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23'}", TypeFactory.defaultInstance().constructType(Instance.class)));
    assertEquals(that, Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017/01/23'}", TypeFactory.defaultInstance().constructType(Instance.class)));
    assertEquals(that, Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '20170123'}", TypeFactory.defaultInstance().constructType(Instance.class)));
    assertEquals(that, Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017.01.23'}", TypeFactory.defaultInstance().constructType(Instance.class)));
    assertEquals(that, Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '20170123'}", TypeFactory.defaultInstance().constructType(Instance.class)));

    Map<String, Object> those = new HashMap<>();
    those.put("versionNo", 1);
    those.put("deleted", false);
    those.put("created", LocalDateTime.of(2017, 1, 1, 1, 23, 45).plus(678, ChronoUnit.MILLIS));
    those.put("modified", LocalDateTime.of(2017, 1, 23, 1, 23, 45).plus(678, ChronoUnit.MILLIS));
    assertEquals(those.keySet().stream().sorted().collect(Collectors.joining()), ((Map<String, Object>) (Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23T01:23:45.678'}", new TypeReference<Map<String, Object>>() {}))).keySet().stream().sorted().collect(Collectors.joining()));
    assertEquals(those.values().stream().map(Objects::toString).sorted().collect(Collectors.joining()), ((Map<String, Object>) (Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23T01:23:45.678'}", new TypeReference<Map<String, Object>>() {}))).values().stream().map(Objects::toString).sorted().collect(Collectors.joining()));

    final class Another extends Instance {
      public boolean created;
    }
    try {
      Jsonifier.deserializeStrictly("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23T01:23:45.678'}", Another.class);
      fail("raise JsonMappingException .");
    } catch (JsonMappingException e) {
      assertTrue(e instanceof JsonMappingException);
    }
    try {
      Jsonifier.deserializeStrictly("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23T01:23:45.678'}", TypeFactory.defaultInstance().constructType(Another.class));
      fail("raise JsonMappingException .");
    } catch (JsonMappingException e) {
      assertTrue(e instanceof JsonMappingException);
    }
    try {
      Jsonifier.deserializeStrictly("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23T01:23:45.678'}", new TypeReference<Set<Integer>>() {});
      fail("raise JsonMappingException .");
    } catch (JsonMappingException e) {
      assertTrue(e instanceof JsonMappingException);
    }
    assertNull(Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23T01:23:45.678'}", Another.class));
    assertNull(Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23T01:23:45.678'}", TypeFactory.defaultInstance().constructType(Another.class)));
    assertNull(Jsonifier.deserialize("{versionNo: '1', deleted: false, created: '2017-01-01T01:23:45.678', modified: '2017-01-23T01:23:45.678'}", new TypeReference<Set<Integer>>() {}));

    ObjectMapper mapper = ((ObjectMapper) SavageReflection.get(Jsonifier.Shell.class, "mapper"));
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, true);
    SavageReflection.set(Jsonifier.class, "mapper", mapper);
    try {
      Jsonifier.serializeStrictly(new Nothing());
      fail("raise JsonMappingException .");
    } catch (JsonMappingException e) {
      assertTrue(e instanceof JsonMappingException);
    }
    assertNull(Jsonifier.serialize(new Nothing()));
    assertEquals(Jsonifier.serializeStrictly(new Instance()), Jsonifier.serialize(new Instance()));

    final String expect = Trebuchet.Functions.orElse((Object) null, (x) -> Jsonifier.serializeStrictly(new Nothing()), (t, e) -> String.format("{\"jsonifier.serializationFailure\":{\"error\":\"%s\",\"message\":\"%s\"}}", e.getClass().getName(), e.getMessage()));
    assertEquals(expect, Jsonifier.serializeOrFailure(new Nothing()));

    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    SavageReflection.set(Jsonifier.class, "mapper", mapper);
  }

  @Test
  public void testBrutaly() throws JsonProcessingException {
    assertEquals("{}", Jsonifier.serialize(new Unseen()));
    assertEquals("{}", Jsonifier.serializeStrictly(new Unseen()));
    assertEquals("{}", Jsonifier.serializeOrFailure(new Unseen()));
    assertEquals("{\"theInt\":123,\"theString\":[\"南\",\"無\",\"阿\",\"弥\",\"陀\",\"仏\"]}", Jsonifier.serializeBrutaly(new Unseen()));
  }
}
