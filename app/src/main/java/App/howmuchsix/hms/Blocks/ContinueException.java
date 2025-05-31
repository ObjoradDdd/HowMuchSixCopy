package App.howmuchsix.hms.Blocks;

public class ContinueException extends ProgramRunException {
    public ContinueException(String id) {
        super("Continue block outside FOR loop", id);
    }
}
