package com.ftkj.server.http;

import java.net.HttpURLConnection;
import java.net.URL;

public class GameHttpServer {
public static void main(String[] args) throws Exception{
    URL url = new URL("http://192.168.12.97:8038");
    HttpURLConnection http = (HttpURLConnection)url.openConnection();
    http.connect();
    http.addRequestProperty("name", 123+"");
    http.getInputStream();
}
}
