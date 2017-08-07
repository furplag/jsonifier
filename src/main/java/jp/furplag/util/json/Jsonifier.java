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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

/**
 * utilities for convert between Object and JSON.
 *
 * @author furplag
 * @since 1.0.0
 */
public class Jsonifier {

  /**
   * Jsonifier instances should NOT be constructed in standard programming.
   */
  protected Jsonifier() {}

  /**
   * {@link com.fasterxml.jackson.databind.ObjectMapper ObjectMapper}.
   */
  static final ObjectMapper mapper;
  static {
    // @formatter:off
    mapper = new ObjectMapper()
      .registerModules(
        new ParameterNamesModule()
      , new Jdk8Module()
      , new JavaTimeModule()
        .addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ISO_LOCAL_DATE))
        .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        .addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ISO_LOCAL_DATE))
        .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
      )
      // enabling
      .enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
      .configure(SerializationFeature.INDENT_OUTPUT, true)
      .configure(SerializationFeature.WRITE_ENUMS_USING_INDEX, true)
      .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
      .configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true)
      .configure(JsonParser.Feature.ALLOW_TRAILING_COMMA, true)

      // disabling
      .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
      .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

      .setVisibility(PropertyAccessor.ALL, Visibility.NONE)
      .setVisibility(PropertyAccessor.FIELD, Visibility.ANY)
    ;
    // @formatter:off
  }

  /**
   * stringify specified object. Throw exceptions if stringify has failed.
   *
   * @param source an Object, may be null.
   * @return JSON String.
   * @throws Throwable
   */
  public static String serialize(final Object source) throws Throwable {
    return source == null ? null : mapper.writeValueAsString(source);
  }

  /**
   * stringify specified object. Return {@code null} if stringify has failed.
   *
   * @param source an Object, may be null.
   * @return JSON String.
   */
  public static String serializeLazy(final Object source) {
    try {
      return serialize(source);
    } catch (Throwable t) {}

    return null;
  }

  /**
   * create the instance of specified class represented by the JSON String. Throw exceptions if convert has failed.
   *
   * @param json JSON String.
   * @param type destination Class.
   * @return the instance of specified Class.
   * @throws Throwable
   */
  public static <T> T deserialize(final String json, final Class<T> type) throws Throwable {
    if (json != null && type != null) return mapper.readValue(json, type);

    return null;
  }

  /**
   * create the instance of specified class represented by the JSON String. Throw exceptions if convert has failed.
   *
   * @param json JSON String.
   * @param valueTypeRef {@link com.fasterxml.jackson.core.type.TypeReference TypeReference}.
   * @return the instance of specified Class.
   * @throws Throwable
   */
  public static <T> T deserialize(final String json, final TypeReference<T> valueTypeRef) throws Throwable {
    if (json != null && valueTypeRef != null) return mapper.readValue(json, valueTypeRef);

    return null;
  }

  /**
   * create the instance of specified class represented by the JSON String. Return {@code null} if convert has failed.
   *
   * @param json JSON String.
   * @param type destination Class.
   * @return the instance of specified Class.
   * @throws Throwable
   */
  public static <T> T deserializeLazy(final String json, final Class<T> type) {
    try {
      return deserialize(json, type);
    } catch (Throwable t) {}

    return null;
  }

  /**
   * create the instance of specified class represented by the JSON String. Return {@code null} if convert has failed.
   *
   * @param json JSON String.
   * @param valueTypeRef {@link com.fasterxml.jackson.core.type.TypeReference TypeReference}.
   * @return the instance of specified Class.
   * @throws Throwable
   */
  public static <T> T deserializeLazy(final String json, final TypeReference<T> valueTypeRef) {
    try {
      return deserialize(json, valueTypeRef);
    } catch (Throwable t) {}

    return null;
  }
}
