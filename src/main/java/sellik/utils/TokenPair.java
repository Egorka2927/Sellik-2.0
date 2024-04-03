package sellik.utils;

public class TokenPair {
    private String jwtToken;
    private String jwtRefreshToken;

    public TokenPair(String jwtToken, String jwtRefreshToken) {
        this.jwtToken = jwtToken;
        this.jwtRefreshToken = jwtRefreshToken;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getJwtRefreshToken() {
        return jwtRefreshToken;
    }

    public void setJwtRefreshToken(String jwtRefreshToken) {
        this.jwtRefreshToken = jwtRefreshToken;
    }
}
