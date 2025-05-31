package App.howmuchsix.hms.Blocks;


import java.util.List;

import App.howmuchsix.hms.Library.Variables;

public class Block {
    public String blockID;
    List<String> scopeNames = List.of("MainScope");
    private String id = "";

    public String getUUID() {
        return id;
    }

    public void setUUID(String id) {
        this.id = id;
    }

    public void Action(List<String> scopes, Variables lib) throws ReturnException, BreakException {
    }

    public void setScopes(List<String> scopes) {
        this.scopeNames = scopes;
    }

    public Object eval(List<String> scopes, Variables lib) throws ReturnException, BreakException {
        return null;
    }
}
