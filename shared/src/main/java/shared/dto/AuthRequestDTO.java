package shared.dto;

public class AuthRequestDTO {

    private String username, password;

    /**
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username to be set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @param password to be set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * empty constructor to not run into json parsing errors
     */
    public AuthRequestDTO(){}


    /**
     * constructor
     * @param username
     * @param password
     */
    public AuthRequestDTO(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }
}
