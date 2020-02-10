package com.example.ball

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.contracts.ConditionalEffect
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), SensorEventListener,SurfaceHolder.Callback{


    //SurfaceView宣言
    var mHolder:SurfaceHolder by Delegates.notNull<SurfaceHolder>()
    var mSurfaceWidth = 0
    var mSurfaceHight = 0

    //Ball初期値
    var mBallx = 0.0
    var mBally = 0.0
    var mVX = 0.0
    var mVY = 0.0
    var mFrom = 0.0
    var mTo = 0.0

    var flg = false

    var left =10.toInt()
    var top =100.toInt()
    var right=300.toInt()
    var bottom=300.toInt()

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        mSensorManager.unregisterListener(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        mFrom = System.currentTimeMillis().toDouble()
        mSensorManager.registerListener(this,mAccSensor,SensorManager.SENSOR_DELAY_GAME)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        mSurfaceWidth = width
        mSurfaceHight = height
        mBallx = width / 2.0
        mBally = height / 2.0
        mVX = 0.0
        mVY = 0.0

    }

    //センサーの変数宣言
    private var mSensorManager:SensorManager by Delegates.notNull<SensorManager>()
    private var mAccSensor:Sensor by Delegates.notNull<Sensor>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //センサーマネージャの値を取得
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        //加速度計のセンサーの値を取得する
        mAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        //SurfaceViewの初期化


        mHolder = surfaceView.holder
        Log.d("Surface View",mHolder.toString())
        mHolder.addCallback(this)
    }

    //センサーの精度に変更があった場合呼び出される
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    //センサーの値に変更があった場合呼び出される
    override fun onSensorChanged(event: SensorEvent?) {
        Log.d("Sensormanager", "----------")
        Log.d("x",event?.values!![0].toString())
        Log.d("y",event?.values!![1].toString())
        Log.d("z",event?.values!![2].toString())

        var x = -event?.values[0]
        var y = event?.values[1]
        var z = event?.values[2]

        mTo = System.currentTimeMillis().toDouble()
        var t = (mTo - mFrom)
        t = t / 1000.0

        var dx = mVX * t + x * t * t /2.0
        var dy = mVY * t + y * t * t /2.0

        mBallx = mBallx + dx * COEF
        mBally = mBally + dy * COEF
        mVX = mVX + x * t
        mVY = mVY + y * t

        if(mBallx - RADIUS < 0 && mVX < 0){
            mVX = -mVX / 1.5
            mBallx = RADIUS
        }else if(mBallx + RADIUS > mSurfaceWidth && mVX > 0){
            mVX = -mVX / 1.5
            mBallx = mSurfaceWidth - RADIUS
        }

        if(mBally - RADIUS < 0 && mVY < 0){
            mVY = -mVY / 1.5
            mBally = RADIUS
        }else if(mBally + RADIUS > mSurfaceHight && mVY > 0){
            mVY = -mVY / 1.5
            mBally = mSurfaceHight - RADIUS
        }

        if(left <= mBallx + RADIUS && right >= mBallx - RADIUS && top <= mBally + RADIUS && bottom >= mBally - RADIUS){
            flg=true
        }else{
            flg=false
        }

        mFrom = System.currentTimeMillis().toDouble()
        drawCanvas()

    }
    /*
       //Resumeセンサー稼働監視
       override fun onResume() {
           super.onResume()
           mSensorManager.registerListener(this,mAccSensor,SensorManager.SENSOR_DELAY_GAME)

       }
       */
/*
   //Pauseセンサー監視終了
   override fun onPause() {
       super.onPause()
       mSensorManager.unregisterListener(this)
   }
*/
    companion object {
        const val RADIUS = 50.0
        const val COEF = 1000.0
    }

    private fun drawCanvas(){
        var c:Canvas = mHolder.lockCanvas()
        c.drawColor(Color.YELLOW)
        var paint = Paint()
        paint.setColor(Color.MAGENTA)
        c.drawCircle(mBallx.toFloat(),mBally.toFloat(), RADIUS.toFloat(),paint)
        paint.setColor(Color.BLUE)
        var rect= Rect(left,top,right,bottom)
        c.drawRect(rect,paint)
        if(flg==true){
            c.drawText("Hello Text",100F, 100F,paint)
        }
        mHolder.unlockCanvasAndPost(c)

    }
}

