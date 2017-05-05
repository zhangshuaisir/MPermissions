package core.zs.mpermissions;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * 工具类。
 * Created by ZhangShuai on 2017/5/5.
 */
public final class MUtils {

    /**
     * 判断当前系统是否为Android 6.0及以上版本
     *
     * @return true ：Android 6.0及以上 false:Android 6.0以下
     */
    public static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }


    /**
     * 获取被拒绝权限
     *
     * @param activity
     *         待检查类
     * @param permission
     *         权限集合
     *
     * @return 被拒绝权限
     */
    @TargetApi(value = Build.VERSION_CODES.M)
    public static List<String> findDeniedPermissions(Activity activity, String... permission) {
        List<String> denyPermissions = new ArrayList<>();
        for (String value : permission) {
            if (activity.checkSelfPermission(value) != PackageManager.PERMISSION_GRANTED) {
                denyPermissions.add(value);
            }
        }
        return denyPermissions;
    }

    /**
     * 获取Activity实例。
     *
     * @param object
     *         Activity or Fragemnt
     *
     * @return Activity实例。
     */
    public static Activity getActivity(Object object) {
        if (object instanceof Fragment) {
            return ((Fragment) object).getActivity();
        }
        else if (object instanceof Activity) {
            return (Activity) object;
        }
        return null;
    }
}
