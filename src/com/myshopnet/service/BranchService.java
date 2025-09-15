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
        this.branchRepository = new BranchRepository();
        for (Employee e : employeeRepository.getAll()) {
            e.registerObserver(this);
        }
    }

    @Override
    public void onStatusChanged(Employee employee, EmployeeStatus oldStatus, EmployeeStatus newStatus) {
        if (newStatus == EmployeeStatus.AVAILABLE) {
            UserAccount ua = userAccountRepository.get(employee.getUserId());
            if (ua != null) {
                try {
                    notifyAndPollWaitingEmployeeToChat(ua);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public Branch createNewBranch(String branchName) {
        Branch newBranch = new Branch(UUID.randomUUID().toString(), branchName);
        return branchRepository.create(newBranch);
    }

    public void deleteBranch(String id) {
        branchRepository.delete(id);
    }

    public List<Branch> getAllBranches() {
        List<Branch> all = branchRepository.getAll();
        return all;
    }

    public Branch getBranchById(String id) {
        return branchRepository.get(id);
    }

    public List<Employee> getAllEmployeesInBranch(String branchId) {
        List<Employee> employees = employeeRepository.getAll().stream()
                .filter(e -> e.getBranchId().equals(branchId))
                .collect(Collectors.toList());
        return employees;
    }

    public void addEmployeeToWaitingBranchQueue(String branchId, UserAccount employeeRequesting) {
        Branch branch = branchRepository.get(branchId);
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
            Singletons.CHAT_SERVICE.createChat(userAccountWaitingToChat, employeeAvailableToChat);
        } else {
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
        return inStock;
    }

    public UserAccount findAvailableEmployee(Branch branch) {
        UserAccount availableEmployee = null;

        List<UserAccount> allEmployesAvailable = userAccountRepository.getAll().stream()
                .filter(userAccount -> userAccount.getUser() instanceof Employee)
                .filter(userAccount -> (!((Employee) userAccount.getUser()).getBranchId().equals(branch.getId())) &&
                        ((Employee)userAccount.getUser()).getEmployeeStatus().equals(EmployeeStatus.AVAILABLE))
                .toList();

        if (!allEmployesAvailable.isEmpty()) {
            availableEmployee =  allEmployesAvailable.getFirst();
        }

        return availableEmployee;
    }

    public UserAccount findAvailableEmployeeInOtherBranch(String requestingBranchId) {
        Branch branch = branchRepository.get(requestingBranchId);

        return findAvailableEmployee(branch);
    }
}
