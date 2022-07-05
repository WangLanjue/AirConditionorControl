package com.wlj.airconditionorcontrol.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

//intent工具类
public class IntentUtils {

    public static  void SetIntent(Context context, Class<?> mClass){
        Intent intent = new Intent();
        intent.setClass(context, mClass);
        context.startActivity(intent);
    }

    public static  void SetIntentandfinish(Context context,Class<?>mClass,boolean isclose){
        Intent intent = new Intent();
        intent.setClass(context, mClass);
        context.startActivity(intent);
        if (isclose){
            ((Activity)context).finish();
        }
    }

    public static void SetIntentBundle(Context context, Class<?>mClass, Bundle bundle){
        Intent intent = new Intent();
        intent.setClass(context, mClass);
        intent.putExtra("bundle",bundle);
        context.startActivity(intent);

    }
}
