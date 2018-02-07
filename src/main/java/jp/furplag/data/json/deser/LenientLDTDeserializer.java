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
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

/**
 * lazy deserialization for {@link java.time.LocalDateTime} .
 *
 * @author furplag
 *
 */
public class LenientLDTDeserializer extends LocalDateTimeDeserializer {

  private static final DateTimeFormatter DATE_DIGITS;
  static {
    DATE_DIGITS = DateTimeFormatter.ofPattern("yMMdd").withResolverStyle(ResolverStyle.LENIENT);
  }

  public LenientLDTDeserializer() {
    super(DateTimeFormatter.ISO_LOCAL_DATE_TIME.withResolverStyle(ResolverStyle.LENIENT));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    LocalDateTime deserialized = null;
    try {
      deserialized = super.deserialize(parser, context);
    } catch (DateTimeException | JsonMappingException e) {
      try {
        deserialized = parseDateTime(parser.getText());
      } catch (DateTimeException | JsonMappingException e2) {}
    }

    return deserialized;
  }

  private LocalDateTime parseDateTime(String dateTimeString) {
    final int[] ymds = Arrays.stream(Objects.toString(dateTimeString, "").trim().split("[\\D]+", 8)).filter(((Predicate<String>) String::isEmpty).negate()).mapToInt(Integer::valueOf).toArray();
    LocalDateTime parsed = null;
    switch (ymds.length) {
      case 1:
        parsed = LocalDate.parse(Integer.toString(ymds[0]), DATE_DIGITS).atStartOfDay();
        break;
      case 3:
        parsed = LocalDate.of(ymds[0], ymds[1], ymds[2]).atStartOfDay();
        break;
      case 4:
        parsed = LocalDateTime.of(ymds[0], ymds[1], ymds[2], ymds[3], 0);
        break;
      case 5:
        parsed = LocalDateTime.of(ymds[0], ymds[1], ymds[2], ymds[3], ymds[4]);
        break;
      case 6:
        parsed = LocalDateTime.of(ymds[0], ymds[1], ymds[2], ymds[3], ymds[4], ymds[5]);
        break;
      case 7:
        parsed = LocalDateTime.of(ymds[0], ymds[1], ymds[2], ymds[3], ymds[4], ymds[5]).plus(ymds[6], ChronoUnit.MILLIS);
        break;
      default:
        break;
    }

    return parsed;
  }
}
