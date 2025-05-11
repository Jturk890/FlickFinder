package flickfinder.Model;

import flickfinder.DAO.UserDAO;

import org.springframework.stereotype.Component;

@Component
public class LoginModel {
    private final UserDAO userDao;

    public LoginModel(UserDAO userDao){
        this.userDao = userDao;
    }

    //Calls the authenticate SQL statement using the login DAO 
    public boolean authenticate(String username, String password){
        return userDao.authenticate(username, password);
    }

    //Calls the insert statement and inserts user data if username is unique 
    public boolean registerUser(String username, String password){
        return userDao.registerUser(username, password);
    }

    //Calls select statement to get the user's id that logged in 
    public int getUserId(String username){
        return userDao.getUserId(username);
    }
}
