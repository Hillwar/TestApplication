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
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.hillwar.testapplication.R
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import kotlin.collections.ArrayList

class WorkoutFragment : Fragment() {

    private var anim: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_workout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
                list.add(p0.toString())
                val temp = list[0]
                list.removeAt(0)
                list.shuffle()
                list.add(0, temp)
                adapter.notifyDataSetChanged()
            }

            override fun onRightCardExit(p0: Any?) {
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
                        if (swipe.selectedView.findViewById<TextView>(R.id.quest).text != BD.answer[data.toString()]) swipe.selectedView.findViewById<TextView>(R.id.quest).text = BD.answer[data.toString()]
                        else swipe.selectedView.findViewById<TextView>(R.id.quest).text = data.toString()
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
