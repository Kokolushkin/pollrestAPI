package alexander.rest.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * This class provides work time intervals for weekends and weekdays.
 */
public class WorkingTimeInterval {

    private static final String START_ON_WEEKENDS = "12:00:00";
    private static final String END_ON_WEEKENDS = "18:00:00";

    private static final String START_ON_WEEKDAYS = "10:00:00";
    private static final String END_ON_WEEKDAYS = "20:00:00";

    private LocalTime startTime;
    private LocalTime endTime;

    private WorkingTimeInterval(String startTime, String endTime){
        this.startTime = LocalTime.parse(startTime);
        this.endTime = LocalTime.parse(endTime);
    }

    /**
     * Creates {@link WorkingTimeInterval} relative to the given {@link LocalDate}
     * @param localDate The day for which the {@link WorkingTimeInterval} will be created
     * @return
     */
    public static WorkingTimeInterval createInterval(LocalDate localDate){
        int dayOfWeek = getDayOfWeek(localDate);
        if(dayOfWeek == 1 || dayOfWeek == 7){
            return new WorkingTimeInterval(START_ON_WEEKENDS, END_ON_WEEKENDS);
        } else {
            return new WorkingTimeInterval(START_ON_WEEKDAYS, END_ON_WEEKDAYS);
        }
    }

    /**
     * @param time {@link LocalTime} to check
     * @return true  - If time belongs to the interval <br>
     *         false - All other cases
     */
    public boolean checkIntervalMembership(LocalTime time){
        return time.isAfter(startTime) && time.isBefore(endTime) ||
               time.equals(startTime) || time.equals(endTime);
    }

    /**
     * Returns the number of the day of the week.<br>
     * 1 - Sunday<br>
     * 2 - Monday<br>
     * 3 - Tuesday<br>
     * 4 - Wednesday<br>
     * 5 - Thursday<br>
     * 6 - Friday<br>
     * 7 - Saturday<br>
     * @param localDate
     * @return
     */
    private static int getDayOfWeek(LocalDate localDate) {
        Calendar calendar = Calendar.getInstance();
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        calendar.setTime(date);

        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
}
