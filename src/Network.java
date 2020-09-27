/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Network class
 *
 * @author Kerly Titus
 */
public class Network extends Thread {

  // maximum number of simultaneous transactions handled by the network buffer
  private static int maxNbPackets;
  // Network buffer indices for accessing the input buffer (inputIndexClient, outputIndexServer)
  // and output buffer (inputIndexServer, outputIndexClient)
  private static int inputIndexClient, inputIndexServer, outputIndexServer, outputIndexClient;
  // IP number of the client application
  private static String clientIP;
  // IP number of the server application
  private static String serverIP;
  // Port ID of the client application
  private static int portID;
  // Client connection status - connected, disconnected, idle
  private static String clientConnectionStatus;
  // Server connection status - connected, disconnected, idle
  private static String serverConnectionStatus;
  // Incoming network buffer
  private static Transactions[] inComingPacket;
  // Outgoing network buffer
  private static Transactions[] outGoingPacket;
  // Current status of the network buffers - normal, full, empty
  private static String inBufferStatus, outBufferStatus;
  // Network status - active, inactive
  private static String networkStatus;

  /**
   * Constructor of the Network class
   *
   * @param context network context
   */
  Network(String context) {
    int i;

    /* Initialization of the network components */
    if (context.equals("network")) {
      System.out.println("\n Activating the network ...");
      clientIP = "192.168.2.0";
      serverIP = "216.120.40.10";
      clientConnectionStatus = "idle";
      serverConnectionStatus = "idle";
      portID = 0;
      maxNbPackets = 10;
      inComingPacket = new Transactions[maxNbPackets];
      outGoingPacket = new Transactions[maxNbPackets];
      for (i = 0; i < maxNbPackets; i++) {
        inComingPacket[i] = new Transactions();
        outGoingPacket[i] = new Transactions();
      }
      inBufferStatus = "empty";
      outBufferStatus = "empty";
      inputIndexClient = 0;
      inputIndexServer = 0;
      outputIndexServer = 0;
      outputIndexClient = 0;
      networkStatus = "active";
    } else /* Activate network components for client or server */
      System.out.println("\n Activating network components for " + context + "...");
  }

  /**
   * Accessor method of Network class
   *
   * @return clientIP
   */
  public String getClientIP() {
    return clientIP;
  }

  /**
   * Mutator method of Network class
   *
   * @param cip clientIP
   */
  public void setClientIP(String cip) {
    clientIP = cip;
  }

  /**
   * Accessor method of Network class
   *
   * @return serverIP
   */
  public String getServerIP() {
    return serverIP;
  }

  /**
   * Mutator method of Network class
   *
   * @param sip serverIP
   */
  public void setServerIP(String sip) {
    serverIP = sip;
  }

  /**
   * Accessor method of Network class
   *
   * @return clientConnectionStatus
   */
  public String getClientConnectionStatus() {
    return clientConnectionStatus;
  }

  /**
   * Mutator method of Network class
   *
   * @param connectStatus connectionStatus
   */
  public void setClientConnectionStatus(String connectStatus) {
    clientConnectionStatus = connectStatus;
  }

  /**
   * Accessor method of Network class
   *
   * @return serverConnectionStatus
   */
  public String getServerConnectionStatus() {
    return serverConnectionStatus;
  }

  /**
   * Mutator method of Network class
   *
   * @param connectStatus connectionStatus
   */
  public void setServerConnectionStatus(String connectStatus) {
    serverConnectionStatus = connectStatus;
  }

  /**
   * Accessor method of Network class
   *
   * @return portID
   */
  public int getPortID() {
    return portID;
  }

  /**
   * Mutator method of Network class
   *
   * @param pid portID
   */
  public void setPortID(int pid) {
    portID = pid;
  }

  /**
   * Accessor method of Network class
   *
   * @return inBufferStatus
   */
  public String getInBufferStatus() {
    return inBufferStatus;
  }

  /**
   * Mutator method of Network class
   *
   * @param inBufStatus input buffer status
   */
  public void setInBufferStatus(String inBufStatus) {
    inBufferStatus = inBufStatus;
  }

  /**
   * Accessor method of Network class
   *
   * @return outBufferStatus
   */
  public String getOutBufferStatus() {
    return outBufferStatus;
  }

  /**
   * Mutator method of Network class
   *
   * @param outBufStatus output buffer status
   */
  public void setOutBufferStatus(String outBufStatus) {
    outBufferStatus = outBufStatus;
  }

  /**
   * Accessor method of Network class
   *
   * @return networkStatus
   */
  public String getNetworkStatus() {
    return networkStatus;
  }

