import java.time.*;
import java.time.format.DateTimeFormatter;

public class Main {
  public static void main(String[] args) {
    System.out.println(MonthDay.now()); // --MM-dd
    System.out.println(LocalDate.now()); // YYYY-MM-dd
    System.out.println(LocalDateTime.now()); // datetime including milliseconds(OS TimeZone)
    System.out.println(Instant.now()); // datetime including milliseconds(UTC)
    System.out.println(ZonedDateTime.now()); // datetime including milliseconds and offset and TimeZone data

    System.out.println(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

    Instant startInstant = Instant.parse("2024-01-01T00:00:00Z");
    Instant endInstant = Instant.parse("2024-04-01T00:00:00Z");
    System.out.println(Duration.between(startInstant, endInstant)); // datetime diff (Instant)

    LocalDateTime startLocalDateTime = LocalDateTime.parse("2024-01-01T00:00:00");
    LocalDateTime endLocalDateTime = LocalDateTime.parse("2024-04-01T00:00:00");
    System.out.println(Duration.between(startLocalDateTime, endLocalDateTime)); // datetime diff (LocalDateTime)

    ZonedDateTime startZonedDateTime = ZonedDateTime.parse("2024-01-01T00:00:00+01:00[Etc/UTC]");
    ZonedDateTime endZonedDateTime = ZonedDateTime.parse("2024-04-01T00:00:00+01:00[Etc/UTC]");
    System.out.println(Duration.between(startZonedDateTime, endZonedDateTime)); // datetime diff (ZonedDateTime)

    LocalDate startLocalDate = LocalDate.parse("2024-01-01");
    LocalDate endLocalDate = LocalDate.parse("2024-04-01");
    System.out.println(endLocalDate.toEpochDay() - startLocalDate.toEpochDay()); // LocalDate diff
  }
}
