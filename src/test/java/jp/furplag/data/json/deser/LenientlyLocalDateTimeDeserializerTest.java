/*
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
package jp.furplag.data.json.deser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.IOException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

public class LenientlyLocalDateTimeDeserializerTest {

  @Test
  void test() throws JsonProcessingException, IOException {
    // @formatter:off
    final ObjectMapper objectMapper = new ObjectMapper().registerModules(
      new ParameterNamesModule()
    , new Jdk8Module()
    , new JavaTimeModule().addDeserializer(LocalDateTime.class, new LenientlyLocalDateTimeDeserializer())
    );
    // @formatter:on

    assertNull(objectMapper.readValue((String) "\"\"", LocalDateTime.class));
    assertNull(objectMapper.readValue((String) "\"1\"", LocalDateTime.class));
    assertNull(objectMapper.readValue((String) "\"南無阿弥陀仏\"", LocalDateTime.class));
    assertNull(objectMapper.readValue((String) "\"123\"", LocalDateTime.class));
    assertNull(objectMapper.readValue((String) "\"1234\"", LocalDateTime.class));
    assertNull(objectMapper.readValue((String) "\"12345678901234567890\"", LocalDateTime.class));

    assertEquals((LocalDateTime) null, objectMapper.readValue("\"0123\"", LocalDateTime.class));
    assertEquals((LocalDateTime) null, objectMapper.readValue("\"01-23\"", LocalDateTime.class));
    assertEquals(LocalDateTime.of(126, 11, 6, 0, 0), objectMapper.readValue("\"01234567\"", LocalDateTime.class));
    assertEquals(LocalDateTime.of(2017, 1, 1, 1, 23, 45, 678 * 1000000), objectMapper.readValue("\"2017-01-01T01:23:45.678\"", LocalDateTime.class));
    assertEquals(LocalDateTime.of(2017, 1, 1, 0, 0), objectMapper.readValue("\"2017-01-01\"", LocalDateTime.class));
    assertEquals((LocalDateTime) null, objectMapper.readValue("\"2017-01-01T12:34:56.789+9\"", LocalDateTime.class));
    assertEquals(LocalDateTime.of(2017, 1, 1, 9, 0), objectMapper.readValue("\"2017-01-01T9\"", LocalDateTime.class));
    assertEquals(LocalDateTime.of(2017, 1, 1, 12, 34), objectMapper.readValue("\"2017-01-01T12:34\"", LocalDateTime.class));
    assertEquals(LocalDateTime.of(2017, 1, 1, 12, 34, 56), objectMapper.readValue("\"2017-01-01T12:34:56\"", LocalDateTime.class));
    assertEquals(LocalDateTime.of(2017, 1, 1, 12, 34, 56, 789 * 1000000), objectMapper.readValue("\"2017-01-01T12:34:56.789\"", LocalDateTime.class));

    assertEquals(LocalDateTime.of(2017, 1, 1, 0, 0), objectMapper.readValue("\"2017/01/01\"", LocalDateTime.class));
    assertEquals((LocalDateTime) null, objectMapper.readValue("\"2017/01/01 12:34:56.789+9\"", LocalDateTime.class));
    assertEquals(LocalDateTime.of(2017, 1, 1, 9, 0), objectMapper.readValue("\"2017/01/01 9\"", LocalDateTime.class));
    assertEquals(LocalDateTime.of(2017, 1, 1, 12, 34), objectMapper.readValue("\"2017/01/01 12:34\"", LocalDateTime.class));
    assertEquals(LocalDateTime.of(2017, 1, 1, 12, 34, 56), objectMapper.readValue("\"2017/01/01 12:34:56\"", LocalDateTime.class));
    assertEquals(LocalDateTime.of(2017, 1, 1, 12, 34, 56, 789 * 1000000), objectMapper.readValue("\"2017/01/01 12:34:56.789\"", LocalDateTime.class));
  }

  @Test
  void testNonCustomized() throws JsonProcessingException, IOException {
    try {
      new ObjectMapper().readValue("\"2017-01-01\"", LocalDateTime.class);
      fail("there must raise InvalidDefinitionException .");
    } catch (Exception e) {
      assertTrue(e instanceof InvalidDefinitionException);
    }
  }
}
