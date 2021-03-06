import java.util.ArrayList;
import java.util.NoSuchElementException;

//SUBMIT
public class BNode implements BNodeInterface {

    // ///////////////////BEGIN DO NOT CHANGE ///////////////////
    // ///////////////////BEGIN DO NOT CHANGE ///////////////////
    // ///////////////////BEGIN DO NOT CHANGE ///////////////////
    private final int t;
    private int numOfBlocks;
    private boolean isLeaf;
    private ArrayList<Block> blocksList;
    private ArrayList<BNode> childrenList;

    /**
     * Constructor for creating a node with a single child.<br>
     * Useful for creating a new root.
     */
    public BNode(int t, BNode firstChild) {
        this(t, false, 0);
        this.childrenList.add(firstChild);
    }

    /**
     * Constructor for creating a <b>leaf</b> node with a single block.
     */
    public BNode(int t, Block firstBlock) {
        this(t, true, 1);
        this.blocksList.add(firstBlock);
    }

    public BNode(int t, boolean isLeaf, int numOfBlocks) {
        this.t = t;
        this.isLeaf = isLeaf;
        this.numOfBlocks = numOfBlocks;
        this.blocksList = new ArrayList<Block>();
        this.childrenList = new ArrayList<BNode>();
    }

    // For testing purposes.
    public BNode(int t, int numOfBlocks, boolean isLeaf,
                 ArrayList<Block> blocksList, ArrayList<BNode> childrenList) {
        this.t = t;
        this.numOfBlocks = numOfBlocks;
        this.isLeaf = isLeaf;
        this.blocksList = blocksList;
        this.childrenList = childrenList;
    }

    @Override
    public int getT() {
        return t;
    }

    @Override
    public int getNumOfBlocks() {
        return numOfBlocks;
    }

    @Override
    public boolean isLeaf() {
        return isLeaf;
    }

    @Override
    public ArrayList<Block> getBlocksList() {
        return blocksList;
    }

    @Override
    public ArrayList<BNode> getChildrenList() {
        return childrenList;
    }

    @Override
    public boolean isFull() {
        return numOfBlocks == 2 * t - 1;
    }

    @Override
    public boolean isMinSize() {
        return numOfBlocks == t - 1;
    }

    @Override
    public boolean isEmpty() {
        return numOfBlocks == 0;
    }

    @Override
    public int getBlockKeyAt(int indx) {
        return blocksList.get(indx).getKey();
    }

    @Override
    public Block getBlockAt(int indx) {
        return blocksList.get(indx);
    }

    @Override
    public BNode getChildAt(int indx) {
        return childrenList.get(indx);
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((blocksList == null) ? 0 : blocksList.hashCode());
        result = prime * result
                + ((childrenList == null) ? 0 : childrenList.hashCode());
        result = prime * result + (isLeaf ? 1231 : 1237);
        result = prime * result + numOfBlocks;
        result = prime * result + t;
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BNode other = (BNode) obj;
        if (blocksList == null) {
            if (other.blocksList != null)
                return false;
        } else if (!blocksList.equals(other.blocksList))
            return false;
        if (childrenList == null) {
            if (other.childrenList != null)
                return false;
        } else if (!childrenList.equals(other.childrenList))
            return false;
        if (isLeaf != other.isLeaf)
            return false;
        if (numOfBlocks != other.numOfBlocks)
            return false;
        if (t != other.t)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "BNode [t=" + t + ", numOfBlocks=" + numOfBlocks + ", isLeaf="
                + isLeaf + ", blocksList=" + blocksList + ", childrenList="
                + childrenList + "]";
    }

    // ///////////////////DO NOT CHANGE END///////////////////
    // ///////////////////DO NOT CHANGE END///////////////////
    // ///////////////////DO NOT CHANGE END///////////////////


    @Override
    public Block search(int key) {
        // find block index(child ot block)
        int i = 0;
        while (i < this.numOfBlocks && key > this.getBlockKeyAt(i))
            i++;

        if (i < this.numOfBlocks && key == this.getBlockKeyAt(i)) // if found return block
            return this.getBlockAt(i);
        else if (this.isLeaf) // if leaf -> no block, return
            return null;
        else // if not leaf -> search at child
            return this.getChildAt(i).search(key);
    }

    @Override
    public void insertNonFull(Block b) {
        // find block index(child ot block)
        int i = 0;
        while (i < numOfBlocks && getBlockKeyAt(i) < b.getKey())
            i++;

        if (isLeaf) {
            if (i == numOfBlocks || getBlockKeyAt(i) != b.getKey()) { // if is leaf and key not exist
                blocksList.add(i, b);
                numOfBlocks++;
            }
        } else {
            // if child is full split child before inserting
            if (getChildAt(i).isFull()) {
                splitChild(i);

                if (b.getKey() < getBlockKeyAt(i)) // check where to insert after split
                    getChildAt(i).insertNonFull(b);
                else
                    getChildAt(i + 1).insertNonFull(b);
            } else
                getChildAt(i).insertNonFull(b); // insert in child node
        }
    }

