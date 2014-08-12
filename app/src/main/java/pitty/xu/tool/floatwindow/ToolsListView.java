package pitty.xu.tool.floatwindow;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 */
public class ToolsListView implements View.OnClickListener {
    View toolListView;
    CheckBox mem_percent;
    CheckBox top_activity;
    CheckBox app_mem;
    CheckBox stopped_app;
    TextView app_mem_tv;
    Context context;
    public ToolsListView(View view) {
        this(view, null);
    }
    public ToolsListView(View view, Context context) {
        toolListView = view;
        if (null != context && context instanceof Activity) {
            this.context = context;
        }
        mem_percent = (CheckBox) toolListView.findViewById(R.id.tool_mem_percent_cb);
        top_activity = (CheckBox) toolListView.findViewById(R.id.tool_top_activity_cb);
        app_mem = (CheckBox) toolListView.findViewById(R.id.tool_app_mem_cb);
        stopped_app = (CheckBox) toolListView.findViewById(R.id.tool_stopped_app_cb);
        app_mem_tv = (TextView) toolListView.findViewById(R.id.tool_app_mem_tv);
    }

    public void init() {
        mem_percent.setChecked(Tools.isContainsTool(Tools.MEMORY_PERCENT));
        top_activity.setChecked(Tools.isContainsTool(Tools.TOP_ACTIVITY));
        app_mem.setChecked(Tools.isContainsTool(Tools.APP_MEMORY));
        stopped_app.setChecked(Tools.isContainsTool(Tools.STOPPED_APP));
        mem_percent.setOnClickListener(this);
        top_activity.setOnClickListener(this);
        app_mem.setOnClickListener(this);
        stopped_app.setOnClickListener(this);
        if (null != context) {
            app_mem_tv.setOnClickListener(this);
            app_mem_tv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
        }
        app_mem_tv.setText(Tools.getMemPkg());
    }

    @Override
    public void onClick(View v) {
        if (v instanceof CheckBox) {
            final CheckBox checkBox = (CheckBox) v;
            switch (v.getId()) {
                case R.id.tool_mem_percent_cb:
                    if (checkBox.isChecked()) {
                        Tools.appendTools(Tools.MEMORY_PERCENT);
                    } else {
                        Tools.removeTools(Tools.MEMORY_PERCENT);
                    }
                    break;
                case R.id.tool_top_activity_cb:
                    if (checkBox.isChecked()) {
                        Tools.appendTools(Tools.TOP_ACTIVITY);
                    } else {
                        Tools.removeTools(Tools.TOP_ACTIVITY);
                    }
                    break;
                case R.id.tool_app_mem_cb:
                    if (checkBox.isChecked()) {
                        if (null == Tools.getMemPkg() || "".equals(Tools.getMemPkg())) {
                            showDialog(context);
                        }
                        Tools.appendTools(Tools.APP_MEMORY);
                    } else {
                        Tools.removeTools(Tools.APP_MEMORY);
                    }
                    break;
                case R.id.tool_stopped_app_cb:
                    if (checkBox.isChecked()) {
                        Tools.appendTools(Tools.STOPPED_APP);
                    } else {
                        Tools.removeTools(Tools.STOPPED_APP);
                    }
                    break;
            }
        } else if (v instanceof TextView) {
            final TextView textView = (TextView) v;
            switch (textView.getId()) {
                case R.id.tool_app_mem_tv:
                    showDialog(context);
                    break;
            }
        }
    }

    private void showDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.tool_app_mem_hint);
        // Set up the input
        final EditText input = new EditText(context);
        input.setText(Tools.getMemPkg());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Tools.setMemPkg(input.getText().toString());
                app_mem_tv.setText(Tools.getMemPkg());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}
