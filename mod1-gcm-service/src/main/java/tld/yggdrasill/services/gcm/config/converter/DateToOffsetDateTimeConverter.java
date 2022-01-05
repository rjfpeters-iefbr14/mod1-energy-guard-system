package tld.yggdrasill.services.gcm.config.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class DateToOffsetDateTimeConverter implements Converter<Date, OffsetDateTime> {

  @Override
  public OffsetDateTime convert(Date source) {
    return OffsetDateTime.ofInstant(source.toInstant(), ZoneOffset.UTC);
  }
}
