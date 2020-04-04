package com.sirme.tv.tim

import android.content.Context
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import com.tencent.imsdk.*
import com.tencent.imsdk.TIMLogLevel
import com.tencent.imsdk.TIMSdkConfig
import android.os.Environment
import android.util.Log
import com.tencent.imsdk.TIMManager
import io.flutter.Log.setLogLevel
import com.tencent.imsdk.session.SessionWrapper
import com.tencent.imsdk.TIMCallBack
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken;
import java.util.Collection;



/** TimPlugin */
public class TimPlugin: FlutterPlugin, MethodCallHandler {
  private var context: Context? = null

//  companion object {
//      val channel = MethodChannel(registrar.messenger(), "tim")
//      channel.setMethodCallHandler(TimPlugin())
//    }
//  }

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    this.context = flutterPluginBinding.applicationContext
    Log.i("context", context.toString())
    val channel = MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "tim")
    channel.setMethodCallHandler(TimPlugin())

    if (SessionWrapper.isMainProcess(this.context)) {
      val config = TIMSdkConfig(1400023089)
              .enableLogPrint(false)
              .setLogLevel(TIMLogLevel.DEBUG)
//              .setLogPath(Environment.getExternalStorageDirectory().path + "/justfortest/")

      //初始化 SDK
      var res = TIMManager.getInstance().init(flutterPluginBinding.applicationContext, config)
      if (res) {
        Log.i("init", "success")
      }

//      var userId = "2434830"
//      var sign = "eJxNjV1PgzAYRv9Lb2e0hZKBiReTNKwZzEwwAWPSNOOdK4xCum66Gf*7lWzR23Oejy9UpPmtXK-7g7bCngZA9wijmxGrGrRVGwXGQY-6NPSvSg6DqoW0wjf1v8a*bsWoHCMUY*z5OIwuEj4HZUDIjR0HSRAEnotc7BHMXvX69wmTgLgi-pNWdTBWQhq5WY9e-9S7wxl7iTl7wt42nPJuCVVSPup00cYA02IuV6skzl-TI8dlkVdpn80Um30w0hm1S5rJ5BxlfYUP2fzcdNH22ejm7S45SbZbUr4AXrYP6PsHsN1W-g__"
//
//      TIMManager.getInstance().login(userId, sign, object :TIMCallBack {
//        override fun onError(p0: Int, p1: String?) {
//          Log.i("login", "error")
//        }
//
//        override fun onSuccess() {
//          Log.i("login", "success")
//        }
//      } )

      TIMManager.getInstance().setUserConfig(TIMUserConfig().setRefreshListener(object : TIMRefreshListener{
        override fun onRefresh() {
          Log.i("onRefresh", "onRefresh")
        }

        override fun onRefreshConversation(p0: MutableList<TIMConversation>?) {
          Log.i("onRefreshConversation", p0.toString())
        }
      }))

      TIMManager.getInstance().addMessageListener(object: TIMMessageListener{
        override fun onNewMessages(p0: MutableList<TIMMessage>): Boolean {
          // Log.i("msg", p0[0].toString())
          channel?.invokeMethod("onNewMessages", Gson().toJson(p0[0].toString()))
          return false
        }
      })
    }

  }


  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when(call.method) {
      "getPlatformVersion"  -> {
        result.success("Android ${android.os.Build.VERSION.RELEASE}")
      }
      "init" -> {
//        if (SessionWrapper.isMainProcess(this.context)) {
//          val config = TIMSdkConfig(1400023089)
//                  .enableLogPrint(true)
//                  .setLogLevel(TIMLogLevel.DEBUG)
//                  .setLogPath(Environment.getExternalStorageDirectory().path + "/justfortest/")
//
//          //初始化 SDK
//          var res = TIMManager.getInstance().init(this.context, config)
//
//          print(res)
          result.success(this.context)
//        } else {
//          result.error("init error", "err", "...")
//        }
      }
      "login" -> {
        var userId = "2434830"
        var sign = "eJxNjV1PgzAYRv9Lb2e0hZKBiReTNKwZzEwwAWPSNOOdK4xCum66Gf*7lWzR23Oejy9UpPmtXK-7g7bCngZA9wijmxGrGrRVGwXGQY-6NPSvSg6DqoW0wjf1v8a*bsWoHCMUY*z5OIwuEj4HZUDIjR0HSRAEnotc7BHMXvX69wmTgLgi-pNWdTBWQhq5WY9e-9S7wxl7iTl7wt42nPJuCVVSPup00cYA02IuV6skzl-TI8dlkVdpn80Um30w0hm1S5rJ5BxlfYUP2fzcdNH22ejm7S45SbZbUr4AXrYP6PsHsN1W-g__"

        TIMManager.getInstance().login(userId, sign, object :TIMCallBack {
          override fun onError(p0: Int, p1: String?) {
            result.error("1", "2", "3")
          }

          override fun onSuccess() {
            result.success("hello world")
          }
        } )

        var a = call.argument<String>("appId")
      }
      "getConversationList" -> {
        var d = TIMManager.getInstance().conversationList
//        var s = Gson().toJson(d, object :TypeToken<Collection<TIMConversation>>(){}.type)
//        Log.i("s", s)
        var peers = ArrayList<String>()
        peers.add(d[0].peer)
        TIMFriendshipManager.getInstance().getUsersProfile(peers, false, object: TIMValueCallBack<List<TIMUserProfile>>{
          override fun onSuccess(p0: List<TIMUserProfile>) {
            val ava = p0[0].faceUrl
            var data: MutableList<HashMap<String, String>> = ArrayList()
            for( i :TIMConversation in d) {
              var map = HashMap<String, String>()
              val item : TIMElem = i.lastMsg.getElement(0)
              when(item.type){
                TIMElemType.Text -> {
                  var abc = item as TIMTextElem

                  map.put("text", abc.text)
                }
                TIMElemType.Image -> {
                  var abc = item as TIMImageElem
                  Log.i("list-item", abc.imageList[0].url)
                }
                TIMElemType.File -> {
                  var abc = item as TIMFileElem
                  abc.getUrl(object : TIMValueCallBack<String>{
                    override fun onSuccess(p0: String?) {
                      Log.i("list-item", p0)
                    }
                    override fun onError(p0: Int, p1: String?) {
                      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                  })
                }
                TIMElemType.Custom -> {}
                TIMElemType.Sound -> {
                  var abc = item as TIMSoundElem
                  abc.getUrl(object : TIMValueCallBack<String>{
                    override fun onSuccess(p0: String?) {
                      Log.i("list-item", p0)
                    }

                    override fun onError(p0: Int, p1: String?) {
                      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                  })
                }
                TIMElemType.Video -> {
                  var abc = item as TIMVideoElem
                  Log.i("list-item", "video")
                }
                TIMElemType.Face -> {
                  var abc = item as TIMFaceElem

                  Log.i("list-item", abc.data[0].toString())
                  Log.i("list-item", abc.index.toString())

                }
                TIMElemType.Location -> {}
                TIMElemType.SNSTips -> {}
                TIMElemType.GroupSystem -> {}
                else -> {

                }
              }
              map.put("type", i.type.toString())
              map.put("peer", i.peer)
              map.put("unreadMessageNum", i.unreadMessageNum.toString())
              map.put("ava", ava)
              data.add(map)
            }
            result.success(data)
          }

          override fun onError(p0: Int, p1: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
          }
        })


      }
      else -> {
        result.notImplemented()
      }
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
  }


}
