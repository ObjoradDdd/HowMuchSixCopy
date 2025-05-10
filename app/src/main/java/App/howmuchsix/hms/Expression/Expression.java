package App.howmuchsix.hms.Expression;

import App.howmuchsix.hms.Blocks.Types;

public interface Expression<T> {
    T eval();
    Types getType();
}