  /**
   * Mutator method of Network class
   *
   * @param netStatus network status
   */
  public void setNetworkStatus(String netStatus) {
    networkStatus = netStatus;
  }

  /**
   * Accessor method of Network class
   *
   * @return inputIndexClient
   */
  public int getInputIndexClient() {
    return inputIndexClient;
  }

  /**
   * Mutator method of Network class
   *
   * @param i1 input index client
   */
  public void setInputIndexClient(int i1) {
    inputIndexClient = i1;
  }

  /**
   * Accessor method of Network class
   *
   * @return inputIndexServer
   */
  public int getInputIndexServer() {
    return inputIndexServer;
  }

  /**
   * Mutator method of Network class
   *
   * @param i2 input index server
   */
  public void setInputIndexServer(int i2) {
    inputIndexServer = i2;
  }

  /**
   * Accessor method of Network class
   *
   * @return outputIndexServer
   */
  public int getOutputIndexServer() {
    return outputIndexServer;
  }

  /**
   * Mutator method of Network class
   *
   * @param o1 output index server
   */
  public void setOutputIndexServer(int o1) {
    outputIndexServer = o1;
  }

  /**
   * Accessor method of Network class
   *
   * @return outputIndexClient
   */
  public int getOutputIndexClient() {
    return outputIndexClient;
  }

  /**
   * Mutator method of Network class
   *
   * @param o2 output index client
   */
  public void setOutputIndexClient(int o2) {
    outputIndexClient = o2;
  }

  /**
   * Accessor method of Network class
   *
   * @return maxNbPackets
   */
  public int getMaxNbPackets() {
    return maxNbPackets;
  }

  /**
   * Mutator method of Network class
   *
   * @param maxPackets max packets number
   */
  public void setMaxNbPackets(int maxPackets) {
    maxNbPackets = maxPackets;
  }

  /**
   * Transmitting the transactions from the client to the server through the network
   *
   * @param inPacket transaction transferred from the client
   */
  public void send(Transactions inPacket) {
    inComingPacket[inputIndexClient].setAccountNumber(inPacket.getAccountNumber());
    inComingPacket[inputIndexClient].setOperationType(inPacket.getOperationType());
    inComingPacket[inputIndexClient].setTransactionAmount(inPacket.getTransactionAmount());
    inComingPacket[inputIndexClient].setTransactionBalance(inPacket.getTransactionBalance());
    inComingPacket[inputIndexClient].setTransactionError(inPacket.getTransactionError());
    inComingPacket[inputIndexClient].setTransactionStatus("transferred");

    System.out.println("\n DEBUG : Network.send() - index inputIndexClient " + inputIndexClient);
    System.out.println(
        "\n DEBUG : Network.send() - account number "
            + inComingPacket[inputIndexClient].getAccountNumber());

    /* Increment the input buffer index for the client */
    setInputIndexClient(((getInputIndexClient() + 1) % getMaxNbPackets()));

    /* Check if input buffer is full */
    if (getInputIndexClient() == getOutputIndexServer()) {
      setInBufferStatus("full");

      System.out.println(
          "\n DEBUG : Network.send() - inComingBuffer status " + getInBufferStatus());
      Thread.yield();
    } else setInBufferStatus("normal");
  }

  /**
   * Transmitting the transactions from the server to the client through the network
   *
   * @param outPacket updated transaction received by the client
   */
  public void receive(Transactions outPacket) {
    outPacket.setAccountNumber(outGoingPacket[outputIndexClient].getAccountNumber());
    outPacket.setOperationType(outGoingPacket[outputIndexClient].getOperationType());
    outPacket.setTransactionAmount(outGoingPacket[outputIndexClient].getTransactionAmount());
    outPacket.setTransactionBalance(outGoingPacket[outputIndexClient].getTransactionBalance());
    outPacket.setTransactionError(outGoingPacket[outputIndexClient].getTransactionError());
    outPacket.setTransactionStatus("done");

    System.out.println(
        "\n DEBUG : Network.receive() - index outputIndexClient " + outputIndexClient);
    System.out.println(
        "\n DEBUG : Network.receive() - account number " + outPacket.getAccountNumber());

    /* Increment the output buffer index for the client */
    setOutputIndexClient(((getOutputIndexClient() + 1) % getMaxNbPackets()));

    /* Check if output buffer is empty */
    if (getOutputIndexClient() == getInputIndexServer()) {
      setOutBufferStatus("empty");
      System.out.println(
          "\n DEBUG : Network.receive() - outGoingBuffer status " + getOutBufferStatus());
    } else setOutBufferStatus("normal");
  }

