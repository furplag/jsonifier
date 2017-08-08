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
package jp.furplag.util.json.deser;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.function.Predicate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

public class LenientLDTDeserializer extends LocalDateTimeDeserializer {

  private static final long serialVersionUID = 1L;

  public LenientLDTDeserializer(DateTimeFormatter formatter) {
    super(formatter);
  }

  @Override
  public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    if (parser.hasTokenId(JsonTokenId.ID_STRING)) {
      String string = parser.getText().trim();
      if (string.length() == 0) {
        return null;
      }
      try {
        if (string.length() == 10) {
          return LocalDate.parse(string.replaceAll("[\\/\\.\\-]", "-"), DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        } else if (8 <= string.length() && string.length() < 10) {
          int[] ymds = Arrays.stream(string.split("[\\/\\.\\-]")).filter(((Predicate<String>)String::isEmpty).negate()).mapToInt(Integer::valueOf).toArray();
          if (ymds.length == 3) {
            return LocalDate.of(ymds[0], ymds[1], ymds[2]).atStartOfDay();
          } else if (8 == string.length() && string.matches("^\\d{8}$")) {
            return LocalDate.parse(string, DateTimeFormatter.ofPattern("yyyyMMdd")).atStartOfDay();
          }
        }
      } catch (DateTimeException e) {
          _rethrowDateTimeException(parser, context, e, string);
      }
    }

    return super.deserialize(parser, context);
  }
}
