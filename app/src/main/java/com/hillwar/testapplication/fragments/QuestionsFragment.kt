package com.hillwar.testapplication.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.hillwar.testapplication.R

class QuestionsFragment : Fragment() {

    private var anim: Boolean = true

    private var visual: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_questions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val text = view.findViewById<CardView>(R.id.frame)
        text.setOnClickListener {
            if (anim) {
                anim = false
                val scale = view.context.resources.displayMetrics.density
                text.cameraDistance = 2000 * scale
                val oa1 = ObjectAnimator.ofFloat(text, View.ROTATION_Y, 0f, 90f)
                val oa2 = ObjectAnimator.ofFloat(text, View.ROTATION_Y, -90F, 0f)
                oa1.interpolator = DecelerateInterpolator()
                oa2.interpolator = AccelerateDecelerateInterpolator()
                oa1.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        if (visual) {
                            view.findViewById<TextView>(R.id.quest).visibility = View.INVISIBLE
                            view.findViewById<TextView>(R.id.ans).visibility = View.VISIBLE
                        } else {
                            view.findViewById<TextView>(R.id.quest).visibility = View.VISIBLE
                            view.findViewById<TextView>(R.id.ans).visibility = View.INVISIBLE
                        }
                        visual = !visual
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

        view.findViewById<ImageButton>(R.id.plus).setOnClickListener() {
            val question = view.findViewById<TextView>(R.id.quest).text.toString()
            val answer = view.findViewById<TextView>(R.id.ans).text.toString()
            BD.answer[question] = answer
            BD.flip[answer] = question
            BD.questions.add(question)
            view.findViewById<TextView>(R.id.quest).text = ""
            view.findViewById<TextView>(R.id.ans).text = ""
        }

        super.onViewCreated(view, savedInstanceState)
    }
}
