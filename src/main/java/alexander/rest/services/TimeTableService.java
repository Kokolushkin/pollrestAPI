package alexander.rest.services;

import alexander.rest.util.ConnectionHelper;
import alexander.rest.util.Validator;
import alexander.rest.util.WorkingTimeInterval;
import alexander.rest.model.TimeTable;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class TimeTableService {
    private static final int MAX_CLIENTS = 10;
    private static final int MAX_NUMBER_VISIT = 2;

    private static final String SPACE_CHARACTER = "\\u0020";
    private static final String ID_DELIMITER = "_";

    private static final String NUMBER_BOOKED_FOR_DATETIME = "SELECT COUNT(*) FROM timetable WHERE date_=? AND time_=?";
    private static final String NUMBER_BOOKED_PER_PERSON = "SELECT COUNT(*) FROM timetable WHERE date_=? AND client_id=?";
    private static final String INSERT_QUERY = "INSERT INTO timetable (order_id, date_, time_, client_id) VALUES(?, ?, ?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM timetable WHERE order_id=? AND client_id=?";
    private static final String SELECT_QUERY = "select time_, COUNT(*) as \"Count reserved\" from timetable where date_=? group by time_";
    private static final String SELECT_BY_NAME_QUERY = "SELECT id, name, order_id, date_, time_ FROM clients INNER JOIN timetable ON client_id = id  WHERE name=?";
    private static final String SELECT_BY_DATE_QUERY = "SELECT id, name, order_id, date_, time_ FROM clients INNER JOIN timetable ON client_id = id  WHERE date_=?";

    private static Connection connection;

    public TimeTableService(){
        connection = ConnectionHelper.getConnection();
    }

    public JsonArray getAll(String date) {
        JsonArray result = new JsonArray();
        getBookedTimeForDate(date).entrySet().forEach(entry -> {
            JsonObject object = new JsonObject();
            object.addProperty("time", entry.getKey());
            object.addProperty("count", entry.getValue());
            result.add(object);
        });

        return result;
    }

    public JsonArray getAvailable(String date){
        JsonArray result = new JsonArray();
        getBookedTimeForDate(date).entrySet().forEach(entry -> {
            JsonObject object = new JsonObject();
            object.addProperty("time", entry.getKey());
            object.addProperty("count", MAX_CLIENTS - entry.getValue());
            result.add(object);
        });

        return result;
    }

    public JsonObject reserve(TimeTable timeTable) {
        JsonObject result = new JsonObject();
        String datetime = timeTable.getDatetime();

        try{
            Validator.validateDatetime(datetime);
        } catch (DateTimeParseException | IllegalArgumentException exception){
            System.out.println(exception.getMessage());
            return result;
        }

        String[] splittedDateTime = datetime.split(SPACE_CHARACTER);

        if (numberOfBooked(splittedDateTime[0], splittedDateTime[1]) >= MAX_CLIENTS) {
            System.out.println("There is no available booking for the specified time");
            return result;
        }

        if(numberOfBookedPerPerson(splittedDateTime[0], timeTable.getClientId()) > MAX_NUMBER_VISIT){
            System.out.println("It is not possible to book more than twice on the same day");
            return result;
        }

        String orderId = timeTable.getClientId() + ID_DELIMITER + splittedDateTime[0] + ID_DELIMITER + splittedDateTime[1];

        try (PreparedStatement statement = connection.prepareStatement(INSERT_QUERY)) {
            statement.setString(1, orderId);
            statement.setString(2, splittedDateTime[0]);
            statement.setString(3, splittedDateTime[1]);
            statement.setInt(4, timeTable.getClientId());
            statement.executeUpdate();
        } catch (SQLException exception){
            System.out.println(exception.getMessage());
            return result;
        }

        result.addProperty("orderId", orderId);
        return result;
    }

    public void cancel(TimeTable timeTable) {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_QUERY)) {
            statement.setString(1, timeTable.getOrderId());
            statement.setInt(2, timeTable.getClientId());
            statement.executeUpdate();
        } catch (SQLException exception){
            System.out.println(exception.getMessage());
        }
    }

    public JsonArray getBookedTimeByName(String name){
        JsonArray result = new JsonArray();

        try(PreparedStatement statement = connection.prepareStatement(SELECT_BY_NAME_QUERY)){
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                JsonObject object = new JsonObject();
                object.addProperty("id", resultSet.getInt("id"));
                object.addProperty("name", resultSet.getString("name"));
                object.addProperty("orderId", resultSet.getString("order_id"));
                object.addProperty("date", resultSet.getString("date_"));
                object.addProperty("time", resultSet.getString("time_"));
                result.add(object);
            }
        } catch (SQLException exception){
            System.out.println(exception.getMessage());
        }

         return result;
    }

    public JsonArray getInfoByDate(String date){
        JsonArray result = new JsonArray();

        try (PreparedStatement statement = connection.prepareStatement(SELECT_BY_DATE_QUERY)) {
            statement.setString(1, date);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                JsonObject object = new JsonObject();
                object.addProperty("id", resultSet.getInt("id"));
                object.addProperty("name", resultSet.getString("name"));
                object.addProperty("orderId", resultSet.getString("order_id"));
                object.addProperty("date", resultSet.getString("date_"));
                object.addProperty("time", resultSet.getString("time_"));
                result.add(object);
            }
        } catch (SQLException exception){
            System.out.println(exception.getMessage());
        }

        return result;
    }

    private int numberOfBooked(String date, String time){
        try (PreparedStatement statement = connection.prepareStatement(NUMBER_BOOKED_FOR_DATETIME)) {
            statement.setString(1, date);
            statement.setString(2, time);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                return resultSet.getInt("COUNT(*)");
            }
        } catch (SQLException exception){
            System.out.println(exception.getMessage());
        }

        return 0;
    }

    private int numberOfBookedPerPerson(String date, int clientId){
        try(PreparedStatement statement = connection.prepareStatement(NUMBER_BOOKED_PER_PERSON)){
            statement.setString(1, date);
            statement.setInt(2, clientId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                return resultSet.getInt("COUNT(*)");
            }
        } catch (SQLException exception){
            System.out.println(exception.getMessage());
        }

        return 0;
    }


    private LinkedHashMap<String, Integer> getBookedTimeForDate(String date){
        LinkedHashMap<String, Integer> resultMap = new LinkedHashMap<>();

        WorkingTimeInterval interval = WorkingTimeInterval.createInterval(LocalDate.parse(date));
        LocalTime curTime = interval.getStartTime();
        while (curTime.isBefore(interval.getEndTime()) || curTime.equals(interval.getEndTime())){
            resultMap.put(curTime.toString(), 0);
            curTime = curTime.plusHours(1);
        }

        try (PreparedStatement statement = connection.prepareStatement(SELECT_QUERY)){
            statement.setString(1, date);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                String time = resultSet.getString("time_");
                int count = resultSet.getInt("Count reserved");
                resultMap.put(LocalTime.parse(time).toString(), count);
            }
        } catch (SQLException exception){
            System.out.println(exception.getMessage());
        }

        return resultMap;
    }
}
