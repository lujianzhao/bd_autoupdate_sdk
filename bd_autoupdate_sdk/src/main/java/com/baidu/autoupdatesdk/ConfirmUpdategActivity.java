//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.autoupdatesdk.r.ID;
import com.baidu.autoupdatesdk.utils.DeviceUtils;
import com.othershe.nicedialog.BaseNiceDialog;
import com.othershe.nicedialog.NiceDialog;
import com.othershe.nicedialog.ViewConvertListener;
import com.othershe.nicedialog.ViewHolder;

@SuppressLint({"NewApi"})
public class ConfirmUpdategActivity extends FragmentActivity implements OnClickListener {
    public static final int ACTION_DOWNLOAD = 1;
    public static final int ACTION_INSTALL = 2;
    private static ConfirmUpdategActivity.OnActionListener actionListener;
    private TextView txtTitle;
    private TextView txtMainTip;
    private TextView txtMinorTip;
    private ImageView imgClose;
    private Button btnUpdate;
    private Button btnUpdateRecommend;
    private TextView txtIgnore;
    private int action;
    private String mainTip;
    private String minorTip;
    private boolean canClose;
    private int ignoreVersion;
    private boolean haveInstallPermission;

    public ConfirmUpdategActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setFinishOnTouchOutside(false);
        this.setContentView(ID.getLayout(this, "bdp_update_activity_confirm_update"));
        this.initView();
        this.initParams();
        this.setView();
        this.setOrientation(this.getResources().getConfiguration());
    }

    private void initView() {
        this.txtTitle = (TextView)this.findViewById(ID.getId(this, "txt_title"));
        this.txtMainTip = (TextView)this.findViewById(ID.getId(this, "txt_main_tip"));
        this.txtMinorTip = (TextView)this.findViewById(ID.getId(this, "txt_minor_tip"));
        this.imgClose = (ImageView)this.findViewById(ID.getId(this, "imgClose"));
        this.btnUpdate = (Button)this.findViewById(ID.getId(this, "btnUpdate"));
        this.btnUpdateRecommend = (Button)this.findViewById(ID.getId(this, "btnUpdateRecommend"));
        this.txtIgnore = (TextView)this.findViewById(ID.getId(this, "txtIgnore"));
    }

    private void initParams() {
        if (this.getIntent() != null) {
            this.action = this.getIntent().getIntExtra("intent_key_action", 0);
            this.mainTip = this.getIntent().getStringExtra("intent_key_main_tip");
            this.minorTip = this.getIntent().getStringExtra("intent_key_minor_tip");
            this.canClose = this.getIntent().getBooleanExtra("intent_key_canclose", true);
            this.ignoreVersion = this.getIntent().getIntExtra("intent_key_ignore_version", 0);
        }

    }

    private void setView() {
        if (this.action == 1) {
            this.txtTitle.setText(ID.getString(this, "bdp_update_title_download"));
            this.btnUpdateRecommend.setText(ID.getString(this, "bdp_update_action_update_by_as"));
            this.btnUpdate.setText(ID.getString(this, "bdp_update_action_update"));
            this.txtIgnore.setVisibility(View.VISIBLE);
        } else {
            if (this.action != 2) {
                throw new RuntimeException("Illegal Action: " + this.action);
            }

            this.txtTitle.setText(ID.getString(this, "bdp_update_title_install"));
            this.btnUpdateRecommend.setText(ID.getString(this, "bdp_update_action_install"));
            this.btnUpdate.setVisibility(View.GONE);
            this.txtIgnore.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(this.mainTip)) {
            this.txtMainTip.setText(this.mainTip);
        }

        if (!TextUtils.isEmpty(this.minorTip)) {
            this.txtMinorTip.setText(Html.fromHtml(this.minorTip));
        }

        this.imgClose.setOnClickListener(this);
        this.btnUpdateRecommend.setOnClickListener(this);
        this.btnUpdate.setOnClickListener(this);
        this.txtIgnore.setOnClickListener(this);
        if (!this.canClose) {
            this.imgClose.setVisibility(View.GONE);
            this.txtIgnore.setVisibility(View.GONE);
        }

        if (this.ignoreVersion == 0) {
            this.txtIgnore.setVisibility(View.GONE);
        }

    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.setOrientation(newConfig);
    }

    public void onBackPressed() {
        if (this.canClose) {
            if (actionListener != null) {
                actionListener.onClose(this);
            }

            super.onBackPressed();
        }
    }

    private void setOrientation(Configuration config) {
        int width = DeviceUtils.getScreenWidth(this) - DeviceUtils.dip2px(this, 12.0F) * 2;
        LayoutParams params;
        if (config.orientation == 2) {
            params = this.getWindow().getAttributes();
            int landPadding = DeviceUtils.dip2px(this, 80.0F);
            if (params == null) {
                params = new LayoutParams(width - landPadding * 2, -2);
            } else {
                params.width = width - landPadding * 2;
            }

            this.getWindow().setAttributes(params);
        } else if (config.orientation == 1) {
            params = this.getWindow().getAttributes();
            if (params == null) {
                params = new LayoutParams(width, -2);
            } else {
                params.width = width;
            }

            this.getWindow().setAttributes(params);
        }

    }

    public void onClick(View v) {
        if (v == this.btnUpdate) {
//            if (actionListener != null) {
//                actionListener.onUpdate(this);
//            }
            checkInstallPermission(REQUEST_CODE_UPDATE);
        } else if (v == this.btnUpdateRecommend) {
//            if (actionListener != null) {
//                actionListener.onUpdateRecommend(this);
//            }
            checkInstallPermission(REQUEST_CODE_UPDATE_RECOMMEND);
        } else if (v == this.txtIgnore) {
            if (actionListener != null) {
                actionListener.onIgnoreVersion(this, this.ignoreVersion);
            }

            this.close();
        } else if (v == this.imgClose) {
            this.close();
        }

    }

    private static final int REQUEST_CODE_UPDATE = 0;
    private static final int REQUEST_CODE_UPDATE_RECOMMEND = 1;

    private void checkInstallPermission(final int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //先获取是否有安装未知来源应用的权限
            haveInstallPermission = getPackageManager().canRequestPackageInstalls();
            if (!haveInstallPermission) {//没有权限
                NiceDialog.init()
                        .setLayoutId(R.layout.confirm_layout)
                        .setConvertListener(new ViewConvertListener() {
                            @Override
                            public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                                holder.setOnClickListener(R.id.cancel, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });

                                holder.setOnClickListener(R.id.ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        startInstallPermissionSettingActivity(requestCode);
                                    }
                                });
                            }
                        })
                        .setMargin(60)
                        .setOutCancel(false)
                        .show(getSupportFragmentManager());
                return;
            }
        }

        installProcess(requestCode);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity(int requestCode) {
        Uri packageURI = Uri.parse("package:" + getPackageName());
        //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            installProcess(requestCode);
        }
    }

    private void installProcess(int requestCode) {
        switch (requestCode) {
            case REQUEST_CODE_UPDATE:
                if (actionListener != null) {
                    actionListener.onUpdate(this);
                }
                this.close();
                break;
            case REQUEST_CODE_UPDATE_RECOMMEND:
                if (actionListener != null) {
                    actionListener.onUpdateRecommend(this);
                }
                break;
        }

    }

    private void close() {
        if (this.canClose) {
            if (actionListener != null) {
                actionListener.onClose(this);
            }
            this.finish();
        }
    }

    public static void show(Context ctx, int action, String mainTip, String minorTip, boolean canClose, int ignoreVersion, ConfirmUpdategActivity.OnActionListener actionListener) {
        Intent intent = new Intent(ctx, ConfirmUpdategActivity.class);
        intent.putExtra("intent_key_action", action);
        intent.putExtra("intent_key_main_tip", mainTip);
        intent.putExtra("intent_key_minor_tip", minorTip);
        intent.putExtra("intent_key_canclose", canClose);
        intent.putExtra("intent_key_ignore_version", ignoreVersion);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
        ConfirmUpdategActivity.actionListener = actionListener;
    }

    public interface OnActionListener {
        void onUpdate(Context var1);

        void onUpdateRecommend(Context var1);

        void onIgnoreVersion(Context var1, int var2);

        void onClose(Context var1);
    }
}
