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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sytycc.sytycc.app.data.Product;
import com.sytycc.sytycc.app.data.Transaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jeknocka on 29/03/14.
 */
public class AccessAPI{

    private String apikey = "93ub7fAkQmG170m90hdOxbmlSPA3MTHY";
    private String apiurl = "https://apisandbox.ingdirect.es";
    private Context context;
    private String username = "55663504P";
    private String birthday;
    private String sessionid = null;
    private boolean sessionrequested = false;
    private boolean productsloaded = false;

    private final static AccessAPI accessapi = new AccessAPI();

    private AccessAPI(){
        // Left empty
    }

    public static AccessAPI getInstance(){
        return accessapi;
    }

    public void init(Context ctxt, final SessionListener listener){
        if (!sessionrequested) {
            // Read settings
            context = ctxt;
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctxt);
            //username = sharedPref.getString("pref_key_username", null);
            birthday = sharedPref.getString("pref_key_birthday", null);
            requestTicket(listener);
        }
    }

    public void productsOk(){
        this.productsloaded = true;
    }

    private void requestTicket(final SessionListener listener){
        sessionrequested = true;
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
                sessionrequested = false;
                System.out.println("66:"+error);
            }
        });
        queue.add(jsObjRequest);
    }

    private void createSession(final SessionListener listener,final String ticket){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = getAPIURL("/openapi/login/auth/response");

        StringRequest strRequest = new StringRequest(Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Left empty
                sessionrequested = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("97: "+error);
                sessionrequested = false;
            }
        }){
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                Response<String> resp = super.parseNetworkResponse(response);
                String cookie = response.headers.get("Set-Cookie");
                String[] splitCookie = cookie.split(";");
                sessionid = splitCookie[0];
                //writeCookieToFile();
                listener.sessionReady();
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

    public void destroySession(){
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest strRequest = new StringRequest(Request.Method.PUT,getAPIURL("/openapi/rest/session"),new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Session destroyed");
                sessionid = null;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("97: "+error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return addSessionCookie(super.getHeaders());
            }
        };
        queue.add(strRequest);
    }

    public boolean isSessionReady(){
        return !sessionrequested;
    }

    private void getRequest(String requestpath, Map<String, String> arguments, final Response.Listener responselistener, boolean object){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = getAPIURL(requestpath);
        JSONObject req = null;
        if (arguments != null && !arguments.isEmpty()){
            req = new JSONObject(arguments);
        }
        Response.ErrorListener errlistener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("140: "+error);
            }
        };
        JsonRequest jsreq;
        if (object) {
            jsreq = new JsonObjectRequest(Request.Method.GET, url, req, responselistener, errlistener) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return addSessionCookie(super.getHeaders());
                }
            };
        }
        else{
            jsreq = new JsonArrayRequest(url, responselistener, errlistener) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return addSessionCookie(super.getHeaders());
                }
            };
        }
        queue.add(jsreq);
    }

    private Map addSessionCookie(Map<String, String> headers){
        // Add session cookie to header
        if (headers == null
                || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }
        else{
            System.out.println("headers");
        }
        headers.put("Cookie", sessionid);
        return headers;
    }

    /**
     * Create an URL for API Requests
     * @param request the request URL
     * @return the url
     */
    private String getAPIURL(String request){
        return apiurl+request+"?apikey="+apikey;
    }

    public void getProducts(final APIListener listener){
        getRequest("/openapi/rest/products", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<Product> products = new ArrayList<Product>();
                JSONArray productarray = response;
                for (int i = 0; i < productarray.length(); i++) {
                    try {
                        JSONObject productobj = productarray.getJSONObject(i);
                        //System.out.println(productobj.toString());
                        Product prod = new Product(productobj.getString("name"), productobj.getString("productNumber"),
                        -1, productobj.getString("iban"), productobj.getString("bic"),
                        productobj.getString("openingDate"), productobj.getInt("type"), -1,
                        productobj.getDouble("availableBalance"), productobj.getDouble("balance"), productobj.getString("uuid"));
                        if (productobj.has("subtype")){
                            prod.setSubtype(productobj.getInt("subtype"));
                        }
                        products.add(prod);
                    } catch (JSONException e) {
                        System.out.println("194: "+e);
                    }
                }
                listener.receiveAnswer(products);
            }
        },false);
    }

    public void getProductTransactions(final String uuid, final APIListener listener){

        getRequest("/openapi/rest/products/" + uuid + "/movements", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                List<Transaction> transactions = new ArrayList<Transaction>();
                try {
                    JSONArray producttransactions = response.getJSONArray("elements");
                    for (int i = 0; i < producttransactions.length(); i++) {
                        JSONObject transactionobj = producttransactions.getJSONObject(i);
                        //System.out.println(transactionobj);
                        try {
                            Date effectiveDate = format.parse(transactionobj.getString("effectiveDate"));
                            Date valDate = format.parse(transactionobj.getString("valDate"));
                            Transaction transaction = new Transaction(uuid,null, transactionobj.getString("description"),
                                    transactionobj.getString("typeDesc"), transactionobj.getDouble("amount"),
                                    effectiveDate, valDate);
                            if (transactionobj.has("bankName")){
                                transaction.setBankName(transactionobj.getString("bankName"));
                            }
                            transactions.add(transaction);
                        } catch (ParseException e) {
                            System.out.println("237: "+e);
                        }
                    }
                    listener.receiveAnswer(transactions);
                } catch (JSONException e) {
                    System.out.println("196: " + e);
                }
            }
        },true);
    }


}
