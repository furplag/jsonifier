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

import jp.furplag.sandbox.stream.Streamr;
import jp.furplag.sandbox.text.Commonizr;
import jp.furplag.sandbox.trebuchet.Trebuchet;

/**
 * leniently deserialization for java.time.LocalDateTime .
 *
 * @author furplag
 *
 */
public final class LenientlyLocalDateTimeDeserializer extends LocalDateTimeDeserializer {

  /** container of {@link LocalDateTime} parser which includes {@link DateTimeFormatter} . */
  private static final Map<Integer, Function<Long[], LocalDateTime>> parsers = Collections.unmodifiableMap(new HashMap<>() {{/* @formatter:off */
    final Function<String, DateTimeFormatter> dateTimeFormatter = (pattern) -> DateTimeFormatter.ofPattern(pattern).withResolverStyle(ResolverStyle.LENIENT);
    put(1, (args) -> LocalDate.parse(Objects.toString(args[0]), dateTimeFormatter.apply("yMMdd")).atStartOfDay());
    put(3, (args) -> LocalDate.parse(String.format("%04d-%02d-%02d", (Object[]) args), dateTimeFormatter.apply("y-M-d")).atStartOfDay());
    put(4, (args) -> LocalDateTime.parse(String.format("%04d-%02d-%02dT%02d", (Object[]) args), dateTimeFormatter.apply("y-M-d'T'H")));
    put(5, (args) -> LocalDateTime.parse(String.format("%04d-%02d-%02dT%02d:%02d", (Object[]) args), dateTimeFormatter.apply("y-M-d'T'H:m")));
    put(6, (args) -> LocalDateTime.parse(String.format("%04d-%02d-%02dT%02d:%02d:%02d", (Object[]) args), dateTimeFormatter.apply("y-M-d'T'H:m:s")));
    put(7, (args) -> {
      args[args.length - 2] += (args[args.length - 1] / 1000);
      args[args.length - 1] %= 1000;
      return LocalDateTime.parse(String.format("%04d-%02d-%02dT%02d:%02d:%02d.%03d", (Object[]) args), dateTimeFormatter.apply("y-M-d'T'H:m:s.SSS"));
    });
  }});

  /** intermediates for deserializing {@link String} to {@link LocalDateTime} . */
  private static final Function<String, String[]> optimizr = Trebuchet.Functions.Uni.of(Commonizr::optimize)
    .andThen((t) -> t.replaceAll("[\\D]*[\\D&&[^\\-]]", "."))
    .andThen((t) -> t.replaceAll("(^\\.)|(\\.$)", ""))
    .andThen((t) -> t.replaceAll("(\\d)\\D+(\\d)", "$1.$2"))
    .andThen((t) -> t.split("\\.+", 8));

  /** deserialize to {@link LocalDateTime} using the one of {@link #parsers} which seems to fit . */
  private static final Function<String, LocalDateTime> deserializr = (t) -> Trebuchet.Functions.orNot(argumentify(t), (x) -> parsers.get(x.length).apply(x));

  /** unnecessary, maybe . */
  public LenientlyLocalDateTimeDeserializer() {
    super(DateTimeFormatter.ISO_LOCAL_DATE_TIME.withResolverStyle(ResolverStyle.LENIENT));
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

  /**
   * {@inheritDoc}
   */
  @Override
  public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    try {
      return super.deserialize(parser, context);
    } catch (DateTimeException | JsonMappingException e) {}

    return Trebuchet.Functions.orNot(parser.getText(), deserializr::apply);
  }
}
