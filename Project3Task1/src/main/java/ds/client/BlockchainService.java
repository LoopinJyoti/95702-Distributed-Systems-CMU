package ds.client;
/** BlockchainService.java /**

Author: JYOTI GIRDHARI KHANCHANDANI (JKHANCHA)
Last Modified: October 31, 2024

This interface defines the contract for blockchain operations in the system.
It serves as the foundation for both the proxy and the actual service implementation.
The interface includes methods for:
Retrieving blockchain status
Adding transactions to the blockchain
Verifying the integrity of the blockchain
Viewing the entire blockchain
Corrupting the chain (for testing purposes)
Repairing the chain
This interface is crucial for implementing the Proxy design pattern,
allowing for a seamless interaction between the client and the server-side
blockchain operations.
        */


public interface BlockchainService {
    String getBlockchainStatus();
    String addTransaction(String data, int difficulty);
    String verifyBlockchain();
    String viewBlockchain();
    String corruptChain(int blockId, String newData);
    String repairChain();
}