  /**
   * Transferring the completed transactions from the server to the network buffer
   *
   * @param outPacket updated transaction transferred by the server to the network output buffer
   */
  public boolean transferOut(Transactions outPacket) {
    outGoingPacket[inputIndexServer].setAccountNumber(outPacket.getAccountNumber());
    outGoingPacket[inputIndexServer].setOperationType(outPacket.getOperationType());
    outGoingPacket[inputIndexServer].setTransactionAmount(outPacket.getTransactionAmount());
    outGoingPacket[inputIndexServer].setTransactionBalance(outPacket.getTransactionBalance());
    outGoingPacket[inputIndexServer].setTransactionError(outPacket.getTransactionError());
    outGoingPacket[inputIndexServer].setTransactionStatus("transferred");

    System.out.println(
        "\n DEBUG : Network.transferOut() - index inputIndexServer " + inputIndexServer);
    System.out.println(
        "\n DEBUG : Network.transferOut() - account number "
            + outGoingPacket[inputIndexServer].getAccountNumber());
    /* Increment the output buffer index for the server */
    /* Check if output buffer is full */
    setInputIndexServer(((getInputIndexServer() + 1) % getMaxNbPackets()));
    if (getInputIndexServer() == getOutputIndexClient()) {
      setOutBufferStatus("full");

      System.out.println(
          "\n DEBUG : Network.transferOut() - outGoingBuffer status " + getOutBufferStatus());
    } else setOutBufferStatus("normal");

    return true;
  }

  /**
   * Transferring the transactions from the network buffer to the server
   *
   * @param inPacket transaction transferred from the input buffer to the server
   */
  public boolean transferIn(Transactions inPacket) {
    System.out.println(
        "\n DEBUG : Network.transferIn - account number "
            + inComingPacket[outputIndexServer].getAccountNumber());
    inPacket.setAccountNumber(inComingPacket[outputIndexServer].getAccountNumber());
    inPacket.setOperationType(inComingPacket[outputIndexServer].getOperationType());
    inPacket.setTransactionAmount(inComingPacket[outputIndexServer].getTransactionAmount());
    inPacket.setTransactionBalance(inComingPacket[outputIndexServer].getTransactionBalance());
    inPacket.setTransactionError(inComingPacket[outputIndexServer].getTransactionError());
    inPacket.setTransactionStatus("received");

    System.out.println(
        "\n DEBUG : Network.transferIn() - index outputIndexServer " + outputIndexServer);
    System.out.println(
        "\n DEBUG : Network.transferIn() - account number " + inPacket.getAccountNumber());

    /* Increment the input buffer index for the server */
    setOutputIndexServer(((getOutputIndexServer() + 1) % getMaxNbPackets()));

    /* Check if input buffer is empty */
    if (getOutputIndexServer() == getInputIndexClient()) {
      setInBufferStatus("empty");
      System.out.println(
          "\n DEBUG : Network.transferIn() - inComingBuffer status " + getInBufferStatus());
    } else setInBufferStatus("normal");

    return true;
  }

  /**
   * Handling of connection requests through the network
   *
   * @return valid connection
   * @param IP ip address
   */
  public boolean connect(String IP) {
    if (getNetworkStatus().equals("active")) {
      if (getClientIP().equals(IP)) {
        setClientConnectionStatus("connected");
        setPortID(0);
      } else if (getServerIP().equals(IP)) {
        setServerConnectionStatus("connected");
      }
      return true;
    } else return false;
  }

  /**
   * Handling of disconnection requests through the network
   *
   * @return valid disconnection
   * @param IP ip address
   */
  public boolean disconnect(String IP) {
    if (getNetworkStatus().equals("active")) {
      if (getClientIP().equals(IP)) {
        setClientConnectionStatus("disconnected");
      } else if (getServerIP().equals(IP)) {
        setServerConnectionStatus("disconnected");
      }
      return true;
    } else return false;
  }

  /**
   * Create a String representation based on the Network Object
   *
   * @return String representation
   */
  public String toString() {
    return ("\n Network status "
        + getNetworkStatus()
        + "Input buffer "
        + getInBufferStatus()
        + "Output buffer "
        + getOutBufferStatus());
  }

  /***********************************************************************************************************************************************
   * TODO : implement the method Run() to execute the server thread				 																*
   * *********************************************************************************************************************************************/

  /** Code for the run method */
  public void run() {
    System.out.println("\n DEBUG : Network.run() - starting network thread");

    while (!getClientConnectionStatus().equals("disconnected")
        || !getServerConnectionStatus().equals("disconnected")) {
      Thread.yield();
    }
    System.out.println("\n Terminating network thread - Client disconnected Server disconnected");
  }
}
