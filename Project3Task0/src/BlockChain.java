import java.util.ArrayList;
import java.sql.Timestamp;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

/**
 * Author: JYOTI GIRDHARI KHANCHANDANI (JKHANCHA)
 * Last Modified: October 27, 2024
 *
 * This class represents a simple BlockChain.
 * It maintains an ArrayList of Blocks, a chain hash of the most recently added Block,
 * and an approximation of hashes per second on the current machine.
 *
 * Note: This code was generated with assistance from Perplexity AI.
 */
public class BlockChain {
    private ArrayList<Block> blocks;
    private String chainHash;
    private int hashesPerSecond;

    /**
     * Constructs a new BlockChain.
     * Initializes an empty ArrayList for Block storage, sets the chain hash to an empty string,
     * and sets hashes per second to 0.
     */
    public BlockChain() {
        this.blocks = new ArrayList<>();
        this.chainHash = "";
        this.hashesPerSecond = 0;
        computeHashesPerSecond();
    }

    /**
     * Gets the chain hash.
     *
     * @return The hash of the most recently added Block.
     */
    public String getChainHash() {
        return chainHash;
    }

    /**
     * Gets the current system time.
     *
     * @return The current system time as a Timestamp.
     */
    public Timestamp getTime() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * Gets the most recently added Block.
     *
     * @return A reference to the most recently added Block, or null if the chain is empty.
     */
    public Block getLatestBlock() {
        return blocks.isEmpty() ? null : blocks.get(blocks.size() - 1);
    }

    /**
     * Gets the size of the chain.
     *
     * @return The number of Blocks in the chain.
     */
    public int getChainSize() {
        return blocks.size();
    }

    /**
     * Computes the approximate number of hashes per second on this machine.
     * This method computes exactly 2 million hashes and times how long that process takes.
     */
    public void computeHashesPerSecond() {
        long startTime = System.currentTimeMillis();
        String testString = "00000000";
        for (int i = 0; i < 2_000_000; i++) {
            simulateHashCalculation(testString);
        }
        long endTime = System.currentTimeMillis();
        double seconds = (endTime - startTime) / 1000.0;
        this.hashesPerSecond = (int) (2_000_000 / seconds);
    }

    /**
     * Gets the number of hashes per second.
     *
     * @return The approximate number of hashes per second.
     */
    public int getHashesPerSecond() {
        return hashesPerSecond;
    }

    /**
     * Adds a new Block to the BlockChain.
     *
     * @param newBlock The Block to be added to the chain.
     */
    public void addBlock(Block newBlock) {
        if (!blocks.isEmpty()) {
            Block latestBlock = getLatestBlock();
            newBlock.setPreviousHash(latestBlock.calculateHash());
        }
        newBlock.proofOfWork();
        blocks.add(newBlock);
        chainHash = newBlock.calculateHash();
    }

    /**
     * Returns a String representation of the entire chain.
     *
     * @return A JSON-like String representation of the BlockChain.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (Block block : blocks) {
            sb.append(block.toString()).append(",\n");
        }
        if (!blocks.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("\n]");
        return sb.toString();
    }

    /**
     * Gets the Block at a specific position in the chain.
     *
     * @param i The index of the Block to retrieve.
     * @return The Block at position i, or null if i is out of bounds.
     */
    public Block getBlock(int i) {
        return (i >= 0 && i < blocks.size()) ? blocks.get(i) : null;
    }

    /**
     * Computes the total difficulty of all Blocks on the chain.
     *
     * @return The sum of difficulties of all Blocks.
     */
    public int getTotalDifficulty() {
        return blocks.stream().mapToInt(Block::getDifficulty).sum();
    }

    /**
     * Computes the expected number of hashes required for the entire chain.
     *
     * @return The total expected number of hashes for all Blocks.
     */
    public double getTotalExpectedHashes() {
        return blocks.stream().mapToDouble(b -> Math.pow(16, b.getDifficulty())).sum();
    }

