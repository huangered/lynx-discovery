package com.yih.lynx.client;

import com.google.gson.Gson;
import com.yih.lynx.core.SvcDesc;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;

@Slf4j
public class LynxClientImpl implements LynxClient {

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    private final String url;
    private final int port;
    private final OkHttpClient client;
    private final SvcDesc svcDesc;
    private final Gson gson = new Gson();

    public LynxClientImpl(String url, int port, String svcName, String svcUrl, int svcPort, String svcHealthUrl) {
        this.url = url;
        this.port = port;
        this.svcDesc = new SvcDesc(svcName, svcUrl, svcPort, svcHealthUrl);
        this.client = new OkHttpClient();
    }

    @Override
    public boolean echo() {
        String path = String.format("%s:%s%s", url, port, "/");
        Request request = new Request.Builder()
                .url(path)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body().string().equals("echo")) {
                return true;
            }
        } catch (IOException ex) {
            log.error("Send echo fail", ex);
        }
        return false;
    }

    @Override
    public boolean register() {
        String path = String.format("%s:%s%s", url, port, "/register");

        RequestBody body = RequestBody.create(gson.toJson(svcDesc), JSON);
        Request request = new Request.Builder()
                .url(path)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body().string().equals("ok")) {
                return true;
            }
        } catch (IOException ex) {
            log.error("Register fail", ex);
        }
        return false;
    }

    @Override
    public boolean unregister() {
        String path = String.format("%s:%s%s", url, port, "/unregister");

        RequestBody body = RequestBody.create(gson.toJson(svcDesc), JSON);
        Request request = new Request.Builder()
                .url(path)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body().string().equals("ok")) {
                return true;
            }
        } catch (IOException ex) {
            log.error("Register fail", ex);
        }
        return false;
    }

    @Override
    public SvcDesc query(String name) {
        String host = String.format("%s:%s%s", url, port, "/query");

        String path = HttpUrl.parse(host)
                .newBuilder().addQueryParameter("svc", name).build().toString();

        Request request = new Request.Builder()
                .url(path)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                SvcDesc svcDesc = gson.fromJson(response.body().string(), SvcDesc.class);
                return svcDesc;
            }
        } catch (IOException ex) {
            log.error("Register fail", ex);
        }
        return null;
    }
}