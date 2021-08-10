package org.example.cayenne.persistent;


import org.example.cayenne.persistent.auto._Artist;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Artist extends _Artist {

    static final String DEFAULT_DATE_FORMAT = "yyyyMMdd";
    private static final long serialVersionUID = 1L;

    public void setDateOfBirthString(String yearMonthDay) {
        if (yearMonthDay == null) {
            setDateOfBirth(null);
        } else {

            LocalDate date;
            try {
                DateTimeFormatter formatter = DateTimeFormatter
                        .ofPattern(DEFAULT_DATE_FORMAT);
                date = LocalDate.parse(yearMonthDay, formatter);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException(
                        "A date argument must be in format '"
                                + DEFAULT_DATE_FORMAT + "': " + yearMonthDay);
            }
            setDateOfBirth(date);
        }
    }
}
