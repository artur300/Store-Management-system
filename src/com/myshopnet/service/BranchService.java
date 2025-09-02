package com.myshopnet.service;

import com.myshopnet.auth.UserAccount;
import com.myshopnet.errors.EntityNotFoundException;
import com.myshopnet.models.Branch;
import com.myshopnet.models.Employee;
import com.myshopnet.models.EmployeeStatus;
import com.myshopnet.repository.BranchRepository;
import com.myshopnet.repository.EmployeeRepository;
import com.myshopnet.repository.UserAccountRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BranchService {
    private UserAccountRepository userAccountRepository = new UserAccountRepository();
    private EmployeeRepository employeeRepository = new EmployeeRepository();
    private AuthService authService = new AuthService();
    private BranchRepository branchRepository = new BranchRepository();
    private ChatService chatService = new ChatService();

    public BranchService() {
        this.branchRepository = new BranchRepository();
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

    public UserAccount findAvailableEmployee(Branch branch) {
        UserAccount userAccount = null;
        List<Employee> employeesOfBranch = getAllEmployeesInBranch(branch.getId()).stream()
                .filter(employee -> employee.getEmployeeStatus().equals(EmployeeStatus.AVAILABLE))
                .toList();

        if (!employeesOfBranch.isEmpty()) {
            userAccount = userAccountRepository.get(employeesOfBranch.getFirst().getId());
        }

        return userAccount;
    }
}
