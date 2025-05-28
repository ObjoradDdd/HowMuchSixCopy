package App.howmuchsix.hms.Blocks;

public class BreakException extends RuntimeException {
    public BreakException() {
        super("Break block outside a loop");
    }
}
