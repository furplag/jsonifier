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

package jp.furplag.data.json.deser;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import jp.furplag.function.ThrowableFunction;
import jp.furplag.sandbox.stream.Streamr;
import jp.furplag.text.optimize.Optimizr;

/**
 * lazy deserialization for {@link java.time.LocalDateTime} .
 *
 * <h3>note</h3>:
 * <ul>
 * <li>Lenient parsing .</li>
 * <li>Lenient parsing .</li>
 * <ul>
 *
 * @author furplag
 *
 */
public final class LazyLocalDateTimeDeserializer extends LocalDateTimeDeserializer {

  /** container of {@link LocalDateTime} parser which includes {@link DateTimeFormatter} . */
  private static final Map<Integer, Function<Long[], LocalDateTime>> parsers;

  /** intermediates for deserializing {@link String} to {@link LocalDateTime} . */
  private static final Function<String, String[]> optimizr;

  /** deserialize to {@link LocalDateTime} using the one of {@link #parsers} which seems to fit . */
  private static final Function<String, LocalDateTime> deserializr;

  static {
    final Function<String, DateTimeFormatter> dateTimeFormatter = (pattern) -> DateTimeFormatter.ofPattern(pattern).withResolverStyle(ResolverStyle.LENIENT);
    final Map<Integer, Function<Long[], LocalDateTime>> parsers0 = new HashMap<>();
    parsers0.put(1, (args) -> LocalDate.parse(Objects.toString(args[0]), dateTimeFormatter.apply("yMMdd")).atStartOfDay());
    parsers0.put(3, (args) -> LocalDate.parse(String.format("%04d-%02d-%02d", (Object[]) args), dateTimeFormatter.apply("y-M-d")).atStartOfDay());
    parsers0.put(4, (args) -> LocalDateTime.parse(String.format("%04d-%02d-%02dT%02d", (Object[]) args), dateTimeFormatter.apply("y-M-d'T'H")));
    parsers0.put(5, (args) -> LocalDateTime.parse(String.format("%04d-%02d-%02dT%02d:%02d", (Object[]) args), dateTimeFormatter.apply("y-M-d'T'H:m")));
    parsers0.put(6, (args) -> LocalDateTime.parse(String.format("%04d-%02d-%02dT%02d:%02d:%02d", (Object[]) args), dateTimeFormatter.apply("y-M-d'T'H:m:s")));
    // @formatter:off
    parsers0.put(7, (args) -> {
      args[args.length - 2] += (args[args.length - 1] / 1000);
      args[args.length - 1] %= 1000;
      return LocalDateTime.parse(String.format("%04d-%02d-%02dT%02d:%02d:%02d.%03d", (Object[]) args), dateTimeFormatter.apply("y-M-d'T'H:m:s.SSS"));
    });
    // @formatter:on
    parsers = Collections.unmodifiableMap(parsers0);
    optimizr = ThrowableFunction.of(Optimizr::optimize, (t, e) -> (String) null)
      .andThen((t) -> t.replaceAll("[\\D]*[\\D&&[^\\-]]", "."))
      .andThen((t) -> t.replaceAll("(^\\.)|(\\.$)", ""))
      .andThen((t) -> t.replaceAll("(\\d)\\D+(\\d)", "$1.$2"))
      .andThen((t) -> t.split("\\.+", 8));
    deserializr = (t) -> ThrowableFunction.orNull(argumentify(t), (x) -> parsers.get(x.length).apply(x));
  }

  /** unnecessary, maybe . */
  public LazyLocalDateTimeDeserializer() {
    super(DateTimeFormatter.ISO_LOCAL_DATE_TIME.withResolverStyle(ResolverStyle.LENIENT));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    try {
      return super.deserialize(parser, context);
    } catch (DateTimeException | JsonMappingException e) {
    }

    return ThrowableFunction.orNull(parser.getText(), deserializr::apply);
  }

  /**
   * intermediates for deserializing {@link String} to {@link LocalDateTime} .
   *
   * @param text the text which possibly parsable to {@link LocalDateTime}
   * @return the array of numerics
   */
  private static final Long[] argumentify(final String text) {
    return Streamr.stream(optimizr.apply(text)).map(Long::valueOf).mapToLong(Long::longValue).boxed().toArray(Long[]::new);
  }
}
