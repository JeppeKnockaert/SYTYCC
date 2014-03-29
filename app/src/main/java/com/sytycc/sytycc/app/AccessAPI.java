package com.sytycc.sytycc.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sytycc.sytycc.app.data.Transaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jeknocka on 29/03/14.
 */
public class AccessAPI{

    private static String apikey;
    private static String apiurl;
    private static Context context;
    private static String username;
    private static String birthday;

    private static AccessAPI accessapi;

    private AccessAPI(){
        // Left empty
    }

    public static AccessAPI getInstance(){
        return accessapi;
    }

    public static void init(Context ctxt, final SessionListener listener){
        // Read settings
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        apikey = sharedPref.getString("pref_key_apikey",null);
        apiurl = sharedPref.getString("pref_key_apiurl",null);
        username = sharedPref.getString("pref_key_username",null);
        birthday = sharedPref.getString("pref_key_birthday",null);
        context = ctxt;

        requestTicket(listener);
    }

    private static void requestTicket(final SessionListener listener){
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
                    createSession(listener, response.getString("ticket"));
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

    private static void createSession(final SessionListener listener,final String ticket){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = getAPIURL("/openapi/login/auth/response");

        StringRequest strRequest = new StringRequest(Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Left empty
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("97: "+error);
            }
        }){
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                Response<String> resp = super.parseNetworkResponse(response);
                String cookie = response.headers.get("Set-Cookie");
                String[] splitCookie = cookie.split(";");
                listener.sessionReady(splitCookie[0]);
                return resp;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("ticket", ticket);
                return params;
            }

        };

        queue.add(strRequest);
    }

    private void getRequest(String requestpath, Map<String, String> arguments, final Response.Listener<JSONObject> responselistener, final String sessionid){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = getAPIURL(requestpath);
        JSONObject req = null;
        if (arguments != null && !arguments.isEmpty()){
            req = new JSONObject(arguments);
        }
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, req, responselistener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("66:"+error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Add session cookie to header
                Map<String, String> headers = super.getHeaders();
                if (headers == null
                        || headers.equals(Collections.emptyMap())) {
                    headers = new HashMap<String, String>();
                }
                headers.put("Cookie", sessionid);
                return headers;
            }
        };
        queue.add(jsObjRequest);
    }

    /**
     * Create an URL for API Requests
     * @param request the request URL
     * @return the url
     */
    private static String getAPIURL(String request){
        return apiurl+request+"?apikey="+apikey;
    }

    public void getTimeLine(String offset, String limit, String fromDate, String toDate, final APIListener listener, final String sessionid){
        Map<String, String> arguments = new HashMap<String, String>();
        if (offset != null && !offset.isEmpty()){
            arguments.put("offset",offset);
        }
        if (limit != null && !limit.isEmpty()){
            arguments.put("limit",limit);
        }
        if (fromDate != null && !fromDate.isEmpty()){
            arguments.put("fromDate",fromDate);
        }
        if (toDate != null && !toDate.isEmpty()){
            arguments.put("toDate",toDate);
        }
        getRequest("/openapi/rest/timeline",arguments,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                List<Transaction> transactions = new ArrayList<Transaction>();
                try {
                    JSONArray transactionarray = response.getJSONArray("elements");
                    for (int i = 0; i < transactionarray.length(); i++) {
                        JSONObject transactionobj = transactionarray.getJSONObject(i);
                        Transaction transaction = new Transaction(transactionobj.getString("description"),
                                transactionobj.getDouble("amount"),transactionobj.getString("effectiveDate"),
                                transactionobj.getString("accountFrom"),transactionobj.getString("accountTo"),
                                transactionobj.getString("tranCode"),transactionobj.getString("typeCod"),
                                transactionobj.getString("movementType").charAt(0));
                        transactions.add(transaction);
                    }
                    listener.receiveAnswer(transactions);
                } catch (JSONException e) {
                    System.out.println("196: "+e);
                }
            }
        },sessionid);
    }


}
