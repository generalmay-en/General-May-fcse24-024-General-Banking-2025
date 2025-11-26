package com.banking;

import com.banking.dao.*;
import com.banking.model.*;

/**
 * SampleDataLoader populates the database with sample customers and accounts.
 * This provides test data for demonstration and testing purposes.
 */
public class SampleDataLoader {

    private static final String BANK_NAME = "𝐆𝐄𝐍𝐄𝐑𝐀𝐋 ₿𝐀𝐍𝐊";
    private static final String BANK_ICON = "𓃵";
    private static final String BANK_CODE = "BAC";

    public static void loadSampleData() {
        CustomerDAO customerDAO = new CustomerDAO();
        AccountDAO accountDAO = new AccountDAO();
        UserDAO userDAO = new UserDAO();

        // Prevent duplicate loading
        if (customerDAO.findAll().size() > 0) {
            System.out.println("✓Sample data already exists. Skipping...");
            return;
        }

        System.out.println("\n-------------------------------------");
        System.out.println("→Loading sample data...");
        System.out.println("-------------------------------------");

        Bank bank = new Bank(BANK_NAME + " " + BANK_ICON, BANK_CODE);

        try {
            createSampleUsers(userDAO);

            safeRun(() -> createSampleCustomer1(bank, customerDAO, accountDAO));
            safeRun(() -> createSampleCustomer2(bank, customerDAO, accountDAO));
            safeRun(() -> createSampleCustomer3(bank, customerDAO, accountDAO));
            safeRun(() -> createSampleCustomer4(bank, customerDAO, accountDAO));
            safeRun(() -> createSampleCustomer5(bank, customerDAO, accountDAO));
            safeRun(() -> createSampleCustomer6(bank, customerDAO, accountDAO));
            safeRun(() -> createSampleCustomer7(bank, customerDAO, accountDAO));
            safeRun(() -> createSampleCustomer8(bank, customerDAO, accountDAO));
            safeRun(() -> createSampleCustomer9(bank, customerDAO, accountDAO));
            safeRun(() -> createSampleCustomer10(bank, customerDAO, accountDAO));
            safeRun(() -> createSampleCustomer11(bank, customerDAO, accountDAO));
safeRun(() -> createSampleCustomer12(bank, customerDAO, accountDAO));
safeRun(() -> createSampleCustomer13(bank, customerDAO, accountDAO));
safeRun(() -> createSampleCustomer14(bank, customerDAO, accountDAO));
safeRun(() -> createSampleCustomer15(bank, customerDAO, accountDAO));
safeRun(() -> createSampleCustomer16(bank, customerDAO, accountDAO));
safeRun(() -> createSampleCustomer17(bank, customerDAO, accountDAO));
safeRun(() -> createSampleCustomer18(bank, customerDAO, accountDAO));
safeRun(() -> createSampleCustomer19(bank, customerDAO, accountDAO));
safeRun(() -> createSampleCustomer20(bank, customerDAO, accountDAO));


            System.out.println("\n✓ Sample data loaded successfully!");
            System.out.println("   • Customers: 20 created");
            System.out.println("   • Accounts: Savings / Investment / Cheque opened");
            System.out.println("   • Staff Users: teller01, manager01");
            System.out.println("   • Bank: " + BANK_NAME);
            System.out.println("-------------------------------------\n");

        } catch (Exception e) {
            System.err.println("Fatal error loading sample data: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /** Unified safe execution wrapper so one failure doesn't stop entire loading */
    private static void safeRun(Runnable task) {
        try {
            task.run();
        } catch (Exception e) {
            System.err.println("Error creating sample customer: " + e.getMessage());
        }
    }

    /** Customer creation helper to avoid repeated code */
    private static Customer createCustomer(
            Bank bank, CustomerDAO customerDAO,
            String firstName, String lastName, String address,
            String phone, String email) {

        Customer c = bank.registerCustomer(firstName, lastName, address);

        if (phone != null) c.setPhoneNumber(phone);
        if (email != null) c.setEmail(email);

        customerDAO.save(c);
        return c;
    }


    private static void createSampleUsers(UserDAO userDAO) {
        if (!userDAO.exists("teller01")) {
            userDAO.saveWithPassword("teller01", "Rebaone Mlalazi", "teller123", "TELLER");
        }

        if (!userDAO.exists("manager01")) {
            userDAO.saveWithPassword("manager01", "General May", "manager123", "MANAGER");
        }
    }

    // Sample Customers

    private static void createSampleCustomer1(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
        Customer c = createCustomer(bank, customerDAO,
                "Kgosi", "Motlhanka", "Plot 234, Gaborone",
                "72345678", "kgosi.m@email.bw");

        SavingsAccount s = bank.openSavingsAccount(c.getCustomerId(), 5000, "Gaborone Main");
        accountDAO.save(s);
    }

    private static void createSampleCustomer2(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
        Customer c = createCustomer(bank, customerDAO,
                "Thato", "Mogorosi", "Block 8, Gaborone",
                "71234567", "thato.m@email.bw");

        InvestmentAccount i = bank.openInvestmentAccount(c.getCustomerId(), 10000, "Gaborone Main");
        accountDAO.save(i);
    }

    private static void createSampleCustomer3(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
        Customer c = createCustomer(bank, customerDAO,
                "Kefilwe", "Sebele", "Extension 12, Gaborone",
                "75678901", "kefilwe.s@email.bw");

        ChequeAccount ch = bank.openChequeAccount(c.getCustomerId(), 3000, "Gaborone Main",
                "Botswana Accountancy College", "Gaborone CBD");
        accountDAO.save(ch);
    }

    private static void createSampleCustomer4(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
        Customer c = createCustomer(bank, customerDAO,
                "Lesego", "Mothibi", "Mogoditshane",
                "72789012", null);

        SavingsAccount s = bank.openSavingsAccount(c.getCustomerId(), 2000, "Gaborone Main");
        InvestmentAccount i = bank.openInvestmentAccount(c.getCustomerId(), 5000, "Gaborone Main");
        accountDAO.save(s);
        accountDAO.save(i);
    }

    private static void createSampleCustomer5(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
        Customer c = createCustomer(bank, customerDAO,
                "Neo", "Kgosidiile", "Tlokweng",
                "74567890", "neo.k@email.bw");

        SavingsAccount s = bank.openSavingsAccount(c.getCustomerId(), 15000, "Gaborone Main");
        accountDAO.save(s);
    }

    private static void createSampleCustomer6(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
        Customer c = createCustomer(bank, customerDAO,
                "Gorata", "Tshwenyego", "Broadhurst, Gaborone",
                "76543210", null);

        InvestmentAccount i = bank.openInvestmentAccount(c.getCustomerId(), 25000, "Gaborone Main");
        accountDAO.save(i);
    }

    private static void createSampleCustomer7(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
        Customer c = createCustomer(bank, customerDAO,
                "Ontlametse", "Moremi", "Phakalane",
                "71987654", "ontlametse.m@email.bw");

        ChequeAccount ch = bank.openChequeAccount(c.getCustomerId(), 8000, "Gaborone Main",
                "First National Bank", "Main Mall, Gaborone");
        accountDAO.save(ch);
    }

    private static void createSampleCustomer8(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
        Customer c = createCustomer(bank, customerDAO,
                "Kabelo", "Galeforolwe", "Old Naledi",
                "73456789", null);

        SavingsAccount s = bank.openSavingsAccount(c.getCustomerId(), 1000, "Gaborone Main");
        InvestmentAccount i = bank.openInvestmentAccount(c.getCustomerId(), 500, "Gaborone Main");
        ChequeAccount ch = bank.openChequeAccount(c.getCustomerId(), 2000, "Gaborone Main",
                "Botswana Power Corporation", "Gaborone");
        accountDAO.save(s);
        accountDAO.save(i);
        accountDAO.save(ch);
    }

    private static void createSampleCustomer9(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
        Customer c = createCustomer(bank, customerDAO,
                "Mosa", "Kelebeng", "Village, Gaborone",
                "72345678", "mosa.k@email.bw");

        SavingsAccount s = bank.openSavingsAccount(c.getCustomerId(), 7500, "Gaborone Main");
        accountDAO.save(s);
    }

    private static void createSampleCustomer10(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
        Customer c = createCustomer(bank, customerDAO,
                "Karabo", "Moeti", "Gaborone West",
                "75432109", null);

        InvestmentAccount i = bank.openInvestmentAccount(c.getCustomerId(), 12000, "Gaborone Main");
        ChequeAccount ch = bank.openChequeAccount(c.getCustomerId(), 5000, "Gaborone Main",
                "Water Utilities Corporation", "Gaborone");
        accountDAO.save(i);
        accountDAO.save(ch);
    }
    private static void createSampleCustomer11(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
    Customer c = createCustomer(bank, customerDAO,
            "Boitumelo", "Rakhudu", "Ramotswa",
            "76781234", "boitumelo.r@email.bw");

    SavingsAccount s = bank.openSavingsAccount(c.getCustomerId(), 3200, "Gaborone Main");
    accountDAO.save(s);
}

private static void createSampleCustomer12(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
    Customer c = createCustomer(bank, customerDAO,
            "Lorato", "Kgonanyane", "Mmopane",
            "71892345", null);

    InvestmentAccount i = bank.openInvestmentAccount(c.getCustomerId(), 9000, "Gaborone Main");
    accountDAO.save(i);
}

private static void createSampleCustomer13(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
    Customer c = createCustomer(bank, customerDAO,
            "Tshepo", "Gaone", "Gaborone Block 5",
            "73345621", "tshepo.g@email.bw");

    ChequeAccount ch = bank.openChequeAccount(c.getCustomerId(), 6000, "Gaborone Main",
            "Orange Botswana", "Gaborone CBD");
    accountDAO.save(ch);
}

private static void createSampleCustomer14(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
    Customer c = createCustomer(bank, customerDAO,
            "Palesa", "Mokgadi", "Serowe",
            "74561239", null);

    SavingsAccount s = bank.openSavingsAccount(c.getCustomerId(), 2100, "Gaborone Main");
    InvestmentAccount i = bank.openInvestmentAccount(c.getCustomerId(), 3500, "Gaborone Main");
    accountDAO.save(s);
    accountDAO.save(i);
}

private static void createSampleCustomer15(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
    Customer c = createCustomer(bank, customerDAO,
            "Otsile", "Tebogo", "Molepolole",
            "76450012", "otsile.t@email.bw");

    SavingsAccount s = bank.openSavingsAccount(c.getCustomerId(), 18000, "Gaborone Main");
    accountDAO.save(s);
}

private static void createSampleCustomer16(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
    Customer c = createCustomer(bank, customerDAO,
            "Kgetse", "Monare", "Kanye",
            "72123456", null);

    InvestmentAccount i = bank.openInvestmentAccount(c.getCustomerId(), 28000, "Gaborone Main");
    accountDAO.save(i);
}

private static void createSampleCustomer17(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
    Customer c = createCustomer(bank, customerDAO,
            "Dineo", "Sebogodi", "Lobatse",
            "71122334", "dineo.s@email.bw");

    ChequeAccount ch = bank.openChequeAccount(c.getCustomerId(), 7000, "Gaborone Main",
            "Debswana", "Lobatse");
    accountDAO.save(ch);
}

private static void createSampleCustomer18(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
    Customer c = createCustomer(bank, customerDAO,
            "Thabiso", "Ramatlhare", "Letlhakane",
            "74678901", "thabiso.r@email.bw");

    SavingsAccount s = bank.openSavingsAccount(c.getCustomerId(), 900, "Gaborone Main");
    InvestmentAccount i = bank.openInvestmentAccount(c.getCustomerId(), 700, "Gaborone Main");
    accountDAO.save(s);
    accountDAO.save(i);
}

private static void createSampleCustomer19(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
    Customer c = createCustomer(bank, customerDAO,
            "Naledi", "Modungwa", "Francistown",
            "76234190", null);

    SavingsAccount s = bank.openSavingsAccount(c.getCustomerId(), 6400, "Gaborone Main");
    accountDAO.save(s);
}

private static void createSampleCustomer20(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
    Customer c = createCustomer(bank, customerDAO,
            "Thapelo", "Rangaka", "Maun",
            "75890123", "thapelo.r@email.bw");

    InvestmentAccount i = bank.openInvestmentAccount(c.getCustomerId(), 11000, "Gaborone Main");
    ChequeAccount ch = bank.openChequeAccount(c.getCustomerId(), 4500, "Gaborone Main",
            "Botswana Tourism", "Maun");
    accountDAO.save(i);
    accountDAO.save(ch);
}

}
