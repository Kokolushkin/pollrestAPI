package alexander.rest.services;

import alexander.rest.util.ConnectionHelper;
import alexander.rest.util.Validator;
import alexander.rest.model.Client;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;

import java.sql.*;

@Service
public class ClientDAO {
    private static final String UPDATE_QUERY = "UPDATE clients SET name=?, phone=?, email=? WHERE id=?";
    private static final String INSERT_QUERY = "INSERT INTO clients (name, phone, email) VALUES (?, ?, ?)";
    private static final String SELECT_QUERY = "SELECT * FROM clients WHERE id=?";

    private static Connection connection;

    public ClientDAO(){
        connection = ConnectionHelper.getConnection();
    }

    public JsonArray getClients(){
        JsonArray result = new JsonArray();

        try {
            Statement statement = connection.createStatement();
            String SQL = "SELECT id, name FROM clients";
            ResultSet resultSet = statement.executeQuery(SQL);

            while (resultSet.next()) {
                JsonObject object = new JsonObject();

                object.addProperty("id", resultSet.getInt("id"));
                object.addProperty("name", resultSet.getString("name"));

                result.add(object);
            }
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }

        return result;
    }

    public JsonObject getClient(Number id) {
        JsonObject result = new JsonObject();

        try (PreparedStatement statement = connection.prepareStatement(SELECT_QUERY)){
            statement.setInt(1, id.intValue());

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                result.addProperty("id", resultSet.getInt("id"));
                result.addProperty("name", resultSet.getString("name"));
                result.addProperty("phone", resultSet.getString("phone"));
                result.addProperty("email", resultSet.getString("email"));
            }
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
        return result;
    }

    public void add(Client client){
        try (PreparedStatement statement = connection.prepareStatement(INSERT_QUERY)){
            validateClientData(client.getPhone(), client.getEmail());

            statement.setString(1, client.getName());
            statement.setString(2, client.getPhone());
            statement.setString(3, client.getEmail());
            statement.executeUpdate();
        } catch (SQLException | IllegalArgumentException  exception) {
            System.out.println(exception.getMessage());
        }
    }

    public void update(Client client) {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {
            validateClientData(client.getPhone(), client.getEmail());

            statement.setString(1, client.getName());
            statement.setString(2, client.getPhone());
            statement.setString(3, client.getEmail());
            statement.setInt(4, client.getId());
            statement.executeUpdate();
        } catch (SQLException | IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void validateClientData(String phone, String email) {
        Validator.validateTelephoneNumber(phone);
        Validator.validateEmail(email);
    }
}
