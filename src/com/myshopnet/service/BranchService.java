package com.myshopnet.service;

import com.myshopnet.models.Branch;
import com.myshopnet.repository.BranchRepository;

import java.util.List;
import java.util.UUID;

public class BranchService {
    private BranchRepository branchRepository;

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
}
