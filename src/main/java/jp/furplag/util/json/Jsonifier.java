package jp.furplag.util.json;

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

  static final ObjectMapper mapper;
  static {
    // @formatter:off
    mapper = new ObjectMapper()
      .registerModules(
        new ParameterNamesModule()
      , new Jdk8Module()
      , new JavaTimeModule()
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

  public static String serialize(final Object source) throws Throwable {
    return source == null ? null : mapper.writeValueAsString(source);
  }

  public static String serializeLazy(final Object source) {
    try {
      return serialize(source);
    } catch (Throwable t) {}

    return null;
  }

  public static <T> T deserialize(final String json, final Class<T> type) throws Throwable {
    if (json != null && type != null) return mapper.readValue(json, type);

    return null;
  }

  public static <T> T deserialize(final String json, final TypeReference<T> valueTypeRef) throws Throwable {
    if (json != null && valueTypeRef != null) return mapper.readValue(json, valueTypeRef);

    return null;
  }

  public static <T> T deserializeLazy(final String json, final Class<T> type) {
    try {
      return deserialize(json, type);
    } catch (Throwable t) {}

    return null;
  }

  public static <T> T deserializeLazy(final String json, final TypeReference<T> valueTypeRef) {
    try {
      return deserialize(json, valueTypeRef);
    } catch (Throwable t) {}

    return null;
  }
}
