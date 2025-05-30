package App.howmuchsix.hms.Blocks;

public class BreakException extends ProgramRunException {
    public BreakException(String id) {
        super("Break block outside a loop", id);
    }
}
