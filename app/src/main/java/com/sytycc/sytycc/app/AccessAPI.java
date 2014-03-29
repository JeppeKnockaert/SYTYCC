package com.sytycc.sytycc.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jeknocka on 29/03/14.
 */
public class AccessAPI{

    private final String apikey;
    private final String apiurl;
    private final Context context;
    private final String username;
    private final String birthday;

    private String ticket;

    public AccessAPI(Context context){
        // Read settings
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        apikey = sharedPref.getString("pref_key_apikey",null);
        apiurl = sharedPref.getString("pref_key_apiurl",null);
        username = sharedPref.getString("pref_key_username",null);
        birthday = sharedPref.getString("pref_key_birthday",null);
        this.context = context;
    }

    private void requestTicket(){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = getAPIURL("/openlogin/rest/ticket");

        JSONObject req = new JSONObject();
        try {
            JSONObject logindoc = new JSONObject();
            logindoc.put("documentType","0");
            logindoc.put("document",username);
            req.put("loginDocument",logindoc);
            req.put("birthday",birthday);
        } catch (JSONException e) {
            System.out.println("50:"+e);
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, req, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    setTicket(response.getString("ticket"));
                } catch (JSONException e) {
                    System.out.println("60:"+e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("66:"+error);
            }
        });
        queue.add(jsObjRequest);
    }

    private void createSession(){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = getAPIURL("/openapi/login/auth/response");

        StringRequest strRequest = new StringRequest(Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("97: "+error);
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("ticket", ticket);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        queue.add(strRequest);
    }

    private void setTicket(String ticket){
        this.ticket = ticket;
        createSession();
    }

    /**
     * Create an URL for API Requests
     * @param request the request URL
     * @return the url
     */
    private String getAPIURL(String request){
        return apiurl+request+"?apikey="+apikey;
    }
}
