package apps.codette.utils;

/**
 * Created by user on 23-02-2018.
 */

public class LoginTimer implements Runnable{

    SessionManager session;

    public LoginTimer(SessionManager session){
        this.session=session;
    }

    @Override
    public void run() {}
}
