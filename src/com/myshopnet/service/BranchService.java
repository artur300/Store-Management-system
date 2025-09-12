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
    private UserAccountRepository userAccountRepository = Singletons.USER_ACCOUNT_REPO;
    private EmployeeRepository employeeRepository = Singletons.EMPLOYEE_REPO;
    private AuthService authService = Singletons.AUTH_SERVICE;
    private BranchRepository branchRepository = Singletons.BRANCH_REPO;
    private ChatService chatService = Singletons.CHAT_SERVICE;
    private ProductRepository productRepository = Singletons.PRODUCT_REPO;

    public BranchService() {
        this.branchRepository = new BranchRepository();
        // Register as observer to all existing employees
        for (Employee e : employeeRepository.getAll()) {
            e.registerObserver(this);
        }
    }

    @Override
    public void onStatusChanged(Employee employee, EmployeeStatus oldStatus, EmployeeStatus newStatus) {
        // When an employee becomes AVAILABLE from BUSY, try to match waiting chat requests
        if (oldStatus == EmployeeStatus.BUSY && newStatus == EmployeeStatus.AVAILABLE) {
            UserAccount ua = userAccountRepository.get(employee.getUserId());

            if (ua != null) {
                notifyAndPollWaitingEmployeeToChat(ua);
            }
        }
    }

    public Branch createNewBranch(String branchName) {
        Branch newBranch = new Branch(UUID.randomUUID().toString(),branchName);

        return branchRepository.create(newBranch);
    }

    public void deleteBranch(String id) {
        branchRepository.delete(id);
    }

    public List<Branch> getAllBranches() {
        return branchRepository.getAll();
    }

    public Branch getBranchById(String id) {
        return branchRepository.get(id);
    }

    public List<Employee> getAllEmployeesInBranch(String branchId) {
        return employeeRepository.getAll().stream()
                .filter(e -> e.getBranchId().equals(branchId))
                .collect(Collectors.toList());
    }

    public void addEmployeeToWaitingBranchQueue(String branchId, UserAccount employeeRequesting) {
        Branch branch = branchRepository.get(branchId);

        branch.getEmployeesWaitingToChat().add(employeeRequesting);
    }

    public void notifyAndPollWaitingEmployeeToChat(UserAccount employeeAvailableToChat) {
        Branch branch = branchRepository.get(((Employee)(employeeAvailableToChat.getUser())).getBranchId());

        if (branch == null) {
            throw new EntityNotFoundException("Branch");
        }

        UserAccount userAccountWaitingToChat = branch.getEmployeesWaitingToChat().poll();
        chatService.createChat(employeeAvailableToChat, userAccountWaitingToChat);
    }

    public Boolean checkIfProductInStockInBranch(String branchId, String sku, Long quantity) {
        boolean inStock = false;

        Branch branch =  branchRepository.get(branchId);
        Product product = productRepository.get(sku);

        if (branch == null) {
            throw new EntityNotFoundException("Branch");
        }

        if (product == null) {
            throw new EntityNotFoundException("Product");
        }

        inStock = branch.getProductsStock().getStockOfProducts().get(product) - quantity >= 0;
        return inStock;
    }

        public UserAccount findAvailableEmployee(Branch branch) {
        UserAccount userAccount = null;
        List<Employee> employeesOfBranch = getAllEmployeesInBranch(branch.getId()).stream()
                .filter(employee -> employee.getEmployeeStatus().equals(EmployeeStatus.AVAILABLE))
                .toList();

        if (!employeesOfBranch.isEmpty()) {
            userAccount = userAccountRepository.get(employeesOfBranch.getFirst().getUserId());
        }

        return userAccount;
    }
}
