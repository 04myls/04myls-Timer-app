package com.example.mycountdowntimer

import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.AdapterView
import android.widget.SeekBar
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.mycountdowntimer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var soundPool: SoundPool
    private var soundResId = 0

    inner class MyCountDownTimer(
        millisInFuture: Long,
        countDownInterval: Long
    ) : CountDownTimer(millisInFuture, countDownInterval) {
        var isRunning = false

        override fun onTick(millisUntilFinished: Long) {
            val minute = millisUntilFinished / 1000L / 60L
            val second = millisUntilFinished / 1000L % 60L
            binding.timerText.text = "%1d:%2$02d".format(minute, second)
        }

        override fun onFinish() {
            binding.timerText.text = "0:00"
            soundPool.play(soundResId, 1.0f, 100f, 0, 0, 1.0f)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.timerText.text = "3:00"
        var timer = MyCountDownTimer(3 * 60 * 1000, 100)
        binding.playStop.setOnClickListener {
            timer.isRunning = when (timer.isRunning) {
                true -> {
                    timer.cancel()
                    binding.playStop.setImageResource(
                        R.drawable.ic_baseline_play_arrow_24
                    )
                    false
                }
                false -> {
                    timer.start()
                    binding.playStop.setImageResource(
                        R.drawable.ic_baseline_stop_24
                    )
                    true
                }
            }
        }

        binding.spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    timer.cancel()
                    binding.playStop.setImageResource(
                        R.drawable.ic_baseline_play_arrow_24
                    )
                    val spinner = parent as? Spinner
                    val item = spinner?.selectedItem as? String
                    item?.let {
                        if (it.isNotEmpty()) binding.timerText.text = it
                        val times = it.split(":")
                        val min = times[0].toLong()
                        val sec = times[1].toLong()
                        timer = MyCountDownTimer((min * 60 + sec) * 1000,
                            100)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) { }
            }

        binding.seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    timer.cancel()
                    binding.playStop.setImageResource(
                        R.drawable.ic_baseline_play_arrow_24
                    )
                    val min = progress / 60L
                    val sec = progress % 60L
                    binding.timerText.text = "%1d:%2$02d".format(min, sec)
                    timer = MyCountDownTimer(progress * 1000L, 100)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) { }

                override fun onStopTrackingTouch(seekBar: SeekBar?) { }
            }
        )
    }

    override fun onResume() {
        super.onResume()
        soundPool =
            SoundPool.Builder().run {
                val audioAttributes = AudioAttributes.Builder().run {
                    setUsage(AudioAttributes.USAGE_ALARM)
                    build()
                }
                setMaxStreams(1)
                setAudioAttributes(audioAttributes)
                build()
            }
        soundResId = soundPool.load(this, R.raw.bellsound, 1)
    }

    override fun onPause() {
        super.onPause()
        soundPool.release()
    }
}