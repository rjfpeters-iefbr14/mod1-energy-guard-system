package tld.yggdrasill.services.dsa.config.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class OffsetDateTimeToDateConverter implements Converter<OffsetDateTime, Date> {

  @Override
  public Date convert(OffsetDateTime source) {
    return Date.from(Instant.from(source.toInstant().atOffset(ZoneOffset.UTC)));
  }
}
