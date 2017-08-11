package com.safemooney.http;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.safemooney.http.models.Transaction;
import com.safemooney.http.models.UserPreview;

public class TransactionClient
{
    private Gson serializer;
    private static final String host = "http://safemooney.azurewebsites.net";
    private static final String charsetName = "utf-8";
    private static final String dateFormat = "yyyy-MM-dd'T'HH:mm:ss";
    private String tokenKey;
    private int userId;

    public TransactionClient(int userId ,String tokenKey)
    {
        if(userId < 0)
            throw new IllegalArgumentException("userId can't be negative");

        if(tokenKey == null)
            throw new IllegalArgumentException("tokenKey has NULL value");

        this.tokenKey = tokenKey;
        this.userId = userId;
        serializer = new Gson();
    }

    public List<UserPreview> getUserList()
    {
        try
        {
            String getUserListPath = host + "/api/" + userId + "/transactions/getuserlist";
            URL getUserListUrl = new URL(getUserListPath);
            HttpURLConnection urlConnection = (HttpURLConnection)getUserListUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.addRequestProperty("Accept-Charset", "utf-8");
            urlConnection.addRequestProperty("Content-Type", "application/json");
            urlConnection.addRequestProperty("Authorization", "Basic " + tokenKey);
            urlConnection.setDoInput(true);
            urlConnection.connect();

            BufferedInputStream bufferedStream = new BufferedInputStream(urlConnection.getInputStream());
            byte[] bytes = new byte[bufferedStream.available()];
            bufferedStream.read(bytes);
            bufferedStream.close();

            String json = new String(bytes, charsetName);
            bytes = null;
            List<LinkedTreeMap> userList = serializer.fromJson(json, List.class);

            List<UserPreview> userPreviews = new ArrayList<UserPreview>(userList.size());

            for(LinkedTreeMap u : userList)
            {
                UserPreview up = new UserPreview();
                up.setUserId(Integer.valueOf((String) u.get("UserId")));
                up.setFirstName((String)u.get("FirstName"));
                up.setLastName((String)u.get("LastName"));
                up.setUsername((String)u.get("Username"));
                userPreviews.add(up);
            }

            return userPreviews;
        }
        catch(IOException e)
        {
            return  null;
        }
    }