    @Override
    public void delete(int key) {
        // find block index(child ot block)
        int i = 0;
        while (i < numOfBlocks && getBlockKeyAt(i) < key)
            i++;

        if (isLeaf) {
            if (i < numOfBlocks && getBlockKeyAt(i) == key) { // if leaf and key found, remove block
                blocksList.remove(i);
                numOfBlocks--;
            }
        } else {
            if (i < numOfBlocks && getBlockKeyAt(i) == key) { // if key found  in current node
                BNode leftChild = getChildAt(i);
                BNode rightChild = getChildAt(i + 1);

                if (leftChild.isMinSize() && rightChild.isMinSize()) { // if both minimal size, merge them and delete
                    mergeWithRightSibling(i);
                    leftChild.delete(key);
                } else if (!leftChild.isMinSize()) { // if left is not minimal, remove it
                    Block childMax = leftChild.getMaxKeyBlock();
                    blocksList.remove(i);
                    blocksList.add(i, childMax);
                    leftChild.delete(childMax.getKey());
                } else { // if right is not minimal, remove it
                    Block childMin = rightChild.getMinKeyBlock();
                    blocksList.remove(i);
                    blocksList.add(i, childMin);
                    rightChild.delete(childMin.getKey());
                }
            } else { // if key is at child i
                BNode child = getChildAt(i);
                if (child.isMinSize()) { // if child is min size
                    if (childHasNonMinimalLeftSibling(i))
                        shiftFromLeftSibling(i);
                    else if (childHasNonMinimalRightSibling(i))
                        shiftFromRightSibling(i);
                    else // both left and right are minimal merge children
                        mergeChildWithSibling(i);
                }
                child.delete(key);
            }
        }
    }

    @Override
    public MerkleBNode createHashNode() {
        ArrayList<MerkleBNode> merkleChildren = new ArrayList<MerkleBNode>(numOfBlocks + 1);
        ArrayList<byte[]> bytes = new ArrayList<>(numOfBlocks); //list of bytes for sha1Hash
        for (int i = 0; i <= numOfBlocks; i++) {
            if (!isLeaf) { // if not leaf add children data
                MerkleBNode child = getChildAt(i).createHashNode();
                merkleChildren.add(child);
                bytes.add(child.getHashValue());
            }
            if (i < numOfBlocks) // go until <= numOfBlocks, last time for child only
                bytes.add(getBlockAt(i).getData());
        }
        return new MerkleBNode(HashUtils.sha1Hash(bytes), isLeaf, merkleChildren);
    }


    /**
     * Splits the child node at childIndex into 2 nodes.
     *
     * @param childIndex
     */
    public void splitChild(int childIndex) {
        BNode child = getChildAt(childIndex);
        BNode newChild = new BNode(t, child.isLeaf, t - 1);
        for (int i = 0; i < t - 1; i++) // move t-1 blocks to newChild
            newChild.blocksList.add(child.blocksList.remove(t));

        if (!child.isLeaf) // if not child move children as well
            for (int j = 0; j < t; j++)
                newChild.childrenList.add(child.childrenList.remove(t));

        blocksList.add(childIndex, child.blocksList.remove(t - 1)); // add block at t-1 to parent
        childrenList.add(childIndex + 1, newChild); // add newChild to parent at childIndex + 1
        numOfBlocks++; // increase numOfBlocks
        child.numOfBlocks = t - 1; // set numOfBlock for child to t - 1
    }

    /**
     * True if the child node at childIndx-1 exists and has more than t-1 blocks.
     *
     * @param childIndx
     * @return
     */
    private boolean childHasNonMinimalLeftSibling(int childIndx) {
        // if child at childIndx - 1 exists and has more than t-1 blocks
        return numOfBlocks >= childIndx - 1 && childIndx > 0 && getChildAt(childIndx - 1).getNumOfBlocks() > t - 1;
    }

    /**
     * True if the child node at childIndx+1 exists and has more than t-1 blocks.
     *
     * @param childIndx
     * @return
     */
    private boolean childHasNonMinimalRightSibling(int childIndx) {
        // if child at childIndx + 1 exists and has more than t-1 blocks
        return numOfBlocks >= childIndx + 1 && getChildAt(childIndx + 1).getNumOfBlocks() > t - 1;
    }

