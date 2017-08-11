package com.safemooney.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.Gson;
import com.safemooney.http.models.User;

public class AccountClient
{
    private Gson serializer;
    private static final String host = "http://safemooney.azurewebsites.net";
    private static final String charsetName = "utf-8";

    public AccountClient()
    {
        serializer = new Gson();
    }

    public User logIn(String username, String password)
    {
        if(username == null || password == null)
            throw new IllegalArgumentException("One of arguments has NULL value");

        try
        {
            String logInPath = host + "/api/account/login";
            URL logInUrl = new URL(logInPath);
            HttpURLConnection urlConnection = (HttpURLConnection) logInUrl.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.addRequestProperty("Accept-Charset", charsetName);
            urlConnection.addRequestProperty("Content-Type", "application/json");

            String json = serializer.toJson(new UserCredential(username, password));
            BufferedOutputStream buf = new BufferedOutputStream(urlConnection.getOutputStream());
            buf.write(json.getBytes());
            buf.close();

            urlConnection.connect();

            if(urlConnection.getResponseCode() == urlConnection.HTTP_OK)
            {
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
                inputStream.close();

                String resultJson = new String(bytes, charsetName);
                TokenResponse tokenResponse = serializer.fromJson(resultJson, TokenResponse.class);

                if(tokenResponse == null)
                    return null;
                //building user with parametrs
                User result = new User();
                result.setId(tokenResponse.UserId);
                result.setTokenkey(tokenResponse.Access_Token);
                result.setUsername(tokenResponse.Username);
                result.setFirstname(tokenResponse.FirstName);
                result.setLastname(tokenResponse.LastName);
                result.setPassword("empty");

                return result;
            }
            else
            {
                return null;
            }
        }
        catch(IOException e)
        {
            return null;
        }
    }

    public boolean logOut(int userId, String tokenkey)
    {
        if(userId < 0)
            throw new IllegalArgumentException("userId can't be negative");

        if(tokenkey == null)
            throw new IllegalArgumentException("One of arguments has NULL value");

        try
        {
            String logOutPath = host + "/api/" + userId + "/account/logout";
            URL logOutUrl = new URL(logOutPath);
            HttpURLConnection urlConnection = (HttpURLConnection) logOutUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.addRequestProperty("Accept-Charset", "utf-8");
            urlConnection.addRequestProperty("Content-Type", "application/json");
            urlConnection.addRequestProperty("Authorization", "Basic " + tokenkey);
            urlConnection.connect();

            if(urlConnection.getResponseCode() == urlConnection.HTTP_OK)
            {
                return true;
            }

            return false;
        }
        catch(IOException e)
        {
            return false;
        }
    }

    public boolean signUp(User user)
    {
        if(user == null)
            throw new IllegalArgumentException("user has NULL value");

        try
        {
            String signUpPath = host+ "/api/account/signup";
            URL signUpUrl = new URL(signUpPath);
            HttpURLConnection urlConnection = (HttpURLConnection) signUpUrl.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.addRequestProperty("Accept-Charset", "utf-8");
            urlConnection.addRequestProperty("Content-Type", "application/json");

            UserRegistration userRegistration = new UserRegistration();
            userRegistration.firstname = user.getFirstname();
            userRegistration.lastname = user.getLastname();
            userRegistration.username = user.getUsername();
            userRegistration.password = user.getPassword();

            String json = serializer.toJson(userRegistration);

            BufferedOutputStream buf = new BufferedOutputStream(urlConnection.getOutputStream());
            buf.write(json.getBytes());
            buf.close();

            urlConnection.connect();

            if(urlConnection.getResponseCode() == urlConnection.HTTP_OK)
            {
                return true;
            }

            return false;
        }
        catch(IOException e)
        {
            return false;
        }
    }

    public boolean change(User user)
    {
        if(user == null)
            throw new IllegalArgumentException("user has NULL value");

        try
        {
            String changePath = host + "/api/" + user.getId() + "/account/change";
            URL changeUrl = new URL(changePath);
            HttpURLConnection urlConnection = (HttpURLConnection)changeUrl.openConnection();

            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.addRequestProperty("Accept-Charset", "utf-8");
            urlConnection.addRequestProperty("Content-Type", "application/json");
            urlConnection.addRequestProperty("Authorization", "Basic " + user.getTokenkey());

            UserChange userChange = new UserChange();
            userChange.UserId = user.getId();
            userChange.FirstName = user.getFirstname();
            userChange.Username = user.getUsername();
            userChange.LastName = user.getLastname();
            userChange.Password = user.getPassword();

            BufferedOutputStream outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
            outputStream.write(serializer.toJson(userChange, userChange.getClass()).getBytes(charsetName));
            outputStream.close();

            urlConnection.connect();

            if(urlConnection.getResponseCode() == urlConnection.HTTP_OK)
            {
                return true;
            }

            return false;
        }
        catch(IOException e)
        {
            return false;
        }
    }



    private static class UserCredential implements Serializable
    {

        public String username;
        public String password;

        public UserCredential(String username, String password){
            this.username = username;
            this.password = password;
        }
    }

    private static class TokenResponse implements Serializable
    {
        public int UserId;
        public String Username;
        public String FirstName;
        public String LastName;
        public String Access_Token;
    }

    private static class UserRegistration implements Serializable
    {
        public String username;
        public String password;
        public String firstname;
        public String lastname;
    }

    private static class UserChange implements Serializable
    {
        public int UserId;
        public String Username;
        public String Password;
        public String FirstName;
        public String LastName;
    }
}
