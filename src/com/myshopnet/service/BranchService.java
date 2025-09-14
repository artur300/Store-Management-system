package com.myshopnet.service;

import com.myshopnet.auth.UserAccount;
import com.myshopnet.errors.EntityNotFoundException;
import com.myshopnet.models.*;
import com.myshopnet.repository.BranchRepository;
import com.myshopnet.repository.EmployeeRepository;
import com.myshopnet.repository.ProductRepository;
import com.myshopnet.repository.UserAccountRepository;
import com.myshopnet.utils.Singletons;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BranchService implements EmployeeStatusObserver {
    private final UserAccountRepository userAccountRepository = Singletons.USER_ACCOUNT_REPO;
    private final EmployeeRepository employeeRepository = Singletons.EMPLOYEE_REPO;
    private final AuthService authService = Singletons.AUTH_SERVICE;
    private BranchRepository branchRepository = Singletons.BRANCH_REPO;
    private final ProductRepository productRepository = Singletons.PRODUCT_REPO;

    public BranchService() {
        // אם תרצה, אפשר להשאיר; כל ה-Repositories עובדים מול Data סטטי ולכן זה לא שוברת כלום
        this.branchRepository = new BranchRepository();
        System.out.println("[DEBUG] BranchService initialized, totalEmployees=" + employeeRepository.getAll().size());

        for (Employee e : employeeRepository.getAll()) {
            e.registerObserver(this);
        }
    }

    // === תיקון קריטי ===
    @Override
    public void onStatusChanged(Employee employee, EmployeeStatus oldStatus, EmployeeStatus newStatus) {
        System.out.println("[DEBUG] onStatusChanged -> employee=" + employee.getFullName()
                + ", from=" + oldStatus + ", to=" + newStatus);

        // כל פעם שעובד הופך ל-AVAILABLE ננסה לשדך לו ממתין מהתור
        if (newStatus == EmployeeStatus.AVAILABLE) {
            UserAccount ua = userAccountRepository.get(employee.getUserId());
            if (ua != null) {
                try {
                    notifyAndPollWaitingEmployeeToChat(ua);
                } catch (Exception ex) {
                    System.out.println("[ERROR] onStatusChanged -> failed to match from queue: " + ex);
                    ex.printStackTrace();
                }
            }
        }
    }

    public Branch createNewBranch(String branchName) {
        Branch newBranch = new Branch(UUID.randomUUID().toString(), branchName);
        System.out.println("[DEBUG] createNewBranch -> " + branchName + ", id=" + newBranch.getId());
        return branchRepository.create(newBranch);
    }

    public void deleteBranch(String id) {
        System.out.println("[DEBUG] deleteBranch -> " + id);
        branchRepository.delete(id);
    }

    public List<Branch> getAllBranches() {
        List<Branch> all = branchRepository.getAll();
        System.out.println("[DEBUG] getAllBranches -> found=" + all.size());
        return all;
    }

    public Branch getBranchById(String id) {
        System.out.println("[DEBUG] getBranchById -> id=" + id);
        return branchRepository.get(id);
    }

    public List<Employee> getAllEmployeesInBranch(String branchId) {
        List<Employee> employees = employeeRepository.getAll().stream()
                .filter(e -> e.getBranchId().equals(branchId))
                .collect(Collectors.toList());
        System.out.println("[DEBUG] getAllEmployeesInBranch -> branchId=" + branchId + ", count=" + employees.size());
        return employees;
    }

    public void addEmployeeToWaitingBranchQueue(String branchId, UserAccount employeeRequesting) {
        Branch branch = branchRepository.get(branchId);
        System.out.println("[DEBUG] addEmployeeToWaitingBranchQueue -> branch=" + branch.getName()
                + ", employee=" + employeeRequesting.getUsername());
        branch.getEmployeesWaitingToChat().add(employeeRequesting);
    }

    // === שימוש Lazy ב-ChatService מתוך Singletons כדי לשבור את התלות המעגלית ===
    public void notifyAndPollWaitingEmployeeToChat(UserAccount employeeAvailableToChat) {
        Branch branch = branchRepository.get(((Employee) (employeeAvailableToChat.getUser())).getBranchId());
        if (branch == null) {
            throw new EntityNotFoundException("Branch");
        }

        UserAccount userAccountWaitingToChat = branch.getEmployeesWaitingToChat().poll();
        if (userAccountWaitingToChat != null) {
            System.out.println("[DEBUG] notifyAndPollWaitingEmployeeToChat -> available="
                    + employeeAvailableToChat.getUsername()
                    + ", waiting=" + userAccountWaitingToChat.getUsername());

            // סדר פרמטרים לא משנה; נשתמש במבקש-ראשון לזיהוי ברור בלוגים
            Singletons.CHAT_SERVICE.createChat(userAccountWaitingToChat, employeeAvailableToChat);
        } else {
            System.out.println("[DEBUG] notifyAndPollWaitingEmployeeToChat -> no waiting users in branch=" + branch.getName());
        }
    }

    public Boolean checkIfProductInStockInBranch(String branchId, String sku, Long quantity) {
        Branch branch = branchRepository.get(branchId);
        Product product = productRepository.get(sku);

        if (branch == null) {
            throw new EntityNotFoundException("Branch");
        }
        if (product == null) {
            throw new EntityNotFoundException("Product");
        }

        boolean inStock = branch.getProductsStock().getStockOfProducts().get(product) - quantity >= 0;
        System.out.println("[DEBUG] checkIfProductInStockInBranch -> branch=" + branch.getName()
                + ", product=" + product.getName()
                + ", quantity=" + quantity + ", inStock=" + inStock);
        return inStock;
    }

    public UserAccount findAvailableEmployee(Branch branch) {
        List<Employee> employeesOfBranch = getAllEmployeesInBranch(branch.getId()).stream()
                .filter(employee -> {
                    UserAccount ua = userAccountRepository.get(employee.getUserId());
                    return employee.getEmployeeStatus() == EmployeeStatus.AVAILABLE
                            && ua != null
                            && Singletons.AUTH_SERVICE.isLoggedIn(ua);
                })
                .toList();

        System.out.println("[DEBUG] findAvailableEmployee -> branch=" + branch.getName()
                + ", availableLoggedInCount=" + employeesOfBranch.size());

        if (!employeesOfBranch.isEmpty()) {
            Employee chosen = employeesOfBranch.getFirst();
            UserAccount ua = userAccountRepository.get(chosen.getUserId());
            System.out.println("[DEBUG] Chosen logged-in employee=" + chosen.getFullName()
                    + ", username=" + ua.getUsername());
            return ua;
        }

        return null;
    }

    public UserAccount findAvailableEmployeeInOtherBranch(String requestingBranchId) {
        System.out.println("[DEBUG] findAvailableEmployeeInOtherBranch -> excluding branchId=" + requestingBranchId);

        for (Branch branch : branchRepository.getAll()) {
            if (!branch.getId().equals(requestingBranchId)) {
                System.out.println("[DEBUG] Checking branch=" + branch.getName());
                UserAccount available = findAvailableEmployee(branch);
                if (available != null) {
                    System.out.println("[DEBUG] ✅ Returning available employee from branch=" + branch.getName()
                            + ", username=" + available.getUsername());
                    return available;
                } else {
                    System.out.println("[DEBUG] No available employees in branch=" + branch.getName());
                }
            }
        }

        System.out.println("[DEBUG] ❌ No available employees in other branches");
        return null;
    }
}
