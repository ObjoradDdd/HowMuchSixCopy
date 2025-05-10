package App.howmuchsix.hms.Handlers;

public final class Token {

    private TokenType type;
    private final String text;

    public Token(TokenType type, String text) {
        this.type = type;
        this.text = text;
    }

    public TokenType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public void setType(TokenType type) {
        this.type = type;
    }
}
