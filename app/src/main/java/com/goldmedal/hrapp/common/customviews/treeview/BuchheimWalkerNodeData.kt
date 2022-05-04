package com.goldmedal.hrapp.common.customviews.treeview




/**
 * Class to save additional data used by the buchheim-walker algorithm.
 */
public class BuchheimWalkerNodeData {
    private var mAncestor: TreeNode? = null
    private var mThread: TreeNode? = null
    var number = 0
    var depth = 0
    var prelim = 0.0
    var modifier = 0.0
    var shift = 0.0
    var change = 0.0
    var ancestor: TreeNode?
        get() = mAncestor
        set(ancestor) {
            mAncestor = ancestor
        }
    var thread: TreeNode?
        get() = mThread
        set(thread) {
            mThread = thread
        }
}