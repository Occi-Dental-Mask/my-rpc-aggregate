Introduction:
  1. RPC, remote procedure call
  2. This is a simple implementation of RPC, making user call remotely as if they were calling locally
  3. This project includes registering netty server and client to Zookeeper and polling strategy, CGlib proxy, etc.

     
Structure:
  1. my-rpc-server: netty server; specific implementation of user-api
  2. my-rpc-consumer: netty client; dynamic proxy function; annotation for remote call; test
  3. user-api: user's interface that are exposed
  4. my-rpc-common: common class like Response


Usage:
  1. Start zookeeper server
  2. Start netty server in my-rpc-server
  3. Annotate the interface you want to test "@RemoteInvoke" in my-rpc-consumer
  4. Just call the interface as if you were calling it locally
  
