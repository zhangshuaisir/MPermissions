# MPermissions

基于Annotation Processor的简单易用的处理Android M运行时权限的库。

对于低于6.0版本的请求，直接回调申请成功方法。

使用编译时注解，解决运行时注解运行效率低的问题。



## 使用

* 申请权限

```java
MPermissions.requestPermissions(MainActivity.this, REQUECT_CODE_SDCARD, Manifest.permission.WRITE_EXTERNAL_STORAGE);
```

* 处理权限回调

```java
@Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
{
	MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
	super.onRequestPermissionsResult(requestCode, permissions, grantResults);
}
```

* 是否需要弹出解释

```
if (!MPermissions.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUECT_CODE_SDCARD))
{
    MPermissions.requestPermissions(MainActivity.this, REQUECT_CODE_SDCARD, Manifest.permission.WRITE_EXTERNAL_STORAGE);
}
```

如果需要解释，会自动执行使用`@ShowRequestPermissionRationale`注解的方法。

授权成功以及失败调用的分支方法通过注解`@OnPermissionGranted`和`@OnPermissionDenied`进行标识，详细参考下面的例子或者sample。

## 例子

* in Activity:

```java
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
```



