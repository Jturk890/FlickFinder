package flickfinder.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAO{
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDAO(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    //SQL Select statement to check if the user details entered are in the database
    public boolean authenticate(String username, String password){
        String sql = "SELECT COUNT(*) FROM logins WHERE username = ? AND password = ?";
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username, password);
            return count != null && count > 0;
        } catch (DataAccessException e){
            return false;
        }
    }

    //SQL Insert statement that inserts user into database if username isn't already in the database
    public boolean registerUser(String username, String password){
        String sql = "INSERT INTO logins (username, password) VALUES (?,?)";
        try {
            int rowsAffected = jdbcTemplate.update(sql, username, password);
            return rowsAffected > 0;
        } catch (DataAccessException e) {
            return false;
        }
    }

    public int getUserId(String username){
        String sql = "SELECT idlogins FROM logins WHERE username = ?";
        try {
            int id = jdbcTemplate.queryForObject(sql, Integer.class, username);
            return id;
        } catch (DataAccessException e){
            return -1;
        }
    }
}
