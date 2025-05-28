package App.howmuchsix.hms.Blocks;
public class ContinueException extends RuntimeException {
    public ContinueException() {
        super("Continue block outside FOR loop");
    }
}
