package cn.lingmar.itolker.frags.assist;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.lingmar.itolker.R;
import cn.lingmar.itolker.frags.media.GalleryFragment;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * A simple {@link Fragment} subclass.
 */
public class PermissionsFragment extends BottomSheetDialogFragment {


    public PermissionsFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // 直接复用
        return new GalleryFragment.TransStatusBottomSheetDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_permissions, container, false);
        refreshState(view);
        return view;
    }

    /**
     * 刷新布局中的图片的状态
     * @param view
     */
    private void refreshState(View view) {
        Context context = getContext();

        view.findViewById(R.id.im_state_permission_network)
                .setVisibility(haveNetword(context) ? View.VISIBLE : View.INVISIBLE);
        view.findViewById(R.id.im_state_permission_read)
                .setVisibility(haveReadPerm(context) ? View.VISIBLE : View.INVISIBLE);
        view.findViewById(R.id.im_state_permission_write)
                .setVisibility(haveWritePerm(context) ? View.VISIBLE : View.INVISIBLE);
        view.findViewById(R.id.im_state_permission_record_audio)
                .setVisibility(haveRecordAudioPerm  (context) ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * 获取是否有网络权限
     * @param context
     * @return
     */
    private static boolean haveNetword(Context context) {
        String[] perms = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
        };

        return EasyPermissions.hasPermissions(context, perms);
    }

    /**
     * 获取是否有文件读取权限
     * @param context
     * @return
     */
    private static boolean haveReadPerm(Context context) {
        String[] perms = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
        };

        return EasyPermissions.hasPermissions(context, perms);
    }

    /**
     * 获取是否有文件写入权限
     * @param context
     * @return
     */
    private static boolean haveWritePerm(Context context) {
        String[] perms = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
        };

        return EasyPermissions.hasPermissions(context, perms);
    }

    /**
     * 获取是否有录音权限
     * @param context
     * @return
     */
    private static boolean haveRecordAudioPerm(Context context) {
        String[] perms = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
        };

        return EasyPermissions.hasPermissions(context, perms);
    }

    /**
     * 私有的show方法
     * @param manager
     * @return
     */
    private static void show(FragmentManager manager) {
        // 调用BottomSheetDialogFragment以及准备好的显示方法
        new PermissionsFragment()
                .show(manager, PermissionsFragment.class
                .getName());
    }

    /**
     * 检查是否具有权限
     * @param context
     * @param manager
     * @return
     */
    public static boolean haveAll(Context context, FragmentManager manager) {
        // 检查是否具有所有的权限
        boolean haveAll = haveNetword(context)
                && haveReadPerm(context)
                && haveWritePerm(context)
                && haveRecordAudioPerm(context);

        if(!haveAll){
            show(manager);
        }

        return haveAll;
    }

    private void requestPerm() {

    }

}
