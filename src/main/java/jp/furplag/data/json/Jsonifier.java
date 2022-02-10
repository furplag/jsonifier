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
package jp.furplag.data.json;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import jp.furplag.data.json.deser.LenientlyLocalDateTimeDeserializer;
import jp.furplag.sandbox.reflect.SavageReflection;
import jp.furplag.sandbox.trebuchet.Trebuchet;

/**
 * easy to use (for me) JSON in Java .
 *
 * @author furplag
 *
 */
public interface Jsonifier {

  /** lazy initialization for {@link ObjectMapper#ObjectMapper()} . */
  static final class Shell {

    /** {@link ObjectMapper#ObjectMapper()} . */
    private static final ObjectMapper mapper;
    static {/* @formatter:off */
      mapper = new ObjectMapper(new JsonFactoryBuilder()
        // Allow /** comment */ .
        .enable(JsonReadFeature.ALLOW_JAVA_COMMENTS)
        // Allow # comment .
        .enable(JsonReadFeature.ALLOW_YAML_COMMENTS)
        // Allow "{"key": "value"... , }" .
        .enable(JsonReadFeature.ALLOW_TRAILING_COMMA)
        // Allow "{'key': 'value'}" .
        .enable(JsonReadFeature.ALLOW_SINGLE_QUOTES)
        // Allow "{key: "value"}" .
        .enable(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES)
        .build()
      )

      // @formatter:off
      .registerModules(
        new ParameterNamesModule()
      , new Jdk8Module()
      , new JavaTimeModule().addDeserializer(LocalDateTime.class, new LenientlyLocalDateTimeDeserializer())
      )
      .configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true)
      .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
      // Allow "{key: "value"}" .
      .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
      // sort by key name .
      // .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)

      // pretty print for Date/Time .
      .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
      // against failure if no fields .
      .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
      // against failure if undefined field .
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .configure(DeserializationFeature.FAIL_ON_TRAILING_TOKENS, false)
      // igonre empty field .
      .setDefaultPropertyInclusion(JsonInclude.Include.NON_EMPTY)
      // @formatter:on
      ;
    }

    /**
     * create the instance of specified class represented by the JSON String .
     *
     * @param <T> the type of instance
     * @param content a text which maybe JSON formatted
     * @param valueType {@link Class} or an instance of {@link JavaType} or {@link TypeReference}
     * @return an instance of T
     * @throws JsonProcessingException if the input JSON structure does not match structure expected for result type
     * @throws IOException if a low-level I/O problem (unexpected end-of-input, network error) occurs
     */
    @SuppressWarnings({ "unchecked" })
    private static <T> T deserialize(final String content, final Object valueType) throws JsonProcessingException, IOException {
      return content == null || !Stream.of((Predicate<Object>) Shell::isClass, Shell::isTypeReference, Shell::isJavaType).anyMatch((t) -> t.test(valueType)) ? null
          : isJavaType(valueType) ? mapper.readValue(content, (JavaType) valueType) : isTypeReference(valueType) ? mapper.readValue(content, (TypeReference<T>) valueType) : mapper.readValue(content, (Class<T>) valueType);
    }

    /**
     * DRY : test if the object is any of {@link Class} .
     *
     * @param mysterio the object which probably the type of {@link Class}
     * @return true if the object is any of {@link Class}
     */
    private static boolean isClass(final Object mysterio) {
      return mysterio instanceof Class;
    }

    /**
     * DRY : test if the object is any of {@link JavaType} .
     *
     * @param mysterio the object which probably the type of {@link JavaType}
     * @return true if the object is any of {@link JavaType}
     */
    private static boolean isJavaType(final Object mysterio) {
      return mysterio != null && JavaType.class.isAssignableFrom(mysterio.getClass());
    }

    /**
     * DRY : test if the object is any of {@link TypeReference} .
     *
     * @param mysterio the object which probably the type of {@link TypeReference}
     * @return true if the object is any of {@link TypeReference}
     */
    private static boolean isTypeReference(final Object mysterio) {
      return mysterio != null && TypeReference.class.isAssignableFrom(mysterio.getClass());
    }

