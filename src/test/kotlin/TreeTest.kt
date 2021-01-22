import BTree.BNode
import org.junit.Assert
import org.junit.Test
import java.util.*

class TreeTest : Assert() {
    @Test
    fun insertElementsTree() {
        val t = 2
        val tree = BTree(t)
        val expectedTree = BTree(t)
        expectedTree.insert(1)
        expectedTree.insert(2)
        expectedTree.insert(3)
        expectedTree.insert(4)
        expectedTree.insert(5)
        expectedTree.insert(6)

        tree.insert(1)
        tree.insert(2)
        tree.insert(3)
        tree.insert(4)
        tree.insert(5)
        tree.insert(6)
        assertEquals(expectedTree, tree)
    }

    @Test
    fun searchElement() {
        val t = 2
        val tree = BTree(t)
        tree.insert(1)
        tree.insert(2)
        tree.insert(3)
        tree.insert(4)
        tree.insert(5)
        val result = tree.search(6)
        assertFalse(result)
    }

    @Test
    fun deleteElementTree() {
        val t = 2
        val tree = BTree(t)
        val treeExpected = BTree(t)
        treeExpected.insert(1)
        treeExpected.insert(2)
        treeExpected.insert(4)
        treeExpected.insert(5)
        treeExpected.insert(6)

        tree.insert(1)
        tree.insert(2)
        tree.insert(3)
        tree.insert(4)
        tree.insert(5)
        tree.insert(6)
        tree.delete(3)
        assertEquals(treeExpected, tree)
    }
}