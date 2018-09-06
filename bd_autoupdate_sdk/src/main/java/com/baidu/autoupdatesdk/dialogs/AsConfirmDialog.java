//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.autoupdatesdk.analytics.Tag;
import com.baidu.autoupdatesdk.analytics.TagRecorder;
import com.baidu.autoupdatesdk.r.ID;
import com.baidu.autoupdatesdk.utils.DeviceUtils;

public class AsConfirmDialog extends BaseDialog {
    private Button btnInstall;
    private TextView txtCancel;
    private View.OnClickListener installListener;

    public AsConfirmDialog(Context context) {
        super(context);
    }

    protected View onInflateView(LayoutInflater inflater) {
        View v = inflater.inflate(ID.getLayout(this.context, "bdp_update_dialog_confirm_as"), (ViewGroup)null);
        this.btnInstall = (Button)v.findViewById(ID.getId(this.context, "btnInstall"));
        this.txtCancel = (TextView)v.findViewById(ID.getId(this.context, "txtCancel"));
        return v;
    }

    protected void onInitView(View view) {
        super.onInitView(view);
        this.btnInstall.setOnClickListener(this);
        this.txtCancel.setOnClickListener(this);
    }

    public void onClick(View v) {
        super.onClick(v);
        if (v == this.btnInstall) {
            if (this.installListener != null) {
                this.installListener.onClick(v);
            }

            this.dismiss();
        } else if (v == this.txtCancel) {
            TagRecorder.onTag(this.context, Tag.newInstance(13));
            this.dismiss();
        }

    }

    public void onScreenOrientationChanged() {
        int orientation = DeviceUtils.getScreenOrientation(this.context);
        int padding = DeviceUtils.dip2px(this.context, 11.0F);
        int width = 0;
        if (orientation == 1) {
            width = DeviceUtils.getScreenHeight(this.context) - padding * 2;
            width = (int)((float)width * 1.15F);
        } else if (orientation == 0) {
            width = DeviceUtils.getScreenWidth(this.context) - padding * 2;
        }

        if (this.getWindow() != null) {
            this.getWindow().setLayout(width, -2);
        }

    }

    public void setBtnInstallListener(View.OnClickListener listener) {
        this.installListener = listener;
    }
}
