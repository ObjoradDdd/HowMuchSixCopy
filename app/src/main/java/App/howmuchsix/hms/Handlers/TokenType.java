package App.howmuchsix.hms.Handlers;

public enum TokenType {
    INT,
    DOUBLE,
    STRING,
    STAR, // *
    PLUS, // +
    MINUS, // -
    SLASH, // /
    REMAINDER, // %
    OPEN_PAREN, // (
    CLOSE_PAREN, // )
    POWER, // ^
    WORD,
    EQ, // =
    MORE, // >
    MORE_EQ, // >=
    LESS, // <
    LESS_EQ, // <=
    NOT_EQ, // !=
    AND,       // &&
    OR,        // ||
    NOT,       // !
    MATH,
    STR,
    TRUE,
    FUNCTION,
    FALSE,
    NULL,
    ARRAY,
    EOF,
    INTERPOLATION_START,
    INTERPOLATION_END,
    TAG,
    TEXT
}
