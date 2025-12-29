package util;

import java.util.regex.Pattern;
import java.util.Date;
import java.util.Calendar;
import javax.swing.JOptionPane;
import java.awt.Component;

/**
 *
 * @author andyb
 */
public class ValidationUtil {
    
    // ============================================
    // REGEX PATTERNS
    // ============================================
    
    // Email pattern (RFC 5322 simplified)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    
    // Phone pattern (Rwanda format: 07XXXXXXXX or +25078XXXXXXX)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^(\\+?250|0)?7[0-9]{8}$"
    );
    
    // Username pattern (alphanumeric, underscore, 3-20 chars)
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_]{3,20}$"
    );
    
    // Password pattern (min 6 chars, at least 1 letter and 1 number)
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{6,}$"
    );
    
    // National ID pattern (Rwanda: 16 digits)
    private static final Pattern NATIONAL_ID_PATTERN = Pattern.compile(
        "^[0-9]{16}$"
    );
    
    // Alphanumeric with spaces (for names)
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "^[a-zA-Z\\s]{2,50}$"
    );
    
    // ============================================
    // VALIDATION RESULT CLASS
    // ============================================
    
    public static class ValidationResult {
        private boolean valid;
        private String message;
        
        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getMessage() {
            return message;
        }
        
        public static ValidationResult success() {
            return new ValidationResult(true, "Validation successful");
        }
        
        public static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }
    }
    
    // ============================================
    // JOPTIONPANE DISPLAY UTILITIES
    // ============================================
    
    /**
     * Show validation error message
     * Red X icon, "Validation Error" title
     */
    public static void showError(Component parent, ValidationResult result) {
        if (!result.isValid()) {
            JOptionPane.showMessageDialog(
                parent,
                result.getMessage(),
                "Validation Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * Show validation error message with custom title
     */
    public static void showError(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            title,
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * Show validation warning message
     * Yellow exclamation icon, "Warning" title
     */
    public static void showWarning(Component parent, ValidationResult result) {
        if (!result.isValid()) {
            JOptionPane.showMessageDialog(
                parent,
                result.getMessage(),
                "Warning",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }
    
    /**
     * Show validation warning with custom title
     */
    public static void showWarning(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            title,
            JOptionPane.WARNING_MESSAGE
        );
    }
    
    /**
     * Show information message
     * Blue i icon, "Information" title
     */
    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            "Information",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Show success message
     * Green checkmark icon (using INFORMATION), "Success" title
     */
    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            "Success",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Validate and show error if invalid
     * Returns true if valid, false if invalid (and shows error)
     */
    public static boolean validateAndShow(Component parent, ValidationResult result) {
        if (!result.isValid()) {
            showError(parent, result);
            return false;
        }
        return true;
    }
    
    /**
     * Validate multiple results and show first error
     * Returns true only if ALL validations pass
     */
    public static boolean validateAllAndShow(Component parent, ValidationResult... results) {
        for (ValidationResult result : results) {
            if (!result.isValid()) {
                showError(parent, result);
                return false;
            }
        }
        return true;
    }
    
    /**
     * Show confirmation dialog
     * Returns true if user clicks Yes, false if No
     */
    public static boolean showConfirmation(Component parent, String message, String title) {
        int choice = JOptionPane.showConfirmDialog(
            parent,
            message,
            title,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        return choice == JOptionPane.YES_OPTION;
    }
    
    /**
     * Show delete confirmation
     * Returns true if user confirms deletion
     */
    public static boolean showDeleteConfirmation(Component parent, String itemName) {
        return showConfirmation(
            parent,
            "Are you sure you want to delete " + itemName + "?\nThis action cannot be undone.",
            "Confirm Delete"
        );
    }
    
    // ============================================
    // TECHNICAL VALIDATIONS
    // ============================================
    
    /**
     * Validate if string is not null or empty
     */
    public static ValidationResult validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return ValidationResult.error(fieldName + " cannot be empty");
        }
        return ValidationResult.success();
    }
    
    /**
     * Validate string length
     */
    public static ValidationResult validateLength(String value, String fieldName, 
                                                  int minLength, int maxLength) {
        if (value == null) {
            return ValidationResult.error(fieldName + " cannot be null");
        }
        
        int length = value.trim().length();
        
        if (length < minLength) {
            return ValidationResult.error(fieldName + " must be at least " + minLength + " characters");
        }
        
        if (length > maxLength) {
            return ValidationResult.error(fieldName + " cannot exceed " + maxLength + " characters");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate email format
     */
    public static ValidationResult validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return ValidationResult.error("Email cannot be empty");
        }
        
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return ValidationResult.error("Invalid email format. Example: user@example.com");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate phone number (Rwanda format)
     */
    public static ValidationResult validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return ValidationResult.error("Phone number cannot be empty");
        }
        
        // Remove spaces and dashes
        String cleanPhone = phone.trim().replaceAll("[\\s-]", "");
        
        if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
            return ValidationResult.error("Invalid phone format. Use: 0781234567 or +250781234567");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate username
     */
    public static ValidationResult validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return ValidationResult.error("Username cannot be empty");
        }
        
        if (!USERNAME_PATTERN.matcher(username.trim()).matches()) {
            return ValidationResult.error("Username must be 3-20 characters (letters, numbers, underscore only)");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate password strength
     */
    public static ValidationResult validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return ValidationResult.error("Password cannot be empty");
        }
        
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return ValidationResult.error("Password must be at least 6 characters with letters and numbers");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate national ID (Rwanda format)
     */
    public static ValidationResult validateNationalId(String nationalId) {
        if (nationalId == null || nationalId.trim().isEmpty()) {
            return ValidationResult.error("National ID cannot be empty");
        }
        
        String cleanId = nationalId.trim().replaceAll("\\s", "");
        
        if (!NATIONAL_ID_PATTERN.matcher(cleanId).matches()) {
            return ValidationResult.error("National ID must be exactly 16 digits");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate name (alphabetic with spaces)
     */
    public static ValidationResult validateName(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) {
            return ValidationResult.error(fieldName + " cannot be empty");
        }
        
        if (!NAME_PATTERN.matcher(name.trim()).matches()) {
            return ValidationResult.error(fieldName + " must contain only letters (2-50 characters)");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate numeric value
     */
    public static ValidationResult validateNumeric(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return ValidationResult.error(fieldName + " cannot be empty");
        }
        
        try {
            Double.parseDouble(value.trim());
            return ValidationResult.success();
        } catch (NumberFormatException e) {
            return ValidationResult.error(fieldName + " must be a valid number");
        }
    }
    
    /**
     * Validate integer value
     */
    public static ValidationResult validateInteger(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return ValidationResult.error(fieldName + " cannot be empty");
        }
        
        try {
            Integer.parseInt(value.trim());
            return ValidationResult.success();
        } catch (NumberFormatException e) {
            return ValidationResult.error(fieldName + " must be a valid whole number");
        }
    }
    
    /**
     * Validate date is not null
     */
    public static ValidationResult validateDate(Date date, String fieldName) {
        if (date == null) {
            return ValidationResult.error(fieldName + " cannot be empty");
        }
        return ValidationResult.success();
    }
    
    // ============================================
    // BUSINESS VALIDATIONS
    // ============================================
    
    /**
     * Validate price is positive
     */
    public static ValidationResult validatePrice(double price) {
        if (price <= 0) {
            return ValidationResult.error("Price must be greater than 0");
        }
        
        if (price > 10000000) { // 10 million max
            return ValidationResult.error("Price seems unreasonably high. Please verify.");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate stock quantity
     */
    public static ValidationResult validateStockQuantity(int quantity) {
        if (quantity < 0) {
            return ValidationResult.error("Stock quantity cannot be negative");
        }
        
        if (quantity > 1000000) {
            return ValidationResult.error("Stock quantity seems unreasonably high. Please verify.");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate sale quantity against available stock
     */
    public static ValidationResult validateSaleQuantity(int requestedQuantity, 
                                                       int availableStock) {
        if (requestedQuantity <= 0) {
            return ValidationResult.error("Quantity must be greater than 0");
        }
        
        if (requestedQuantity > availableStock) {
            return ValidationResult.error("Insufficient stock " + 
                ". Available: " + availableStock + ", Requested: " + requestedQuantity);
        }
        
        if (requestedQuantity > 1000) {
            return ValidationResult.error("Quantity seems unusually high. Maximum 1000 items per transaction.");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate salary amount
     */
    public static ValidationResult validateSalary(double salary) {
        if (salary <= 0) {
            return ValidationResult.error("Salary must be greater than 0");
        }
        
        // Rwanda minimum wage (approximate)
        if (salary < 10000) {
            return ValidationResult.error("Salary is below minimum wage (10,000 RWF)");
        }
        
        if (salary > 50000000) { // 50 million max
            return ValidationResult.error("Salary seems unreasonably high. Please verify.");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate age (from date of birth)
     */
    public static ValidationResult validateAge(Date dateOfBirth, int minAge, int maxAge) {
        if (dateOfBirth == null) {
            return ValidationResult.error("Date of birth cannot be empty");
        }
        
        Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();
        birthDate.setTime(dateOfBirth);
        
        // Check if date is in future
        if (birthDate.after(today)) {
            return ValidationResult.error("Date of birth cannot be in the future");
        }
        
        // Calculate age
        int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
        
        if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        
        if (age < minAge) {
            return ValidationResult.error("Age must be at least " + minAge + " years");
        }
        
        if (age > maxAge) {
            return ValidationResult.error("Age cannot exceed " + maxAge + " years");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate employee minimum age (18 years)
     */
    public static ValidationResult validateEmployeeAge(Date dateOfBirth) {
        return validateAge(dateOfBirth, 18, 70);
    }
    
    /**
     * Validate discount percentage
     */
    public static ValidationResult validateDiscount(double discount) {
        if (discount < 0) {
            return ValidationResult.error("Discount cannot be negative");
        }
        
        if (discount > 100) {
            return ValidationResult.error("Discount cannot exceed 100%");
        }
        
        if (discount > 50) {
            return ValidationResult.error("Discount exceeds 50%. Manager approval required.");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate reorder level against current stock
     */
    public static ValidationResult validateReorderLevel(int reorderLevel, int currentStock) {
        if (reorderLevel < 0) {
            return ValidationResult.error("Reorder level cannot be negative");
        }
        
        if (reorderLevel > currentStock * 2) {
            return ValidationResult.error("Reorder level seems unusually high compared to current stock");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate transaction amount
     */
    public static ValidationResult validateTransactionAmount(double amount) {
        if (amount <= 0) {
            return ValidationResult.error("Transaction amount must be greater than 0");
        }
        
        if (amount > 10000000) { // 10 million max per transaction
            return ValidationResult.error("Transaction amount exceeds maximum limit (10,000,000 RWF)");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate date range (from date must be before to date)
     */
    public static ValidationResult validateDateRange(Date fromDate, Date toDate) {
        if (fromDate == null || toDate == null) {
            return ValidationResult.error("Both dates must be selected");
        }
        
        if (fromDate.after(toDate)) {
            return ValidationResult.error("'From Date' must be before 'To Date'");
        }
        
        // Check if range is too large (e.g., more than 1 year)
        Calendar from = Calendar.getInstance();
        from.setTime(fromDate);
        Calendar to = Calendar.getInstance();
        to.setTime(toDate);
        
        long diffInMillis = to.getTimeInMillis() - from.getTimeInMillis();
        long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);
        
        if (diffInDays > 365) {
            return ValidationResult.error("Date range cannot exceed 1 year. Please select a smaller range.");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate loyalty points
     */
    public static ValidationResult validateLoyaltyPoints(int points) {
        if (points < 0) {
            return ValidationResult.error("Loyalty points cannot be negative");
        }
        
        if (points > 1000000) {
            return ValidationResult.error("Loyalty points seem unreasonably high");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate product category
     */
    public static ValidationResult validateCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return ValidationResult.error("Category cannot be empty");
        }
        
        // Define allowed categories
        String[] allowedCategories = {
            "Grains", "Beverages", "Dairy", "Bakery", "Cooking", 
            "Snacks", "Frozen", "Fresh Produce", "Household", "Personal Care"
        };
        
        boolean valid = false;
        for (String allowed : allowedCategories) {
            if (allowed.equalsIgnoreCase(category.trim())) {
                valid = true;
                break;
            }
        }
        
        if (!valid) {
            return ValidationResult.error("Invalid category. Please select from predefined categories.");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate employee role
     */
    public static ValidationResult validateEmployeeRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return ValidationResult.error("Role cannot be empty");
        }
        
        // Define allowed roles
        String[] allowedRoles = {"ADMIN", "MANAGER", "CASHIER"};
        
        boolean valid = false;
        for (String allowed : allowedRoles) {
            if (allowed.equalsIgnoreCase(role.trim())) {
                valid = true;
                break;
            }
        }
        
        if (!valid) {
            return ValidationResult.error("Invalid role. Allowed roles: ADMIN, MANAGER, CASHIER");
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Validate inventory transaction type
     */
    public static ValidationResult validateTransactionType(String type) {
        if (type == null || type.trim().isEmpty()) {
            return ValidationResult.error("Transaction type cannot be empty");
        }
        
        String[] allowedTypes = {"STOCK_IN", "STOCK_OUT", "ADJUSTMENT"};
        
        boolean valid = false;
        for (String allowed : allowedTypes) {
            if (allowed.equalsIgnoreCase(type.trim())) {
                valid = true;
                break;
            }
        }
        
        if (!valid) {
            return ValidationResult.error("Invalid transaction type. Allowed: STOCK_IN, STOCK_OUT, ADJUSTMENT");
        }
        
        return ValidationResult.success();
    }
    
    // ============================================
    // COMPOSITE VALIDATIONS
    // ============================================
    
    /**
     * Validate complete product data
     */
    public static ValidationResult validateProduct(String productName, String category, 
                                                   double price, int stockQuantity) {
        ValidationResult result;
        
        // Name validation
        result = validateNotEmpty(productName, "Product Name");
        if (!result.isValid()) return result;
        
        result = validateLength(productName, "Product Name", 2, 100);
        if (!result.isValid()) return result;
        
        // Category validation
        result = validateCategory(category);
        if (!result.isValid()) return result;
        
        // Price validation
        result = validatePrice(price);
        if (!result.isValid()) return result;
        
        // Stock validation
        result = validateStockQuantity(stockQuantity);
        if (!result.isValid()) return result;
        
        return ValidationResult.success();
    }
    
    /**
     * Validate complete customer data
     */
    public static ValidationResult validateCustomer(String fullName, String email, 
                                                    String phone, String address) {
        ValidationResult result;
        
        // Name validation
        result = validateName(fullName, "Customer Name");
        if (!result.isValid()) return result;
        
        // Email validation
        result = validateEmail(email);
        if (!result.isValid()) return result;
        
        // Phone validation
        result = validatePhone(phone);
        if (!result.isValid()) return result;
        
        // Address validation
        result = validateNotEmpty(address, "Address");
        if (!result.isValid()) return result;
        
        result = validateLength(address, "Address", 5, 255);
        if (!result.isValid()) return result;
        
        return ValidationResult.success();
    }
    
    /**
     * Validate complete employee data
     */
    public static ValidationResult validateEmployee(String fullName, String role, 
                                                    String username, double salary, 
                                                    String contact, String password) {
        ValidationResult result;
        
        // Name validation
        result = validateName(fullName, "Full Name");
        if (!result.isValid()) return result;
        
        // Role validation
        result = validateEmployeeRole(role);
        if (!result.isValid()) return result;
        
        // Username validation
        result = validateUsername(username);
        if (!result.isValid()) return result;
        
        // Salary validation
        result = validateSalary(salary);
        if (!result.isValid()) return result;
        
        // Contact validation
        result = validateEmail(contact);
        if (!result.isValid()) return result;
        
        // Password validation
        result = validatePassword(password);
        if (!result.isValid()) return result;
        
        return ValidationResult.success();
    }
    
    /**
     * Validate complete supplier data
     */
    public static ValidationResult validateSupplier(String supplierName, String contactPerson,
                                                    String phone, String email, String address) {
        ValidationResult result;
        
        // Supplier name validation
        result = validateNotEmpty(supplierName, "Supplier Name");
        if (!result.isValid()) return result;
        
        result = validateLength(supplierName, "Supplier Name", 2, 100);
        if (!result.isValid()) return result;
        
        // Contact person validation
        result = validateName(contactPerson, "Contact Person");
        if (!result.isValid()) return result;
        
        // Phone validation
        result = validatePhone(phone);
        if (!result.isValid()) return result;
        
        // Email validation
        result = validateEmail(email);
        if (!result.isValid()) return result;
        
        // Address validation
        result = validateNotEmpty(address, "Address");
        if (!result.isValid()) return result;
        
        return ValidationResult.success();
    }
}