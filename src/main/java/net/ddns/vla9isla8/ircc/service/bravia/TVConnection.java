package net.ddns.vla9isla8.ircc.service.bravia;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


/**
 * Created by Vladk on 26.11.2016.
 */
class TVConnection {

    private RemoteService.Path path;
    private String IP = "127.0.0.1";

    public RemoteService.Path getPath() {
        return path;
    }

    public void setPath(RemoteService.Path path) {
        this.path = path;
    }

    TVConnection(String IP) {
        this(IP, RemoteService.Path.SONY);
    }

    TVConnection(String IP, RemoteService.Path path) {
        this.IP = IP;
        this.path = path;
    }

    private HttpURLConnection urlConnection;

    private HttpURLConnection buildConnection(String url, Map<String, String> headers) throws IOException {
        closeConnection();
        urlConnection = (HttpURLConnection) new URL("http://" + IP + path.getPath() + url).openConnection();
        urlConnection.setUseCaches(true);
        urlConnection.setDoInput(true); // Triggers POST.
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("POST");
        for(Map.Entry<String,String> entry: headers.entrySet() ){
            urlConnection.setRequestProperty(entry.getKey(),entry.getValue());
        }

        return urlConnection;
    }

    InputStream makeRequest(String url, Map<String,String> headers, String contentType, String data) throws IOException {
        headers.put("Content-Type", contentType);
        headers.put("Content-Length", Integer.toString(data.getBytes().length));
        HttpURLConnection connection = buildConnection(url,headers);
        DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
        writer.writeBytes(data);
        writer.flush();
        writer.close();
        return connection.getInputStream();
    }

    void closeConnection(){
        if(urlConnection!=null){
            urlConnection.disconnect();
        }
    }
}
