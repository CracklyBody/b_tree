import java.util.*

class BTree(private val t: Int) {
    private val node: BNode?

    init {
        node = BNode(null)
    }
    class BNode (var parent: BNode?) {
        private var keys: MutableList<Int> = mutableListOf()
        var children: MutableList<BNode> = mutableListOf()

        override fun hashCode(): Int {
            var sum = 0
            for (i in children.indices) sum += children[i].hashCode()
            for (i in keys.indices) sum += keys[i]
            return sum + children.size
        }

        fun add_child(child: BNode) {
            children.add(child)
            child.parent = this
        }

        fun remove() {
            parent?.delete_child(this)
            parent = null
            keys = mutableListOf()
        }

        fun delete_child(child: BNode?) {
            children.remove(child)
        }

        fun insert_key(value: Int) {
            keys.add(value)
            keys.sort()
        }

        fun delete_key(value: Int?) {
            keys.remove(value!!)
        }

        fun restruct() {
            if (keys.size != 2 * t - 1) return
            val first_child = BNode(this)
            val second_child = BNode(this)
            for (i in 0 until children.size / 2) {
                first_child.add_child(children[i])
                second_child.add_child(children[i + children.size / 2])
            }
            for (i in 0 until (keys.size - 1) / 2) {
                first_child.insert_key(keys[i])
                second_child.insert_key(keys[(keys.size + 1) / 2])
            }
            if (parent == null) {
                val key = keys[t - 1]
                keys = ArrayList()
                keys.add(key)
                children.add(first_child)
                children.add(second_child)
            } else {
                val key = keys[(keys.size + 1) / 2 - 1]
                parent!!.insert_key(key)
                parent!!.restruct()
                parent!!.add_child(first_child)
                parent!!.add_child(second_child)
                parent!!.children.remove(this)
            }
        }

        fun add_key(value: Int): Boolean {
            if (keys.size == 0 && parent == null) {
                keys.add(value)
                return true
            }
            var flag = false
            if (children.size == 0) {
                var i = 0
                while (i < keys.size && !flag) {
                    if (value < keys[i]) {
                        insert_key(value)
                        flag = true
                    }
                    i++
                }
                if (!flag && keys.size < 2 * t - 1) {
                    insert_key(value)
                    flag = true
                }
                restruct()
            } else {
                var i = 0
                while (i < children.size && !flag) {
                    if (children[i].keys[children[i].keys.size - 1] > value) flag =
                        children[i].add_key(value)
                    i++
                }
            }
            if (parent == null && !flag) {
                var child = children[children.size - 1]
                while (child.children.size > 0) child = child.children[child.children.size - 1]
                child.insert_key(value)
                child.restruct()
            }
            return flag
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as BNode

            if (keys != other.keys) return false
            if (children != other.children) return false
            if (parent != other.parent) return false

            return true
        }
    }

}