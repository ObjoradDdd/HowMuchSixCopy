package App.howmuchsix.hms.Blocks;


import java.util.List;

public class Block {
    public String blockID;
    List<String> scopeNames = List.of("MainScope");

    public void Action(List<String> scopes)  throws ReturnException{
    }

    public void setScopes(List<String> scopes) {
        this.scopeNames = scopes;
    }

    public Object eval(List<String> scopes) {
        return null;
    }
}