    /**
     * Add additional block to the child node at childIndx, by shifting from left sibling.
     *
     * @param childIndx
     */
    private void shiftFromLeftSibling(int childIndx) {
        BNode rightChild = getChildAt(childIndx);
        BNode leftChild = getChildAt(childIndx - 1);

        // move parent block to right child
        Block parentMovingBlock = getBlockAt(childIndx - 1);
        rightChild.blocksList.add(0, parentMovingBlock);
        blocksList.remove(childIndx - 1);
        rightChild.numOfBlocks++;

        if (!leftChild.isLeaf) {
            // move left max child to min right child
            BNode leftChildMaxChild = leftChild.getChildAt(leftChild.numOfBlocks);
            rightChild.childrenList.add(0, leftChildMaxChild);
            leftChild.childrenList.remove(leftChild.numOfBlocks);
        }

        // move left max block to parent
        Block leftChildMaxBlock = leftChild.getBlockAt(leftChild.numOfBlocks - 1);
        blocksList.add(childIndx - 1, leftChildMaxBlock);
        leftChild.blocksList.remove(leftChild.numOfBlocks - 1);
        leftChild.numOfBlocks--;
    }

    /**
     * Add additional block to the child node at childIndx, by shifting from right sibling.
     *
     * @param childIndx
     */
    private void shiftFromRightSibling(int childIndx) {
        BNode leftChild = getChildAt(childIndx);
        BNode rightChild = getChildAt(childIndx + 1);

        // move parent block to left child
        Block parentMovingBlock = getBlockAt(childIndx);
        leftChild.blocksList.add(leftChild.numOfBlocks, parentMovingBlock);
        blocksList.remove(childIndx);
        leftChild.numOfBlocks++;

        if (!rightChild.isLeaf) {
            // move right min child to max right child
            BNode rightChildMinChild = rightChild.getChildAt(0);
            leftChild.childrenList.add(leftChild.numOfBlocks, rightChildMinChild);
            rightChild.childrenList.remove(0);
        }

        // move right min block to parent
        Block rightChildMinBlock = rightChild.getBlockAt(0);
        blocksList.add(childIndx, rightChildMinBlock);
        rightChild.blocksList.remove(0);
        rightChild.numOfBlocks--;

    }

    /**
     * Merges the child node at childIndx with its left or right sibling.
     *
     * @param childIndx
     */
    public void mergeChildWithSibling(int childIndx) {
        if (!childHasNonMinimalLeftSibling(childIndx) && childIndx > 0)
            mergeWithLeftSibling(childIndx);
        else
            mergeWithRightSibling(childIndx);
    }

    /**
     * Merges the child node at childIndx with its left sibling.<br>
     * The left sibling node is removed.
     *
     * @param childIndx
     */
    private void mergeWithLeftSibling(int childIndx) {
        BNode rightChild = childrenList.get(childIndx);
        BNode leftChild = childrenList.get(childIndx - 1);
        rightChild.blocksList.add(0, getBlockAt(childIndx - 1)); // move block in parent between left and right children
        blocksList.remove(childIndx - 1);
        numOfBlocks--;

        for (int i = 0; i <= leftChild.numOfBlocks; i++) {
            if (i != leftChild.numOfBlocks)
                rightChild.blocksList.add(i, leftChild.getBlockAt(i)); // move all blocks from leftChild
            if (!leftChild.isLeaf)
                rightChild.childrenList.add(i, leftChild.getChildAt(i)); // move all children from leftChild
        }
        rightChild.numOfBlocks += leftChild.numOfBlocks + 1;
        childrenList.remove(childIndx - 1); // remove leftChild
    }

    /**
     * Merges the child node at childIndx with its right sibling.<br>
     * The right sibling node is removed.
     *
     * @param childIndx
     */
    private void mergeWithRightSibling(int childIndx) {
        BNode leftChild = childrenList.get(childIndx);
        BNode rightChild = childrenList.get(childIndx + 1);
        // move block in parent between left and right children
        leftChild.blocksList.add(leftChild.numOfBlocks, getBlockAt(childIndx));
        blocksList.remove(childIndx);
        numOfBlocks--;

        leftChild.blocksList.addAll(rightChild.blocksList); // move all blocks from rightChild
        leftChild.childrenList.addAll(rightChild.childrenList); // move all children from rightChild
        leftChild.numOfBlocks += rightChild.numOfBlocks + 1;
        childrenList.remove(childIndx + 1); // remove rightChild
    }

    /**
     * Finds and returns the block with the min key in the subtree.
     *
     * @return min key block
     */
    private Block getMinKeyBlock() {
        if (isLeaf)
            return getBlockAt(0);
        return getChildAt(0).getMinKeyBlock(); // go recursively to most left child
    }

    /**
     * Finds and returns the block with the max key in the subtree.
     *
     * @return max key block
     */
    private Block getMaxKeyBlock() {
        if (isLeaf)
            return getBlockAt(numOfBlocks - 1);
        return getChildAt(numOfBlocks).getMaxKeyBlock(); // go recursively to most right child
    }
}
