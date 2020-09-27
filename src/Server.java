import java.time.Instant;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*package comp546pa1w2020;*/

/**
 * Server class
 *
 * @author Kerly Titus
 */
public class Server extends Thread {

  int numberOfTransactions; /* Number of transactions handled by the server */
  int numberOfAccounts; /* Number of accounts stored in the server */
  int maxNbAccounts; /* maximum number of transactions */
  Transactions transaction; /* Transaction being processed */
  Network objNetwork; /* Server object to handle network operations */
  Accounts[] account; /* Accounts to be accessed or updated */

  /**
   * Constructor method of Client class
   *
   */
  Server() {
    System.out.println("\n Initializing the server ...");
    numberOfTransactions = 0;
    numberOfAccounts = 0;
    maxNbAccounts = 100;
    transaction = new Transactions();
    account = new Accounts[maxNbAccounts];
    objNetwork = new Network("server");
    System.out.println("\n Initializing the Accounts database ...");
    initializeAccounts();
    System.out.println("\n Connecting server to network ...");
    if (!(objNetwork.connect(objNetwork.getServerIP()))) {
      System.out.println("\n Terminating server application, network unavailable");
      System.exit(0);
    }
  }

  /**
   * Accessor method of Server class
   *
   * @return numberOfTransactions
   */
  public int getNumberOfTransactions() {
    return numberOfTransactions;
  }

  /**
   * Mutator method of Server class
   *
   * @param nbOfTrans number of transactions
   */
  public void setNumberOfTransactions(int nbOfTrans) {
    numberOfTransactions = nbOfTrans;
  }

  /**
   * Accessor method of Server class
   *
   * @return numberOfAccounts
   */
  public int getNumberOfAccounts() {
    return numberOfAccounts;
  }

  /**
   * Mutator method of Server class
   *
   * @param nbOfAcc number of accounts
   */
  public void setNumberOfAccounts(int nbOfAcc) {
    numberOfAccounts = nbOfAcc;
  }

  /**
   * Accessor method of Server class
   *
   * @return maxNbAccounts
   */
  public int getmMxNbAccounts() {
    return maxNbAccounts;
  }

  /**
   * Mutator method of Server class
   *
   * @param nbOfAcc number of account
   */
  public void setMaxNbAccounts(int nbOfAcc) {
    maxNbAccounts = nbOfAcc;
  }

  /**
   * Initialization of the accounts from an input file
   *
   */
  public void initializeAccounts() {
    Scanner inputStream = null; /* accounts input file stream */
    int i = 0; /* index of accounts array */

    try {
      inputStream = new Scanner(new FileInputStream("resource/account.txt"));
    } catch (FileNotFoundException e) {
      System.out.println("File account.txt was not found");
      System.out.println("or could not be opened.");
      System.exit(0);
    }
    while (inputStream.hasNextLine()) {
      try {
        account[i] = new Accounts();
        account[i].setAccountNumber(inputStream.next()); /* Read account number */
        account[i].setAccountType(inputStream.next()); /* Read account type */
        account[i].setFirstName(inputStream.next()); /* Read first name */
        account[i].setLastName(inputStream.next()); /* Read last name */
        account[i].setBalance(inputStream.nextDouble()); /* Read account balance */
      } catch (InputMismatchException e) {
        System.out.println("Line " + i + "file account.txt invalid input");
        System.exit(0);
      }
      i++;
    }
    setNumberOfAccounts(i); /* Record the number of accounts processed */

    System.out.println(
        "\n DEBUG : Server.initializeAccounts() " + getNumberOfAccounts() + " accounts processed");

    inputStream.close();
  }

  /**
   * Find and return the index position of an account
   *
   * @return account index position or -1
   * @param accNumber account number
   */
  public int findAccount(String accNumber) {
    int i = 0;

    /* Find account */
    while (!(account[i].getAccountNumber().equals(accNumber))) i++;
    if (i == getNumberOfAccounts()) return -1;
    else return i;
  }