    /**
     * Validates the entire BlockChain.
     *
     * @return "TRUE" if the chain is valid, otherwise a String with an error message.
     */
    public String isChainValid() {
        if (blocks.isEmpty()) {
            return "TRUE";
        }

        for (int i = 0; i < blocks.size(); i++) {
            Block currentBlock = blocks.get(i);
            String blockHash = currentBlock.calculateHash();

            if (!blockHash.substring(0, currentBlock.getDifficulty()).equals("0".repeat(currentBlock.getDifficulty()))) {
                return "Error: Invalid proof of work in block " + i;
            }

            if (i > 0) {
                Block previousBlock = blocks.get(i - 1);
                if (!currentBlock.getPreviousHash().equals(previousBlock.calculateHash())) {
                    return "Chain Validation : FALSE " +
                            "Error: Invalid hash chain at block " + i;
                }
            }
        }

        if (!chainHash.equals(getLatestBlock().calculateHash())) {
            return "Chain Validation : FALSE" +
                    "Error: Invalid chain hash";
        }

        return "Chain Validation : TRUE";
    }

    /**
     * Repairs the chain by recomputing hashes and proof of work where necessary.
     */
    public void repairChain() {
        for (int i = 0; i < blocks.size(); i++) {
            Block currentBlock = blocks.get(i);
            if (i > 0) {
                Block previousBlock = blocks.get(i - 1);
                currentBlock.setPreviousHash(previousBlock.calculateHash());
            }
            currentBlock.proofOfWork();
        }
        chainHash = getLatestBlock().calculateHash();
    }

    /**
     * Simulates a hash calculation for performance testing purposes.
     * This method doesn't actually calculate a hash, but instead performs
     * a simple string manipulation to simulate computational work.
     *
     * @param input The string to be "hashed" (not actually used in this simulation)
     * @return A fixed string, simulating a hash result
     */

    private String simulateHashCalculation(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {



        /*
                                                PERFORMANCE ANALYSIS

                                        addBlock() Operation
As the difficulty increases, the time required to add a new block increases exponentially. This is due to the proof-of-work algorithm needing to find a hash with more leading zeros.
 Difficulty 2: 0 milliseconds
Difficulty 3: 15 milliseconds
Difficulty 4: 127 milliseconds
Difficulty 5: 47 milliseconds (This anomaly IS due to random chance in finding a suitable hash, verified with multiple experiments)
Difficulty 6: 12,568 milliseconds (about 12.6 seconds)
Difficulty 7: 130,342 milliseconds (about 2.17 minutes) We can see that the time increases dramatically as difficulty increases. The jump from difficulty 5 to 6 and then to 7 shows a clear exponential growth in computation time.
isChainValid() Operation
The chain validation operation is consistently fast, taking 0 milliseconds in both experiments.
 This suggests that verifying the chain is a relatively quick process, regardless of the chain's length or the difficulty of its blocks.


chainRepair() Operation
In Experiment 1, repairing the chain took 150 milliseconds for a chain with 4 blocks (3 with difficulty 4 and 1 with difficulty 2).
In Experiment 2, repairing the chain took 40,522 milliseconds (about 40.5 seconds) for a chain with 7 blocks of varying difficulties (up to difficulty 7).
The significant increase in repair time from Experiment 1 to Experiment 2 is due to two factors:

    The increased number of blocks in the chain.
    The higher difficulty levels of the later blocks, especially the last block with difficulty 7.



Overall Observations

    Proof of Work Impact: The time to add a block or repair the chain is primarily determined by the proof-of-work algorithm, which becomes exponentially more time-consuming as difficulty increases.
    Verification Speed: Chain verification remains fast regardless of chain length or block difficulty. This is because verification only involves checking existing hashes, not computing new ones.
    Exponential Growth: The time required for operations involving proof of work (adding blocks and repairing the chain) grows exponentially with increased difficulty.
    Variability in Mining Time: There's some variability in block addition times (e.g., difficulty 5 taking less time than difficulty 4), which is expected due to the random nature of finding a suitable hash.
    Repair Time Correlation: The time to repair the chain correlates strongly with the difficulty of the blocks that need to be re-mined.

In conclusion, while increasing difficulty significantly enhances the security of the blockchain by making it more computationally expensive to add or modify blocks, it also dramatically increases the time and computational resources required for legitimate operations.
This trade-off between security and performance is a fundamental aspect of blockchain design and needs to be carefully balanced based on the specific requirements of the system.

NOTE : Verified with multiple experiments but citing the 2 that have been provided above for better verification.
 */

    }




}




