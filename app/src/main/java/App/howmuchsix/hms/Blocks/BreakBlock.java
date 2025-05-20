package App.howmuchsix.hms.Blocks;

import java.util.List;

public class BreakBlock extends Block{
    @Override
    public void Action(List<String> scopes){
        throw new BreakException("break");
    }
}
