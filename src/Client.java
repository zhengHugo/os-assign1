import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.util.InputMismatchException;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Client class
 *
 * @author Kerly Titus
 */
public class Client extends Thread {

  private static int numberOfTransactions; /* Number of transactions to process */
  private static int maxNbTransactions; /* Maximum number of transactions */
  private static Transactions[] transaction; /* Transactions to be processed */
  private static Network objNetwork; /* Client object to handle network operations */
  private String clientOperation; /* sending or receiving */

  /**
   * Constructor method of Client class
   *
   * @param operation client operation
   */
  Client(String operation) {
    if (operation.equals("sending")) {
      System.out.println("\n Initializing client sending application ...");
      numberOfTransactions = 0;
      maxNbTransactions = 100;
      transaction = new Transactions[maxNbTransactions];
      objNetwork = new Network("client");
      clientOperation = operation;
      System.out.println("\n Initializing the transactions ... ");
      readTransactions();
      System.out.println("\n Connecting client to network ...");
      String cip = objNetwork.getClientIP();
      if (!(objNetwork.connect(cip))) {
        System.out.println("\n Terminating client application, network unavailable");
        System.exit(0);
      }
    } else if (operation.equals("receiving")) {
      System.out.println("\n Initializing client receiving application ...");
      clientOperation = operation;
    }
  }

  /**
   * Accessor method of Client class
   *
   * @return numberOfTransactions
   */
  public int getNumberOfTransactions() {
    return numberOfTransactions;
  }

  /**
   * Mutator method of Client class
   *
   * @param nbOfTrans number of transaction
   */
  public void setNumberOfTransactions(int nbOfTrans) {
    numberOfTransactions = nbOfTrans;
  }

  /**
   * Accessor method of Client class
   *
   * @return clientOperation
   */
  public String getClientOperation() {
    return clientOperation;
  }

  /**
   * Mutator method of Client class
   *
   * @param operation client operation
   */
  public void setClientOperation(String operation) {
    clientOperation = operation;
  }

  /** Reading of the transactions from an input file */
  public void readTransactions() {
    Scanner inputStream = null; /* Transactions input file stream */
    int i = 0; /* Index of transactions array */

    try {
      inputStream = new Scanner(new FileInputStream("resource/transaction.txt"));
    } catch (FileNotFoundException e) {
      System.out.println("File transaction.txt was not found");
      System.out.println("or could not be opened.");
      System.exit(0);
    }
    while (inputStream.hasNextLine()) {
      try {
        transaction[i] = new Transactions();
        transaction[i].setAccountNumber(inputStream.next()); /* Read account number */
        transaction[i].setOperationType(inputStream.next()); /* Read transaction type */
        transaction[i].setTransactionAmount(inputStream.nextDouble()); /* Read transaction amount */
        transaction[i].setTransactionStatus("pending"); /* Set current transaction status */
        i++;
      } catch (InputMismatchException e) {
        System.out.println("Line " + i + "file transactions.txt invalid input");
        System.exit(0);
      }
    }
    setNumberOfTransactions(i); /* Record the number of transactions processed */

    /*System.out.println(
        "\n DEBUG : Client.readTransactions() - "
            + getNumberOfTransactions()
            + " transactions processed");*/

    inputStream.close();
  }

  /** Sending the transactions to the server */
  public void sendTransactions() {
    int i = 0; /* index of transaction array */

    while (i < getNumberOfTransactions()) {

      // Alternatively, busy-wait
      // until the network input buffer is available
      while (objNetwork.getInBufferStatus().equals("full")) {
        Thread.yield();
      }

      transaction[i].setTransactionStatus("sent"); /* Set current transaction status */

      /*System.out.println(
          "\n DEBUG : Client.sendTransactions() - sending transaction on account "
              + transaction[i].getAccountNumber());*/

      objNetwork.send(transaction[i]); /* Transmit current transaction */
      i++;
    }
  }

  /**
   * Receiving the completed transactions from the server
   *
   * @param transact transaction
   */
  public void receiveTransactions(Transactions transact) {
    int i = 0; /* Index of transaction array */

    while (i < getNumberOfTransactions()) {
      while (objNetwork.getOutBufferStatus().equals("empty")) {
        Thread.yield();
      }

      // Alternatively, busy-wait
      // until the network output buffer is available
      objNetwork.receive(transact); /* Receive updated transaction from the network buffer */

      /*System.out.println(
          "\n DEBUG : Client.receiveTransactions() - receiving updated transaction on account "
              + transact.getAccountNumber());*/

      System.out.println(transact); /* Display updated transaction */
      i++;
    }
  }

  /**
   * Create a String representation based on the Client Object
   *
   * @return String representation
   */
  public String toString() {
    return ("\n client IP "
        + objNetwork.getClientIP()
        + " Connection status"
        + objNetwork.getClientConnectionStatus()
        + "Number of transactions "
        + getNumberOfTransactions());
  }

  /** Code for the run method */
  public void run() {
    Transactions transact = new Transactions();
    long sendClientStartTime, sendClientEndTime, receiveClientStartTime, receiveClientEndTime;
    if (this.clientOperation.equals("sending")) {
      sendClientStartTime = Instant.now().toEpochMilli();
      sendTransactions();
      sendClientEndTime = Instant.now().toEpochMilli();
      System.out.printf(
          "\n Terminating client sending thread - Running time %d milliseconds\n",
          sendClientEndTime - sendClientStartTime);
    } else if (this.clientOperation.equals("receiving")) {
      receiveClientStartTime = Instant.now().toEpochMilli();
      receiveTransactions(transact);
      receiveClientEndTime = Instant.now().toEpochMilli();
      System.out.printf(
          "\n Terminating client receiving thread - Running time %d milliseconds\n",
          receiveClientEndTime - receiveClientStartTime);
      objNetwork.setClientConnectionStatus("disconnected");
    }

    /* Implement the code for the run method */
  }
}
