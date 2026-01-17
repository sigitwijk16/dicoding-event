import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalSpaceItemDecoration(
    private val sidePadding: Int,
    private val itemSpacing: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        when (position) {
            0 -> {
                outRect.left = sidePadding
                outRect.right = itemSpacing
            }
            itemCount - 1 -> {
                outRect.left = itemSpacing
                outRect.right = sidePadding
            }
            else -> {
                outRect.left = itemSpacing
                outRect.right = itemSpacing
            }
        }
    }
}
