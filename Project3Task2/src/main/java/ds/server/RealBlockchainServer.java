/**
 * Author: JYOTI GIRDHARI KHANCHANDANI (JKHANCHA)
 * Last Modified: October 31, 2024
 *
 * This class represents a real blockchain server implementing the `IBlockchainServer` interface.
 * * The server maintains a blockchain and can process various types of client requests, such as
 * * adding blocks, validating the chain, corrupting data, and repairing the blockchain.
 * * This server is designed to illustrate blockchain operations within a centralized system.
 *
 *
 *
 *NOTE : This code is generated with assisstance from Perplexity AI.
 *
 */
package ds.server;
import ds.message.RequestMessage;
import ds.message.ResponseMessage;

public class RealBlockchainServer implements IBlockchainServer {
    private BlockChain blockchain;
    /**
     * Constructor initializes the blockchain and creates a genesis block.
     * The genesis block is the first block of the blockchain.
     */
    public RealBlockchainServer() {
        this.blockchain = new BlockChain();
        Block genesisBlock = new Block(0, blockchain.getTime(), "Genesis", 2);
        blockchain.addBlock(genesisBlock);
    }
    /**
     * Processes a client request based on the operation code provided in the request.
     *
     * @param request The RequestMessage object containing the operation code and any required data.
     * @return ResponseMessage A response object containing the result of the requested operation
     *                         and any necessary status or data messages.
     *
     * Operation codes:
     * 0 - Get blockchain status
     * 1 - Add a new block to the blockchain
     * 2 - Validate the blockchain integrity
     * 3 - Retrieve blockchain data as a String
     * 4 - Corrupt data in a specified block
     * 5 - Repair the blockchain
     */
    @Override
    public ResponseMessage processRequest(RequestMessage request) {
        long startTime = System.currentTimeMillis();
        String status = "SUCCESS";
        String data;

        switch (request.getOperation()) {
            case 0:
                data = getBlockchainStatus();
                break;
            case 1:
                Block newBlock = new Block(blockchain.getChainSize(), blockchain.getTime(), request.getData(), request.getDifficulty());
                blockchain.addBlock(newBlock);
                data = "Block added successfully";
                break;
            case 2:
                data = blockchain.isChainValid();
                break;
            case 3:
                data = blockchain.toString();
                break;
            case 4:
                String[] corruptData = request.getData().split(",");
                int blockId = Integer.parseInt(corruptData[0]);
                String newData = corruptData[1];
                Block blockToCorrupt = blockchain.getBlock(blockId);
                if (blockToCorrupt != null) {
                    blockToCorrupt.setData(newData);
                    data = "Block " + blockId + " corrupted";
                } else {
                    status = "ERROR";
                    data = "Invalid block ID";
                }
                break;
            case 5:
                blockchain.repairChain();
                data = "Chain repaired";
                break;
            default:
                status = "ERROR";
                data = "Invalid operation";
        }

        long executionTime = System.currentTimeMillis() - startTime;
        return new ResponseMessage(status, data, executionTime);
    }
    /**
     * Retrieves the current status of the blockchain.
     *
     * @return A string summarizing the blockchain's status.
     */
    private String getBlockchainStatus() {
        return "Current size of chain: " + blockchain.getChainSize() + "\n" +
                "Difficulty of most recent block: " + blockchain.getLatestBlock().getDifficulty() + "\n" +
                "Total difficulty for all blocks: " + blockchain.getTotalDifficulty() + "\n" +
                "Approximate hashes per second on this machine: " + blockchain.getHashesPerSecond() + "\n" +
                "Expected total hashes required for the whole chain: " + blockchain.getTotalExpectedHashes() + "\n" +
                "Nonce for most recent block: " + blockchain.getLatestBlock().getNonce() + "\n" +
                "Chain hash: " + blockchain.getChainHash();
    }
}