  /**
   * Processing of the transactions
   *
   * @param trans transaction
   */
  public boolean processTransactions(Transactions trans) {
    int accIndex; /* Index position of account to update */
    double newBalance; /* Updated account balance */

    /* Process the accounts until the client disconnects */
    while ((!objNetwork.getClientConnectionStatus().equals("disconnected"))) {
       if((objNetwork.getInBufferStatus().equals("empty"))){
         Thread.yield();
       }
      /* Alternatively, busy-wait until the network input buffer is available */

      if (!objNetwork.getInBufferStatus().equals("empty")) {
        System.out.println(
            "\n DEBUG : Server.processTransactions() - transferring in account "
                + trans.getAccountNumber());

        objNetwork.transferIn(trans); /* Transfer a transaction from the network input buffer */

        accIndex = findAccount(trans.getAccountNumber());
        switch (trans.getOperationType()) {
          case "DEPOSIT" -> {
            /* Process deposit operation */
            newBalance = deposit(accIndex, trans.getTransactionAmount());
            trans.setTransactionBalance(newBalance);
            trans.setTransactionStatus("done");
            System.out.println(
                "\n DEBUG : Server.processTransactions() - Deposit of "
                    + trans.getTransactionAmount()
                    + " in account "
                    + trans.getAccountNumber());
          }
          case "WITHDRAW" -> {
            /* Process withdraw operation */
            newBalance = withdraw(accIndex, trans.getTransactionAmount());
            trans.setTransactionBalance(newBalance);
            trans.setTransactionStatus("done");
            System.out.println(
                "\n DEBUG : Server.processTransactions() - Withdrawal of "
                    + trans.getTransactionAmount()
                    + " from account "
                    + trans.getAccountNumber());
          }
          case "QUERY" -> {
            /* Process query operation */
            newBalance = query(accIndex);
            trans.setTransactionBalance(newBalance);
            trans.setTransactionStatus("done");
            System.out.println(
                "\n DEBUG : Server.processTransactions() - Obtaining balance from account"
                    + trans.getAccountNumber());
          }
        }
        // Alternatively,  busy-wait
        // until the network output buffer is available

         while( (objNetwork.getOutBufferStatus().equals("full"))){
           Thread.yield();
         }

        System.out.println(
            "\n DEBUG : Server.processTransactions() - transferring out account "
                + trans.getAccountNumber());

        objNetwork.transferOut(
            trans); /* Transfer a completed transaction from the server to the network output buffer */
        setNumberOfTransactions(
            (getNumberOfTransactions() + 1)); /* Count the number of transactions processed */
      }
    }

    System.out.println(
        "\n DEBUG : Server.processTransactions() - "
            + getNumberOfTransactions()
            + " accounts updated");

    return true;
  }

  /**
   * Processing of a deposit operation in an account
   *
   * @return balance
   * @param i, amount
   */
  public double deposit(int i, double amount) {
    double curBalance; /* Current account balance */

    curBalance = account[i].getBalance(); /* Get current account balance */
    account[i].setBalance(curBalance + amount); /* Deposit amount in the account */
    return account[i].getBalance(); /* Return updated account balance */
  }

  /**
   * Processing of a withdrawal operation in an account
   *
   * @return balance
   * @param i, amount
   */
  public double withdraw(int i, double amount) {
    double curBalance; /* Current account balance */

    curBalance = account[i].getBalance(); /* Get current account balance */
    account[i].setBalance(curBalance - amount); /* Withdraw amount in the account */
    return account[i].getBalance(); /* Return updated account balance */
  }

  /**
   * Processing of a query operation in an account
   *
   * @param i account index
   */
  public double query(int i) {
    double curBalance; /* Current account balance */

    curBalance = account[i].getBalance(); /* Get current account balance */
    return curBalance; /* Return current account balance */
  }

  /**
   * Create a String representation based on the Server Object
   *
   * @return String representation
   */
  public String toString() {
    return ("\n server IP "
        + objNetwork.getServerIP()
        + "connection status "
        + objNetwork.getServerConnectionStatus()
        + "Number of accounts "
        + getNumberOfAccounts());
  }

  /* *********************************************************************************************************************************************
   * TODO : implement the method Run() to execute the server thread				 																*
   * *********************************************************************************************************************************************/

  /**
   * Code for the run method
   *
   */
  public void run() {
    Transactions trans = new Transactions();
    long serverStartTime = 0, serverEndTime = 0;

    System.out.println(
        "\n DEBUG : Server.run() - starting server thread "
            + objNetwork.getServerConnectionStatus());

    serverStartTime = Instant.now().toEpochMilli();
    processTransactions(trans);
    serverEndTime = Instant.now().toEpochMilli();

    System.out.println(
        "\n Terminating server thread - "
            + " Running time "
            + (serverEndTime - serverStartTime)
            + " milliseconds");
    objNetwork.setServerConnectionStatus("disconnected");
  }
}
