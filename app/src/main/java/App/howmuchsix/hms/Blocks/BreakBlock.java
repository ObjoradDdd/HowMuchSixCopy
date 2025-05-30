package App.howmuchsix.hms.Blocks;

import java.util.List;

import App.howmuchsix.hms.Library.Variables;

public class BreakBlock extends Block {
    @Override
    public void Action(List<String> scopes, Variables lib) {
        throw new BreakException(this.getUUID());
    }
}
