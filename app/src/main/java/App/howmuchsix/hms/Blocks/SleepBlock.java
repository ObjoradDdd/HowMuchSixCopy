package App.howmuchsix.hms.Blocks;

import java.util.List;

import App.howmuchsix.hms.Library.Variables;

public class SleepBlock extends Block {
    String timeInMili;

    public SleepBlock(String timeInMili) {
        this.timeInMili = timeInMili;
    }

    @Override
    public void Action(List<String> scopes, Variables lib) {
        int timeExpression = (int) Types.INT.getValue(timeInMili, scopes, lib).eval();
        try {
            Thread.sleep(timeExpression);
        } catch (Exception ignored) {
            throw new RuntimeException("Sleep block error");
        }
    }
}
