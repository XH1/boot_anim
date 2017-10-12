package com.example.xh.boot_anim.dialogclasses;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.xh.boot_anim.ConfigBean;
import com.example.xh.boot_anim.R;

/**
 * Created by xh on 2017/10/8.
 */

public class ConfigFragment extends DialogFragment implements DialogInterface.OnClickListener {
    private TextView reX;
    private TextView reY;
    private TextView frames;
    private Spinner play_type;
    private Spinner recycle;
    private ConfigBean configBean;

    public interface ConfigReturn {
        public void handleReturn(ConfigBean configBean);
    }

    ;

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                ConfigReturn c = (ConfigReturn) getActivity();
                int arg1 = Integer.parseInt(reX.getText().toString());
                int arg2 = Integer.parseInt(reY.getText().toString());
                int arg3 = Integer.parseInt(frames.getText().toString());
                char arg4 = play_type.getSelectedItem().toString().charAt(0);
                int arg5 = Integer.parseInt(recycle.getSelectedItem().toString());
                configBean = new ConfigBean(arg1, arg2, arg3, arg4, arg5);
                c.handleReturn(configBean);

                break;
            case AlertDialog.BUTTON_NEGATIVE:
                dismiss();
                break;
            default:
                break;
        }

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View config_view = getActivity().getLayoutInflater().inflate(R.layout.activity_config, null);
        reX = (TextView) config_view.findViewById(R.id.config_resolution_x);
        reY = (TextView) config_view.findViewById(R.id.config_resolution_y);
        frames = (TextView) config_view.findViewById(R.id.config_frames);
        play_type = (Spinner) config_view.findViewById(R.id.config_play_type);
        recycle = (Spinner) config_view.findViewById(R.id.config_recycle_counts);
        AlertDialog a = new AlertDialog.Builder(getActivity()).setView(config_view).setTitle("设置")
                .setPositiveButton("确定", this)
                .setNegativeButton("取消", this)
                .create();
        a.setCanceledOnTouchOutside(false);
        return a;
    }

    /*@Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_config, container);
    }*/
}
