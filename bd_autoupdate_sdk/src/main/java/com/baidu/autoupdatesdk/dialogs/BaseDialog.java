//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import com.baidu.autoupdatesdk.r.ID;
import com.baidu.autoupdatesdk.utils.DeviceUtils;

public abstract class BaseDialog extends Dialog implements OnClickListener {
    protected Context context;
    protected Handler mHandler;

    public BaseDialog(Context context) {
        super(context, ID.getStyle(context, "bdp_update_dialog_style"));
        this.context = context;
        this.mHandler = new Handler(context.getMainLooper());
    }

    public BaseDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
        this.mHandler = new Handler(context.getMainLooper());
    }

    protected abstract View onInflateView(LayoutInflater var1);

    protected void onInitView(View view) {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = this.onInflateView(inflater);
        this.setContentView(view);
        this.onInitView(view);
    }

    public void show() {
        super.show();
        this.onScreenOrientationChanged();
    }

    public void onScreenOrientationChanged() {
        int orientation = DeviceUtils.getScreenOrientation(this.context);
        int padding = DeviceUtils.dip2px(this.context, 18.0F);
        int width = 0;
        if (orientation == 1) {
            width = DeviceUtils.getScreenHeight(this.context) - padding * 2;
        } else if (orientation == 0) {
            width = DeviceUtils.getScreenWidth(this.context) - padding * 2;
        }

        if (this.getWindow() != null) {
            this.getWindow().setLayout(width, -2);
        }

    }

    public void onBackPressed() {
    }

    public void onClick(View v) {
    }

    protected void setClickable(View parent, int... ids) {
        for(int i = 0; i < ids.length; ++i) {
            View v = parent.findViewById(ids[i]);
            if (v != null) {
                v.setOnClickListener(this);
            }
        }

    }
}
