package diff;

public  class DiffFactory {
    public static Diff getDiffObject(String type){
        Diff obj= null;
        if("working".equals(type)){
            obj = new WorkingDiff();
        }
        else if ("commit".equals(type)){
            obj = new CommitDiff();
        }
        else if( "stage".equals(type)){
            obj = new  StageDiff();
        }
        return obj;
    }
}
