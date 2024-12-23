package ds.server;
import ds.message.RequestMessage;
import ds.message.ResponseMessage;

public interface IBlockchainServer {

    ResponseMessage processRequest(RequestMessage request) throws Exception;
}
