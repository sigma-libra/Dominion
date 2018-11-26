package shared.dto;

public class AuthResponseDTO {
    private String token;

    /**
     * empty constructor
     * needed to not run into json parsing errors
     */
    public AuthResponseDTO() {}

    /**
     * retrieve token
     * @return token
     */
    public String getToken() {
        return token;
    }

    /**
     *
     * @param token to be set
     */
    public void setToken(String token) {
        this.token = token;
    }


    /**
     * constructor
     * @param token
     */
    public AuthResponseDTO(String token) {
        setToken(token);
    }
}
