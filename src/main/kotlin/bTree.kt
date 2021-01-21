import java.util.*

class BTree(private val t: Int) {
    private val node: BNode?

    init {
        node = BNode(null)
    }
    class BNode(private var parent: BNode?){
        private var keys: List<Int> = arrayListOf()
        private var children: List<BNode> = arrayListOf()
    }

}