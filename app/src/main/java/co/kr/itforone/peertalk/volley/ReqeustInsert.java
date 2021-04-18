package co.kr.itforone.peertalk.volley;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ReqeustInsert extends StringRequest {

    private static final String URL = "https://itforone.co.kr/~peertalk/bbs/upload_contacts.php";
    private Map<String, String> parameters = new HashMap();

    public ReqeustInsert(String names, String numbers, String id, Response.Listener<String> paramListener)
    {
        super(Request.Method.POST, URL, paramListener, null);
        this.parameters.put("names", names);
        this.parameters.put("numbers", numbers);
        this.parameters.put("mb_id", id);
    }
    @Override
    public Map<String, String> getParams()
    {
        return this.parameters;
    }

}
