package core.zs.sample;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import core.zs.mpermissions.MPermissions;
import core.zs.mpermissions.annotations.OnPermissionDenied;
import core.zs.mpermissions.annotations.OnPermissionGranted;
import core.zs.mpermissions.annotations.ShowRequestPermissionRationale;


public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_SDCARD_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.requestPermission).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!MPermissions.shouldShowRequestPermissionRationale(MainActivity.this
                        ,Manifest.permission
                        .WRITE_EXTERNAL_STORAGE,REQUEST_SDCARD_CODE)){
                    MPermissions.requestPermissions(MainActivity.this, REQUEST_SDCARD_CODE, Manifest.permission
                            .WRITE_EXTERNAL_STORAGE);
                }

            }
        });
    }

    @OnPermissionDenied(requestCode = REQUEST_SDCARD_CODE)
    public void onPermissionDenied() {
        Toast.makeText(this, "The permission is denied", Toast.LENGTH_LONG).show();
    }

    @OnPermissionGranted(requestCode = REQUEST_SDCARD_CODE)
    public void onPermissionGranted() {
        Toast.makeText(this, "The permission is granted", Toast.LENGTH_LONG).show();
    }

    @ShowRequestPermissionRationale(value = REQUEST_SDCARD_CODE)
    public void ShowRequestPermissionRationale() {
        Toast.makeText(this, "The ShowRequestPermissionRationale", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        MPermissions.onRequestPermissionsResult(MainActivity.this, requestCode, permissions,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
