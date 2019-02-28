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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import jp.furplag.data.json.deser.LazyLocalDateTimeDeserializer;
import jp.furplag.function.ThrowableBiFunction;
import jp.furplag.function.ThrowableFunction;
import jp.furplag.sandbox.reflect.SavageReflection;
import jp.furplag.sandbox.stream.Streamr;

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
    static {
      mapper = new ObjectMapper()
      // @formatter:off
        .registerModules(
          new ParameterNamesModule()
        , new Jdk8Module()
        , new JavaTimeModule().addDeserializer(LocalDateTime.class, new LazyLocalDateTimeDeserializer())
        )
        .configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true)
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
//        // sort by key name .
//        .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)

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
     * DRY : test if the object is any of {@link Class} .
     *
     * @param mysterio the object which probably the type of {@link Class}
     * @return true if the object is any of {@link Class}
     */
    private static boolean isClass(final Object mysterio) {
      return mysterio instanceof Class;
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
     * DRY : test if the object is any of {@link JavaType} .
     *
     * @param mysterio the object which probably the type of {@link JavaType}
     * @return true if the object is any of {@link JavaType}
     */
    private static boolean isJavaType(final Object mysterio) {
      return mysterio != null && JavaType.class.isAssignableFrom(mysterio.getClass());
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
    @SuppressWarnings("unchecked")
    private static <T> T deserialize(final String content, final Object valueType) throws JsonProcessingException, IOException {
      return content == null || !Stream.of((Predicate<Object>) Shell::isClass, Shell::isTypeReference, Shell::isJavaType).anyMatch((t) -> t.test(valueType)) ? null :
        isJavaType(valueType) ? mapper.readValue(content, (JavaType) valueType) :
        isTypeReference(valueType) ? mapper.readValue(content, (TypeReference<T>) valueType) :
        mapper.readValue(content, (Class<T>) valueType);
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
    return ThrowableBiFunction.orNull(content, valueType, Shell::deserialize);
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
    return ThrowableBiFunction.orNull(content, valueType, Shell::deserialize);
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
   * JSON stringify specified object, or null if error occurs .
   *
   * @param source an object
   * @return JSON stringify specified object, or null if error occurs
   */
  static String serialize(final Object source) {
    // @formatter:off
    return ThrowableFunction.orNull(source, Shell::serialize);
    // @formatter:on
  }

  /**
   * JSON stringify specified object, or null if error occurs .
   * <p><strong>Note</strong>:<div>field access using reflection if error occurs ( only for serialization ) .</div></p>
   *
   * @param source an object
   * @return JSON stringify specified object, or null if error occurs
   */
  static String serializeBrutaly(final Object source) {
    // @formatter:off
    return ThrowableFunction.orNull(SavageReflection.read(source), Shell::serialize);
    // @formatter:on
  }

  /**
   * JSON stringify specified object, or error report JSON like below if error occurs .
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
    return ThrowableFunction.orElse(source, Shell::serialize, (t, e) -> failureReport(e));
  }

  /**
   * JSON stringify error .
   *
   * @param error anything thrown
   * @return JSON stringify error
   */
  private static <E extends Throwable> String failureReport(final E error) {
    return ThrowableFunction.applyOrDefault(wrappingFailureReport(propertalizedException(error)), Shell::serialize, Collections.emptyMap().toString());
  }

  /**
   * JSON stringify error .
   *
   * @param error anything thrown
   * @return JSON stringify error
   */
  private static <E extends Throwable> Map<String, String> propertalizedException(final E error) {
    // @formatter:off
    return error == null ? Collections.emptyMap() :
      Streamr.collect(Stream.of(Pair.of("error", error.getClass().getName()), Pair.of("message", error.getMessage())), null, LinkedHashMap::new);
    // @formatter:on
  }

  /**
   * JSON stringify error .
   *
   * @param error anything thrown
   * @return JSON stringify error
   */
  private static Map<String, Map<String, String>> wrappingFailureReport(final Map<String, String> error) {
    // @formatter:off
    return Objects.requireNonNullElse(error, Collections.emptyMap()).isEmpty() ? Collections.emptyMap() :
      Streamr.collect(Stream.of(Map.entry("jsonifier.serializationFailure", error)), null, LinkedHashMap::new);
    // @formatter:on
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
}
