package com.hillwar.testapplication.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.hillwar.testapplication.R
import com.lorentzos.flingswipe.SwipeFlingAdapterView

class QuizFragment : Fragment() {

    private var score: Int = 0

    private var anim: Boolean = true

    private var visual: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quiz, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val timer = view.findViewById<TextView>(R.id.timer)
        var countDownTimer = Timer(5000, 1000, timer, 5)
        countDownTimer.start()
        val swipe = view.findViewById<SwipeFlingAdapterView>(R.id.card)
        val list = BD.questions.toMutableList() as ArrayList<String>
        list.shuffle()
        val adapter =
            ArrayAdapter(view.context, R.layout.item, R.id.quest, list)
        swipe.adapter = adapter
        swipe.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
            override fun removeFirstObjectInAdapter() {
                list.removeAt(0)
                adapter.notifyDataSetChanged()
            }

            override fun onLeftCardExit(p0: Any?) {
                countDownTimer.cancel()
                countDownTimer = Timer(5000, 1000, timer, 5)
                countDownTimer.start()
                list.add(p0.toString())
                val temp = list[0]
                list.removeAt(0)
                list.shuffle()
                list.add(0, temp)
                adapter.notifyDataSetChanged()
            }

            override fun onRightCardExit(p0: Any?) {
                score += countDownTimer.t
                countDownTimer.cancel()
                if (list.size != 0) {
                    countDownTimer = Timer(5000, 1000, timer, 5)
                    countDownTimer.start()
                } else Toast.makeText(view.context, (score / 100.0).toString(), Toast.LENGTH_LONG)
                    .show()
                val list = arrayListOf("Молодец!", "Супер!", "Так держать!", "Превосходно")
                list.shuffle()
                Toast.makeText(view.context, list[0], Toast.LENGTH_SHORT).show()
            }

            override fun onAdapterAboutToEmpty(p0: Int) {
            }

            override fun onScroll(p0: Float) {
            }
        })

        swipe.setOnItemClickListener { _, data ->
            countDownTimer.cancel()
            if (anim) {
                anim = false
                val scale = swipe.selectedView.context.resources.displayMetrics.density
                swipe.selectedView.findViewById<CardView>(R.id.frame).cameraDistance = 2000 * scale
                val oa1 = ObjectAnimator.ofFloat(
                    swipe.selectedView.findViewById<CardView>(R.id.frame),
                    View.ROTATION_Y,
                    0f,
                    90f
                )
                val oa2 = ObjectAnimator.ofFloat(
                    swipe.selectedView.findViewById<CardView>(R.id.frame),
                    View.ROTATION_Y,
                    -90F,
                    0f
                )
                oa1.interpolator = DecelerateInterpolator()
                oa2.interpolator = AccelerateDecelerateInterpolator()
                oa1.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        if (swipe.selectedView.findViewById<TextView>(R.id.quest).text != BD.answer[data.toString()]) swipe.selectedView.findViewById<TextView>(
                            R.id.quest
                        ).text = BD.answer[data.toString()]
                        else swipe.selectedView.findViewById<TextView>(R.id.quest).text =
                            data.toString()
                        oa2.start()
                    }
                })
                oa2.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        anim = true
                    }
                })
                oa1.start()
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }
}

class Timer(
    millisInFuture: Long,
    countDownInterval: Long,
    private var timer: TextView,
    private var time: Int
) :
    CountDownTimer(millisInFuture, countDownInterval) {

    var t = 0

    override fun onTick(millisUntilFinished: Long) {
        timer.text = time.toString()
        time--
        t++
    }

    override fun onFinish() {
        timer.text = "Раунд!"
    }
}
