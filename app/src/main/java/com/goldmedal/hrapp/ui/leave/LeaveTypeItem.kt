package com.goldmedal.hrapp.ui.leave


import android.graphics.Color
import android.view.View
import androidx.databinding.BindingAdapter
import com.goldmedal.hrapp.R
import com.goldmedal.hrapp.data.model.LeaveTypeData
import com.goldmedal.hrapp.databinding.LeaveTypeRowBinding
import com.xwray.groupie.viewbinding.BindableItem
import de.hdodenhof.circleimageview.CircleImageView


class LeaveTypeItem(
        private val leaveRecord: LeaveTypeData?,
val leaveTypeListener: OnLeaveTypeClickedListener) : BindableItem<LeaveTypeRowBinding>() {

    override fun bind(viewBinding: LeaveTypeRowBinding, position: Int) {
        viewBinding.leaveTypeModel = leaveRecord
        setImageViewResource(viewBinding.imvLeaveType,leaveRecord?.LeaveTypeColour ?: "#d32f2f")

        viewBinding.itemView.setOnClickListener {

            leaveTypeListener.onLeaveTypeClicked(leaveRecord)
        }

    }


    override fun getLayout() = R.layout.leave_type_row
    override fun initializeViewBinding(view: View): LeaveTypeRowBinding = LeaveTypeRowBinding.bind(view)
    interface OnLeaveTypeClickedListener {
        fun onLeaveTypeClicked(model: LeaveTypeData?)
    }
}
@BindingAdapter("android:src")
fun setImageViewResource(view: CircleImageView, hexString : String) {
    view.circleBackgroundColor = Color.parseColor(hexString)
}