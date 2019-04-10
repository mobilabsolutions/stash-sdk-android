package com.mobilabsolutions.payment.sample.main.items

/**
 * @author <a href="biju@mobilabsolutions.com">Biju Parvathy</a> on 09-04-2019.
 */

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.mobilabsolutions.payment.sample.R
import kotlinx.android.synthetic.main.layout_item.view.*
import java.math.BigDecimal

@EpoxyModelClass(layout = R.layout.layout_item)
abstract class ItemModel : EpoxyModelWithHolder<ItemModel.ViewHolder>() {

    @EpoxyAttribute
    var id: Long = 0

    @EpoxyAttribute
    @DrawableRes
    var image: Int = 0

    @EpoxyAttribute
    var title: String? = ""

    @EpoxyAttribute
    var desc: String = ""

    @EpoxyAttribute
    var price: BigDecimal = BigDecimal.ZERO

    override fun bind(holder: ViewHolder) {
        holder.imageView.setImageResource(image)
        holder.titleView.text = title
        holder.descView.text = desc
        holder.priceView.text = price.toPlainString()
    }

    inner class ViewHolder : EpoxyHolder() {

        lateinit var imageView: ImageView

        lateinit var titleView: TextView

        lateinit var descView: TextView

        lateinit var priceView: TextView

        override fun bindView(itemView: View) {
            imageView = itemView.image
            titleView = itemView.title
            descView = itemView.desc
            priceView = itemView.price
        }
    }
}