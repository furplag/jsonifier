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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import jp.furplag.data.json.deser.LenientLDTDeserializer;

/**
 * utilities for convert between Object and JSON.
 *
 * @author furplag
 * @since 1.0.0
 */
public class Jsonifier {

  /**
   * {@link com.fasterxml.jackson.databind.ObjectMapper ObjectMapper}.
   */
  private static final ObjectMapper mapper;

  static {
    // @formatter:off
    mapper = new ObjectMapper()
      .registerModules(
        new ParameterNamesModule()
      , new Jdk8Module()
      , new JavaTimeModule().addDeserializer(LocalDateTime.class, new LenientLDTDeserializer())
      )
      .enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)

      .configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true)
      .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)

      // Allow /** comment */ .
      .configure(JsonParser.Feature.ALLOW_COMMENTS, true)
      // Allow # comment .
      .configure(JsonParser.Feature.ALLOW_YAML_COMMENTS, true)
      // Allow "{"key": "value"... , }" .
      .configure(JsonParser.Feature.ALLOW_TRAILING_COMMA, true)
      // Allow "{'key': 'value'}" .
      .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
      // Allow "{key: "value"}" .
      .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)

      // pretty print for Date/Time .
      .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

      // sort by key name .
      .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
      // against failure if no fields .
      .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
      // against failure if undefined field .
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .configure(DeserializationFeature.FAIL_ON_TRAILING_TOKENS, false)

      // igonre empty field .
      .setDefaultPropertyInclusion(JsonInclude.Include.NON_EMPTY)
    ;
    // @formatter:off
  }

  /**
   * stringify specified object. Throw exceptions if that has failed.
   *
   * @param source an Object, may be null.
   * @return JSON String.
   * @throws JsonProcessingException
   * @throws Throwable
   */
  public static String serialize(final Object source) throws JsonProcessingException {
    return source == null ? null : mapper.writeValueAsString(source);
  }

  /**
   * create the instance of specified class represented by the JSON String. Throw exceptions if that has failed.
   *
   * @param json JSON String.
   * @param type destination Class.
   * @return the instance of specified Class.
   * @throws IOException
   * @throws JsonMappingException
   * @throws JsonParseException
   */
  public static <T> T deserialize(final String json, final Class<T> valueType) throws JsonProcessingException, IOException {
    return !deserializable(json, valueType) ? null : mapper.readValue(json, valueType);
  }

  /**
   * create the instance of specified class represented by the JSON String. Throw exceptions if that has failed.
   *
   * @param json JSON String.
   * @param javaType {@link com.fasterxml.jackson.databind.JavaType JavaType} .
   * @return the instance of specified Class.
   * @throws IOException
   * @throws JsonProcessingException
   */
  public static <T> T deserialize(final String json, final JavaType valueType) throws JsonProcessingException, IOException {
    return !deserializable(json, valueType) ? null : mapper.readValue(json, valueType);
  }

  /**
   * create the instance of specified class represented by the JSON String. Throw exceptions if that has failed.
   *
   * @param json JSON String.
   * @param valueTypeRef {@link com.fasterxml.jackson.core.type.TypeReference TypeReference}.
   * @return the instance of specified Class.
   * @throws IOException
   * @throws JsonProcessingException
   */
  public static <T> T deserialize(final String json, final TypeReference<T> valueTypeRef) throws JsonProcessingException, IOException {
    return !deserializable(json, valueTypeRef) ? null : mapper.readValue(json, valueTypeRef);
  }

  private static boolean deserializable(final String json, final Object valueType) {
    return !Objects.toString(json, "").isEmpty() && valueType != null;
  }

  /**
   * Jsonifier instances should NOT be constructed in standard programming.
   */
  protected Jsonifier() {}
}