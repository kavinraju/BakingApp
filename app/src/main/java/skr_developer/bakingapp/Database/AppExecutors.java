package skr_developer.bakingapp.Database;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 *  This class is running background process, here Database quries.
 */
public class AppExecutors {

    private static final Object OBJECT = new Object();
    private static AppExecutors appExecutors;
    private final Executor diskIO;

    private AppExecutors(Executor diskIO){
        this.diskIO = diskIO;
    }

    public static AppExecutors getInstance(){
        if (appExecutors == null){
            synchronized (OBJECT){
                appExecutors = new AppExecutors(Executors.newSingleThreadExecutor());
            }
        }
        return appExecutors;
    }

    public Executor getDiskIO() {
        return diskIO;
    }

}
