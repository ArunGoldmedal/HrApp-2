package com.goldmedal.hrapp.common.customviews.treeview

import android.graphics.Point
import android.view.View
import android.widget.Adapter


/**
 *
 */
interface TreeAdapter<VH> : Adapter, TreeNodeObserver {
    fun notifySizeChanged()

    /**
     * Returns the currently set algorithm. It uses the [BuchheimWalkerAlgorithm] as default,
     * if no algorithm is previously set.
     *
     * @return
     */
    fun getAlgorithm(): Algorithm?

    /**
     * Set an algorithm, which is used for laying out the tree.
     *
     * @param algorithm the algorithm to use for laying out the tree
     */
    fun setAlgorithm(algorithm: Algorithm)

    /**
     * Set a new root node. This triggers the re-drawing of the whole view.
     *
     * @param rootNode
     */
    fun setRootNode(rootNode: TreeNode)

    /**
     * Returns the node at a given {code position}.
     *
     * @param position
     * @return
     */
    fun getNode(position: Int): TreeNode?

    /**
     * Returns the screen position from the node at {code position}
     *
     * @param position
     * @return
     */
    fun getScreenPosition(position: Int): Point?
    fun onCreateViewHolder(view: View): VH
    fun onBindViewHolder(viewHolder: VH, data: Any?, position: Int)
}