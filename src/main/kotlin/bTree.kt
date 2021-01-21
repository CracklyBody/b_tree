import java.util.*

class BTree(private val parameter: Int) {
    private val node: BNode?

    init {
        node = BNode(null)
    }
    inner class BNode (var parent: BNode?) {
        var keys: MutableList<Int> = mutableListOf()
        var children: MutableList<BNode> = mutableListOf()

        override fun hashCode(): Int {
            var sum = 0
            for (i in children.indices) sum += children[i].hashCode()
            for (i in keys.indices) sum += keys[i]
            return sum + children.size
        }

        private fun add_child(child: BNode) {
            children.add(child)
            child.parent = this
        }

        fun remove() {
            parent?.delete_child(this)
            parent = null
            keys = mutableListOf()
        }

        private fun delete_child(child: BNode?) {
            children.remove(child)
        }

        fun insert_key(value: Int) {
            keys.add(value)
            keys.sort()
        }

        fun delete_key(value: Int?) {
            keys.remove(value!!)
        }

        private fun restruct() {
            if (keys.size != 2 * parameter - 1) return
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
                val key = keys[parameter - 1]
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
                if (!flag && keys.size < 2 * parameter - 1) {
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

    fun search(key: Int): Boolean {
        var search_node = node!!
        var flag = false
        var index_node = 0
        if (search_node.keys[search_node.keys.size - 1] >= key) {
            var i = 0
            while (i < search_node.keys.size && !flag) {
                if (key == search_node.keys[i]) flag = true
                i++
            }
        }
        while (!flag) {
            for (child in search_node.children) {
                if (child.keys[child.keys.size - 1]
                    >= key
                ) {
                    for (value in child.keys) {
                        if (key == value) {
                            flag = true
                            return flag
                        }
                    }
                    index_node = search_node.children.indexOf(child)
                    break
                }
            }
            search_node = if (search_node.children.size != 0) search_node.children[index_node] else break
        }
        return flag
    }

    private fun delete_node(bnode: BNode, key: Int) {
        var left_right = 0
        var flag = false
        if (bnode.children.size == 0) {
            var bNodeNeighbor = bnode
            var key_del = -1
            var i = 1
            while (i < bnode.parent!!.keys.size && key_del == -1) {
                if (key > bnode.parent!!.keys[i]) key_del = bnode.parent!!.keys[i]
                i++
            }
            if (bnode.parent!!.children.indexOf(bnode) != 0) {
                bNodeNeighbor = bnode.parent!!.children[bnode.parent!!.children.indexOf(bnode) - 1]
                if (bNodeNeighbor.keys.size > parameter - 1) {
                    bnode.parent!!.delete_key(key_del)
                    bnode.insert_key(key_del)
                    bnode.parent!!.insert_key(bNodeNeighbor.keys[bNodeNeighbor.keys.size - 1])
                    bNodeNeighbor.delete_key(bNodeNeighbor.keys[bNodeNeighbor.keys.size - 1])
                    flag = true
                } else left_right = -1
            } else if (bnode.parent!!.children.indexOf(bnode) != bnode.parent!!
                    .children.size - 1 && !flag
            ) {
                bNodeNeighbor = bnode.parent!!.children[bnode.parent!!.children.indexOf(bnode) + 1]
                if (bNodeNeighbor.keys.size > parameter - 1) {
                    bnode.parent!!.delete_key(key_del)
                    bnode.insert_key(key_del)
                    bnode.parent!!.insert_key(bNodeNeighbor.keys[0])
                    bNodeNeighbor.delete_key(bNodeNeighbor.keys[0])
                    flag = true
                } else left_right = 1
            } else if (!flag) {
                for (value in bnode.keys) {
                    bnode.parent!!.children[bnode.parent!!.children.indexOf(bnode) + left_right]
                        .insert_key(value)
                }
                bnode.parent!!.children[bnode.parent!!.children.indexOf(bnode) + left_right]
                    .insert_key(key_del)
                bnode.parent!!.delete_key(key_del)
                bnode.remove()
            }
        } else {
            var right_child = bnode
            var left_child = bnode
            for (i in bnode.children.indices) {
                if (key > bnode.children[i].keys[0]) {
                    right_child = bnode.children[i]
                    left_child = bnode.children[i - 1]
                    break
                }
            }
            when {
                left_child.keys.size > parameter - 1 -> {
                    val child_key: Int = left_child.keys.get(left_child.keys.size - 1)
                    bnode.insert_key(child_key)
                    left_child.delete_key(child_key)
                    delete_node(left_child, child_key)
                }
                right_child.keys.size > parameter - 1 -> {
                    val child_key: Int = right_child.keys.get(0)
                    bnode.insert_key(child_key)
                    right_child.delete_key(child_key)
                    delete_node(right_child, child_key)
                }
                else -> {
                    for (value in right_child.keys) {
                        left_child.insert_key(value)
                    }
                    right_child.remove()
                    delete_node(left_child, key)
                }
            }
        }
    }

    fun delete(key: Int) {
        var search_node = node!!
        var index_node = 0
        if (search_node.keys[search_node.keys.size - 1] >= key) {
            for (i in search_node.keys.indices) if (key == search_node.keys[i]) {
                search_node.delete_key(key)
                if (search_node.keys.size <= parameter - 1) delete_node(search_node, key)
                return
            }
        }
        while (true) {
            for (child in search_node.children) {
                if (child.keys[child.keys.size - 1]
                    >= key
                ) {
                    for (value in child.keys) {
                        if (key == value) {
                            child.delete_key(key)
                            if (child.keys.size <= parameter - 1) delete_node(child, key)
                            return
                        }
                    }
                    index_node = search_node.children.indexOf(child)
                    break
                }
            }
            search_node = if (search_node.children.size != 0) search_node.children[index_node] else break
        }
    }

    fun insert(value: Int?): BNode? {
        node!!.add_key(value!!)
        return node
    }
}