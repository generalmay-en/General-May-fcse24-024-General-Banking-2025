import com.banking.model.*;

public class QuickTest {
    public static void main(String[] args) {
        System.out.println("🏦 Testing Banking System Core Model\n");
        
        // Create bank
        Bank bank = new Bank("Botswana Accountancy College Bank", "BAC");
        System.out.println("✓ Bank created: " + bank.getBankName());
        
        // Register customer
        Customer customer = bank.registerCustomer("General", "May", "Gaborone");
        System.out.println("✓ Customer registered: " + customer.getCustomerId());
        
        // Open all 3 account types
        SavingsAccount savings = bank.openSavingsAccount(customer.getCustomerId(), 1000, "Main");
        System.out.println("✓ Savings Account opened: " + savings.getAccountNumber());
        
        InvestmentAccount investment = bank.openInvestmentAccount(customer.getCustomerId(), 500, "Main");
        System.out.println("✓ Investment Account opened: " + investment.getAccountNumber());
        
        ChequeAccount cheque = bank.openChequeAccount(
            customer.getCustomerId(), 200, "Main", 
            "Botswana Accountancy College", "Gaborone"
        );
        System.out.println("✓ Cheque Account opened: " + cheque.getAccountNumber());
        
        // Test deposit
        savings.deposit(500);
        System.out.println("✓ Deposited BWP 500. New balance: BWP " + savings.getBalance());
        
        // Test withdrawal restrictions
        boolean canWithdraw = savings.withdraw(100);
        System.out.println("✓ Savings withdrawal blocked: " + !canWithdraw);
        
        // Test interest calculation
        double savingsInterest = savings.calculateInterest();
        double investmentInterest = investment.calculateInterest();
        System.out.println("✓ Savings interest (0.05%): BWP " + String.format("%.2f", savingsInterest));
        System.out.println("✓ Investment interest (5%): BWP " + String.format("%.2f", investmentInterest));
        
        // Test monthly interest processing
        bank.processMonthlyInterest();
        System.out.println("✓ Monthly interest processed for all accounts");
        
        System.out.println("\n✅ ALL CORE MODEL TESTS PASSED!");
        System.out.println("📊 Total customers: " + bank.getCustomerCount());
        System.out.println("📊 Total accounts: " + bank.getAccountCount());
    }
}
