package com.breakingbyte.wrap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import com.breakingbyte.game.engine.EngineState;
import com.breakingbyte.game.resource.Resource.ResourceLevel;
import com.breakingbyte.wrap.shared.Renderer.GraphicsAPI;

public class Platform {
    
    public static String name = "Android";
    
    public static GraphicsAPI renderingAPI = GraphicsAPI.GLES1;
    
    public static boolean canGenerateTextTexture = true;
    
    public static int engineStartLevel = ResourceLevel.ENGINE;
    
    public static final void exitApplication() {
        EngineState.saveToStorage();
        Vibration.stop();
        //Disable analytics
        System.exit(0);
    }
    
    public static final long getNanoTime() {
        return System.nanoTime();
    }
    
    public static final double getMillisecondTime() {
        return System.nanoTime() / (double)1000000;
    }
    
    public static final String getDeviceModel() {
        return Build.MODEL.replaceAll("\\|", "-");
    }
    
    public static final boolean supportInAppPurchase() {
        return true;
    }
    
    public static final void garbageCollect() {
        System.gc();
    }
    
    private static ProgressDialog progressDialog;
    
    public static final void displayLoadingDialog(final boolean display) {
        BaseActivity.instance.runOnUiThread(new Runnable() {
            public void run() {
                if (!display) {
                    if (progressDialog != null) progressDialog.hide();
                    progressDialog = null;
                } else {
                    progressDialog = ProgressDialog.show(BaseActivity.instance, "", 
                            "Loading...", true);
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                }
            }
        });
    }
    
    public static final void toastNotify(final String message) {
        BaseActivity.instance.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(BaseActivity.instance, message, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    public static final void openURL(final String url) {
        BaseActivity.instance.runOnUiThread(new Runnable() {
            public void run() {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                BaseActivity.instance.startActivity(i);
            }
        });
    }
}
