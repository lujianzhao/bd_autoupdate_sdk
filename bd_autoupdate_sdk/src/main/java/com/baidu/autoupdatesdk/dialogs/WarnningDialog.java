//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.dialogs;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.baidu.autoupdatesdk.r.ID;
import com.baidu.autoupdatesdk.utils.DeviceUtils;

public class WarnningDialog extends BaseDialog {
    private TextView txt_content;
    private Button btn_a;
    private Button btn_b;
    private View.OnClickListener listenerA;
    private View.OnClickListener listenerB;
    private String btnTextA;
    private String btnTextB;
    private WarnningDialog.ButtonType buttonTypeA;
    private WarnningDialog.ButtonType buttonTypeB;
    private String content;

    public WarnningDialog(Context context) {
        super(context);
        this.buttonTypeA = WarnningDialog.ButtonType.defualt;
        this.buttonTypeB = WarnningDialog.ButtonType.defualt;
    }

    protected View onInflateView(LayoutInflater inflater) {
        View v = inflater.inflate(ID.getLayout(this.context, "bdp_update_dialog_warnning"), (ViewGroup)null);
        this.txt_content = (TextView)v.findViewById(ID.getId(this.context, "txt_content"));
        this.btn_a = (Button)v.findViewById(ID.getId(this.context, "btn_a"));
        this.btn_b = (Button)v.findViewById(ID.getId(this.context, "btn_b"));
        if (!TextUtils.isEmpty(this.content)) {
            this.txt_content.setText(this.content);
        }

        if (!TextUtils.isEmpty(this.btnTextA)) {
            this.btn_a.setText(this.btnTextA);
            this.btn_a.setOnClickListener(this);
            this.btn_a.setVisibility(View.VISIBLE);
            this.setButtonType(this.buttonTypeA, this.btn_a);
        }

        if (!TextUtils.isEmpty(this.btnTextB)) {
            this.btn_b.setText(this.btnTextB);
            this.btn_b.setOnClickListener(this);
            this.btn_b.setVisibility(View.VISIBLE);
            this.setButtonType(this.buttonTypeB, this.btn_b);
        }

        return v;
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

    public WarnningDialog setButtonA(String text, View.OnClickListener listener) {
        this.btnTextA = text;
        this.listenerA = listener;
        return this;
    }

    public WarnningDialog setButtonA(String text, View.OnClickListener listener, WarnningDialog.ButtonType buttonType) {
        this.btnTextA = text;
        this.listenerA = listener;
        this.buttonTypeA = buttonType;
        return this;
    }

    public WarnningDialog setButtonB(String text, View.OnClickListener listener) {
        this.btnTextB = text;
        this.listenerB = listener;
        return this;
    }

    public WarnningDialog setButtonB(String text, View.OnClickListener listener, WarnningDialog.ButtonType buttonType) {
        this.btnTextB = text;
        this.listenerB = listener;
        this.buttonTypeB = buttonType;
        return this;
    }

    public WarnningDialog setContent(String content) {
        this.content = content;
        return this;
    }

    private void setButtonType(WarnningDialog.ButtonType type, Button btn) {
        switch(type) {
            case notSuggestion:
                btn.setTextColor(this.context.getResources().getColor(ID.getColor(this.context, "bdp_deep_gray")));
                btn.setBackgroundResource(ID.getDrawable(this.context, "bdp_update_bg_dialog_btn_white"));
                break;
            case defualt:
            default:
                btn.setTextColor(this.context.getResources().getColor(ID.getColor(this.context, "bdp_white")));
                btn.setBackgroundResource(ID.getDrawable(this.context, "bdp_update_bg_dialog_btn_blue"));
        }

    }

    public void onClick(View v) {
        super.onClick(v);
        if (v == this.btn_a) {
            if (this.listenerA != null) {
                this.listenerA.onClick(v);
            }

            this.dismiss();
        } else if (v == this.btn_b) {
            if (this.listenerB != null) {
                this.listenerB.onClick(v);
            }

            this.dismiss();
        }

    }

    public static enum ButtonType {
        defualt,
        notSuggestion;

        private ButtonType() {
        }
    }
}
