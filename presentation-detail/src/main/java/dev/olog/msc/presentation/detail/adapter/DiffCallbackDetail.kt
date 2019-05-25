package dev.olog.msc.presentation.detail.adapter

import androidx.recyclerview.widget.DiffUtil
import dev.olog.msc.presentation.base.list.model.DisplayableItem

object DiffCallbackDetail : DiffUtil.ItemCallback<DisplayableItem>(){
    override fun areItemsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: DisplayableItem, newItem: DisplayableItem): Any? {
        if (!newItem.mediaId.isLeaf && oldItem.subtitle != newItem.subtitle){
            return newItem.subtitle
        }
        return super.getChangePayload(oldItem, newItem)
    }

}