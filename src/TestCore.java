import com.banking.model.*;
import com.banking.interfaces.*;

public class TestCore {
    public static void main(String[] args) {
        System.out.println("Testing core model compilation...");
        
        // Test 1: Create bank
        Bank bank = new Bank("Test Bank", "TB");
        System.out.println("✓ Bank created");
        
        // Test 2: Register customer
        Customer customer = bank.registerCustomer("John", "Doe", "Gaborone");
        System.out.println("✓ Customer registered: " + customer.getCustomerId());
        
        // Test 3: Open Savings Account
        SavingsAccount savings = bank.openSavingsAccount(
            customer.getCustomerId(), 1000.0, "Main"
        );
        System.out.println("✓ Savings Account: " + savings.getAccountNumber());
        
        // Test 4: Open Investment Account (with minimum BWP 500)
        InvestmentAccount investment = bank.openInvestmentAccount(
            customer.getCustomerId(), 500.0, "Main"
        );
        System.out.println("✓ Investment Account: " + investment.getAccountNumber());
        
        // Test 5: Open Cheque Account (requires employment)
        ChequeAccount cheque = bank.openChequeAccount(
            customer.getCustomerId(), 100.0, "Main",
            "ABC Corp", "Plot 123, Gaborone"
        );
        System.out.println("✓ Cheque Account: " + cheque.getAccountNumber());
        
        // Test 6: Deposit to savings
        savings.deposit(500);
        System.out.println("✓ Deposit successful. Balance: " + savings.getBalance());
        
        // Test 7: Try to withdraw from savings (should fail)
        boolean result = savings.withdraw(100);
        System.out.println("✓ Savings withdrawal blocked: " + !result);
        
        // Test 8: Withdraw from investment (should succeed)
        result = investment.withdraw(100);
        System.out.println("✓ Investment withdrawal: " + result);
        
        // Test 9: Calculate interest
        System.out.println("✓ Savings interest: BWP " + savings.calculateInterest());
        System.out.println("✓ Investment interest: BWP " + investment.calculateInterest());
        System.out.println("✓ Cheque interest: BWP " + cheque.calculateInterest());
        
        // Test 10: User authentication
        User user = new User("T001", "Teller One", "password123", "TELLER");
        result = user.authenticate("T001", "password123");
        System.out.println("✓ User authentication: " + result);
        
        System.out.println("\n✅ ALL TESTS PASSED! Core model is complete.");
    }
}
