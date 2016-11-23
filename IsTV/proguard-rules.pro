
#jar包依赖 build.gradle 里面已经配置过了,这里不能再重复配置jar依赖
#-libraryjars libs/android-support-v4.jar
#-libraryjars libs/fastjson-1.2.4.jar
#-libraryjars libs/MiStats_SDK_Client_1_3_0.jar
#-libraryjars libs/nineoldandroids-2.4.0.jar
#-libraryjars libs/umeng-analytics-v5.6.1.jar
#-libraryjars libs/xUtils-2.6.14.jar



-keep public class * extends android.app.Activity

-keep public class * extends android.app.Application

-keep public class * extends android.app.Service

-keep public class * extends android.content.BroadcastReceiver

-keep public class * extends android.content.ContentProvider

-keep public class * extends android.app.backup.BackupAgentHelper

-keep public class * extends android.preference.Preference

-keep public class com.android.vending.licensing.ILicensingService
-dontskipnonpubliclibraryclassmembers

################xutils##################
#-libraryjars libs/xUtils-2.6.14.jar
-keep class com.lidroid.xutils.** { *; }
-keep public class * extends com.lidroid.xutils.**
-keepattributes Signature
-keepattributes *Annotation*
-keep public interface com.lidroid.xutils.** {*;}
-dontwarn com.lidroid.xutils.**
-keepclasseswithmembers class com.jph.android.entity.** {
    <fields>;
    <methods>;
}

################支付宝##################
#-libraryjars libs/alipaysecsdk.jar
#-libraryjars libs/alipayutdid.jar
#-libraryjars libs/alipaysdk.jar
-keep class com.alipay.android.app.IAliPay{*;}
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.lib.ResourceMap{*;}

#-keepclasseswithmembernames class * { native ();}

#-keepclasseswithmembers class * {
#public *(android.content.Context, android.util.AttributeSet);
#}
#
#-keepclasseswithmembers class * {
#public *(android.content.Context, android.util.AttributeSet, int);
#}

-keep class com.modernsky.istv.bean.** { *; } #自定义java bean不参与混淆
-keep class com.modernsky.istv.view.** { *; } #自定义控件不参与混淆
-keep class com.modernsky.istv.widget.** { *; } #自定义控件不参与混淆

-keepclassmembers class * extends android.app.Activity {
public void *(android.view.View);
}


-keep class * implements android.os.Parcelable {
public static final android.os.Parcelable$Creator *;
}


#融云--start
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
 public *;
}

-keepattributes Exceptions,InnerClasses

-keep class io.rong.** {*;}
-dontwarn io.rong.**

-keep class * implements io.rong.imlib.model.MessageContent{*;}

-keepattributes Signature

-keepattributes *Annotation*

-keep class sun.misc.Unsafe { *; }

-keep class com.google.gson.examples.android.model.** { *; }

-keepclassmembers class * extends com.sea_monster.dao.AbstractDao {
 public static java.lang.String TABLENAME;
}
-keep class **$Properties
-dontwarn org.eclipse.jdt.annotation.**

-keep class com.ultrapower.** {*;}
#融云--end
#友盟——start
-dontwarn com.umeng.**
-dontwarn com.umeng.socialize.**
-keep class org.json.** { *; }
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-keep public class com.modernsky.istv.R$*{
public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class android.support.** { *; }
-dontwarn android.support.**
-dontwarn com.alibaba.fastjson.**
#友盟--end
#fastjson--start

-dontskipnonpubliclibraryclassmembers
-dontskipnonpubliclibraryclasses

-keep class com.baidu.** { *; }
-keep class com.alibaba.fastjson.** { *; }

-keepclassmembers class * {
public <methods>;
}


################gson##################
#-libraryjars libs/gson-2.2.4.jar
-keep class com.google.gson.** {*;}
#-keep class com.google.**{*;}
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keep class com.google.** {
    <fields>;
    <methods>;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
        public <fields>;
}
-dontwarn com.google.gson.**



####################umeng##################
#-libraryjars libs/umeng-analytics-v5.2.4.jar
-keep class com.umeng.analytics.** {*;}
-dontwarn com.umeng.analytics.**

#-keep public class * extends com.umeng.**
#-keep public class * extends com.umeng.analytics.**
#-keep public class * extends com.umeng.common.**
#-keep public class * extends com.umeng.newxp.**
-keep class com.umeng.** { *; }
-keep class com.umeng.analytics.** { *; }
-keep class com.umeng.common.** { *; }
-keep class com.umeng.newxp.** { *; }

-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-keep class com.umeng.**

-keep public class com.idea.fifaalarmclock.app.R$*{
    public static final int *;
}

-keep public class com.umeng.fb.ui.ThreadView {
}

-dontwarn com.umeng.**

-dontwarn org.apache.commons.**

-keep public class * extends com.umeng.**
-keep class com.umeng.** {*; }




#nineoldandroids
-dontwarn com.nineoldandroids.**

-keep class com.nineoldandroids.** { *;}


#乐视sdk
-keep class com.letv.** { *;}
-keep class com.lecloud.** {*;}
-keep class android.webkit.** { *;}

-dontwarn com.avdmg.avdsmart.**
-dontwarn com.lecloud.**
-dontwarn com.letv.adlib.**
-dontwarn com.letv.play.**
-dontwarn com.letv.pp.**
-dontwarn org.rajawali3d.**
-dontwarn android.webkit.**
-dontwarn com.letv.universal.widget.**


-keep class com.squareup.** { *;}
-dontwarn com.squareup.**

-dontwarn com.nineoldandroids.**
-keep class com.nineoldandroids.** { *;}
#webview js
-keepclassmembers class com.modernsky.istv.WebActivity$AppAndroid {
  public *;
}

-keepattributes *Annotation*
-keepattributes *JavascriptInterface*