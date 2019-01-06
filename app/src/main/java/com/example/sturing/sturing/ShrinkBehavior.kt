package com.example.sturing.sturing

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.util.AttributeSet
import android.view.View

class ShrinkBehavior(context: Context, attributeSet: AttributeSet): CoordinatorLayout.Behavior<View>(context, attributeSet) {

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return dependency is Snackbar.SnackbarLayout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        if (parent == null || child == null || dependency == null)
            return false

        val distanceY = getViewOffsetForSnackbar(parent, child)
        val fractionComplete = distanceY / dependency.height
        val scaleFactor = 1 - fractionComplete

        child.scaleX = scaleFactor
        child.scaleY = scaleFactor
        return true
    }

    private fun getViewOffsetForSnackbar(parent: CoordinatorLayout, view: View): Float {
        var maxOffset = 0f
        val dependecies = parent.getDependencies(view)

        dependecies.forEach {dependecy ->
            if (dependecy is Snackbar.SnackbarLayout && parent.doViewsOverlap(view, dependecy)) {
                maxOffset = Math.max(maxOffset, (dependecy.translationY - dependecy.height) * -1)
            }
        }

        return maxOffset
    }

}