    /**
     * stringify specified object .
     *
     * @param source an object
     * @return JSON string
     * @throws JsonProcessingException if error occured
     */
    private static String serialize(final Object source) throws JsonProcessingException {
      return source == null ? null : mapper.writeValueAsString(source);
    }
  }

  /**
   * create the instance of specified class represented by the JSON String .
   *
   * @param <T> the type of instance
   * @param content a text which maybe JSON formatted
   * @param valueType {@link Class} or an instance of {@link JavaType} or {@link TypeReference}
   * @return an instance of T, or null if error occurs
   */
  static <T> T deserialize(final String content, final Object valueType) {
    return Trebuchet.Functions.orNot(content, valueType, Shell::deserialize);
  }

  /**
   * create the instance of specified class represented by the JSON String .
   *
   * @param <T> the type of instance
   * @param content a text which maybe JSON formatted
   * @param valueType {@link TypeReference}
   * @return an instance of T, or null if error occurs
   */
  static <T> T deserialize(final String content, final TypeReference<T> valueType) {
    return Trebuchet.Functions.orNot(content, valueType, Shell::deserialize);
  }

  /**
   * create the instance of specified class represented by the JSON String .
   *
   * @param <T> the type of instance
   * @param content a text which maybe JSON formatted
   * @param valueType {@link Class} or an instance of {@link JavaType} or {@link TypeReference}
   * @return an instance of T, or null if error occurs
   */
  static <T> T deserializeStrictly(final String content, final Object valueType) throws JsonProcessingException, IOException {
    return Shell.deserialize(content, valueType);
  }

  /**
   * create the instance of specified class represented by the JSON String .
   *
   * @param <T> the type of instance
   * @param content a text which maybe JSON formatted
   * @param valueType {@link Class} or an instance of {@link JavaType} or {@link TypeReference}
   * @return an instance of T, or null if error occurs
   */
  static <T> T deserializeStrictly(final String content, final TypeReference<T> valueType) throws JsonProcessingException, IOException {
    return Shell.deserialize(content, valueType);
  }

  /**
   * JSON stringify error .
   *
   * @param error anything thrown
   * @return JSON stringify error
   */
  private static <EX extends Throwable> String failureReport(final EX error) {
    return Trebuchet.Functions.orElse(wrappingFailureReport(propertalizedException(error)), Shell::serialize, () -> "{}");
  }

  /**
   * JSON stringify error .
   *
   * @param error anything thrown
   * @return JSON stringify error
   */
  private static <EX extends Throwable> Map<String, String> propertalizedException(final EX error) {/* @formatter:off */
    return new LinkedHashMap<>() {{
      Optional.ofNullable(error).ifPresent(((Consumer<EX>) (_error) -> put("error", _error.getClass().getName())).andThen((_error) -> put("message", _error.getMessage())));
    }};
  /* @formatter:on */}

  /**
   * JSON stringify specified object, or null if error occurs .
   *
   * @param source an object
   * @return JSON stringify specified object, or null if error occurs
   */
  static String serialize(final Object source) {
    // @formatter:off
    return Trebuchet.Functions.orNot(source, Shell::serialize);
    // @formatter:on
  }

  /**
   * JSON stringify specified object, or null if error occurs .
   * <p>
   * <strong>Note</strong>:<div>field access using reflection if error occurs ( only for serialization ) .</div>
   * </p>
   *
   * @param source an object
   * @return JSON stringify specified object, or null if error occurs
   */
  static String serializeBrutaly(final Object source) {
    // @formatter:off
    return Trebuchet.Functions.orNot(SavageReflection.read(source), Shell::serialize);
    // @formatter:on
  }

  /**
   * JSON stringify specified object, or error report JSON like below if error occurs .
   *
   * <pre>
   * {
   *   "jsonifier.serializationFailure": {
   *     error : " class name of error . "
   *   , message: " error message . "
   *   }
   * }
   * </pre>
   *
   * @param source an object
   * @return JSON stringify specified object, or error
   */
  static Object serializeOrFailure(final Object source) {
    return Trebuchet.Functions.orElse(source, Shell::serialize, (t, e) -> failureReport(e));
  }

  /**
   * stringify specified object .
   *
   * @param source an object
   * @return JSON string
   * @throws JsonProcessingException if error occured
   */
  static String serializeStrictly(final Object source) throws JsonProcessingException {
    return Shell.serialize(source);
  }

  /**
   * JSON stringify error .
   *
   * @param error anything thrown
   * @return JSON stringify error
   */
  private static Map<String, Map<String, String>> wrappingFailureReport(final Map<String, String> error) {/* @formatter:off */
    return new HashMap<>() {{
      Optional.ofNullable(error).ifPresent((_error) -> put("jsonifier.serializationFailure", _error));
    }};
  /* @formatter:on */}
}
