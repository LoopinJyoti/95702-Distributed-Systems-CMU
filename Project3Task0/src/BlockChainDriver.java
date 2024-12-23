import java.util.Scanner;

/**
 * Author: JYOTI GIRDHARI KHANCHANDANI (JKHANCHA)
 * Last Modified: October 27, 2024
 *
 * This class serves as a driver for the BlockChain implementation.
 * It provides a menu-driven interface for interacting with the blockchain.
 */
public class BlockChainDriver {

    /**
     * This routine acts as a test driver for the BlockChain.
     * It provides a menu-driven interface for interacting with the blockchain.
     *
     * @param args Command line arguments (unused)
     */
    public static void main(String[] args) {
        BlockChain blockchain = new BlockChain();
        Scanner scanner = new Scanner(System.in);

        // Add Genesis block
        Block genesisBlock = new Block(0, blockchain.getTime(), "Genesis", 2);
        blockchain.addBlock(genesisBlock);

        while (true) {
            System.out.println("\nBlock Chain Menu:");
            System.out.println("0. View basic blockchain status.");
            System.out.println("1. Add a transaction to the blockchain.");
            System.out.println("2. Verify the blockchain.");
            System.out.println("3. View the blockchain.");
            System.out.println("4. Corrupt the chain.");
            System.out.println("5. Hide the corruption by repairing the chain.");
            System.out.println("6. Exit");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 0:
                    viewBlockchainStatus(blockchain);
                    break;
                case 1:
                    addTransaction(blockchain, scanner);
                    break;
                case 2:
                    verifyBlockchain(blockchain);
                    break;
                case 3:
                    viewBlockchain(blockchain);
                    break;
                case 4:
                    corruptChain(blockchain, scanner);
                    break;
                case 5:
                    repairChain(blockchain);
                    break;
                case 6:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    /**
     * Displays the current status of the blockchain.
     *
     * This method prints out various statistics about the blockchain, including:
     * - Current size of the chain
     * - Difficulty of the most recent block
     * - Total difficulty for all blocks
     * - Approximate hashes per second on the current machine
     * - Expected total hashes required for the whole chain
     * - Nonce for the most recent block
     * - Current chain hash
     *
     * @param blockchain The BlockChain object to display status for
     */
    private static void viewBlockchainStatus(BlockChain blockchain) {
        System.out.println("Current size of chain: " + blockchain.getChainSize());
        Block latestBlock = blockchain.getLatestBlock();
        System.out.println("Difficulty of most recent block: " + latestBlock.getDifficulty());
        System.out.println("Total difficulty for all blocks: " + blockchain.getTotalDifficulty());
        System.out.println("Approximate hashes per second on this machine: " + blockchain.getHashesPerSecond());
        System.out.println("Expected total hashes required for the whole chain: " + blockchain.getTotalExpectedHashes());
        System.out.println("Nonce for most recent block: " + latestBlock.getNonce());
        System.out.println("Chain hash: " + blockchain.getChainHash());
    }
    /**
     * Adds a new transaction to the blockchain.
     *
     * This method prompts the user for a difficulty level and transaction data,
     * creates a new Block with this information, and adds it to the blockchain.
     * It also measures and reports the execution time for adding the block.
     *
     * @param blockchain The BlockChain object to add the transaction to
     * @param scanner A Scanner object for user input
     */
    private static void addTransaction(BlockChain blockchain, Scanner scanner) {
        System.out.print("Enter difficulty > 1: ");
        int difficulty = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter transaction: ");
        String transaction = scanner.nextLine();

        long startTime = System.currentTimeMillis();
        Block newBlock = new Block(blockchain.getChainSize(), blockchain.getTime(), transaction, difficulty);
        blockchain.addBlock(newBlock);
        long endTime = System.currentTimeMillis();

        System.out.println("Total execution time to add this block was " + (endTime - startTime) + " milliseconds");
    }
    /**
     * Verifies the integrity of the entire blockchain.
     *
     * This method checks if the blockchain is valid and reports the result.
     * It also measures and reports the execution time for the verification process.
     *
     * @param blockchain The BlockChain object to verify
     */
    private static void verifyBlockchain(BlockChain blockchain) {
        System.out.println("Verifying entire chain");
        long startTime = System.currentTimeMillis();
        String result = blockchain.isChainValid();
        long endTime = System.currentTimeMillis();

        System.out.println("Chain verification: " + result);
        System.out.println("Total execution time required to verify the chain was " + (endTime - startTime) + " milliseconds");
    }
    /**
     * Displays the entire contents of the blockchain.
     *
     * This method prints out a string representation of the entire blockchain.
     *
     * @param blockchain The BlockChain object to display
     */
    private static void viewBlockchain(BlockChain blockchain) {
        System.out.println("View the Blockchain");
        System.out.println(blockchain.toString());
    }
    /**
     * Corrupts a specific block in the blockchain for testing purposes.
     *
     * This method prompts the user for a block ID and new data, then modifies
     * the specified block with the new data, potentially invalidating the chain.
     *
     * @param blockchain The BlockChain object to corrupt
     * @param scanner A Scanner object for user input
     */
    private static void corruptChain(BlockChain blockchain, Scanner scanner) {
        System.out.println("Corrupt the Blockchain");
        System.out.print("Enter block ID of block to corrupt: ");
        int blockId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (blockId < 0 || blockId >= blockchain.getChainSize()) {
            System.out.println("Invalid block ID");
            return;
        }

        System.out.print("Enter new data for block " + blockId + ": ");
        String newData = scanner.nextLine();

        Block blockToCorrupt = blockchain.getBlock(blockId);
        blockToCorrupt.setData(newData);

        System.out.println("Block " + blockId + " now holds " + newData);
    }
    /**
     * Attempts to repair the entire blockchain.
     *
     * This method calls the blockchain's repair function and measures
     * the execution time required for the repair process.
     *
     * @param blockchain The BlockChain object to repair
     */
    private static void repairChain(BlockChain blockchain) {
        System.out.println("Repairing the entire chain");
        long startTime = System.currentTimeMillis();
        blockchain.repairChain();
        long endTime = System.currentTimeMillis();

        System.out.println("Total execution time required to repair the chain was " + (endTime - startTime) + " milliseconds");
    }


}
