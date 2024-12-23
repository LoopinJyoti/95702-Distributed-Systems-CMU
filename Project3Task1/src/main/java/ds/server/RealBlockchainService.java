/**
 * Author: JYOTI GIRDHARI KHANCHANDANI (JKHANCHA)
 * Last Modified: October 31, 2024
 *
 * This class implements a RealBlockchainService that provides functionality
 * for managing a blockchain. It implements the BlockchainService interface
 * and offers methods to interact with the blockchain, including adding
 * transactions, verifying the chain, viewing the blockchain, and more.
 *
 * NOTE : This code is generated with assisstance from Perplexity AI.
 *
 */

package ds.server;

import ds.client.BlockchainService;

public class RealBlockchainService implements BlockchainService {
    /**
     * The blockchain instance managed by this service.
     */
    private BlockChain blockchain;
    /**
     * Constructor for RealBlockchainService.
     * Initializes the blockchain and adds a genesis block.
     */
    public RealBlockchainService() {
        this.blockchain = new BlockChain();
        Block genesisBlock = new Block(0, blockchain.getTime(), "Genesis", 2);
        blockchain.addBlock(genesisBlock);
    }
    /**
     * Retrieves the current status of the blockchain.
     *
     * @return A string containing information about the blockchain's current state,
     *         including chain size, difficulty, total difficulty, hashes per second,
     *         expected total hashes, nonce of the latest block, and the chain hash.
     */
    @Override
    public String getBlockchainStatus() {
        return "Current size of chain: " + blockchain.getChainSize() + "\n" +
                "Difficulty of most recent block: " + blockchain.getLatestBlock().getDifficulty() + "\n" +
                "Total difficulty for all blocks: " + blockchain.getTotalDifficulty() + "\n" +
                "Approximate hashes per second on this machine: " + blockchain.getHashesPerSecond() + "\n" +
                "Expected total hashes required for the whole chain: " + blockchain.getTotalExpectedHashes() + "\n" +
                "Nonce for most recent block: " + blockchain.getLatestBlock().getNonce() + "\n" +
                "Chain hash: " + blockchain.getChainHash();
    }
    /**
     * Adds a new transaction to the blockchain.
     *
     * @param data The data to be included in the new block.
     * @param difficulty The mining difficulty for the new block.
     * @return A string indicating the success of the operation.
     */
    @Override
    public String addTransaction(String data, int difficulty) {
        Block newBlock = new Block(blockchain.getChainSize(), blockchain.getTime(), data, difficulty);
        blockchain.addBlock(newBlock);
        return "Block added successfully";
    }
    /**
     * Verifies the integrity of the entire blockchain.
     *
     * @return A string indicating whether the blockchain is valid or not.
     */
    @Override
    public String verifyBlockchain() {
        return blockchain.isChainValid();
    }
    /**
     * Provides a string representation of the entire blockchain.
     *
     * @return A string containing details of all blocks in the chain.
     */
    @Override
    public String viewBlockchain() {
        return blockchain.toString();
    }
    /**
     * Deliberately corrupts a specified block in the chain for testing purposes.
     *
     * @param blockId The ID of the block to corrupt.
     * @param newData The new data to replace the existing data in the specified block.
     * @return A string indicating the success or failure of the corruption attempt.
     */
    @Override
    public String corruptChain(int blockId, String newData) {
        Block blockToCorrupt = blockchain.getBlock(blockId);
        if (blockToCorrupt != null) {
            blockToCorrupt.setData(newData);
            return "Block " + blockId + " corrupted";
        } else {
            return "Invalid block ID";
        }
    }
    /**
     * Attempts to repair the blockchain by recalculating hashes and validating the chain.
     *
     * @return A string indicating the completion of the repair attempt.
     */
    @Override
    public String repairChain() {
        blockchain.repairChain();
        return "Chain repaired";
    }
}

