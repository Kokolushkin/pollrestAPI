package alexander.rest.util;

import org.apache.commons.validator.routines.EmailValidator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.regex.Pattern;

/**
 * Helper class that allows you to validate received arguments.
 */
public class Validator {

    private static final String SIMPLE_PHONE_PATTERN = "\\d{10}";
    private static final String SPACE_CHARACTER = "\\u0020";

    private Pattern datePattern = Pattern.compile("");

    /**
     * @param datetime Datetime string to check.
     * @return true - If the received datetime conforms to the format YY-MM-DD HH:MM:SS
     *                and belongs to the {@link WorkingTimeInterval}.<br><br>
     *         throw {@link IllegalArgumentException} - All other cases.
     */
    public static boolean validateDatetime(String datetime) {
        String[] splitedDatetime = datetime.split(SPACE_CHARACTER);
        if(splitedDatetime.length != 2){
            throw new IllegalArgumentException("Incorrect datetime format");
        }

        LocalDate date = LocalDate.parse(splitedDatetime[0]);
        LocalTime time = LocalTime.parse(splitedDatetime[1]);

        LocalDate dateNow = LocalDate.now();
        LocalTime timeNow = LocalTime.now();
        if(date.isBefore(dateNow) || (date.equals(dateNow) && time.isBefore(timeNow))){
            throw new IllegalArgumentException("Unable to book a time in the past");
        }

        WorkingTimeInterval interval = WorkingTimeInterval.createInterval(date);
        if(!interval.checkIntervalMembership(time)){
            throw new IllegalArgumentException("Unable to book non-working hours");
        }

        if(time.getMinute() != 0 || time.getSecond() != 0){
            throw new IllegalArgumentException("unable to book an out-of-schedule time");
        }

        return true;
    }

    /**
     * @param phoneNumber Phone string to check.
     * @return true - If matches the {{@link #SIMPLE_PHONE_PATTERN}} pattern.<br>
     *         throw {@link IllegalArgumentException} - All other cases.
     */
    public static boolean validateTelephoneNumber(String phoneNumber){
        if(!phoneNumber.matches(SIMPLE_PHONE_PATTERN)){
            throw new IllegalArgumentException("Invalid phone number format");
        }

        return true;
    }

    /**
     * @param email Email string to check.
     * @return true - If {@link EmailValidator} returns true.<br>
     *         throw {@link IllegalArgumentException} - All other cases.
     */
    public static boolean validateEmail(String email){
        if(!EmailValidator.getInstance().isValid(email)){
            throw new IllegalArgumentException("Invalid email format");
        }

        return true;
    }
}
