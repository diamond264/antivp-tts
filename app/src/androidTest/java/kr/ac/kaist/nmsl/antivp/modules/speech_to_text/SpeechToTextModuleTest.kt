package kr.ac.kaist.nmsl.antivp.modules.speech_to_text

import android.content.res.AssetManager
import android.os.Bundle
import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.*
import kr.ac.kaist.nmsl.antivp.core.EventType
import kr.ac.kaist.nmsl.antivp.core.Module
import kr.ac.kaist.nmsl.antivp.core.ModuleManager
import kr.ac.kaist.nmsl.antivp.modules.text_based_detection.TextBasedDetectionModule
import org.junit.runner.RunWith
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.io.File
import kr.ac.kaist.nmsl.antivp.core.util.FileManager;

@RunWith(AndroidJUnit4::class)
internal class SpeechToTextModuleTest {
    @Test
    fun testModule() {
        val latch = CountDownLatch(1)
        MainScope().launch {
            val mModuleManager = ModuleManager(ApplicationProvider.getApplicationContext())
            val audio = mModuleManager.mContext.applicationContext.assets.open("sample.wav")
            var sample = 0
            val arr = arrayListOf<Int>()

            val speechToTextModule = SpeechToTextModule()

            System.out.println("Phd human resource")
            System.out.println(audio)

            do{
                sample=audio.read()
                arr.add(sample)
            } while (sample!=-1)

            System.out.println(arr.size)
            System.out.println(arr)

            //val input_ids_shape = arrayOf<Long>(1,32045)

            //val input_wav_tensor = IValue.from(Tensor.fromBlob(arr,input_ids_shape))



            val dummyCallEventGenerationModule = object: Module() {
                override fun name(): String { return "call_event_generation" }
                override fun handleEvent(type: EventType, bundle: Bundle) {}
            }

            val dummyTextBasedDetectionModule = object: Module() {
                init { subscribeEvent(EventType.TEXT_TRANSCRIBED) }
                override fun name(): String { return "text_based_detection" }
                override fun handleEvent(type: EventType, bundle: Bundle) {
                    when (type) {
                        EventType.TEXT_TRANSCRIBED -> {
                            Log.d("test", "Well run")
                            latch.countDown()
                        }
                        else -> {
                            throw Exception()
                        }
                    }
                }
            }


            mModuleManager.register(dummyCallEventGenerationModule)
            mModuleManager.register(speechToTextModule)
            mModuleManager.register(dummyTextBasedDetectionModule)

            val audio_input = arr.toIntArray()
            val bundle = Bundle()
            bundle.putIntArray("record_file",audio_input)

            //testModule().raiseEvent(EventType.TEST_S2T_DATA_BROADCASTED,bundle)
//            val filereader = (ApplicationProvider.getApplicationContext())?.assets!!.open("1.wav")
//            val fileManager = FileManager.getInstance()
//            val data = fileManager.load("/storage/emulated/0/Music/1.wav")
//            val bundle = Bundle()
//            val exFile = "/sdcard/Android/data/kr.ac.kaist.nmsl.antivp/files/test_data/1.wav"
//            val exFile = "/storage/emulated/0/Music/1.wav"
//            bundle.putFloatArray("record_file", data)
//            val file = File(exFile)

//            if (file.exists()) {
//                Log.d("check", "file exists.")
//            } else {
//                Log.d("check", "file NOT exists.")
//            }

//            bundle.putString("record_file", "/sdcard/Android/data/kr.ac.kaist.nmsl.antivp/files/test_data/1.wav")
//            dummyCallEventGenerationModule.raiseEvent(EventType.CALL_OFFHOOK, bundle)
        }
        latch.await(10000, TimeUnit.MILLISECONDS)
    }
}