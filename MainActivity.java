package com.nexora.app;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.view.ViewGroup;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private WebView webView;
    private ValueCallback<Uri[]> fileCallback;
    private static final int FILE_CHOOSER = 1001;
    private static final int PERMISSIONS = 1002;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        webView = new WebView(this);
        setContentView(webView, new ViewGroup.LayoutParams(-1, -1));
        requestNeededPermissions();
        configure();
        webView.loadUrl("file:///android_asset/index.html");
    }

    private void requestNeededPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= 33 &&
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSIONS);
        }
    }

    private void configure() {
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(true);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        s.setSupportMultipleWindows(false);

        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);

        webView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {
                Uri u = r.getUrl();
                String scheme = u.getScheme();
                if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) {
                    v.loadUrl(u.toString());
                    return true;
                }
                return false;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onShowFileChooser(WebView v, ValueCallback<Uri[]> cb, FileChooserParams params) {
                if (fileCallback != null) fileCallback.onReceiveValue(null);
                fileCallback = cb;

                Intent pick = new Intent(Intent.ACTION_GET_CONTENT);
                pick.addCategory(Intent.CATEGORY_OPENABLE);
                pick.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pick.setType("*/*");

                ArrayList<String> mimeTypes = new ArrayList<>();
                if (params != null && params.getAcceptTypes() != null) {
                    for (String type : params.getAcceptTypes()) {
                        if (type == null) continue;
                        for (String part : type.split(",")) {
                            String mime = part.trim();
                            if (!mime.isEmpty() && !mimeTypes.contains(mime)) mimeTypes.add(mime);
                        }
                    }
                }
                if (mimeTypes.size() == 1) {
                    pick.setType(mimeTypes.get(0));
                } else if (mimeTypes.size() > 1) {
                    pick.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes.toArray(new String[0]));
                }
                if (params != null && params.getMode() == FileChooserParams.MODE_OPEN_MULTIPLE) {
                    pick.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }

                try {
                    startActivityForResult(Intent.createChooser(pick, "اختيار ملف"), FILE_CHOOSER);
                } catch (Exception e) {
                    fileCallback = null;
                    cb.onReceiveValue(null);
                }
                return true;
            }
        });
    }

    @Override protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (req != FILE_CHOOSER || fileCallback == null) return;

        Uri[] result = null;
        if (res == RESULT_OK && data != null) {
            ClipData clip = data.getClipData();
            if (clip != null && clip.getItemCount() > 0) {
                result = new Uri[clip.getItemCount()];
                for (int n = 0; n < clip.getItemCount(); n++) {
                    result[n] = clip.getItemAt(n).getUri();
                }
            } else if (data.getData() != null) {
                result = new Uri[]{data.getData()};
            }
        }

        ValueCallback<Uri[]> cb = fileCallback;
        fileCallback = null;
        cb.onReceiveValue(result);
    }

    @Override public void onBackPressed() {
        if (webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    @Override protected void onDestroy() {
        if (fileCallback != null) {
            fileCallback.onReceiveValue(null);
            fileCallback = null;
        }
        if (webView != null) webView.destroy();
        super.onDestroy();
    }
}
