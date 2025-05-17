package App.howmuchsix.hms.Handlers;

import static java.util.Collections.emptyList;

import java.util.List;

public final class Token {

    private TokenType type;
    private final String text;

    private String body = null;
    private List<String> arguments = emptyList();

    public Token(TokenType type, String text, String body) {
        this.type = type;
        this.text = text;
        this.body = body;
    }
    public Token(TokenType type, String text) {
        this.type = type;
        this.text = text;
    }

    public Token(TokenType type, String text, List<String> arguments) {
        this.type = type;
        this.text = text;
        this.arguments = arguments;
    }

    public TokenType getType() {
        return type;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public String getBody(){
        return body;
    }

    public String getText() {
        return text;
    }

    public void setType(TokenType type) {
        this.type = type;
    }
}
