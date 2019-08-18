package utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.util.Log;

import com.terry.AudioFx.R;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by lenovo on 2018/3/12.
 */

public class PermissionChecker {
    private Activity mActivity;
    private String[] mPermissions;
    private PermissionCheckCallback mCallback;
    private static final String TAG = "PermissionChecker";
    private static final int REQUST_CODE = 0;
    private boolean isDefaultDialog = false;

    public PermissionChecker setActivity(Activity activity) {
        this.mActivity = activity;
        return this;
    }

    public PermissionChecker setPermissions(String[] permissions) {
        this.mPermissions = permissions;
        return this;
    }

    public PermissionChecker setCallback(PermissionCheckCallback callback) {
        this.mCallback = callback;
        return this;
    }

    public PermissionChecker setDefaultDialog(boolean isDefaultDialog) {
        this.isDefaultDialog = isDefaultDialog;
        return this;
    }

    public boolean isPermissionGrant(String permission) {
        return mActivity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void checkPermission() {

        if (mPermissions == null || mActivity == null || mPermissions == null)
            return;

        List<String> permissionToRequestList = new LinkedList<String>();
        for (String permission : mPermissions) {
            if(mActivity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                permissionToRequestList.add(permission);
        }
        String[] permissionToRequest = (String[])permissionToRequestList.toArray(new String[permissionToRequestList.size()]);
        if(permissionToRequest.length > 0){
            mActivity.requestPermissions(permissionToRequest, REQUST_CODE);
            if (mCallback != null)
                mCallback.onRequest();
        } else {
            if (mCallback != null) {
                mCallback.onGranted();
            }
        }
    }

    public interface PermissionCheckCallback {
        void onRequest();
        void onGranted();
        void onGrantSuccess();
        void onGrantFail();
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUST_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Grant permission successfully");
                    if (mCallback != null)
                        mCallback.onGrantSuccess();
                } else {
                    if (isDefaultDialog) {
                        popupWarningDialog();
                        return;
                    }
                    if (mCallback != null) {
                        mCallback.onGrantFail();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void popupWarningDialog(){

        DialogInterface.OnClickListener dialogOnclicListener=new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case Dialog.BUTTON_POSITIVE:
                        if (mCallback != null)
                            mCallback.onGranted();
                        break;
                    case Dialog.BUTTON_NEGATIVE:
                        mActivity.finish();
                        break;
                    default:
                        break;
                }
            }
        };

        AlertDialog.Builder builder=new AlertDialog.Builder(mActivity);
        builder.setTitle("Warning");
        builder.setMessage("PermissionNotGrant");
        builder.setPositiveButton("OK",dialogOnclicListener);
        builder.setNegativeButton("Cancel", dialogOnclicListener);
        builder.create().show();
    }
}
