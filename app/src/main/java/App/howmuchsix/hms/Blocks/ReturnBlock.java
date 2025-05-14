package App.howmuchsix.hms.Blocks;

public class ReturnBlock extends Block{

    private final String value;
    public ReturnBlock(String value){
        this.blockID = "return_block";
        this.value = value;
    }

    public String eval(){
        return value;
    }
}
