package com.goldmedal.hrapp.ui.dashboard.profile

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.common.customviews.treeview.BaseTreeAdapter
import com.goldmedal.hrapp.common.customviews.treeview.TreeNode
import com.goldmedal.hrapp.common.customviews.treeview.TreeView
import com.goldmedal.hrapp.util.snackbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.hdodenhof.circleimageview.CircleImageView


class FamilyTreeActivity : AppCompatActivity() {

    private var mCurrentNode: TreeNode? = null
    private var nodeCount = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_family_tree)

        val treeView = findViewById<TreeView>(R.id.tree)
        val addButton = findViewById<FloatingActionButton>(R.id.addNode)

        val adapter: BaseTreeAdapter<ViewHolder> = object : BaseTreeAdapter<ViewHolder>(this, R.layout.tree_node) {
            override fun onCreateViewHolder(view: View): ViewHolder {



                return ViewHolder(view)
            }

            override fun onBindViewHolder(viewHolder: ViewHolder, data: Any?, position: Int) {


                if(position == 0){
                viewHolder.mImageView.visibility = View.GONE
                    viewHolder.mTextView.text = " Family Tree "
                    viewHolder.mTextView.setTextColor(resources.getColor(R.color.colorWhite))
                    viewHolder.mLinearLayout.setBackgroundColor(resources.getColor(R.color.light_blue_900))
                }else {
                    viewHolder.mImageView.visibility = View.VISIBLE
                    viewHolder.mTextView.setTextColor(resources.getColor(R.color.colorBlack))
                    viewHolder.mLinearLayout.setBackgroundColor(resources.getColor(R.color.colorWhite))
                    viewHolder.mTextView.text = data.toString()
                }


            }
        }

        treeView.adapter = adapter
        addButton.setOnClickListener {
            if (mCurrentNode != null) {
                mCurrentNode?.addChild(TreeNode(getNodeText()))
            } else {
                adapter.setRootNode(TreeNode(getNodeText()))
            }
        }

        // example tree
        mCurrentNode = TreeNode( " Family Tree " )
        val father = (TreeNode("Father"))
        val mom = (TreeNode("Mother"))
        val me = (TreeNode("me"))
        val wife = (TreeNode("wife"))
        val scott = (TreeNode("scott"))
        val matt  = (TreeNode("matt"))
        val daisy = (TreeNode("daisy"))
       val parents =  mCurrentNode?.addChildren(father,mom)

//        mCurrentNode?.addChild(mom)
//        parents.add(me)
        mom.addChild(me)
        mom.addChild(wife)

        wife.addChildren(scott,matt,daisy)




        mCurrentNode?.let {
            adapter.setRootNode(it)
        }


        treeView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            mCurrentNode = adapter.getNode(position)
            treeView.snackbar(mCurrentNode?.data.toString())
        }

    }

    private class ViewHolder(view: View) {
        var mTextView: TextView
        var mImageView: CircleImageView
        var mLinearLayout: LinearLayout

        init {
            mImageView = view.findViewById(R.id.imgProfilePic)
            mTextView = view.findViewById(R.id.textView)
            mLinearLayout = view.findViewById(R.id.root_layout)
        }
    }

    private fun getNodeText(): String? {
        return "Node " + nodeCount++
    }

    companion object {
        const val VIEW_TYPE_HEADER  = 1
        const val VIEW_TYPE_ITEM = 2
    }
}