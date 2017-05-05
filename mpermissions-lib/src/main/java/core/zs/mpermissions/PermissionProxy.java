package core.zs.mpermissions;

/**
 * Created by ZhangShuai on 2017/5/5.
 */
public interface PermissionProxy<T>
{
    void grant(T source, int requestCode);

    void denied(T source, int requestCode);

    void rationale(T source, int requestCode);

    boolean needShowRationale(int requestCode);
}
