Structure:
  my-rpc-server:
    netty server
    specific affair: user (expose interface, including the real implementation)
  my-rpc-consumer:
    netty client
    dynamic proxy function
    annotation for remote call
    test

Usage:
  1. Start zookeeper server
  2. Start netty server in my-rpc-server
  3. Annotate the interface you want to test "@RemoteInvoke" in my-rpc-consumer
  4. Just call the interface as if you were calling it locally
  
