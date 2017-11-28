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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

public class LenientLDTDeserializer extends LocalDateTimeDeserializer {

  public LenientLDTDeserializer(DateTimeFormatter formatter) {
    super(formatter);
  }

  @Override
  public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    final int[] ymds = Arrays.stream(Objects.toString(parser.getText(), "").trim().split("[\\D]+")).filter(((Predicate<String>) String::isEmpty).negate()).mapToInt(Integer::valueOf).toArray();

    return
      ymds.length == 3 ? LocalDate.of(ymds[0], ymds[1], ymds[2]).atStartOfDay() :
      ymds.length == 1 && Integer.toString(ymds[0]).length() == 8 ? LocalDate.parse(Integer.toString(ymds[0]), DateTimeFormatter.ofPattern("yyyyMMdd")).atStartOfDay() :
      super.deserialize(parser, context);
  }
}