    public boolean addTransaction(Transaction transaction)
    {
        if(transaction == null)
            throw new IllegalAccessError("transaction has NULL value");

        try
        {
            String addTransactionPath = host + "/api/" + transaction.getUser1Id() + "/transactions/add";
            URL addTransactionUrl = new URL(addTransactionPath);
            HttpURLConnection urlConnection = (HttpURLConnection)addTransactionUrl.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.addRequestProperty("Accept-Charset", charsetName);
            urlConnection.addRequestProperty("Content-Type", "application/json");
            urlConnection.addRequestProperty("Authorization", "Basic " + tokenKey);
            urlConnection.setDoOutput(true);

            TransactionModel trans = TransactionModel.getTransaction(transaction);
            String json = serializer.toJson(trans);

            BufferedOutputStream outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
            outputStream.write(json.getBytes());
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

    public List<Transaction> checkQueue()
    {
        try
        {
            String checkQueuePath = host + "/api/" + userId + "/transactions/checkqueue";
            URL checkQueueUrl = new URL(checkQueuePath);
            HttpURLConnection urlConnection = (HttpURLConnection)checkQueueUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.addRequestProperty("Accept-Charset", "utf-8");
            urlConnection.addRequestProperty("Content-Type", "application/json");
            urlConnection.addRequestProperty("Authorization", "Basic " + tokenKey);
            urlConnection.setDoInput(true);
            urlConnection.connect();

            if(urlConnection.getResponseCode() != urlConnection.HTTP_OK)
                return null;

            BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();

            String json = new String(bytes, charsetName);
            List<LinkedTreeMap> transactionList = serializer.fromJson(json, List.class);

            List<Transaction> transactions = new ArrayList<Transaction>(transactionList.size());

            DateFormat df = new SimpleDateFormat(dateFormat);

            for(LinkedTreeMap m : transactionList)
            {
                Transaction tr = new Transaction();
                tr.setId(Integer.valueOf((String) m.get("Id")));
                tr.setUser1Id(userId);
                tr.setUser2Id(Integer.valueOf((String)m.get("User2Id")));
                tr.setCount((String)m.get("Count"));
                tr.setDate(df.parse((String) m.get("Date")));
                tr.setPeriod(Integer.valueOf((String)m.get("Period")));
                tr.setClosed(false);
                tr.setPermited(false);

                transactions.add(tr);
            }

            return transactions;
        }
        catch(IOException e)
        {
            return null;
        }
        catch(ParseException e)
        {
            return null;
        }
    }

    public boolean confirmTransaction(int transId)
    {
        if(transId < 0)
            throw new IllegalAccessError("transaction has negative value");

        try
        {
            String checkQueuePath = host + "/api/" + userId + "/transactions/confirm/" + transId;
            URL checkQueueUrl = new URL(checkQueuePath);
            HttpURLConnection urlConnection = (HttpURLConnection)checkQueueUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.addRequestProperty("Accept-Charset", charsetName);
            urlConnection.addRequestProperty("Content-Type", "application/json");
            urlConnection.addRequestProperty("Authorization", "Basic " + tokenKey);

            urlConnection.connect();

            if(urlConnection.getResponseCode() == urlConnection.HTTP_OK)
                return true;

            return false;
        }
        catch(IOException e)
        {
            return false;
        }
    }

    public boolean closeTransaction(int transactionId)
    {
        if(transactionId < 0)
            throw new IllegalArgumentException("transactionId can't be negative");
        try
        {
            String checkQueuePath = host + "/api/" + userId + "/transactions/close/" + transactionId;
            URL checkQueueUrl = new URL(checkQueuePath);
            HttpURLConnection urlConnection = (HttpURLConnection)checkQueueUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.addRequestProperty("Accept-Charset", charsetName);
            urlConnection.addRequestProperty("Content-Type", "application/json");
            urlConnection.addRequestProperty("Authorization", "Basic " + tokenKey);
            urlConnection.connect();

            if(urlConnection.getResponseCode() == urlConnection.HTTP_OK)
                return true;

            return false;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    public List<Transaction> fetchTransactions() throws MalformedURLException, IOException
    {
        try
        {
            String checkQueuePath = host + "/api/" + userId + "/transactions/fetch";
            URL checkQueueUrl = new URL(checkQueuePath);
            HttpURLConnection urlConnection = (HttpURLConnection)checkQueueUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.addRequestProperty("Accept-Charset", "utf-8");
            urlConnection.addRequestProperty("Content-Type", "application/json");
            urlConnection.addRequestProperty("Authorization", "Basic " + tokenKey);
            urlConnection.setDoInput(true);
            urlConnection.connect();

            if(urlConnection.getResponseCode() != urlConnection.HTTP_OK)
                return null;

            BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();
            String json = new String(bytes, charsetName);
            bytes = null;

            List<LinkedTreeMap> transactionList = serializer.fromJson(json, List.class);

            List<Transaction> transactions = new ArrayList<Transaction>(transactionList.size());

            DateFormat df = new SimpleDateFormat(dateFormat);

            for(LinkedTreeMap m : transactionList)
            {
                Transaction tr = new Transaction();
                tr.setId(Integer.valueOf((String)m.get("Id")));
                tr.setUser1Id(Integer.valueOf((String)m.get("User1Id")));
                tr.setUser2Id(Integer.valueOf((String)m.get("User2Id")));
                tr.setCount((String)m.get("Count"));
                tr.setDate(df.parse((String) m.get("Date")));
                tr.setPeriod(Integer.valueOf((String)m.get("Period")));
                tr.setClosed((Boolean) m.get("IsClosed"));
                tr.setPermited((Boolean)m.get("IsPermited"));

                transactions.add(tr);
            }

            return transactions;
        }
        catch(IOException e)
        {
            return null;
        }
        catch(ParseException e)
        {
            return null;
        }
    }




    private static class TransactionModel implements Serializable
    {
        public int transactionId;
        public int userId;
        public String count;
        public Date date;
        public int period;

        public static TransactionModel getTransaction(Transaction transaction)
        {
            TransactionModel trans = new TransactionModel();
            trans.transactionId = transaction.getId();
            trans.userId = transaction.getUser2Id();
            trans.count = transaction.getCount();
            trans.date = transaction.getDate();
            trans.period = transaction.getPeriod();

            return  trans;
        }

    }

}
