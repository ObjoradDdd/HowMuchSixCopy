package App.howmuchsix.hms.Blocks;


public class ProgramRunException extends RuntimeException {

    private final String id;
    public ProgramRunException(String message, String id) {
        super(message);
        this.id = id;
    }

    public String getID() {
        return this.id;
    }
}
