package com.savaari.savaari_rider.services.network;

import android.text.TextUtils;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.savaari.savaari_rider.ride.entity.Rider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

// This class holds static functions for interacting with the API Layer
public class NetworkUtil
{
    // Main Attributes
    private static NetworkUtil instance = null;
    private static final String TAG = NetworkUtil.class.getSimpleName();

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Cookie Management Variables
    private  final String COOKIES_HEADER = "Set-Cookie";
    private  final java.net.CookieManager msCookieManager = new java.net.CookieManager();
    private Map<String, List<String>> headerFields;
    private List<String> cookiesHeader;

    // Private Constructor
    private NetworkUtil()
    {
        // Empty
    }

    public synchronized static NetworkUtil getInstance() {
        if (instance == null) {
            instance = new NetworkUtil();
        }
        return instance;
    }

    // -------------------------------------------------------------------------------
    //                                 Main Methods
    // -------------------------------------------------------------------------------

    // Sending POST Requests
    private String sendPost(String urlAddress, JSONObject jsonParam, boolean needResponse) {
        try
        {
            // Creating the HTTP Connection
            URL url = new URL(urlAddress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept","application/json");

            /*
            * Add cookies to request header
            * While joining the Cookies, use ',' or ';' as needed. Most of the servers are using ';'
            * */
            if (msCookieManager.getCookieStore().getCookies().size() > 0) {
                conn.setRequestProperty("Cookie",
                        TextUtils.join(";",  msCookieManager.getCookieStore().getCookies()));
            }
            conn.setDoOutput(true);
            conn.setDoInput(true);

            // Sending the Data and Receiving Output
            Log.i(TAG, "sendPost: " + jsonParam.toString());
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(jsonParam.toString());

            // Flushing output streams
            os.flush();
            os.close();

            Log.i(TAG, "sendPost: Status: " + conn.getResponseCode());
            Log.i(TAG, "sendPost: Response Message: " + conn.getResponseMessage());

            // Save cookie
            headerFields = conn.getHeaderFields();
            cookiesHeader = headerFields.get(COOKIES_HEADER);

            if (cookiesHeader != null) {
                for (String cookie : cookiesHeader) {
                    msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                }
            }

            // Sending the Response Back to the User in JSON
            if (needResponse)
            {
                Scanner scanner;
                try
                {
                    scanner = new Scanner(conn.getInputStream());
                    String response = null;

                    if (scanner.hasNext()) {
                        response = scanner.useDelimiter("\\Z").next();
                        Log.d(TAG, "sendPost: " + response);
                    }
                    else {
                        Log.d(TAG, "sendPost: received null Input Stream");
                    }
                    scanner.close();
                    conn.disconnect();
                    return response;
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            return null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /*
    *  END OF RIDER-SIDE MATCHMAKING FUNCTIONS -----------------------------------------------------
    */

    // Sign-Up
    public boolean signup(String urlAddress, String username, String emailAddress, String password)
    {
        try {
            String url = urlAddress + "add_rider";
            Log.d("NetworkUtil: ", "signup() called");
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("username", username);
            jsonParam.put("email_address", emailAddress);
            jsonParam.put("password", password);

            String resultString = sendPost(url, jsonParam, true);
            return ((resultString != null) && (new JSONObject(resultString).getInt("STATUS_CODE") == 200));
        }
        catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Login
    public int login(String urlAddress, String username, String password)
    {
        String url = urlAddress + "login_rider";
        try
        {
            // Creating the JSON Object
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("username", username);
            jsonParam.put("password", password);

            // Sending Request
            String resultString = sendPost(url, jsonParam, true);
            return ((resultString == null)? -1 : new JSONObject(resultString).getInt("USER_ID"));

        } catch (Exception e)
        {
            e.printStackTrace();
            return -1;
        }
    }

    // Login with credentials
    public boolean persistLogin(String urlAddress, Integer userID) {

        try {
            String url = urlAddress + "persistRiderLogin";
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("USER_ID" ,userID);
            String resultString = sendPost(url, jsonParam, true);
            return ((resultString != null) && new JSONObject(resultString).getInt("STATUS_CODE") == 200);
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, " :persistLogin - Exception");
            return false;
        }
    }

    public boolean logout(String urlAddress, Integer userID) {
        try{
            String url = urlAddress + "logoutRider";
            String resultString = sendPost(url, new JSONObject(), true);
            return ((resultString != null) && new JSONObject(resultString).getInt("STATUS_CODE") == 200);
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, " :logout - Exception");
            return false;
        }
    }
    // Loading User Data
    public Rider loadUserData(String urlAddress, int currentUserID)
    {
        String url = urlAddress + "rider_data";
        JSONObject jsonParam = new JSONObject();
        try
        {
            jsonParam.put("USER_ID", currentUserID);
            String resultString = sendPost(url, jsonParam, true);
            return ((resultString == null)? null : objectMapper.readValue(resultString, Rider.class));
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /* End of section */
}
