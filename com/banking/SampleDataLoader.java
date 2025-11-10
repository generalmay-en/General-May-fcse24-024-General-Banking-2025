package com.banking;

import com.banking.dao.*;
import com.banking.model.*;

/**
 * SampleDataLoader populates the database with sample customers and accounts.
 * This provides test data for demonstration and testing purposes.
 */
public class SampleDataLoader {
    
    /**
     * Loads sample data into the database if it's empty.
     * Creates 10 customers with various account types.
     */
    public static void loadSampleData() {
        CustomerDAO customerDAO = new CustomerDAO();
        AccountDAO accountDAO = new AccountDAO();
        UserDAO userDAO = new UserDAO();
        
        // Check if data already exists
        if (customerDAO.findAll().size() > 0) {
            System.out.println("Sample data already exists. Skipping...");
            return;
        }
        
        System.out.println("Loading sample data...");
        
        // Create Bank instance for ID generation
        Bank bank = new Bank("Botswana Accountancy College Bank", "BAC");
        
        try {
            // Create sample users/tellers
            createSampleUsers(userDAO);
            
            // Create 10 sample customers with accounts
            createSampleCustomer1(bank, customerDAO, accountDAO);
            createSampleCustomer2(bank, customerDAO, accountDAO);
            createSampleCustomer3(bank, customerDAO, accountDAO);
            createSampleCustomer4(bank, customerDAO, accountDAO);
            createSampleCustomer5(bank, customerDAO, accountDAO);
            createSampleCustomer6(bank, customerDAO, accountDAO);
            createSampleCustomer7(bank, customerDAO, accountDAO);
            createSampleCustomer8(bank, customerDAO, accountDAO);
            createSampleCustomer9(bank, customerDAO, accountDAO);
            createSampleCustomer10(bank, customerDAO, accountDAO);
            
            System.out.println("✓ Sample data loaded successfully!");
            System.out.println("  - 10 customers created");
            System.out.println("  - Multiple accounts opened");
            System.out.println("  - Test users available");
            
        } catch (Exception e) {
            System.err.println("Error loading sample data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Creates sample teller users for testing.
     */
    private static void createSampleUsers(UserDAO userDAO) {
        // Admin user already created by DatabaseManager
        
        // Create teller user
        if (!userDAO.exists("teller01")) {
            userDAO.saveWithPassword("teller01", "Jane Smith", "teller123", "TELLER");
        }
        
        // Create manager user
        if (!userDAO.exists("manager01")) {
            userDAO.saveWithPassword("manager01", "Michael Johnson", "manager123", "MANAGER");
        }
    }
    
    private static void createSampleCustomer1(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
        Customer c = bank.registerCustomer("Kgosi", "Motlhanka", "Plot 234, Gaborone");
        c.setPhoneNumber("72345678");
        c.setEmail("kgosi.m@email.bw");
        customerDAO.save(c);
        
        SavingsAccount s = bank.openSavingsAccount(c.getCustomerId(), 5000, "Gaborone Main");
        accountDAO.save(s);
    }
    
    private static void createSampleCustomer2(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
        Customer c = bank.registerCustomer("Thato", "Mogorosi", "Block 8, Gaborone");
        c.setPhoneNumber("71234567");
        c.setEmail("thato.m@email.bw");
        customerDAO.save(c);
        
        InvestmentAccount i = bank.openInvestmentAccount(c.getCustomerId(), 10000, "Gaborone Main");
        accountDAO.save(i);
    }
    
    private static void createSampleCustomer3(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
        Customer c = bank.registerCustomer("Kefilwe", "Sebele", "Extension 12, Gaborone");
        c.setPhoneNumber("75678901");
        c.setEmail("kefilwe.s@email.bw");
        customerDAO.save(c);
        
        ChequeAccount ch = bank.openChequeAccount(c.getCustomerId(), 3000, "Gaborone Main",
            "Botswana Accountancy College", "Gaborone CBD");
        accountDAO.save(ch);
    }
    
    private static void createSampleCustomer4(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
        Customer c = bank.registerCustomer("Lesego", "Mothibi", "Mogoditshane", "Gaborone");
        c.setPhoneNumber("72789012");
        customerDAO.save(c);
        
        // Customer with multiple accounts
        SavingsAccount s = bank.openSavingsAccount(c.getCustomerId(), 2000, "Gaborone Main");
        InvestmentAccount i = bank.openInvestmentAccount(c.getCustomerId(), 5000, "Gaborone Main");
        accountDAO.save(s);
        accountDAO.save(i);
    }
    
    private static void createSampleCustomer5(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
        Customer c = bank.registerCustomer("Neo", "Kgosidiile", "Tlokweng");
        c.setPhoneNumber("74567890");
        c.setEmail("neo.k@email.bw");
        customerDAO.save(c);
        
        SavingsAccount s = bank.openSavingsAccount(c.getCustomerId(), 15000, "Gaborone Main");
        accountDAO.save(s);
    }
    
    private static void createSampleCustomer6(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
        Customer c = bank.registerCustomer("Gorata", "Tshwenyego", "Broadhurst, Gaborone");
        c.setPhoneNumber("76543210");
        customerDAO.save(c);
        
        InvestmentAccount i = bank.openInvestmentAccount(c.getCustomerId(), 25000, "Gaborone Main");
        accountDAO.save(i);
    }
    
    private static void createSampleCustomer7(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
        Customer c = bank.registerCustomer("Ontlametse", "Moremi", "Phakalane");
        c.setPhoneNumber("71987654");
        c.setEmail("ontlametse.m@email.bw");
        customerDAO.save(c);
        
        ChequeAccount ch = bank.openChequeAccount(c.getCustomerId(), 8000, "Gaborone Main",
            "First National Bank", "Main Mall, Gaborone");
        accountDAO.save(ch);
    }
    
    private static void createSampleCustomer8(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
        Customer c = bank.registerCustomer("Kabelo", "Galeforolwe", "Old Naledi");
        c.setPhoneNumber("73456789");
        customerDAO.save(c);
        
        // All three account types
        SavingsAccount s = bank.openSavingsAccount(c.getCustomerId(), 1000, "Gaborone Main");
        InvestmentAccount i = bank.openInvestmentAccount(c.getCustomerId(), 500, "Gaborone Main");
        ChequeAccount ch = bank.openChequeAccount(c.getCustomerId(), 2000, "Gaborone Main",
            "Botswana Power Corporation", "Gaborone");
        accountDAO.save(s);
        accountDAO.save(i);
        accountDAO.save(ch);
    }
    
    private static void createSampleCustomer9(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
        Customer c = bank.registerCustomer("Mosa", "Kelebeng", "Village, Gaborone");
        c.setPhoneNumber("72345678");
        c.setEmail("mosa.k@email.bw");
        customerDAO.save(c);
        
        SavingsAccount s = bank.openSavingsAccount(c.getCustomerId(), 7500, "Gaborone Main");
        accountDAO.save(s);
    }
    
    private static void createSampleCustomer10(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
        Customer c = bank.registerCustomer("Karabo", "Moeti", "Gaborone West");
        c.setPhoneNumber("75432109");
        customerDAO.save(c);
        
        InvestmentAccount i = bank.openInvestmentAccount(c.getCustomerId(), 12000, "Gaborone Main");
        ChequeAccount ch = bank.openChequeAccount(c.getCustomerId(), 5000, "Gaborone Main",
            "Water Utilities Corporation", "Gaborone");
        accountDAO.save(i);
        accountDAO.save(ch);
    }
}